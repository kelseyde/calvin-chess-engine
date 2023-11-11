package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.score.PieceValues;
import com.kelseyde.calvin.movegeneration.MoveGeneration;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import com.kelseyde.calvin.search.moveordering.StaticExchangeEvaluator;
import com.kelseyde.calvin.search.transposition.NodeType;
import com.kelseyde.calvin.search.transposition.TranspositionNode;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import com.kelseyde.calvin.tuning.SearchResult;
import com.kelseyde.calvin.utils.notation.NotationUtils;
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
public class Searcher implements Search {

    private static final int MIN_EVAL = Integer.MIN_VALUE + 1;
    private static final int MAX_EVAL = Integer.MAX_VALUE - 1;

    private static final int MAX_DEPTH = 256;

    private static final int ASPIRATION_WINDOW_BUFFER = 50;
    private static final int ASPIRATION_WINDOW_FAIL_BUFFER = 150;

    private static final int[] FUTILITY_PRUNING_MARGIN = new int[] { 0, 170, 260, 450, 575 };
    private static final int[] REVERSE_FUTILITY_PRUNING_MARGIN = new int[] { 0, 120, 240, 360, 480 };
    private static final int DELTA_PRUNING_MARGIN = 200;

    private static final int CHECKMATE_SCORE = 1000000;
    private static final int DRAW_SCORE = 0;

    private MoveGeneration moveGenerator;
    private MoveOrdering moveOrderer;
    private Evaluation evaluator;
    private StaticExchangeEvaluator see;
    private TranspositionTable transpositionTable;
    private ResultCalculator resultCalculator;

    private @Getter Board board;

    private Instant timeout;

    private SearchResult result;
    private SearchResult resultCurrentDepth;
    private int currentDepth;

    public Searcher(Board board) {
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

    /**
     * Search for the best move in the current {@link Board} position.
     * @param duration How long we should search
     * @return A {@link SearchResult} containing the best move and the current evaluation.
     */
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

        while (!isTimeoutExceeded() && currentDepth < MAX_DEPTH) {
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
                retryMultiplier++;
                alpha -= ASPIRATION_WINDOW_FAIL_BUFFER * retryMultiplier;
                beta += ASPIRATION_WINDOW_BUFFER;
                continue;
            }
            if (eval >= beta) {
                // The result is greater than alpha, so search again at the same depth with an expanded aspiration window.
                retryMultiplier++;
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
        //System.out.printf("max depth: %s, eval: %s%n", currentDepth, result.eval());
        return result;

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth. Since this function is called
     * recursively until the depth limit is reached, 'depth' needs to be split into two parameters: 'ply remaining' and
     * 'ply from root'.
     *
     * @param plyRemaining        The number of ply deeper left to go in the current search
     * @param plyFromRoot         The number of ply already examined in this iteration of the search.
     * @param alpha               The lower bound for child nodes at the current search depth.
     * @param beta                The upper bound for child nodes at the current search depth.
     * @param allowNullPruning    Whether to allow null-move pruning in this search iteration.
     */
    public int search(int plyRemaining, int plyFromRoot, int alpha, int beta, boolean allowNullPruning) {

        if (isTimeoutExceeded()) {
            return 0;
        }
        if (plyRemaining <= 0) {
            // In the case that search depth is reached, begin quiescence search
            return quiescenceSearch(alpha, beta, 1);
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
            // Found checkmate / stalemate
            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());
            return isCheck ? -CHECKMATE_SCORE + plyFromRoot : DRAW_SCORE;
        }

        // Null-move pruning: give the opponent an extra move to try produce a cut-off
        if (allowNullPruning && plyRemaining >= 2) {
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

        // Futility pruning: in nodes close to the horizon, discard moves which have no potential of falling within the window.
        boolean isFutilityPruningEnabled = false;
        if (plyRemaining <= 4) {
            // Do not prune positions where we are in check.
            boolean isNotCheck = !moveGenerator.isCheck(board, board.isWhiteToMove());
            // Do not prune positions where we are hunting for checkmate.
            boolean isNotMateHunting = Math.abs(alpha) < CHECKMATE_SCORE - 100;

            int staticEval = evaluator.get();

            int reverseFutilityMargin = REVERSE_FUTILITY_PRUNING_MARGIN[plyRemaining];
            boolean isAssumedFailHigh = staticEval - reverseFutilityMargin > beta;
            if (isAssumedFailHigh && isNotCheck && isNotMateHunting) {
                // In 'reverse futility pruning', we check if the static evaluation - some margin is still > beta.
                // If so, then let's assume there's no point searching any moves in this position, since it's likely to
                // produce a cut-off no matter what we do.
                return staticEval - reverseFutilityMargin;
            }

            int futilityMargin = FUTILITY_PRUNING_MARGIN[plyRemaining];
            boolean isAssumedFailLow = staticEval + futilityMargin < alpha;
            if (isAssumedFailLow && isNotCheck && isNotMateHunting) {
                // In standard futility pruning, we check if the static evaluation + some margin is still below alpha.
                // If so, we still need to search interesting moves (checks, captures, promotions), in case we have a
                // saving move, but we assume that all quiet moves can be skipped.
                isFutilityPruningEnabled = true;
            }
        }

        List<Move> orderedMoves = moveOrderer.orderMoves(board, legalMoves, previousBestMove, true, plyFromRoot);

        Move bestMove = null;
        int originalAlpha = alpha;

        for (int i = 0; i < orderedMoves.size(); i++) {

            Move move = orderedMoves.get(i);
            boolean isCapture = board.pieceAt(move.getEndSquare()) != null;
            boolean isPromotion = move.getPromotionPieceType() != null;

            makeMove(move);
            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());

            if (isFutilityPruningEnabled && !isCheck && !isCapture && !isPromotion) {
                unmakeMove();
                continue;
            }

            // Search extensions: if the move meets particular criteria (e.g. is a check), then extend the search depth by one ply.
            int extensions = 0;
            if (isCheck || isPromotion) {
                extensions = 1;
            }

            // Search reductions: if the move is ordered late in the list, so less likely to be good, reduce the search depth by one ply.
            int reductions = 0;
            if (plyRemaining >= 4 && i >= 2 && !isCapture && !isCheck && !isPromotion) {
                reductions = i < 5 ? 1 : plyRemaining / 3;
            }

            int eval = -search(plyRemaining - 1 + extensions - reductions, plyFromRoot + 1, -beta, -alpha, true);

            if (reductions > 0 && eval > alpha) {
                // In case we reduced the search but the move beat alpha, do a full-depth search to get a more accurate eval
                eval = -search(plyRemaining - 1 + extensions, plyFromRoot + 1, -beta, -alpha, true);
            }
            unmakeMove();

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
                bestMove = move;
                alpha = eval;
                if (plyFromRoot == 0) {
                    resultCurrentDepth = new SearchResult(eval, move);
                }
            }
        }

        NodeType transpositionType = alpha <= originalAlpha ? NodeType.UPPER_BOUND : NodeType.EXACT;
        transpositionTable.put(transpositionType, plyRemaining, bestMove, alpha);
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
        // First exit if we have already stored an accurate eval in the TT
        TranspositionNode transposition = transpositionTable.get();
        if (isUsefulTransposition(transposition, alpha, beta)) {
            return transposition.getValue();
        }

        // Then check stand-pat score.
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
            // Static exchange evaluation: filter out likely bad captures (e.g. QxP -> PxQ)
            int seeEval = see.evaluate(board, move);
            if ((depth <= 3 && seeEval < 0) || (depth > 3 && seeEval <= 0)) {
                continue;
            }

            // Futility pruning: if the captured piece + a margin still has no potential of raising alpha, prune this node.
            Piece capturedPieceType = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.getEndSquare());
            int delta = standPat + PieceValues.valueOf(capturedPieceType) + DELTA_PRUNING_MARGIN;
            if (delta < alpha && !move.isPromotion()) {
                continue;
            }

