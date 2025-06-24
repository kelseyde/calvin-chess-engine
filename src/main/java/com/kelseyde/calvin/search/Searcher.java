package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.NNUE;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;
import com.kelseyde.calvin.search.picker.*;
import com.kelseyde.calvin.tables.tt.HashEntry;
import com.kelseyde.calvin.tables.tt.HashFlag;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.uci.UCI;

import java.util.List;

import static com.kelseyde.calvin.search.Searcher.SearchLimit.HARD;
import static com.kelseyde.calvin.search.Searcher.SearchLimit.SOFT;

/**
 * Classical alpha-beta search with iterative deepening. This is the main search algorithm used by the engine.
 * </p>
 * Alpha-beta search seeks to reduce the number of nodes that need to be evaluated in the search tree. It does this by
 * pruning branches that are guaranteed to be worse than the best move found so far, or that are guaranteed to be 'too
 * good' and could only be reached by sup-optimal play by the opponent.
 * @see <a href="https://www.chessprogramming.org/Alpha-Beta">Chess Programming Wiki</a>
 */
public class Searcher implements Search {

    final EngineConfig config;
    final TranspositionTable tt;
    final MoveGenerator movegen;
    final SearchHistory history;
    final SearchStack ss;
    final ThreadData td;
    final NNUE eval;

    SearchLimits limits;
    Board board;

    public Searcher(EngineConfig config, TranspositionTable tt, ThreadData td) {
        this.config = config;
        this.tt = tt;
        this.td = td;
        this.ss = new SearchStack();
        this.history = new SearchHistory(config, ss);
        this.movegen = new MoveGenerator();
        this.eval = new NNUE();
    }

    /**
     * Iterative Deepening Search.
     * Iterative deepening is a search strategy that does a full search at a depth of 1 ply, then a full search at 2 ply,
     * then 3 ply and so on, until the time limit is exhausted. In case the timeout is reached in the middle of an iteration,
     * the search can still fall back on the best move found in the previous iteration. By prioritising searching the best
     * move found in the previous iteration -- and by using a {@link TranspositionTable} -- the iterative approach is much
     * more efficient than it might sound.
     * @see <a href="https://www.chessprogramming.org/Iterative_Deepening">Chess Programming Wiki</a>
     */
    @Override
    public SearchResult search(SearchLimits limits) {

        final List<Move> rootMoves = movegen.generateMoves(board);
        this.limits = limits;

        if (rootMoves.isEmpty())
            return handleNoLegalMoves();

        if (rootMoves.size() == 1)
            return handleOneLegalMove(rootMoves);

        ss.clear();
        td.reset();

        // Initialise alpha-beta window to the maximum size.
        int alpha = Score.MIN;
        int beta  = Score.MAX;

        int reduction = 0;
        int maxReduction = config.aspMaxReduction();
        int window = config.aspDelta();
        int windowExpansions = 0;

        while (!shouldStop(SOFT) && td.depth < Search.MAX_DEPTH) {

            td.resetIteration();

            // Perform alpha-beta search for the current depth
            int score = search(td.depth - reduction, 0, alpha, beta, false);

            // Update the best move and score if a better move is found
            td.updateBestMove();

            // Report search progress as UCI output
            if (td.isMainThread())
                UCI.writeSearchInfo(SearchResult.of(td, limits));

            // Check if search is cancelled or a checkmate is found
            if (shouldStop(HARD) || Score.isMate(score))
                break;

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
                    windowExpansions++;
                    reduction = 0;
                    continue;
                }
                if (score >= beta) {
                    // If score >= beta, re-search with an expanded aspiration window
                    beta += window;
                    window = window * config.aspWideningFactor() / 100;
                    windowExpansions++;
                    reduction = Math.min(maxReduction, reduction + 1);
                    continue;
                }

                // Center the aspiration window around the score from the current iteration, to be used next time.
                windowExpansions /= 2;
                window = config.aspDelta() + 6 * (windowExpansions >= 4 ? 1 : 0);
                alpha = score - window;
                beta = score + window;
            }

