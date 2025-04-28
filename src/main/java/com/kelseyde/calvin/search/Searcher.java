package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.NNUE;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;
import com.kelseyde.calvin.search.picker.MovePicker;
import com.kelseyde.calvin.search.picker.QuiescentMovePicker;
import com.kelseyde.calvin.search.picker.ScoredMove;
import com.kelseyde.calvin.tables.tt.HashEntry;
import com.kelseyde.calvin.tables.tt.HashFlag;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.uci.UCI;

import java.util.List;

/**
 * Classical alpha-beta search with iterative deepening. This is the main search algorithm used by the engine.
 * </p>
 * Alpha-beta search seeks to reduce the number of nodes that need to be evaluated in the search tree. It does this by
 * pruning branches that are guaranteed to be worse than the best move found so far, or that are guaranteed to be 'too
 * good' and could only be reached by sup-optimal play by the opponent.
 * @see <a href="https://www.chessprogramming.org/Alpha-Beta">Chess Programming Wiki</a>
 * </p>
 * Iterative deepening is a search strategy that does a full search at a depth of 1 ply, then a full search at 2 ply,
 * then 3 ply and so on, until the time limit is exhausted. In case the timeout is reached in the middle of an iteration,
 * the search can still fall back on the best move found in the previous iteration. By prioritising searching the best
 * move found in the previous iteration -- and by using a {@link TranspositionTable} -- the iterative approach is much
 * more efficient than it might sound.
 * @see <a href="https://www.chessprogramming.org/Iterative_Deepening">Chess Programming Wiki</a>
 */
public class Searcher implements Search {

    final EngineConfig config;
    final TranspositionTable tt;
    final MoveGenerator movegen;
    final SearchHistory history;
    final SearchStack ss;
    final ThreadData td;
    final NNUE eval;

    Move bestMoveCurrent;
    int bestScoreCurrent;

    TimeControl tc;
    Board board;

    public Searcher(EngineConfig config, TranspositionTable tt, ThreadData td) {
        this.config = config;
        this.tt = tt;
        this.td = td;
        this.history = new SearchHistory(config);
        this.movegen = new MoveGenerator();
        this.ss = new SearchStack();
        this.eval = new NNUE();
    }