            makeMove(move);
            eval = -quiescenceSearch(-beta, -alpha, depth + 1);
            unmakeMove();

            if (eval >= beta) {
                return beta;
            }
            if (eval > alpha) {
                alpha = eval;
            }
        }

        return alpha;

    }

    private void makeMove(Move move) {
        board.makeMove(move);
        evaluator.makeMove(move);
    }

    private void unmakeMove() {
        board.unmakeMove();
        evaluator.unmakeMove();
    }

    /**
     * Check if the hit from the transposition table is 'useful' in the current search. A TT-hit is useful either if it
     * 1) contains an exact evaluation, so we don't need to search any further, 2) contains a fail-high greater than our
     * current beta value, or 3) contains a fail-low lesser than our current alpha value.
     */
    private boolean isUsefulTransposition(TranspositionNode transposition, int plyRemaining, int alpha, int beta) {
        return isUsefulTransposition(transposition, alpha, beta) && transposition.getDepth() >= plyRemaining;
    }

    private boolean isUsefulTransposition(TranspositionNode transposition, int alpha, int beta) {
        if (transposition == null) {
            return false;
        }
        NodeType type = transposition.getType();

        // Previous search returned the exact evaluation for this position.
        boolean isInWindow = type.equals(NodeType.EXACT);

        // Previous search failed low, beating alpha score; only use it if it beats the current alpha.
        boolean isFailLow = type.equals(NodeType.UPPER_BOUND) && transposition.getValue() <= alpha;

        // Previous search failed high, causing a beta cut-off; only use it if greater than current beta.
        boolean isFailHigh = type.equals(NodeType.LOWER_BOUND) && transposition.getValue() >= beta;

        return isInWindow || isFailLow || isFailHigh;
    }

    private boolean hasBestMove(TranspositionNode transposition) {
        return transposition != null && transposition.getBestMove() != null;
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

    private String side() {
        return board.isWhiteToMove() ? "w" : "b";
    }

    private String alphaBeta(int alpha, int beta) {
        return String.format("(%s, %s)", alpha, beta);
    }

    private String move(Move move) {
        return NotationUtils.toNotation(move);
    }

    private String history() {
        return NotationUtils.toNotation(board.getMoveHistory());
    }

    private String moves(List<Move> moves) {
        return moves.stream()
                .map(NotationUtils::toNotation)
                .toList()
                .toString();
    }

}
