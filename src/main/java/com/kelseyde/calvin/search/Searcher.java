package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.Result;
import com.kelseyde.calvin.evaluation.Score;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import com.kelseyde.calvin.search.moveordering.StaticExchangeEvaluator;
import com.kelseyde.calvin.transposition.HashEntry;
import com.kelseyde.calvin.transposition.HashFlag;
import com.kelseyde.calvin.transposition.TranspositionTable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Iterative deepening is a search strategy that does a full search at a depth of 1 ply, then a full search at 2 ply,
 * then 3 ply and so on, until the time limit is exhausted. In case the timeout is reached in the middle of an iteration,
 * the search can still fall back on the best move found in the previous iteration. By prioritising searching the best
 * move found in the previous iteration, as well as the other ordering heuristics in the {@link MoveOrderer} -- and by
 * using a {@link TranspositionTable} -- the iterative approach is much more efficient than it might sound.
 *
 * @see <a href="https://www.chessprogramming.org/Iterative_Deepening">Chess Programming Wiki</a>
 */
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Searcher implements Search {

    EngineConfig config;
    ThreadManager threadManager;
    MoveGeneration moveGenerator;
    MoveOrdering moveOrderer;
    Evaluation evaluator;
    StaticExchangeEvaluator see;
    TranspositionTable transpositionTable;

    Board board;
    int nodes;
    Instant start;
    Instant timeout;
    boolean cancelled;

    int currentDepth;
    int maxDepth = 256;

    Move bestMove;
    Move bestMoveCurrentDepth;
    int bestEval;
    int bestEvalCurrentDepth;

    public Searcher(EngineConfig config,
                    ThreadManager threadManager,
                    MoveGeneration moveGenerator,
                    MoveOrdering moveOrderer,
                    Evaluation evaluator,
                    TranspositionTable transpositionTable) {
        this.config = config;
        this.threadManager = threadManager;
        this.moveGenerator = moveGenerator;
        this.moveOrderer = moveOrderer;
        this.evaluator = evaluator;
        this.transpositionTable = transpositionTable;
        this.see = new StaticExchangeEvaluator();
    }

    /**
     * Search for the best move in the current {@link Board} position.
     * @param duration How long we should search
     * @return A {@link SearchResult} containing the best move and the current evaluation.
     */
    @Override
    public SearchResult search(Duration duration) {

        start = Instant.now();
        timeout = start.plus(duration);
        nodes = 0;
        currentDepth = 1;
        bestMove = null;
        bestMoveCurrentDepth = null;
        bestEval = 0;
        bestEvalCurrentDepth = 0;
        cancelled = false;
        moveOrderer.ageHistoryTable(board.isWhiteToMove());

        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE - 1;
        int retryMultiplier = 0;
        SearchResult result = null;

        while (!isCancelled() && currentDepth < maxDepth) {
            System.out.println("depth " + currentDepth);
            // Reset variables for the current depth iteration
            bestMoveCurrentDepth = null;
            bestEvalCurrentDepth = 0;

            // Perform alpha-beta search for the current depth
            int eval = search(currentDepth, 0, alpha, beta, true);

            // Update the best move and evaluation if a better move is found
            if (bestMoveCurrentDepth != null) {
                if (board == null) {
                    System.out.println("yowza");
                }
                bestMove = bestMoveCurrentDepth;
                bestEval = bestEvalCurrentDepth;
                result = buildResult();
                threadManager.handleSearchResult(result);
            }

            // Check if search is cancelled or a checkmate is found
            if (isCancelled() || isCheckmateFoundAtCurrentDepth(currentDepth)) {
                break;
            }

            // Adjust the aspiration window in case the score fell outside the current window
            if (eval <= alpha) {
                // If score <= alpha, re-search with an expanded aspiration window
                retryMultiplier++;
                alpha -= config.getAspFailMargin() * retryMultiplier;
                beta += config.getAspMargin();
                continue;
            }
            if (eval >= beta) {
                // If score >= beta, re-search with an expanded aspiration window
                retryMultiplier++;
                beta += config.getAspFailMargin() * retryMultiplier;
                alpha -= config.getAspMargin();
                continue;
            }

            alpha = eval - config.getAspMargin();
            beta = eval + config.getAspMargin();

            // Increment depth and retry multiplier for next iteration
            retryMultiplier = 0;
            currentDepth++;
        }

        // If no move is found within the time limit, choose the first available move
        if (result == null) {
            System.out.println("Time expired before a move was found!");
            bestMove = moveGenerator.generateMoves(board).get(0);
            result = buildResult();
        }

        // Clear move ordering cache and return the search result
        moveOrderer.clear();
        return result;

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth.
     *
     * @param depth               The number of ply deeper left to go in the current search ('ply remaining').
     * @param ply                 The number of ply already examined in the current search ('ply from root').
     * @param alpha               The lower bound for child nodes at the current search depth.
     * @param beta                The upper bound for child nodes at the current search depth.
     * @param allowNull           Whether to allow null-move pruning in this search iteration.
     */
    public int search(int depth, int ply, int alpha, int beta, boolean allowNull) {

        // If timeout is reached, exit immediately
        if (isCancelled()) return alpha;

        // If depth is reached, drop into quiescence search
        if (depth <= 0) return quiescenceSearch(alpha, beta, 1, ply);

        // If the game is drawn by repetition, insufficient material or fifty move rule, return zero
        if (ply > 0 && isDraw()) return Score.DRAW_SCORE;

        // Exit early if we have already found a forced mate at an earlier ply
        // See https://www.chessprogramming.org/Mate_Distance_Pruning
        alpha = Math.max(alpha, -Score.MATE_SCORE + ply);
        beta = Math.min(beta, Score.MATE_SCORE - ply);
        if (alpha >= beta) return alpha;

        // Probe the transposition table in case this node has been searched before
        HashEntry transposition = transpositionTable.get(getKey(), ply);
        if (isUsefulTransposition(transposition, depth, alpha, beta)) {
            if (ply == 0 && transposition.getMove() != null) {
                bestMoveCurrentDepth = transposition.getMove();
                bestEvalCurrentDepth = transposition.getScore();
            }
            return transposition.getScore();
        }
        Move previousBestMove = ply == 0 ? bestMove : null;
        if (hasBestMove(transposition)) {
            previousBestMove = transposition.getMove();
        }

        // If the position has not been searched yet, the search will be potentially expensive. So let's search with a
        // reduced depth expecting to record a move that we can use later for a full-depth search.
        // See https://www.chessprogramming.org/Internal_Iterative_Deepening
        if (transposition == null && ply > 0 && depth >= config.getIirDepth()) {
            --depth;
        }

        List<Move> moves = moveGenerator.generateMoves(board);
        boolean isInCheck = moveGenerator.isCheck(board, board.isWhiteToMove());
        int staticEval = evaluator.evaluate(board);

        if (moves.isEmpty()) {
            // If there are no legal moves, and it's check, then it's checkmate. Otherwise, it's stalemate.
            return isInCheck ? -Score.MATE_SCORE + ply : Score.DRAW_SCORE;
        }
        if (ply == 0 && moves.size() == 1) {
            // If there is only one legal move at the root node, play that move immediately.
            int eval = isDraw() ? Score.DRAW_SCORE : staticEval;
            bestMoveCurrentDepth = moves.get(0);
            bestEvalCurrentDepth = eval;
            cancelled = true;
            return eval;
        }

        boolean pvNode = beta - alpha > 1;

        if (!pvNode && !isInCheck) {
            // If the static evaluation + some significant margin is still above beta, then let's assume this position
            // is a cut-node and will fail-high, and not search any further.
            // See https://www.chessprogramming.org/Reverse_Futility_Pruning
            boolean isMateHunting = Score.isMateScore(alpha);
            if (depth <= config.getRfpDepth()
                && staticEval - config.getRfpMargin()[depth] > beta
                && !isMateHunting) {
                return staticEval - config.getRfpMargin()[depth];
            }

            // If the static evaluation + some significant margin is still above beta after giving the opponent two moves
            // in a row (making a 'null' move), then let's assume this position is a cut-node and will fail-high, and
            // not search any further.
            // See https://www.chessprogramming.org/Null_Move_Pruning
            if (allowNull
                && depth >= config.getNmpDepth()
                && staticEval >= beta - config.getNmpMargin()
                && board.hasPiecesRemaining(board.isWhiteToMove())) {
                board.makeNullMove();
                int eval = -search(depth - 1 - (2 + depth / 7), ply + 1, -beta, -beta + 1, false);
                board.unmakeNullMove();
                if (eval >= beta) {
                    transpositionTable.put(getKey(), HashFlag.LOWER, depth, ply, previousBestMove, beta);
                    return eval;
                }
            }
        }

        // Give each move a 'score' based on how interesting or promising it looks. These scores will be used to select
        // the order in which moves are evaluated.
        int[] scores = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            scores[i] = moveOrderer.scoreMove(board, moves.get(i), previousBestMove, ply);
        }

        Move bestMove = null;
        HashFlag flag = HashFlag.UPPER;

        for (int i = 0; i < moves.size(); i++) {

            // Incremental move ordering: it's unnecessary to sort the entire move list, since most of them are never evaluated.
            // So just select the move with the best score that hasn't been tried yet
            for (int j = i + 1; j < moves.size(); j++) {
                int firstScore = scores[i];
                int secondScore = scores[j];
                Move firstMove = moves.get(i);
                Move secondMove = moves.get(j);
                if (scores[j] > scores[i]) {
                    scores[i] = secondScore;
                    scores[j] = firstScore;
                    moves.set(i, secondMove);
                    moves.set(j, firstMove);
                }
            }

            Move move = moves.get(i);
            boolean isCapture = board.pieceAt(move.getEndSquare()) != null;
            boolean isPromotion = move.getPromotionPiece() != null;

            board.makeMove(move);
            nodes++;

            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());
            boolean isQuiet = !isCheck && !isCapture && !isPromotion;

            // Futility pruning: if the static evaluation + some margin is still < alpha, and the current move is not
            // interesting (checks, captures, promotions), then let's assume it will fail low and prune this node.
            // See https://www.chessprogramming.org/Futility_Pruning
            if (!pvNode && depth <= config.getFpDepth() && staticEval + config.getFpMargin()[depth] < alpha && !isInCheck && isQuiet) {
                board.unmakeMove();
                continue;
            }

            // In certain interesting cases (e.g. promotions, or checks that do not immediately lose material), let's
            // extend the search depth by one ply.
            // See https://www.chessprogramming.org/Extensions
            int extension = 0;
            if (isPromotion || (isCheck && see.evaluateAfterMove(board, move) >= 0)) {
                extension = 1;
            }

            int eval;
            if (isDraw()) {
                eval = Score.DRAW_SCORE;
            }
            else if (pvNode && i == 0) {
                // The first move must be searched with the full alpha-beta window. If our move ordering is any good
                // then we expect this to be the best move, and so we need to retrieve the exact score.
                // See https://www.chessprogramming.org/Principal_Variation_Search
                eval = -search(depth - 1 + extension, ply + 1, -beta, -alpha, true);
            }
            else {
                // If the move is ordered very late in the list, and isn't a 'noisy' move like a check, capture or
                // promotion, let's assume it's less likely to be good, and fully skip searching that move.
                // See https://www.chessprogramming.org/Futility_Pruning#Move_Count_Based_Pruning
                if (!pvNode
                    && !isInCheck
                    && isQuiet
                    && depth <= config.getLmpDepth()
                    && i >= depth * config.getLmpMultiplier()) {
                    board.unmakeMove();
                    continue;
                }
                // If the move is ordered late in the list, and isn't a 'noisy' move like a check, capture or promotion,
                // let's save time by assuming it's less likely to be good, and reduce the search depth.
                // See https://www.chessprogramming.org/Late_Move_Reductions
                int reduction = 0;
                if (depth >= config.getLmrDepth()
                    && i >= (pvNode ? config.getLmrMinSearchedMoves() : config.getLmrMinSearchedMoves() - 1)
                    && isQuiet) {
                    reduction = config.getLmrReductions()[depth][i];
                    if (pvNode || isInCheck) {
                        reduction--;
                    }
                    if (transposition != null && transposition.getMove() != null && isCapture) {
                        reduction++;
                    }
                }

                // For all other moves apart from the principal variation, search with a null window (-alpha - 1, -alpha),
                // to try and prove the move will fail low while saving the time spent on a full search.
                eval = -search(depth - 1 + extension - reduction, ply + 1, -alpha - 1, -alpha, true);

                if (eval > alpha && (eval < beta || reduction > 0)) {
                    // If we reduced the depth and/or used a null window, and the score beat alpha, we need to do a
                    // re-search with the full window and depth. This is costly, but hopefully doesn't happen too often.
                    eval = -search(depth - 1 + extension, ply + 1, -beta, -alpha, true);
                }
            }

            board.unmakeMove();

            if (isCancelled()) {
                return alpha;
            }

            if (eval >= beta) {
                // This is a beta cut-off - the opponent won't let us get here as they already have better alternatives
                transpositionTable.put(getKey(), HashFlag.LOWER, depth, ply, move, beta);
                if (!isCapture) {
                    // Non-captures which cause a beta cut-off are stored as 'killer' and 'history' moves for future move ordering
                    moveOrderer.addKillerMove(ply, move);
                    moveOrderer.incrementHistoryScore(depth, move, board.isWhiteToMove());
                }
                return beta;
            }

            if (eval > alpha) {
                // We have found a new best move
                bestMove = move;
                alpha = eval;
                flag = HashFlag.EXACT;
                if (ply == 0) {
                    bestMoveCurrentDepth = move;
                    bestEvalCurrentDepth = eval;
                }
            }
        }
        transpositionTable.put(getKey(), flag, depth, ply, bestMove, alpha);
        return alpha;

    }

    /**
     * Extend the search by searching captures until a 'quiet' position is reached, where there are no further captures
     * and therefore limited potential for winning tactics that drastically alter the evaluation. Used to mitigate the
     * worst of the 'horizon effect'.
     *
     * @see <a href="https://www.chessprogramming.org/Quiescence_Search">Chess Programming Wiki</a>
     */
    int quiescenceSearch(int alpha, int beta, int depth, int ply) {
        if (isCancelled()) {
            return alpha;
        }
        // Exit the quiescence search early if we already have an accurate score stored in the hash table.
        Move previousBestMove = null;
        HashEntry transposition = transpositionTable.get(getKey(), ply);
        if (isUsefulTransposition(transposition, 1, alpha, beta)) {
            return transposition.getScore();
        }
        if (hasBestMove(transposition)) {
            previousBestMove = transposition.getMove();
        }

        int eval = evaluator.evaluate(board);
        int standPat = eval;

        boolean isInCheck = moveGenerator.isCheck(board, board.isWhiteToMove());

        List<Move> moves;
        if (isInCheck) {
            // If we are in check, we need to generate 'all' legal moves that evade check, not just captures. Otherwise,
            // we risk missing simple mate threats.
            moves = moveGenerator.generateMoves(board, MoveFilter.ALL);
            if (moves.isEmpty()) {
                return -Score.MATE_SCORE + ply;
            }
        } else {
            // If we are not in check, then we have the option to 'stand pat', i.e. decline to continue the capture chain,
            // if the static evaluation of the position is good enough.
            if (eval >= beta) {
                return beta;
            }
            if (eval > alpha) {
                alpha = eval;
            }
            MoveFilter filter = depth == 1 ? MoveFilter.CAPTURES_AND_CHECKS : MoveFilter.CAPTURES_ONLY;
            moves = moveGenerator.generateMoves(board, filter);
        }

        int[] scores = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            scores[i] = moveOrderer.mvvLva(board, moves.get(i), previousBestMove);
        }

        for (int i = 0; i < moves.size(); i++) {
            for (int j = i + 1; j < moves.size(); j++) {
                int firstScore = scores[i];
                int secondScore = scores[j];
                Move firstMove = moves.get(i);
                Move secondMove = moves.get(j);
                if (scores[j] > scores[i]) {
                    scores[i] = secondScore;
                    scores[j] = firstScore;
                    moves.set(i, secondMove);
                    moves.set(j, firstMove);
                }
            }
            Move move = moves.get(i);
            if (!isInCheck) {
                // If the captured piece + a margin still has no potential of raising alpha, let's assume this position
                // is bad for us no matter what we do, and not bother searching any further
                // See https://www.chessprogramming.org/Delta_Pruning
                Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.getEndSquare());
                boolean isFutile = capturedPiece != null && (standPat + capturedPiece.getValue() + config.getDpMargin() < alpha) && !move.isPromotion();
                if (isFutile) {
                    continue;
                }
                // Evaluate the possible captures + recaptures on the target square, in order to ilter out losing capture
                // chains, such as capturing with the queen a pawn defended by another pawn.
                // See https://www.chessprogramming.org/Static_Exchange_Evaluation
                int seeScore = see.evaluate(board, move);
                boolean isBadCapture = (depth <= 3 && seeScore < 0) || (depth > 3 && seeScore <= 0);
                if (isBadCapture) {
                    continue;
                }
            }

            board.makeMove(move);
            nodes++;
            eval = -quiescenceSearch(-beta, -alpha, depth + 1, ply + 1);
            board.unmakeMove();

            if (eval >= beta) {
                return beta;
            }
            if (eval > alpha) {
                alpha = eval;
            }
        }

        return alpha;

    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
        this.evaluator.evaluate(board);
    }

    @Override
    public void setHashSize(int hashSizeMb) {
        this.transpositionTable = new TranspositionTable(hashSizeMb);
    }

    @Override
    public void setThreadCount(int threadCount) {
        // do nothing as this implementation is single-threaded
    }

    /**
     * Check if the hit from the transposition table is 'useful' in the current search. A TT-hit is useful either if it
     * 1) contains an exact evaluation, so we don't need to search any further, 2) contains a fail-high greater than our
     * current beta value, or 3) contains a fail-low lesser than our current alpha value.
     */
    private boolean isUsefulTransposition(HashEntry entry, int depth, int alpha, int beta) {
        return entry != null &&
                entry.getDepth() >= depth &&
                ((entry.getFlag().equals(HashFlag.EXACT)) ||
                 (entry.getFlag().equals(HashFlag.UPPER) && entry.getScore() <= alpha) ||
                 (entry.getFlag().equals(HashFlag.LOWER) && entry.getScore() >= beta));
    }

    private boolean hasBestMove(HashEntry transposition) {
        return transposition != null && transposition.getMove() != null;
    }

    private boolean isCheckmateFoundAtCurrentDepth(int currentDepth) {
        return Math.abs(bestEval) >= Score.MATE_SCORE - currentDepth;
    }

    private SearchResult buildResult() {
        long millis = Duration.between(start, Instant.now()).toMillis();
        long nps = nodes > 0 && millis > 0 ? ((nodes / millis) * 1000) : 0;
        return new SearchResult(bestEval, bestMove, currentDepth, millis, nodes, nps);
    }

    private boolean isCancelled() {
        // Exit if global search is cancelled
        if (config.isSearchCancelled()) return true;
        // Exit if local search is cancelled
        if (cancelled) return true;
        return !config.isPondering() && (timeout != null && !Instant.now().isBefore(timeout));
    }

    private boolean isDraw() {
        return Result.isEffectiveDraw(board);
    }

    private long getKey() {
        return board.getGameState().getZobristKey();
    }


    @Override
    public TranspositionTable getTranspositionTable() {
        return transpositionTable;
    }

    @Override
    public void clearHistory() {
        transpositionTable.clear();
    }

    @Override
    public void logStatistics() {
        //log.info(statistics.generateReport());
    }
}