    /**
     * Search the current position, increasing the depth each iteration, to find the best move within the given time limit.
     * @param timeControl the maximum duration to search
     * @return a {@link SearchResult} containing the best move, the eval, and other search info.
     */
    @Override
    public SearchResult search(TimeControl timeControl) {

        final List<Move> rootMoves = movegen.generateMoves(board);
        tc = timeControl;

        if (rootMoves.isEmpty())
            return handleNoLegalMoves();

        if (rootMoves.size() == 1)
            return handleOneLegalMove(rootMoves);

        ss.clear();
        td.reset();
        history.reset();

        // The best root move and root score is updated as the search progresses.
        Move bestMoveRoot = null;
        int bestScoreRoot = 0;

        // The alpha-beta window is initialised to the maximum value, [Score.MIN, Score.MAX].
        // After each iteration, the window is narrowed around the search score.
        int alpha = Score.MIN;
        int beta = Score.MAX;

        int reduction = 0;
        int maxReduction = config.aspMaxReduction();
        int window = config.aspDelta();

        while (!softLimitReached() && td.depth < Search.MAX_DEPTH) {
            // Reset variables for the current depth iteration
            bestMoveCurrent = null;
            bestScoreCurrent = 0;
            td.seldepth = 0;

            final int searchDepth = td.depth - reduction;

            // Perform alpha-beta search for the current depth
            final int score = search(searchDepth, 0, alpha, beta, false);

            // Update the best move and evaluation if a better move is found
            if (bestMoveCurrent != null) {
                history.updateBestMoveStability(bestMoveRoot, bestMoveCurrent);
                history.updateBestScoreStability(bestScoreRoot, bestScoreCurrent);
                bestMoveRoot = bestMoveCurrent;
                bestScoreRoot = bestScoreCurrent;
                if (td.isMainThread()) {
                    // Write search info as UCI output. This is only done for the main thread.
                    UCI.writeSearchInfo(SearchResult.of(bestMoveRoot, bestScoreRoot, td, tc));
                }
            }

            // Check if search is cancelled or a checkmate is found
            if (hardLimitReached() || Score.isMate(score)) {
                break;
            }

            // Aspiration windows
            // Use the search score from the previous iteration to guess the score from the current iteration.
            // Based on this guess, we can narrow the alpha-beta window around the previous score, causing more cut-offs
            // and thus speeding up the search. If the true score is outside the window, a costly re-search is required.
            if (td.depth > config.aspMinDepth()) {

                // Adjust the aspiration window in case the score fell outside the current window
                if (score <= alpha) {
                    // If score <= alpha, re-search with an expanded aspiration window
                    beta = (alpha + beta) / 2;
                    alpha -= window;
                    window = window * config.aspWideningFactor() / 100;
                    reduction = 0;
                    continue;
                }
                if (score >= beta) {
                    // If score >= beta, re-search with an expanded aspiration window
                    beta += window;
                    window = window * config.aspWideningFactor() / 100;
                    reduction = Math.min(maxReduction, reduction + 1);
                    continue;
                }

                // Center the aspiration window around the score from the current iteration, to be used next time.
                window = config.aspDelta();
                alpha = score - window;
                beta = score + window;
            }

            // Increment depth and reset retry counter for next iteration
            td.depth++;

        }

        // Clear move ordering cache and return the search result
        history.getKillerTable().clear();

        if (bestMoveRoot == null) {
            // If time expired before a best move was found in search, pick the first legal move.
            bestMoveRoot = rootMoves.get(0);
        }

        return SearchResult.of(bestMoveRoot, bestScoreRoot, td, tc);

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth.
     *
     * @param depth               The number of ply deeper left to go in the current search ('ply remaining').
     * @param ply                 The number of ply already examined in the current search ('ply from root').
     * @param alpha               The lower bound for search scores ('we can do at least this well').
     * @param beta                The upper bound for search scores ('our opponent can do at most this well').
     * @param cutNode             Whether this node is an expected cut-node (i.e. a fail-high node).
     */
    public int search(int depth, int ply, int alpha, int beta, boolean cutNode) {

        // If timeout is reached, exit immediately
        if (hardLimitReached()) return alpha;

        // A PV (principal variation) node is one that falls within the alpha-beta window.
        final boolean pvNode = beta - alpha > 1;

        // The root node is the first node in the search tree, and is thus also always a PV node.
        final boolean rootNode = ply == 0;

        // Determine if we are currently in check.
        final boolean inCheck = movegen.isCheck(board);

        // If depth is reached, drop into quiescence search
        if (depth <= 0 && !inCheck) return quiescenceSearch(alpha, beta, ply);
        if (depth < 0) depth = 0;

        // If the game is drawn by repetition, insufficient material or fifty move rule, return zero
        if (ply > 0 && isDraw()) return Score.DRAW;

        // Update the selective search depth
        if (ply + 1 > td.seldepth) td.seldepth = ply + 1;

        // If the maximum depth is reached, return the static evaluation of the position
        if (ply >= MAX_DEPTH) return inCheck ? 0 : eval.evaluate();

        // Mate Distance Pruning
        // Exit early if we have already found a forced mate at an earlier ply
        alpha = Math.max(alpha, -Score.MATE + ply);
        beta = Math.min(beta, Score.MATE - ply);
        if (alpha >= beta) return alpha;

        final SearchStackEntry curr = ss.get(ply);
        final SearchStackEntry prev = ss.get(ply - 1);
        final Move excludedMove = curr.excludedMove;
        final boolean singularSearch = excludedMove != null;

        history.getKillerTable().clear(ply + 1);

        // Transposition table
        // Check if this node has already been searched before. If so, we can potentially re-use the result of the
        // previous search. In any case we can re-use information from the previous search in the current search.
        HashEntry ttEntry = null;
        boolean ttHit = false;
        boolean ttPrune = false;
        Move ttMove = null;
        boolean ttPv = pvNode;

        if (!singularSearch) {
            ttEntry = tt.get(board.key(), ply);
            ttHit = ttEntry != null;
            ttMove = ttHit ? ttEntry.move() : null;
            ttPv = ttPv || (ttHit && ttEntry.pv());

            if (!rootNode
                    && ttHit
                    && isSufficientDepth(ttEntry, depth + 2 * (pvNode ? 1 : 0))
                    && (ttEntry.score() <= alpha || cutNode)) {

                if (isWithinBounds(ttEntry, alpha, beta))
                    ttPrune = true;
                else if (depth <= config.ttExtensionDepth())
                    depth++;
            }
        }

        if (ttPrune) {
            // In non-PV nodes with an eligible TT hit, we fully prune the node.
            // In PV nodes, rather than pruning we reduce search depth.
            if (pvNode)
                depth--;
            else
                return ttEntry.score();
        }

        // Internal Iterative Deepening
        // If the position has not been searched yet, the search will be potentially expensive. So let's search with a
        // reduced depth expecting to record a move that we can use later for a full-depth search.
        if (!rootNode
                && (pvNode || cutNode)
                && (!ttHit || ttMove == null || ttEntry.depth() < depth - config.iirDepth())
                && depth >= config.iirDepth()) {
            --depth;
        }

        if (depth <= 0 && !inCheck)
            return quiescenceSearch(alpha, beta, ply);

        // Static Evaluation
        // Obtain a static evaluation of the current board state. In leaf nodes, this is the final score used in search.
        // In non-leaf nodes, this is used as a guide for several heuristics, such as extensions, reductions and pruning.
        int rawStaticEval = Integer.MIN_VALUE;
        int uncorrectedStaticEval = Integer.MIN_VALUE;
        int staticEval = Integer.MIN_VALUE;

        if (singularSearch) {
            // In singular search, since we are in the same node, we can re-use the static eval on the stack.
            staticEval = curr.staticEval;
        }
        else if (!inCheck) {
            // Re-use cached static eval if available. Don't compute static eval while in check.
            rawStaticEval = ttHit ? ttEntry.staticEval() : eval.evaluate();
            staticEval = ttMove != null ? rawStaticEval : history.correctEvaluation(board, ss, ply, rawStaticEval);
            uncorrectedStaticEval = rawStaticEval;

            // If there is no entry in the TT yet, store the static eval for future re-use.
            if (!ttHit)
                tt.put(board.key(), HashFlag.NONE, 0, 0, null, rawStaticEval, 0, ttPv);

            // If the TT score is within the bounds of the current window, we can use it as a more accurate static eval.
            if (canUseTTScore(ttEntry, rawStaticEval)) {
                staticEval = ttEntry.score();
                uncorrectedStaticEval = staticEval;
            }
        }
        curr.staticEval = staticEval;

        // We are 'improving' if the static eval of the current position is greater than it was on our previous turn.
        // If our position is improving we can be more aggressive in our beta pruning - where the eval is too high - but
        // should be more cautious in our alpha pruning - where the eval is too low.
        final boolean improving = isImproving(ply, staticEval);

        // Pre-move-loop pruning: If the static eval indicates a fail-high or fail-low, there are several heuristics we
        // can employ to prune the node and its entire subtree, without searching any moves.
        if (!pvNode && !inCheck && !singularSearch) {

            // Reverse Futility Pruning
            // Skip nodes where the static eval is far above beta and will thus likely result in a fail-high.
            final int futilityMargin = Math.max(depth - (improving ? 1 : 0), 0) * config.rfpMargin();
            if (depth <= config.rfpDepth()
                    && !Score.isMate(alpha)
                    && staticEval - futilityMargin >= beta) {
                return beta + (staticEval - beta) / 3;
            }

            // Razoring
            // Skip nodes where a quiescence search confirms that the position is bad and will likely result in a fail-low.
            if (depth <= config.razorDepth()
                && staticEval + config.razorMargin() * depth < alpha) {
                final int score = quiescenceSearch(alpha, alpha + 1, ply);
                if (score < alpha) {
                    return score;
                }
            }

            // Null Move Pruning
            // Skip nodes where giving the opponent an extra move (making a 'null move') still results in a fail-high.
            if (curr.nullMoveAllowed
                && ply >= td.nmpPly
                && depth >= config.nmpDepth()
                && staticEval >= beta
                && (!ttHit || cutNode || ttEntry.score() >= beta)
                && board.hasPiecesRemaining(board.isWhite())) {

                int r = config.nmpBase()
                        + depth / config.nmpDivisor()
                        + Math.min((staticEval - beta) / config.nmpEvalScale(), config.nmpEvalMaxReduction());

                ss.get(ply + 1).nullMoveAllowed = false;
                board.makeNullMove();
                td.nodes++;

                final int score = -search(depth - r, ply + 1, -beta, -beta + 1, !cutNode);

                board.unmakeNullMove();
                ss.get(ply + 1).nullMoveAllowed = true;

                if (score >= beta) {

                    // At low depths, we can directly return the result of the null move search.
                    if (td.nmpPly > 0 || depth <= 14)
                        return Score.isMate(score) ? beta : score;

                    // At high depths, let's do a normal search to verify the null move result.
                    td.nmpPly = (3 * (depth - r) / 4) + ply;
                    int verifScore = search(depth - r, ply, beta - 1, beta, true);
                    td.nmpPly = 0;

                    if (verifScore >= beta)
                        return score;

                }
            }

        }

        // We have decided that the current node should not be pruned and is worth examining further.
        // Now we begin iterating through the legal moves in the position and searching deeper in the tree.

        Move bestMove = null;
        int bestScore = Score.MIN;
        int flag = HashFlag.UPPER;

        int searchedMoves = 0, quietMoves = 0, captureMoves = 0;
        curr.quiets = new Move[16];
        curr.captures = new Move[16];

        final MovePicker movePicker = new MovePicker(config, movegen, ss, history, board, ply, ttMove, inCheck);

        while (true) {

            final ScoredMove scoredMove = movePicker.next();
            if (scoredMove == null)
                break;

            final Move move = scoredMove.move();
            if (move.equals(excludedMove))
                continue;
            searchedMoves++;

            final Piece piece = scoredMove.piece();
            final Piece captured = scoredMove.captured();
            final int historyScore = scoredMove.historyScore();
            final boolean isGoodNoisy = scoredMove.isGoodNoisy();
            final boolean isQuiet = scoredMove.isQuiet();
            final boolean isCapture = captured != null;
            final boolean isMateScore = Score.isMate(bestScore);

            int extension = 0;
            int reduction = 0;

            // Check Extensions
            // If we are in check then the position is likely noisy/tactical, so we extend the search depth.
            if (inCheck) {
                extension = 1;
            }

            // Late Move Reductions
            // Moves ordered late in the list are less likely to be good, so we reduce the search depth.
            final int lmrMinMoves = (pvNode ? config.lmrMinPvMoves() : config.lmrMinMoves()) + (rootNode ? 1 : 0);
            if (depth >= config.lmrDepth() && searchedMoves >= lmrMinMoves && !scoredMove.isGoodNoisy()) {

                int r = config.lmrReductions()[isCapture ? 1 : 0][depth][searchedMoves] * 1024;
                r -= ttPv ? config.lmrPvNode() : 0;
                r += cutNode ? config.lmrCutNode() : 0;
                r += !improving ? config.lmrNotImproving() : 0;
                r -= isQuiet
                        ? historyScore / config.lmrQuietHistoryDiv() * 1024
                        : historyScore / config.lmrNoisyHistoryDiv() * 1024;

                int futilityMargin = config.fpMargin()
                        + (depth) * config.fpScale()
                        + (historyScore / config.fpHistDivisor());
                r += staticEval + futilityMargin <= alpha ? config.lmrFutile() : 0;

                reduction = Math.max(0, r / 1024);
            }

            int reducedDepth = depth - reduction;

            // Move-loop pruning: We can save time by skipping individual moves that are unlikely to be good.

            // Futility Pruning
            // Skip quiet moves when the static evaluation + some margin is still below alpha.
            final int futilityMargin = futilityMargin(reducedDepth, historyScore, searchedMoves);
            if (!pvNode
                    && !rootNode
                    && isQuiet
                    && !inCheck
                    && reducedDepth <= config.fpDepth()
                    && staticEval + futilityMargin <= alpha) {
                movePicker.setSkipQuiets(true);
                continue;
            }

            // History pruning
            // Skip quiet moves that have a bad history score.
            final int historyThreshold = config.hpMargin() * depth + config.hpOffset();
            if (!rootNode
                    && isQuiet
                    && reducedDepth <= config.hpMaxDepth()
                    && historyScore < historyThreshold) {
                movePicker.setSkipQuiets(true);
                continue;
            }

            // Late Move Pruning
            // Skip quiet moves ordered very late in the list.
            if (!pvNode
                    && !rootNode
                    && isQuiet
                    && !inCheck
                    && depth <= config.lmpDepth()
                    && searchedMoves >= config.lmpThresholds()[depth][improving ? 1 : 0]) {
                movePicker.setSkipQuiets(true);
                continue;
            }

            // PVS SEE Pruning
            // Skip moves that lose material once all the pieces have been exchanged.
            final int seeThreshold = seeThreshold(depth, historyScore, isQuiet);
            if (!pvNode
                    && !rootNode
                    && depth <= config.seeMaxDepth()
                    && searchedMoves > 1
                    && !isGoodNoisy
                    && !isMateScore
                    && !SEE.see(board, move, seeThreshold)) {
                continue;
            }

            // Singular Extensions
            // Do a reduced-depth search with the TT move excluded. If the result of that search plus some margin
            // doesn't beat the TT score, we assume the TT move is 'singular' (i.e. the only good move), and extend
            // the search depth.
            if (!rootNode
                    && !singularSearch
                    && move.equals(ttMove)
                    && depth >= config.seDepth()
                    && ttEntry.flag() != HashFlag.UPPER
                    && ttEntry.depth() >= depth - config.seTtDepthMargin()) {

                int sBeta = Math.max(-Score.MATE + 1, ttEntry.score() - depth * config.seBetaMargin() / 16);
                int sDepth = (depth - config.seReductionOffset()) / config.seReductionDivisor();

                curr.excludedMove = move;
                int score = search(sDepth, ply, sBeta - 1, sBeta, cutNode);
                curr.excludedMove = null;

                if (score < sBeta) {
                    if (!pvNode && score < sBeta - config.seDoubleExtMargin())
                        extension = 2;
                    else
                        extension = 1;
                }
                else if (cutNode)
                    extension = -2;
                else if (ttEntry.score() >= beta)
                    extension = -1;

            }

            // We have decided that the current move should not be pruned and is worth searching further.
            // Therefore, let's make the move on the board and search the resulting position.
            makeMove(move, piece, captured, curr);

            if (isCapture && captureMoves < 16) {
                curr.captures[captureMoves++] = move;
            }
            else if (quietMoves < 16) {
                curr.quiets[quietMoves++] = move;
            }

            final int nodesBefore = td.nodes;
            td.nodes++;

            int score;

            // Principal Variation Search
            if (searchedMoves == 1) {
                // Since we expect the first move to be the best, we search it with a full window.
                score = -search(depth - 1 + extension, ply + 1, -beta, -alpha, !pvNode && !cutNode);
            }
            else {
                // For all other moves, search with a null window.
                score = -search(depth - 1 - reduction + extension, ply + 1, -alpha - 1, -alpha, !cutNode);

                if (score > alpha && (score < beta || reduction > 0)) {
                    // If the score beats alpha, we need to do a re-search with the full window and depth.
                    score = -search(depth - 1 + extension, ply + 1, -beta, -alpha, false);
                }
            }

            unmakeMove(curr);

            if (rootNode) {
                td.addNodes(move, td.nodes - nodesBefore);
            }

            if (hardLimitReached()) {
                return alpha;
            }

            if (score > bestScore) {
                bestScore = score;
            }

            if (score > alpha) {
                // If the score is greater than alpha, then this is the best move we have examined so far.
                bestMove = move;
                alpha = score;
                flag = HashFlag.EXACT;

                curr.bestMove = move;
                if (rootNode) {
                    bestMoveCurrent = move;
                    bestScoreCurrent = score;
                }

                if (score >= beta) {
                    // If the score is greater than beta, then this position is 'too good' - our opponent won't let us
                    // get here assuming perfect play, and so there's no point searching further.
                    flag = HashFlag.LOWER;
                    break;
                }
            }
        }

        if (searchedMoves == 0) {
            if (singularSearch)
                return alpha;
            // If there are no legal moves, and it's check, then it's checkmate. Otherwise, it's stalemate.
            return inCheck ? -Score.MATE + ply : Score.DRAW;
        }

        if (bestScore >= beta) {
            // Update the search history with the information from the current search, to improve future move ordering.
            final int historyDepth = depth + (staticEval <= alpha ? 1 : 0) + (bestScore > beta + 50 ? 1 : 0);
            history.updateHistory(board, bestMove, curr.quiets, curr.captures, board.isWhite(), historyDepth, ply, ss);
        }

        if (flag == HashFlag.UPPER
                && ply > 0
                && prev.move != null
                && prev.captured == null
                && !prev.move.isPromotion()) {
            // The current node failed low, which means that the parent node will fail high. If the parent move is quiet
            // it will receive a quiet history bonus in the parent node - but we give it one here too, which ensures the
            // best move is updated also during PVS re-searches, hopefully leading to better move ordering.
            history.getQuietHistoryTable().update(prev.move, prev.piece, depth, !board.isWhite(), true);
        }

        if (!inCheck
            && !singularSearch
            && Score.isDefined(bestScore)
            && (bestMove == null || board.isQuiet(bestMove))
            && !(flag == HashFlag.LOWER && uncorrectedStaticEval >= bestScore)
            && !(flag == HashFlag.UPPER && uncorrectedStaticEval <= bestScore)) {
            // Update the correction history table with the current search score, to improve future static evaluations.
            history.updateCorrectionHistory(board, ss, ply, depth, bestScore, staticEval);
        }

        // Store the best move and score in the transposition table for future reference.
        if (!hardLimitReached() && !singularSearch && !ttPrune) {
            tt.put(board.key(), flag, depth, ply, bestMove, rawStaticEval, bestScore, ttPv);
        }

        return bestScore;

    }

    /**
     * Extend the search by searching captures until a 'quiet' position is reached, where there are no further captures
     * and therefore limited potential for winning tactics that drastically alter the evaluation. Used to mitigate the
     * worst of the 'horizon effect'.
     *
     * @see <a href="https://www.chessprogramming.org/Quiescence_Search">Chess Programming Wiki</a>
     */
    int quiescenceSearch(int alpha, int beta, int ply) {

        if (hardLimitReached()) {
            return alpha;
        }

        // If the game is drawn by repetition, insufficient material or fifty move rule, return zero.
        if (ply > 0 && isDraw()) return Score.DRAW;

        // If the maximum depth is reached, return the static evaluation of the position.
        if (ply >= MAX_DEPTH) return movegen.isCheck(board) ? 0 : eval.evaluate();

        // Update the selective search depth
        if (ply + 1 > td.seldepth) td.seldepth = ply + 1;

        final boolean pvNode = beta - alpha > 1;

        // Exit the quiescence search early if we already have an accurate score stored in the hash table.
        final HashEntry ttEntry = tt.get(board.key(), ply);
        final boolean ttHit = ttEntry != null;
        final Move ttMove = ttHit ? ttEntry.move() : null;
        boolean ttPv = pvNode || (ttHit && ttEntry.pv());

        if (!pvNode && ttHit && isWithinBounds(ttEntry, alpha, beta)) {
            return ttEntry.score();
        }

        final boolean inCheck = movegen.isCheck(board);

        MoveFilter filter;

        // Re-use cached static eval if available. Don't compute static eval while in check.
        int rawStaticEval = Integer.MIN_VALUE;
        int staticEval = Integer.MIN_VALUE;

        if (inCheck) {
            // If we are in check, we need to generate 'all' legal moves that evade check, not just captures. Otherwise,
            // we risk missing simple mate threats.
            filter = MoveFilter.ALL;
        } else {
            // If we are not in check, then we have the option to 'stand pat', i.e. decline to continue the capture chain,
            // if the static evaluation of the position is good enough.
            rawStaticEval = ttHit ? ttEntry.staticEval() : eval.evaluate();
            staticEval = ttMove != null ? rawStaticEval : history.correctEvaluation(board, ss, ply, rawStaticEval);

            if (!ttHit)
                tt.put(board.key(), HashFlag.NONE, 0, 0, null, rawStaticEval, 0, ttPv);

            if (canUseTTScore(ttEntry, rawStaticEval))
                staticEval = ttEntry.score();

            if (staticEval >= beta) {
                if (!ttHit || ttEntry.flag() == HashFlag.NONE)
                    tt.put(board.key(), HashFlag.LOWER, 0, ply, null, rawStaticEval, staticEval, ttPv);
                return staticEval;
            }
            if (staticEval > alpha) {
                alpha = staticEval;
            }
            filter = MoveFilter.CAPTURES_ONLY;
        }

        final QuiescentMovePicker movePicker = new QuiescentMovePicker(config, movegen, ss, history, board, ply, ttMove, inCheck);
        movePicker.setFilter(filter);

        SearchStackEntry sse = ss.get(ply);

        int movesSearched = 0;

        Move bestMove = null;
        int bestScore = alpha;
        final int futilityScore = bestScore + config.qsFpMargin();
        int flag = HashFlag.UPPER;

        while (true) {

            final ScoredMove scoredMove = movePicker.next();
            if (scoredMove == null)
                break;
            movesSearched++;

            final Move move = scoredMove.move();
            final Piece piece = scoredMove.piece();
            final Piece captured = scoredMove.captured();
            final boolean capture = captured != null;
            final boolean promotion = move.isPromotion();

            final Move prevMove = ss.get(ply - 1).move;
            final boolean recapture = prevMove != null && prevMove.to() == move.to();

            // Delta Pruning
            // Skip captures where the value of the captured piece plus a margin is still below alpha.
            if (!inCheck && capture && !promotion && !recapture && staticEval + SEE.value(captured) + config.dpMargin() < alpha)
                continue;

            // Futility Pruning
            // Skip captures that don't win material when the static eval is far below alpha.
            if (capture && !recapture && futilityScore <= alpha && !SEE.see(board, move, 1))
                continue;

            // SEE Pruning
            // Skip moves which lose material once all the pieces are swapped off.
            if (!inCheck && !recapture && !SEE.see(board, move, config.qsSeeThreshold()))
                continue;

            makeMove(move, piece, captured, sse);

            td.nodes++;
            final int score = -quiescenceSearch(-beta, -alpha, ply + 1);

            unmakeMove(sse);

            if (score > bestScore) {
                bestScore = score;
            }
            if (score > alpha) {
                flag = HashFlag.EXACT;
                bestMove = move;
                alpha = score;
                if (score >= beta) {
                    flag = HashFlag.LOWER;
                    break;
                }

            }
        }

        if (movesSearched == 0 && inCheck) {
            return -Score.MATE + ply;
        }

        if (bestScore >= beta && !Score.isMate(bestScore) && !Score.isMate(beta)) {
            bestScore = (bestScore + beta) / 2;
        }

        if (!hardLimitReached()) {
            tt.put(board.key(), flag, 0, ply, bestMove, rawStaticEval, bestScore, ttPv);
        }

        return bestScore;

    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
        this.eval.setPosition(board);
    }

    @Override
    public void setHashSize(int hashSizeMb) {
        this.tt.resize(hashSizeMb);
    }

    @Override
    public void setThreadCount(int threadCount) {
        // do nothing as this implementation is single-threaded
    }

    private void makeMove(Move move, Piece piece, Piece captured, SearchStackEntry sse) {
        eval.makeMove(board, move);
        board.makeMove(move);
        sse.move = move;
        sse.piece = piece;
        sse.captured = captured;
    }

    private void unmakeMove(SearchStackEntry sse) {
        eval.unmakeMove();
        board.unmakeMove();
        sse.move = null;
        sse.piece = null;
        sse.captured = null;
    }

    private boolean hardLimitReached() {
        // Exit if hard limit for the current search is reached.
        if (config.pondering || tc == null)
            return false;
        if (config.searchCancelled) return true;
        return tc.isHardLimitReached(td.depth, td.nodes);
    }

    private boolean softLimitReached() {
        // Exit if soft limit for the current search is reached.
        if (config.pondering || tc == null)
            return false;
        final int bestMoveStability = history.getBestMoveStability();
        final int scoreStability = history.getBestScoreStability();
        final int bestMoveNodes = td.getNodes(bestMoveCurrent);
        return tc.isSoftLimitReached(td.depth, td.nodes, bestMoveNodes, bestMoveStability, scoreStability);
    }

    private boolean isDraw() {
        return Score.isEffectiveDraw(board);
    }

    /**
     * Compute whether our position is improving relative to previous static evaluations. If we are in check, we're not
     * improving. If we were in check 2 plies ago, check 4 plies ago. If we were in check 4 plies ago, return true.
     */
    private boolean isImproving(int ply, int staticEval) {
        if (staticEval == Integer.MIN_VALUE) return false;
        if (ply < 2) return false;
        int lastEval = ss.get(ply - 2).staticEval;
        if (lastEval == Integer.MIN_VALUE) {
            if (ply < 4) return false;
            lastEval = ss.get(ply - 4).staticEval;
            if (lastEval == Integer.MIN_VALUE) {
                return true;
            }
        }
        return lastEval < staticEval;
    }

    private SearchResult handleNoLegalMoves() {
        if (td.isMainThread()) {
            UCI.write("info error no legal moves");
        }
        return SearchResult.of(null, 0, td, tc);
    }

    private SearchResult handleOneLegalMove(List<Move> rootMoves) {
        // If there is only one legal move, play it immediately
        final Move move = rootMoves.get(0);
        final int eval = this.eval.evaluate();
        SearchResult result = SearchResult.of(move, eval, td, tc);
        if (td.isMainThread())
            UCI.writeSearchInfo(result);
        return result;
    }

    public boolean isWithinBounds(HashEntry entry, int alpha, int beta) {
        return entry.flag() == HashFlag.EXACT ||
                (Score.isDefined(entry.score()) &&
                        (entry.flag() == HashFlag.UPPER && entry.score() <= alpha ||
                                entry.flag() == HashFlag.LOWER && entry.score() >= beta));
    }

    public boolean isSufficientDepth(HashEntry entry, int depth) {
        return entry.depth() >= depth;
    }

    @Override
    public TranspositionTable getTranspositionTable() {
        return tt;
    }

    @Override
    public void clearHistory() {
        tt.clear();
        eval.clearHistory();
        history.clear();
    }

    private int futilityMargin(int depth, int historyScore, int searchedMoves) {
        return config.fpMargin()
                + depth * config.fpScale()
                + (historyScore / config.fpHistDivisor())
                - searchedMoves * config.fpMoveMultiplier();
    }

    private int seeThreshold(int depth, int historyScore, boolean isQuiet) {
        int threshold = isQuiet ?
                config.seeQuietMargin() * depth :
                config.seeNoisyMargin() * depth * depth;
        threshold -= historyScore / config.seeHistoryDivisor();
        return threshold;
    }

    private boolean canUseTTScore(HashEntry ttEntry, int rawStaticEval) {
        return ttEntry != null &&
                (ttEntry.flag() == HashFlag.EXACT ||
                (ttEntry.flag() == HashFlag.LOWER && ttEntry.score() >= rawStaticEval) ||
                (ttEntry.flag() == HashFlag.UPPER && ttEntry.score() <= rawStaticEval));
    }


}
