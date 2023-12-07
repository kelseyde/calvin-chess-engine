package com.kelseyde.calvin.tuning.copy;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Arbiter;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.score.Score;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
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
 * move found in the previous iteration, as well as the other ordering heuristics in the {@link MoveOrderer2} -- and by
 * using a {@link TranspositionTable} -- the iterative approach is much more efficient than it might sound.
 *
 * @see <a href="https://www.chessprogramming.org/Iterative_Deepening">Chess Programming Wiki</a>
 */
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Searcher2 implements Search {
    EngineConfig config;
    MoveGeneration moveGenerator;
    MoveOrdering moveOrderer;
    Evaluation evaluator;
    StaticExchangeEvaluator see;
    TranspositionTable transpositionTable;

    Board board;
    Instant timeout;
    boolean cancelled;
    SearchResult resultCurrentDepth;
    SearchResult result;

    public Searcher2(EngineConfig config,
                    MoveGeneration moveGenerator,
                    MoveOrdering moveOrderer,
                    Evaluation evaluator,
                    TranspositionTable transpositionTable) {
        this.config = config;
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

        timeout = Instant.now().plus(duration);
        result = null;
        resultCurrentDepth = null;
        cancelled = false;

        int currentDepth = 1;
        int maxDepth = 256;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE - 1;
        int retryMultiplier = 0;

        while (!isCancelled() && currentDepth < maxDepth) {
            resultCurrentDepth = null;

            int eval = search(currentDepth, 0, alpha, beta, true);

            if (resultCurrentDepth != null) {
                result = resultCurrentDepth;
            }

            if (isCancelled() || isCheckmateFoundAtCurrentDepth(currentDepth)) {
                break;
            }

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
            retryMultiplier = 0;
            currentDepth++;
        }

        if (result == null) {
            System.out.println("Time expired before a move was found!");
            Move move = moveGenerator.generateMoves(board).get(0);
            result = new SearchResult(0, move, currentDepth);
        }
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

        if (isCancelled()) {
            return alpha;
        }
        if (depth <= 0) {
            return quiescenceSearch(alpha, beta, 1, ply);
        }
        if (ply > 0 && isDraw()) {
            return Score.DRAW_SCORE;
        }

        // Mate distance pruning: exit early if we have already found a forced mate at an earlier ply
        alpha = Math.max(alpha, -Score.MATE_SCORE + ply);
        beta = Math.min(beta, Score.MATE_SCORE - ply);
        if (alpha >= beta) {
            return alpha;
        }

        boolean pvNode = beta - alpha > 1;
        Move previousBestMove = ply == 0 && result != null ? result.move() : null;

        HashEntry transposition = transpositionTable.get(getKey(), ply);
        if (isUsefulTransposition(transposition, depth, alpha, beta)) {
            if (ply == 0 && transposition.getMove() != null) {
                resultCurrentDepth = new SearchResult(transposition.getScore(), transposition.getMove(), depth);
            }
            return transposition.getScore();
        }
        if (hasBestMove(transposition)) {
            previousBestMove = transposition.getMove();
        }

        List<Move> moves = moveGenerator.generateMoves(board);
        boolean isInCheck = moveGenerator.isCheck(board, board.isWhiteToMove());
        int staticEval = evaluator.evaluate(board);

        if (moves.isEmpty()) {
            // Found checkmate / stalemate
            return isInCheck ? -Score.MATE_SCORE + ply : Score.DRAW_SCORE;
        }
        if (ply == 0 && moves.size() == 1) {
            // Exit immediately if there is only one legal move at the root node
            resultCurrentDepth = new SearchResult(staticEval, moves.get(0), depth);
            cancelled = true;
            return staticEval;
        }

        if (!pvNode && !isInCheck) {

            // Reverse futility pruning: if our position is so good that we don't need to move to beat beta + some small
            // margin, then let's assume we don't need to search any further, and cut off early.
            boolean isMateHunting = Math.abs(alpha) >= Score.MATE_SCORE - 100;
            if (depth <= config.getRfpDepth() && staticEval - config.getRfpMargin()[depth] > beta && !isMateHunting) {
                return staticEval - config.getRfpMargin()[depth];
            }

            // Null move pruning: if the static eval > beta - some margin, and so is likely to fail high, then let's test
            // this theory by giving the opponent an extra move (making a 'null' move), and searching the resulting position
            // to a shallower depth. If the result still fails high, prune this node.
            boolean isPawnEndgame = !board.hasPiecesRemaining(board.isWhiteToMove());
            if (depth >= config.getNmpDepth() && allowNull && staticEval >= beta - config.getNmpMargin() && !isPawnEndgame) {
                board.makeNullMove();
                int eval = -search(depth - 1 - (2 + depth / 7), ply + 1, -beta, -beta + 1, false);
                board.unmakeNullMove();
                if (eval >= beta) {
                    transpositionTable.put(getKey(), HashFlag.LOWER, depth, ply, previousBestMove, beta);
                    return eval;
                }
            }

        }

        moves = moveOrderer.orderMoves(board, moves, previousBestMove, true, ply);
        Move bestMove = null;
        HashFlag flag = HashFlag.UPPER;

        for (int i = 0; i < moves.size(); i++) {

            Move move = moves.get(i);
            boolean isCapture = board.pieceAt(move.getEndSquare()) != null;
            boolean isPromotion = move.getPromotionPieceType() != null;

            board.makeMove(move);

            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());

            // Futility pruning: if the static eval + some margin is still < alpha, and the current move is not interesting
            // (checks, captures, promotions), then let's assume it will fail low and prune this node.
            if (!pvNode && depth <= config.getFpDepth() && staticEval + config.getFpMargin()[depth] < alpha && !isInCheck && !isCheck && !isCapture && !isPromotion) {
                board.unmakeMove();
                continue;
            }

            // Search extensions: in certain interesting cases (promotions, checks that do not immediately lose material),
            // let's extend the search depth by one ply.
            int extension = 0;
            if (isPromotion || (isCheck && see.evaluateAfterMove(board, move) >= 0)) {
                extension = 1;
            }

            int eval;
            if (pvNode && i == 0) {
                // Principal variation search: the first move must be searched with the full alpha-beta window. If our move
                // ordering is any good then we expect this to be the best move, and so we need to retrieve the exact score.
                eval = -search(depth - 1 + extension, ply + 1, -beta, -alpha, true);

            } else {
                // Late move reductions: if the move is ordered late in the list, and isn't a 'noisy' move like a check,
                // capture or promotion, let's save time by assuming it's less likely to be good, and reduce the search depth.
                int reduction = 0;
                if (depth >= config.getLmrDepth() && i >= 2 && !isCapture && !isCheck && !isPromotion) {
                    reduction = i < 5 ? 1 : depth / 3;
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
                    moveOrderer.addHistoryMove(depth, move, board.isWhiteToMove());
                }
                return beta;
            }

            if (eval > alpha) {
                // We have found a new best move
                bestMove = move;
                alpha = eval;
                flag = HashFlag.EXACT;
                if (ply == 0) {
                    resultCurrentDepth = new SearchResult(eval, move, depth);
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

        moves = moveOrderer.orderMoves(board, moves, previousBestMove, false, 0);

        for (Move move : moves) {
            if (!isInCheck) {
                // Futility pruning: if the captured piece + a margin still has no potential of raising alpha, prune this node.
                Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.getEndSquare());
                boolean isFutile = capturedPiece != null && (standPat + capturedPiece.getValue() + config.getDpMargin() < alpha) && !move.isPromotion();
                if (isFutile) {
                    continue;
                }
                // Static exchange evaluation: filter out likely bad captures (e.g. QxP -> PxQ)
                int seeScore = see.evaluate(board, move);
                boolean isBadCapture = (depth <= 3 && seeScore < 0) || (depth > 3 && seeScore <= 0);
                if (isBadCapture) {
                    continue;
                }
            }

            board.makeMove(move);
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
    private boolean isUsefulTransposition(HashEntry entry, int plyRemaining, int alpha, int beta) {
        return entry != null &&
                entry.getDepth() >= plyRemaining &&
                ((entry.getFlag().equals(HashFlag.EXACT)) ||
                        (entry.getFlag().equals(HashFlag.UPPER) && entry.getScore() <= alpha) ||
                        (entry.getFlag().equals(HashFlag.LOWER) && entry.getScore() >= beta));
    }

    private boolean hasBestMove(HashEntry transposition) {
        return transposition != null && transposition.getMove() != null;
    }

    private boolean isCheckmateFoundAtCurrentDepth(int currentDepth) {
        return Math.abs(result.eval()) >= Score.MATE_SCORE - currentDepth;
    }

    private boolean isCancelled() {
        return cancelled || (timeout != null && !Instant.now().isBefore(timeout));
    }

    private boolean isDraw() {
        return Arbiter.isEffectiveDraw(board);
    }

    private long getKey() {
        return board.getGameState().getZobristKey();
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
