package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.score.PieceValues;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.moveordering.StaticExchangeEvaluator;
import com.kelseyde.calvin.search.transposition.NodeType;
import com.kelseyde.calvin.search.transposition.TranspositionNode;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@NoArgsConstructor
public class Searcher2 implements Search {

    private static final int MIN_EVAL = Integer.MIN_VALUE + 1;
    private static final int MAX_EVAL = Integer.MAX_VALUE - 1;

    private static final int ASPIRATION_WINDOW_BUFFER = 50;
    private static final int ASPIRATION_WINDOW_FAIL_BUFFER = 150;

    private static final int[] FUTILITY_PRUNING_MARGIN = new int[] { 0, 200, 300, 500 };
    private static final int DELTA_PRUNING_MARGIN = 200;

    private static final int CHECKMATE_SCORE = 1000000;
    private static final int DRAW_SCORE = 0;

    private MoveGenerator moveGenerator;
    private MoveOrderer moveOrderer;
    private Evaluator evaluator;
    private StaticExchangeEvaluator see;
    private TranspositionTable transpositionTable;
    private ResultCalculator resultCalculator;

    private @Getter Board board;

    private Instant timeout;

    private SearchResult result;
    private SearchResult resultCurrentDepth;

    public Searcher2(Board board) {
        init(board);
    }

    @Override
    public void init(Board board) {
        this.board = board;
        this.moveGenerator = new MoveGenerator();
        this.moveOrderer = new MoveOrderer();
        this.see = new StaticExchangeEvaluator();
        this.resultCalculator = new ResultCalculator();
        this.evaluator = new Evaluator(board);
        this.transpositionTable = new TranspositionTable(board);
    }