            // Increment depth and reset retry counter for next iteration
            td.depth++;
            reduction = 0;

        }

        // If time expired before a best move was found in search, pick the first legal move.
        if (td.bestMove() == null)
            td.updateBestMove(rootMoves.get(0), td.bestScore());

        return SearchResult.of(td, limits);

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
        if (shouldStop(HARD))
            return alpha;

        // A PV (principal variation) node is one that falls within the alpha-beta window.
        final boolean pvNode = beta - alpha > 1;

        // The root node is the first node in the search tree, and is thus also always a PV node.
        final boolean rootNode = ply == 0;

        // Determine if we are currently in check.
        final boolean inCheck = movegen.isCheck(board);

        // If depth is reached, drop into quiescence search
        if (depth <= 0 && !inCheck)
            return qsearch(alpha, beta, ply);

        // Ensure depth is not negative
        if (depth < 0)
            depth = 0;

        // If the game is drawn by repetition, insufficient material or fifty move rule, return zero
        if (ply > 0 && isDraw())
            return Score.DRAW;

        // Update the selective search depth
        if (ply + 1 > td.seldepth)
            td.seldepth = ply + 1;

        // If the maximum depth is reached, return the static evaluation of the position
        if (ply >= MAX_DEPTH)
            return inCheck ? 0 : eval.evaluate();

        // Mate Distance Pruning
        // Exit early if we have already found a forced mate at an earlier ply
        alpha = Math.max(alpha, -Score.MATE + ply);
        beta = Math.min(beta, Score.MATE - ply);
        if (alpha >= beta) return alpha;

        final SearchStackEntry curr = ss.get(ply);
        final SearchStackEntry prev = ss.get(ply - 1);
        curr.inCheck = inCheck;
        curr.pvDistance = pvNode ? 0 : prev.pvDistance + 1;
        final Move excludedMove = curr.excludedMove;
        final boolean singularSearch = excludedMove != null;
        final int priorReduction = rootNode || singularSearch ? 0 : prev.reduction;
        final boolean parentPvNode = curr.pvDistance == 1;

        history.killerTable().clear(ply + 1);
        ss.get(ply + 2).failHighCount = 0;

        HashEntry ttEntry = null;
        boolean ttHit = false;
        boolean ttPrune = false;
        Move ttMove = null;
        boolean ttPv = pvNode;

        // Transposition table
        // Check if this node has already been searched before. If it has, and the depth + alpha/beta bounds match the
        // requirements of the current search, then we can directly return the score from the TT. If the depth and bounds
        // do not match, we can still use information from the TT - such as the best move, score, and static eval -
        // to improve the current search.
        if (!singularSearch) {
            ttEntry = tt.get(board.key(), ply);
            ttHit = ttEntry != null;
            ttMove = ttHit ? ttEntry.move() : null;
            ttPv = ttPv || (ttHit && ttEntry.pv());

            if (!rootNode
                    && ttHit
                    && ttEntry.depth() >= depth + (pvNode ? 2 : 0)
                    && (ttEntry.score() <= alpha || cutNode)) {
                if (isWithinBounds(ttEntry, alpha, beta)) {
                    ttPrune = true;
                    if (!pvNode) {
                        // In non-PV nodes with an TT hit matching the depth and alpha/beta bounds of
                        // the current search, we can cut off the search here and return the TT score.
                        return ttEntry.score();
                    } else {
                        // In PV nodes, rather than cutting off we reduce search depth.
                        depth--;
                    }
                }
                else if (depth <= config.ttExtensionDepth())
                    depth++;
            }
        }

        // Internal Iterative Reductions
        // If the position has not been searched yet, the search will be potentially expensive. So let's search with a
        // reduced depth expecting to record a move that we can use later for a full-depth search.
        if (!rootNode
                && (pvNode || cutNode)
                && (!ttHit || ttMove == null || ttEntry.depth() < depth - config.iirDepth())
                && depth >= config.iirDepth()) {
            depth--;
        }

        if (depth <= 0 && !inCheck)
            return qsearch(alpha, beta, ply);

        // Static Evaluation
        // Obtain a static evaluation of the current board state. In leaf nodes, this is the final score used in search.
        // In non-leaf nodes, this is used as a guide for several heuristics, such as extensions, reductions and pruning.
        int rawStaticEval   = Score.MIN;
        int uncorrectedEval = Score.MIN;
        int staticEval      = Score.MIN;

        // Correction History
        // The static eval is corrected based on the historical difference between the static eval and the search score
        // of similar positions. The complexity is the sum of each correction term squared - if the correction is large,
        // then the position is likely complex, and we should be wary of reducing/pruning the search.
        int correction = 0;
        int complexity = 0;

        if (singularSearch) {
            // In singular search, since we are in the same node, we can re-use the static eval on the stack.
            staticEval = curr.staticEval;
        }
        else if (!inCheck) {
            // Re-use cached static eval if available. Don't compute static eval while in check.
            rawStaticEval = ttHit ? ttEntry.staticEval() : eval.evaluate();
            uncorrectedEval = rawStaticEval;
            correction = ttMove != null ? 0 : history.evalCorrection(board, ply);
            complexity = history.squaredCorrectionTerms(board, ply);
            staticEval = rawStaticEval + correction;

            // If there is no entry in the TT yet, store the static eval for future re-use.
            if (!ttHit)
                tt.put(board.key(), HashFlag.NONE, 0, 0, null, rawStaticEval, 0, ttPv);

            // If the TT score is within the bounds of the current window, we can use it as a more accurate static eval.
            if (canUseTTScore(ttEntry, rawStaticEval)) {
                staticEval = ttEntry.score();
                uncorrectedEval = staticEval;
            }
        }
        curr.staticEval = staticEval;

        // Dynamic policy
        // Use the difference between the static eval in the current node and parent node to update quiet history.
        if (!inCheck
                && !singularSearch
                && !rootNode
                && prev.move != null
                && prev.quiet
                && Score.isDefined(prev.staticEval)) {
            int value = config.dynamicPolicyMult() * -(staticEval + prev.staticEval);
            int bonus = clamp(value, config.dynamicPolicyMin(), config.dynamicPolicyMax());
            history.quietHistory().add(prev.move, prev.piece, !board.isWhite(), bonus);
        }

        // Hindsight extension
        // If we reduced search depth in the parent node, but now the static eval indicates the position is improving,
        // we reduce the parent node's reduction 'in hindsight' by extending search depth in the current node.
        if (!inCheck
                && !rootNode
                && priorReduction >= config.hindsightExtLimit()
                && Score.isDefined(prev.staticEval)
                && staticEval + prev.staticEval < 0) {
            depth++;
        }

        // We are 'improving' if the static eval of the current position is greater than it was on our previous turn.
        // If our position is improving we can be more aggressive in our beta pruning - where the eval is too high - but
        // should be more cautious in our alpha pruning - where the eval is too low.
        final boolean improving = !rootNode && isImproving(ply, staticEval);

        // The inverse of improving, our opponent is worsening if their previous move made the static eval worse for them.
        final boolean opponentWorsening = !rootNode && !inCheck && staticEval + prev.staticEval > 1;

        // Pre-move-loop pruning: If the static eval indicates a fail-high or fail-low, there are several heuristics we
        // can employ to prune the node and its entire subtree, without searching any moves.
        if (!pvNode && !inCheck && !singularSearch) {

            // Reverse Futility Pruning
            // Skip nodes where the static eval is far above beta and will thus likely result in a fail-high.
            final int futilityMargin = depth * config.rfpMargin()
                    - (improving ? config.rfpImprovingMargin() : 0)
                    - (opponentWorsening ? config.rfpWorseningMargin() : 0)
                    + (parentPvNode ? config.rfpParentPvMargin() : 0);
            if (depth <= config.rfpDepth()
                    && !Score.isMate(alpha)
                    && staticEval - futilityMargin >= beta) {
                return beta + (staticEval - beta) / 3;
            }

            // Razoring
            // Skip nodes where a quiescence search confirms that the position is bad and will likely result in a fail-low.
            if (depth <= config.razorDepth()
                && staticEval + config.razorMargin() * depth < alpha) {
                final int score = qsearch(alpha, alpha + 1, ply);
                if (score < alpha)
                    return score;
            }

            // Null Move Pruning
            // Skip nodes where giving the opponent an extra move (making a 'null move') still results in a fail-high.
            if (depth >= config.nmpDepth()
                && ply >= td.nmpPly
                && prev.move != null
                && staticEval >= beta
                && (!ttHit || cutNode || ttEntry.score() >= beta)
                && board.hasNonPawnMaterial()) {

                int r = config.nmpBase()
                        + depth / config.nmpDivisor()
                        + Math.min((staticEval - beta) / config.nmpEvalScale(), config.nmpEvalMaxReduction());

                board.makeNullMove();
                final int score = -search(depth - r, ply + 1, -beta, -beta + 1, !cutNode);
                board.unmakeNullMove();
                td.nodes++;

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
        curr.quiets = new Move[16];
        curr.captures = new Move[16];
        int moveCount = 0, quietMoves = 0, captureMoves = 0;

        MovePicker movePicker = inCheck
                ? new EvasionMovePicker(config, movegen, history, ss, board, ttMove, ply)
                : new StandardMovePicker(config, movegen, history, ss, board, ttMove, ply);

        while (true) {

            final ScoredMove scoredMove = movePicker.next();
            if (scoredMove == null)
                break;

            final Move move = scoredMove.move();
            if (move.equals(excludedMove))
                continue;
            moveCount++;

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
            if (inCheck)
                extension = 1;

            // Late Move Reductions
            // Moves ordered late in the list are less likely to be good, so we reduce the search depth.
            final int lmrMinMoves = (pvNode ? config.lmrMinPvMoves() : config.lmrMinMoves()) + (rootNode ? 1 : 0);
            final boolean doLmr = depth >= config.lmrDepth() && moveCount >= lmrMinMoves && (!isGoodNoisy || !ttPv);
            if (doLmr) {
                int r = config.lmrReductions()[isCapture ? 1 : 0][depth][moveCount] * 1024;
                r -= ttPv ? config.lmrPvNode() : 0;
                r += cutNode ? config.lmrCutNode() : 0;
                r += !improving ? config.lmrNotImproving() : 0;
                r += Math.min(curr.pvDistance * config.lmrPvDistanceMult(), config.lmrPvDistanceMax());
                r -= historyScore / (isQuiet ? config.lmrQuietHistoryDiv() : config.lmrNoisyHistoryDiv()) * 1024;
                r += staticEval + lmrFutilityMargin(depth, historyScore) <= alpha ? config.lmrFutile() : 0;
                r += !rootNode && prev.failHighCount > 2 ? config.lmrFailHighCount() : 0;
                r -= complexity / config.lmrComplexityDivisor();
                reduction = Math.max(0, r / 1024);
            }

            int reducedDepth = depth - reduction;

            // Move-loop pruning: We can save time by skipping individual moves that are unlikely to be good.

            // Futility Pruning
            // Skip quiet moves when the static evaluation + some margin is still below alpha.
            final int futilityMargin = futilityMargin(reducedDepth, historyScore, moveCount);
            if (!pvNode
                    && !rootNode
                    && isQuiet
                    && !inCheck
                    && reducedDepth <= config.fpDepth()
                    && staticEval + futilityMargin <= alpha) {
                movePicker.skipQuiets(true);
                continue;
            }

            // History pruning
            // Skip quiet moves that have a bad history score.
            final int historyThreshold = config.hpMargin() * depth + config.hpOffset();
            if (!rootNode
                    && isQuiet
                    && reducedDepth <= config.hpMaxDepth()
                    && historyScore < historyThreshold) {
                movePicker.skipQuiets(true);
                continue;
            }

            // Late Move Pruning
            // Skip quiet moves ordered very late in the list.
            final int lateMoveThreshold = lateMoveThreshold(depth, improving || staticEval >= beta);
            if (!pvNode
                    && !rootNode
                    && isQuiet
                    && !inCheck
                    && depth <= config.lmpDepth()
                    && moveCount >= lateMoveThreshold) {
                movePicker.skipQuiets(true);
                continue;
            }

            // Bad Noisy Pruning
            // Skip bad noisies when the static evaluation + some margin is still below alpha.
            int margin = staticEval + config.bnpScale() * depth + config.bnpOffset() * moveCount / config.bnpDivisor();
            if (!inCheck
                    && depth < config.bnpDepth()
                    && scoredMove.isBadNoisy()
                    && margin <= alpha) {
                if (!Score.isMate(bestScore) && bestScore <= margin) {
                    bestScore = margin;
                }
                break;
            }

            // PVS SEE Pruning
            // Skip moves that lose material once all the pieces have been exchanged.
            final int seeThreshold = seeThreshold(depth, historyScore, isQuiet);
            if (!pvNode
                    && !rootNode
                    && depth <= config.seeMaxDepth()
                    && moveCount > 1
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
            makeMove(scoredMove, piece, captured, curr);

            final int nodesBefore = td.nodes;
            td.nodes++;

            int newDepth = depth + extension - 1;
            int score = Score.MIN;

            // Principal Variation Search
            // We assume that the first move will be best move, and search all others with a null window and/or reduced depth.
            // If any of those moves beat alpha, we re-search with a full window and depth.
            if (doLmr) {
                // For moves eligible for late move reductions, we apply the reduction and search with a null window.
                curr.reduction = reduction;
                score = -search(newDepth - reduction, ply + 1, -alpha - 1, -alpha, true);
                curr.reduction = 0;

                // If searched at reduced depth and the score beat alpha, re-search at full depth, with a null window.
                if (score > alpha && reduction > 0) {
                    // Adjust the depth of the re-search based on the results of the reduced search.
                    boolean doDeeperSearch = score > bestScore + config.lmrDeeperBase() + config.lmrDeeperScale() * newDepth;
                    boolean doShallowerSearch = score < bestScore + newDepth;
                    newDepth += (doDeeperSearch ? 1 : 0) - (doShallowerSearch ? 1 : 0);

                    score = -search(newDepth, ply + 1, -alpha - 1, -alpha, !cutNode);
                    if (isQuiet && (score <= alpha || score >= beta))
                        history.updateContHistory(move, piece, board.isWhite(), depth, ply, score >= beta);
                }
            }
            // If we're skipping late move reductions - either due to being in a PV node, or searching the first move,
            // or another LMR condition not being met - then we search at full depth with a null-window.
            else if (!pvNode || moveCount > 1)
                score = -search(newDepth, ply + 1, -alpha - 1, -alpha, !cutNode);

            // If we're in a PV node and searching the first move, or the score from reduced search beat alpha, then we
            // search with full depth and alpha-beta window.
            if (pvNode && (moveCount == 1 || score > alpha))
                score = -search(newDepth, ply + 1, -beta, -alpha, false);

            unmakeMove(curr);

            if (rootNode) {
                td.addNodes(move, td.nodes - nodesBefore);
            }

            if (shouldStop(HARD)) {
                return alpha;
            }

            if (score > bestScore) {
                bestScore = score;
            }

            // If the score is greater than alpha, then this is the best move we have examined so far.
            // We therefore update alpha to the current score and update best move to the current move.
            if (score > alpha) {
                bestMove = move;
                alpha = score;
                flag = HashFlag.EXACT;

                curr.bestMove = move;
                if (rootNode)
                    td.updateBestMoveCurrent(bestMove, bestScore);

                // If the score is greater than beta, then this position is 'too good' - our opponent won't let us get
                // here assuming perfect play. The node therefore 'fails high' - there is no point searching further,
                // and we can cut off here.
                if (score >= beta) {
                    flag = HashFlag.LOWER;
                    curr.failHighCount++;
                    break;
                }

                // Alpha raise reduction
                // It is unlikely that multiple moves raise alpha, therefore, if we have already raised alpha, we can
                // reduce the search depth for the remaining moves.
                if (depth > config.alphaReductionMinDepth()
                        && depth < config.alphaReductionMaxDepth()
                        && !Score.isMate(score)) {
                    depth--;
                }
            }

            // Register the current move, to update its history score later.
            if (!move.equals(bestMove)) {
                if (isCapture && captureMoves < 16)
                    curr.captures[captureMoves++] = move;
                else if (quietMoves < 16)
                    curr.quiets[quietMoves++] = move;
            }
        }

        if (moveCount == 0) {
            if (singularSearch)
                return alpha;
            // If there are no legal moves, and it's check, then it's checkmate. Otherwise, it's stalemate.
            return inCheck ? -Score.MATE + ply : Score.DRAW;
        }

        if (bestScore >= beta) {
            // When the best move causes a beta cut-off, we update the various history tables to reward the best move and
            // punish the other searched moves. Doing so will hopefully improve move ordering in subsequent searches.
            int historyDepth = depth
                    + (staticEval <= alpha ? 1 : 0)
                    + (bestScore > beta + config.betaHistBonusMargin() ? 1 : 0);

            if (!board.isCapture(bestMove)) {
                // If the best move was quiet, record it in the killer table and give it a bonus in the quiet history table.
                history.killerTable().add(ply, bestMove);
                history.updateQuietHistories(board, bestMove, board.isWhite(), historyDepth, ply, true);

                // Penalise all the other quiets which failed to cause a beta cut-off.
                for (Move quiet : curr.quiets)
                    history.updateQuietHistories(board, quiet, board.isWhite(), historyDepth, ply, false);
            } else {
                // If the best move was a capture, give it a bonus in the capture history table.
                history.updateCaptureHistory(board, bestMove, board.isWhite(), historyDepth, true);
            }

            // Regardless of whether the best move was quiet or a capture, penalise all other captures.
            for (Move capture : curr.captures)
                history.updateCaptureHistory(board, capture, board.isWhite(), historyDepth, false);
        }

        if (flag == HashFlag.UPPER
                && ply > 0
                && prev.move != null
                && prev.captured == null
                && !prev.move.isPromotion()) {
            // The current node failed low, which means that the parent node will fail high. If the parent move is quiet
            // it will receive a quiet history bonus in the parent node - but we give it one here too, which ensures the
            // best move is updated also during PVS re-searches, hopefully leading to better move ordering.
            history.updateQuietHistory(prev.move, prev.piece, !board.isWhite(), depth, true);
        }

        if (!inCheck
            && !singularSearch
            && Score.isDefined(bestScore)
            && (bestMove == null || board.isQuiet(bestMove))
            && !(flag == HashFlag.LOWER && uncorrectedEval >= bestScore)
            && !(flag == HashFlag.UPPER && uncorrectedEval <= bestScore)) {
            // Update the correction history table with the current search score, to improve future static evaluations.
            history.updateCorrectionHistory(board, ply, depth, bestScore, staticEval);
        }

        // Store the best move and score in the transposition table for future reference.
        if (!shouldStop(HARD) && !singularSearch && !ttPrune)
            tt.put(board.key(), flag, depth, ply, bestMove, rawStaticEval, bestScore, ttPv);

        return bestScore;

    }

    /**
     * Quiescence Search.
     * Extend the search by searching captures until a 'quiet' position is reached, where there are no further captures
     * and therefore limited potential for winning tactics that drastically alter the evaluation. Used to mitigate the
     * worst of the 'horizon effect'.
     *
     * @see <a href="https://www.chessprogramming.org/Quiescence_Search">Chess Programming Wiki</a>
     */
    int qsearch(int alpha, int beta, int ply) {

        if (shouldStop(HARD))
            return alpha;

        // If the game is drawn by repetition, insufficient material or fifty move rule, return zero.
        if (ply > 0 && isDraw())
            return Score.DRAW;

        // If the maximum depth is reached, return the static evaluation of the position.
        if (ply >= MAX_DEPTH)
            return movegen.isCheck(board) ? 0 : eval.evaluate();

        // Update the selective search depth
        if (ply + 1 > td.seldepth)
            td.seldepth = ply + 1;

        final boolean pvNode = beta - alpha > 1;

        // Exit the quiescence search early if we already have an accurate score stored in the hash table.
        final HashEntry ttEntry = tt.get(board.key(), ply);
        final boolean ttHit = ttEntry != null;
        final Move ttMove = ttHit ? ttEntry.move() : null;
        boolean ttPv = pvNode || (ttHit && ttEntry.pv());

        if (!pvNode && ttHit && isWithinBounds(ttEntry, alpha, beta))
            return ttEntry.score();

        final boolean inCheck = movegen.isCheck(board);

        SearchStackEntry curr = ss.get(ply);
        SearchStackEntry prev = ss.get(ply - 1);
        curr.inCheck = inCheck;
        curr.pvDistance = pvNode ? 0 : prev.pvDistance + 1;

        // Re-use cached static eval if available. Don't compute static eval while in check.
        int rawStaticEval = Score.MIN;
        int staticEval    = Score.MIN;
        int correction;

        if (!inCheck) {
            // If we are not in check, then we have the option to 'stand pat', i.e. decline to continue the capture chain,
            // if the static evaluation of the position is good enough.
            rawStaticEval = ttHit ? ttEntry.staticEval() : eval.evaluate();
            correction = ttMove != null ? 0 : history.evalCorrection(board, ply);
            staticEval = rawStaticEval + correction;

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
        }

        Move bestMove = null;
        int bestScore = staticEval;
        final int futilityScore = bestScore + config.qsFpMargin();
        int flag = HashFlag.UPPER;
        int moveCount = 0;

        MovePicker movePicker = new QuiescentMovePicker(config, movegen, history, ss, board, ttMove, ply, inCheck);

        while (true) {

            final ScoredMove scoredMove = movePicker.next();
            if (scoredMove == null)
                break;
            moveCount++;

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
            if (!inCheck && capture && !recapture && futilityScore <= alpha && !SEE.see(board, move, 1)) {
                bestScore = Math.max(bestScore, futilityScore);
                continue;
            }

            // SEE Pruning
            // Skip moves which lose material once all the pieces are swapped off.
            if (!inCheck && !recapture && !SEE.see(board, move, config.qsSeeThreshold()))
                continue;

            // Evasion Pruning
            // In check, stop searching quiet moves after finding at least one non-losing move.
            if (inCheck && moveCount > 1 && scoredMove.isQuiet() && !Score.isMate(bestScore))
                break;

            makeMove(scoredMove, piece, captured, curr);

            td.nodes++;
            final int score = -qsearch(-beta, -alpha, ply + 1);

            unmakeMove(curr);

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

        if (moveCount == 0 && inCheck)
            return -Score.MATE + ply;

        if (bestScore >= beta && !Score.isMate(bestScore) && !Score.isMate(beta))
            bestScore = (bestScore + beta) / 2;

        if (!shouldStop(HARD))
            tt.put(board.key(), flag, 0, ply, bestMove, rawStaticEval, bestScore, ttPv);

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

    private void makeMove(ScoredMove move, Piece piece, Piece captured, SearchStackEntry sse) {
        eval.makeMove(board, move.move());
        board.makeMove(move.move());
        sse.move = move.move();
        sse.quiet = move.isQuiet();
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

    private boolean shouldStop(SearchLimit limit) {
        return switch (limit) {
            case SOFT -> softLimitReached();
            case HARD -> hardLimitReached();
        };
    }

    private boolean hardLimitReached() {
        // Exit if hard limit for the current search is reached.
        if (td.abort || config.searchCancelled)
            return true;
        if (config.pondering || limits == null)
            return false;
        return td.isMainThread() && limits.isHardLimitReached(td.depth, td.nodes);
    }

    private boolean softLimitReached() {
        // Exit if soft limit for the current search is reached.
        if (config.pondering || limits == null)
            return false;
        final int bestMoveStability = td.bestMoveStability();
        final int scoreStability = td.bestScoreStability();
        final int bestMoveNodes = td.nodes(td.bestMove());
        return limits.isSoftLimitReached(td.depth, td.nodes, bestMoveNodes, bestMoveStability, scoreStability);
    }

    private boolean isDraw() {
        return Score.isEffectiveDraw(board);
    }

    /**
     * Compute whether our position is improving relative to previous static evaluations. If we are in check, we're not
     * improving. If we were in check 2 plies ago, check 4 plies ago. If we were in check 4 plies ago, return true.
     */
    private boolean isImproving(int ply, int staticEval) {
        if (!Score.isDefined(staticEval))
            return false;
        if (ply > 1 && Score.isDefined(ss.get(ply - 2).staticEval))
            return staticEval > ss.get(ply - 2).staticEval;
        if (ply > 3 && Score.isDefined(ss.get(ply - 4).staticEval))
            return staticEval > ss.get(ply - 4).staticEval;
        return true;
    }

    private SearchResult handleNoLegalMoves() {
        if (td.isMainThread())
            UCI.write("info error no legal moves");
        return SearchResult.of(td, limits);
    }

    private SearchResult handleOneLegalMove(List<Move> rootMoves) {
        // If there is only one legal move, play it immediately
        final Move move = rootMoves.get(0);
        final int eval = this.eval.evaluate();
        td.updateBestMove(move, eval);
        SearchResult result = SearchResult.of(td, limits);
        if (td.isMainThread())
            UCI.writeSearchInfo(result);
        return result;
    }

    public boolean isWithinBounds(HashEntry entry, int alpha, int beta) {
        if (!Score.isDefined(entry.score()))
            return false;
        return entry.flag() == HashFlag.EXACT
                || (entry.flag() == HashFlag.UPPER && entry.score() <= alpha)
                || (entry.flag() == HashFlag.LOWER && entry.score() >= beta);
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

    public boolean isMainThread() {
        return td.isMainThread();
    }

    public void abort() {
        td.abort = true;
    }

    private int futilityMargin(int depth, int historyScore, int searchedMoves) {
        return config.fpMargin()
                + depth * config.fpScale()
                + (historyScore / config.fpHistDivisor())
                - searchedMoves * config.fpMoveMultiplier();
    }

    private int lmrFutilityMargin(int depth, int historyScore) {
        return config.lmrFutileMargin()
                + depth * config.lmrFutileScale()
                + (historyScore / config.lmrFutileHistDivisor());
    }

    private int lateMoveThreshold(int depth, boolean optimistic) {
        final int base = optimistic ? config.lmpImpBase() : config.lmpBase();
        final int scale = optimistic ? config.lmpImpScale() : config.lmpScale();
        return (base + depth * scale) / 10;
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

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    enum SearchLimit {
        SOFT, HARD
    }


}