    @Override
    public SearchResult search(Duration duration) {

        evaluator.init(board);
        timeout = Instant.now().plus(duration);
        result = null;
        resultCurrentDepth = null;

        int currentDepth = 1;
        int alpha = MIN_EVAL;
        int beta = MAX_EVAL;
        int retryMultiplier = 0;

        while (!isTimeoutExceeded()) {
            resultCurrentDepth = null;

            int eval = search(currentDepth, 0, alpha, beta, true);

            if (resultCurrentDepth != null) {
                result = resultCurrentDepth;
            }

            if (isTimeoutExceeded() || isCheckmateFoundAtCurrentDepth(result.eval(), currentDepth)) {
                // Exit early if time runs out, or we already found forced mate
                break;
            }

            if (eval <= alpha) {
                // The result is less than alpha, so search again at the same depth with an expanded aspiration window.
                retryMultiplier += 1;
                alpha -= ASPIRATION_WINDOW_FAIL_BUFFER * retryMultiplier;
                beta += ASPIRATION_WINDOW_BUFFER;
                continue;
            }
            if (eval >= beta) {
                // The result is greater than alpha, so search again at the same depth with an expanded aspiration window.
                retryMultiplier += 1;
                beta += ASPIRATION_WINDOW_FAIL_BUFFER * retryMultiplier;
                alpha -= ASPIRATION_WINDOW_BUFFER;
                continue;
            }

            alpha = eval - ASPIRATION_WINDOW_BUFFER;
            beta = eval + ASPIRATION_WINDOW_BUFFER;
            retryMultiplier = 0;

            currentDepth++;
        }

        if (result == null) {
            // If we did not find a single move during search (almost impossible), just return a random legal move.
            log.warn("Time expired before a move was found!");
            Move move = moveGenerator.generateMoves(board, false).get(0);
            result = new SearchResult(0, move);
        }
//        System.out.println("eval: " + result.eval());
        return result;

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth. Since this function is called
     * recursively until the depth limit is reached, 'depth' needs to be split into two parameters: 'ply remaining' and
     * 'ply from root'.
     *
     * @param plyRemaining The number of ply deeper left to go in the current search
     * @param plyFromRoot  The number of ply already examined in this iteration of the search.
     * @param alpha        The lower bound for child nodes at the current search depth.
     * @param beta         The upper bound for child nodes at the current search depth.
     * @param allowNull    Whether to allow null-move pruning in this search iteration.
     */
    public int search(int plyRemaining, int plyFromRoot, int alpha, int beta, boolean allowNull) {

        if (isTimeoutExceeded()) {
            return 0;
        }
        if (plyFromRoot > 0) {
            if (resultCalculator.isEffectiveDraw(board)) {
                return DRAW_SCORE;
            }
            // Mate distance pruning: exit early if we have already found a forced mate at an earlier ply
            alpha = Math.max(alpha, -CHECKMATE_SCORE + plyFromRoot);
            beta = Math.min(beta, CHECKMATE_SCORE - plyFromRoot);
            if (alpha >= beta) {
                return alpha;
            }
        }
        Move previousBestMove = plyFromRoot == 0 && result != null ? result.move() : null;

        // Handle possible transposition
        TranspositionNode transposition = transpositionTable.get();
        if (hasBestMove(transposition)) {
            previousBestMove = transposition.getBestMove();
        }
        if (isUsefulTransposition(transposition, plyRemaining, alpha, beta)) {
            if (plyFromRoot == 0) {
                resultCurrentDepth = new SearchResult(transposition.getValue(), transposition.getBestMove());
            }
            return transposition.getValue();
        }

        List<Move> legalMoves = moveGenerator.generateMoves(board, false);

        if (legalMoves.isEmpty()) {
            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());
            // Found checkmate / stalemate
            return isCheck ? -CHECKMATE_SCORE + plyFromRoot : DRAW_SCORE;
        }
        if (plyRemaining <= 0) {
            // In the case that max depth is reached, begin quiescence search
            return quiescenceSearch(alpha, beta, 1);
        }

        // Null-move pruning: give the opponent an extra move to try produce a cut-off
        if (allowNull && plyRemaining >= 2) {
            // Only attempt null-move pruning when the static eval is greater than beta (fail-high).
            boolean isAssumedFailHigh = evaluator.get() >= beta;

            // It is impossible to pass a null-move when in check (we would be checkmated).
            boolean isNotCheck = !moveGenerator.isCheck(board, board.isWhiteToMove());

            // Do not attempt null-move in pawn endgames, due to zugzwang positions in which having the move is a disadvantage.
            boolean isNotPawnEndgame = evaluator.getMaterial(board.isWhiteToMove()).hasPiecesRemaining();

            if (isAssumedFailHigh && isNotCheck && isNotPawnEndgame) {
                board.makeNullMove();
                int reduction = 3;
                int eval = -search(plyRemaining - 1 - reduction, plyFromRoot + 1, -beta, -beta + 1, false);
                board.unmakeNullMove();
                if (eval >= beta) {
                    return eval;
                }
            }
        }

        // Futility pruning: in nodes close to the horizon, discard moves which have no potential of raising alpha.
        boolean isFutilityPruningEnabled = false;
        if (plyRemaining <= 3) {
            // If the static evaluation + futility margin is still less than alpha then we assume this position to be futile
            boolean isAssumedFutile = evaluator.get() + FUTILITY_PRUNING_MARGIN[plyRemaining] < alpha;
            // Do not prune positions where we are in check.
            boolean isNotCheck = !moveGenerator.isCheck(board, board.isWhiteToMove());
            // Do not prune positions where we are hunting for checkmate.
            boolean isNotMateHunting = Math.abs(alpha) < 900000;
            if (isAssumedFutile && isNotCheck && isNotMateHunting) {
                isFutilityPruningEnabled = true;
            }
        }

        List<Move> orderedMoves = moveOrderer.orderMoves(board, legalMoves, previousBestMove, true, plyFromRoot);

        Move bestMoveInThisPosition = null;
        int originalAlpha = alpha;

        for (int i = 0; i < orderedMoves.size(); i++) {

            Move move = orderedMoves.get(i);
            boolean isCapture = board.pieceAt(move.getEndSquare()) != null;
            boolean isPromotion = move.getPromotionPieceType() != null;

            board.makeMove(move);
            evaluator.makeMove(move);
            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());

            if (isFutilityPruningEnabled && !isCheck && !isCapture && !isPromotion) {
                board.unmakeMove();
                evaluator.unmakeMove();
                continue;
            }

            // Search extensions: if the move meets particular criteria (e.g. is a check), then extend the search depth by one ply.
            int extensions = 0;
            if (isCheck || isPromotion) {
                extensions = 1;
            }

            // Search reductions: if the move is ordered late in the list, so less likely to be good, reduce the search depth by one ply.
            int reductions = 0;
            if (plyRemaining >= 3 && i >= 3 && !isCapture && !isCheck && !isPromotion) {
                reductions = 1;
            }

            int eval = -search(plyRemaining - 1 + extensions - reductions, plyFromRoot + 1, -beta, -alpha, true);

            if (reductions > 0 && eval > alpha) {
                // In case we reduced the search but the move beat alpha, do a full-depth search to get a more accurate eval
                eval = -search(plyRemaining - 1 + extensions, plyFromRoot + 1, -beta, -alpha, true);
            }
            board.unmakeMove();
            evaluator.unmakeMove();

            if (isTimeoutExceeded()) {
                return 0;
            }

            if (eval >= beta) {
                // This is a beta cut-off - the opponent won't let us get here as they already have better alternatives
                transpositionTable.put(NodeType.LOWER_BOUND, plyRemaining, move, beta);
                if (!isCapture) {
                    // Non-captures which cause a beta cut-off are stored as 'killer' and 'history' moves for future move ordering
                    moveOrderer.addKillerMove(plyFromRoot, move);
                    moveOrderer.addHistoryMove(plyRemaining, move, board.isWhiteToMove());
                }
                return beta;
            }

            if (eval > alpha) {
                // We have found a new best move
                bestMoveInThisPosition = move;
                alpha = eval;
                if (plyFromRoot == 0) {
                    resultCurrentDepth = new SearchResult(eval, move);
                }
            }
        }

        NodeType transpositionType = alpha <= originalAlpha ? NodeType.UPPER_BOUND : NodeType.EXACT;
        transpositionTable.put(transpositionType, plyRemaining, bestMoveInThisPosition, alpha);
        return alpha;

    }

    /**
     * Extend the search by searching captures until a 'quiet' position is reached, where there are no further captures
     * and therefore limited potential for winning tactics that drastically alter the evaluation. Used to mitigate the
     * worst of the 'horizon effect'.
     *
     * @see <a href="https://www.chessprogramming.org/Quiescence_Search">Chess Programming Wiki</a>
     */
    int quiescenceSearch(int alpha, int beta, int depth) {
        if (isTimeoutExceeded()) {
            return 0;
        }
        // First check stand-pat score.
        int eval = evaluator.get();
        int standPat = eval;
        if (eval >= beta) {
            return beta;
        }
        if (eval > alpha) {
            alpha = eval;
        }

        List<Move> moves = moveGenerator.generateMoves(board, true);
        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, false, 0);

        for (Move move : orderedMoves) {
            // Static exchange evaluation: try to filter out captures that are obviously bad (e.g. QxP -> PxQ)
            int seeEval = see.evaluate(board, move);
            if ((depth <= 4 && seeEval < 0) || (depth > 4 && seeEval <= 0)) {
                continue;
            }

            // Delta pruning: if the captured piece + a margin still has no potential of raising alpha, prune this node.
            Piece capturedPieceType = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.getEndSquare());
            int delta = standPat + PieceValues.valueOf(capturedPieceType) + DELTA_PRUNING_MARGIN;
            if (delta < alpha && !move.isPromotion()) {
                continue;
            }

            board.makeMove(move);
            evaluator.makeMove(move);
            eval = -quiescenceSearch(-beta, -alpha, depth + 1);
            board.unmakeMove();
            evaluator.unmakeMove();

            if (eval >= beta) {
                return beta;
            }
            if (eval > alpha) {
                alpha = eval;
            }
        }

        return alpha;

    }

    private boolean hasBestMove(TranspositionNode transposition) {
        return transposition != null && transposition.getBestMove() != null;
    }

    private boolean isUsefulTransposition(TranspositionNode transposition, int plyRemaining, int alpha, int beta) {
        if (transposition == null || transposition.getDepth() < plyRemaining) {
            return false;
        }
        NodeType type = transposition.getType();
        // Previous search returned the exact evaluation for this position.
        return ((type.equals(NodeType.EXACT))
                // Previous search failed low, beating alpha score; only use it if it beats the current alpha.
                || (type.equals(NodeType.UPPER_BOUND) && transposition.getValue() <= alpha)
                // Previous search failed high, causing a beta cut-off; only use it if greater than current beta.
                || (type.equals(NodeType.LOWER_BOUND) && transposition.getValue() >= beta));
    }

    @Override
    public void clearHistory() {
        if (moveOrderer != null) {
            moveOrderer.clear();
        }
        if (transpositionTable != null) {
            transpositionTable.clear();
        }
    }

    @Override
    public void logStatistics() {
        //log.info(statistics.generateReport());
    }

    private boolean isCheckmateFoundAtCurrentDepth(int bestEval, int currentDepth) {
        return Math.abs(bestEval) >= CHECKMATE_SCORE - currentDepth;
    }

    private boolean isTimeoutExceeded() {
        return !Instant.now().isBefore(timeout);
    }

    public void setTimeout(Instant timeout) {
        this.timeout = timeout;
    }


}
