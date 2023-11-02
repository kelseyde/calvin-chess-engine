package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.see.StaticExchangeEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
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
import java.util.ArrayList;
import java.util.Collections;
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

//    private static final int ASPIRATION_WINDOW_BUFFER = 150;
//    private static final int ASPIRATION_WINDOW_FAIL_BUFFER = 250;
    private static final int ASPIRATION_WINDOW_BUFFER = 50;
    private static final int ASPIRATION_WINDOW_FAIL_BUFFER = 150;

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

    private SearchStatistics statistics = new SearchStatistics();

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

    @Override
    public SearchResult search(Duration duration) {

        evaluator = new Evaluator(board);
        timeout = Instant.now().plus(duration);
        result = null;
        resultCurrentDepth = null;
        statistics = new SearchStatistics();
        statistics.setStart(Instant.now());

        int currentDepth = 1;
        int alpha = MIN_EVAL;
        int beta = MAX_EVAL;
        int retryMultiplier = 0;

        while (!isTimeoutExceeded()) {

            Instant depthStart = Instant.now();
            resultCurrentDepth = null;

            int eval = search(currentDepth, 0, alpha, beta);
//            log.info("depth {} eval: {}", currentDepth, eval);

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
//                log.info("depth {} eval {} failed aspiration window {} - {}, must research", currentDepth, eval, alpha, beta);
                alpha -= ASPIRATION_WINDOW_FAIL_BUFFER * retryMultiplier;
                beta += ASPIRATION_WINDOW_BUFFER;
                continue;
            }
            if (eval >= beta) {
                // The result is greater than alpha, so search again at the same depth with an expanded aspiration window.
                retryMultiplier += 1;
//                log.info("depth {} eval {} failed aspiration window {} - {}, must research", currentDepth, eval, alpha, beta);
                beta += ASPIRATION_WINDOW_FAIL_BUFFER * retryMultiplier;
                alpha -= ASPIRATION_WINDOW_BUFFER;
                continue;
            }

            alpha = eval - ASPIRATION_WINDOW_BUFFER;
            beta = eval + ASPIRATION_WINDOW_BUFFER;
            retryMultiplier = 0;

            statistics.incrementDepth(currentDepth, depthStart, Instant.now());
            currentDepth++;
        }

        if (result == null) {
            // If we did not find a single move during search (almost impossible), just return a random legal move.
            log.warn("Time expired before a move was found!");
            Move move = moveGenerator.generateMoves(board, false).get(0);
            result = new SearchResult(0, move);
        }
        statistics.setEnd(Instant.now());
//        log.info(statistics.generateReport());
//        transpositionTable.logTableSize();
        return result;

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth. Since this function is called
     * recursively until the depth limit is reached, 'depth' needs to be split into two parameters: 'ply remaining' and
     * 'ply from root'.
     *
     * @param plyRemaining   The number of ply deeper left to go in the current search
     * @param plyFromRoot    The number of ply already examined in this iteration of the search.
     * @param alpha          the lower bound for child nodes at the current search depth.
     * @param beta           the upper bound for child nodes at the current search depth.
     */
     public int search(int plyRemaining, int plyFromRoot, int alpha, int beta) {

         if (isTimeoutExceeded()) {
             return 0;
         }
         if (plyFromRoot > 0) {
             if (resultCalculator.isEffectiveDraw(board)) {
//                 log.info("{} {} {} found draw", side(board), alphaBeta(alpha, beta), moveHistory(board));
                 return DRAW_SCORE;
             }
             // Exit early if we have already found a forced mate at an earlier ply
             alpha = Math.max(alpha, -CHECKMATE_SCORE + plyFromRoot);
             beta = Math.min(beta, CHECKMATE_SCORE - plyFromRoot);
             if (alpha >= beta) {
                 return alpha;
             }
         }
         Move previousBestMove = plyFromRoot == 0 && result != null ? result.move() : null;

        // TODO move transposition logic to TranspositionTable
        // Handle possible transposition
        TranspositionNode transposition = transpositionTable.get();
        if (hasBestMove(transposition)) {
            previousBestMove = transposition.getBestMove();
        }
        if (isUsefulTransposition(transposition, plyRemaining, alpha, beta)) {
            if (plyFromRoot == 0) {
                resultCurrentDepth = new SearchResult(transposition.getValue(), transposition.getBestMove());
            }
            //log.info("{} {} {} transposition eval {}", side(board), alphaBeta(alpha, beta), moveHistory(board), transposition.getValue());
            return transposition.getValue();
        }

        List<Move> legalMoves = moveGenerator.generateMoves(board, false);

        // Handle terminal nodes, where search is ended either due to checkmate, draw, or reaching max depth.
        if (plyRemaining == 0) {
            // In the case that max depth is reached, begin the quiescence search
            int eval = quiescenceSearch(alpha, beta, 1);
//            log.info("{} {} {} q eval {}", side(board), alphaBeta(alpha, beta), moveHistory(board), eval);
            return eval;
        }
        if (legalMoves.isEmpty()) {
            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());
            if (isCheck) {
                // Found checkmate
//                log.info("{} {} {} checkmate eval {}", side(board), alphaBeta(alpha, beta), moveHistory(board), checkmateEval);
                return -CHECKMATE_SCORE + plyFromRoot;
            } else {
                // Found stalemate
//                log.info("{} {} {} draw eval {}", side(board), alphaBeta(alpha, beta), moveHistory(board), DRAW_SCORE);
                return DRAW_SCORE;
            }
        }

        List<Move> orderedMoves = moveOrderer.orderMoves(board, legalMoves, previousBestMove, true, plyFromRoot);
//        log.info("{} {} {} moves: {}", side(board), alphaBeta(alpha, beta), moveHistory(board), moves(orderedMoves));

        Move bestMoveInThisPosition = null;
        int originalAlpha = alpha;

        for (int i = 0; i < orderedMoves.size(); i++) {

            Move move = orderedMoves.get(i);
            boolean isCapture = board.pieceAt(move.getEndSquare()) != null;
            boolean isPromotion = move.getPromotionPieceType() != null;

            board.makeMove(move);
            evaluator.makeMove(move);
            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());

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

            int eval = -search(plyRemaining - 1 + extensions - reductions, plyFromRoot + 1, -beta, -alpha);

            if (reductions > 0 && eval > alpha) {
                // In case we reduced the search but the move beat alpha, do a full-depth search to get a more accurate eval
                eval = -search(plyRemaining - 1 + extensions, plyFromRoot + 1, -beta, -alpha);
            }
            board.unmakeMove();
            evaluator.unmakeMove();

            if (isTimeoutExceeded()) {
                return 0;
            }
//            log.info("{} {} {} eval {} {}", side(board), alphaBeta(alpha, beta), moveHistory(board), NotationUtils.toNotation(move), eval);

            if (eval >= beta) {
                // This is a beta cut-off - the opponent won't let us get here as they already have better alternatives
                transpositionTable.put(NodeType.LOWER_BOUND, plyRemaining, move, beta);

                if (!isCapture) {
                    // Non-captures which cause a beta cut-off are stored as 'killer' and 'history' moves for future move ordering
                    moveOrderer.addKillerMove(plyFromRoot, move);
                    moveOrderer.addHistoryMove(plyRemaining, move, board.isWhiteToMove());
                }
//                log.info("{} {} {} cutoff {} {}", side(board), alphaBeta(alpha, beta), moveHistory(board), NotationUtils.toNotation(move), eval);
                return beta;
            }

            if (eval > alpha) {
                // We have found a new best move
                bestMoveInThisPosition = move;
                alpha = eval;
//                log.info("{} {} {} best move {} {}", side(board), alphaBeta(alpha, beta), moveHistory(board), NotationUtils.toNotation(move), eval);
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
        // First check the 'stand pat' score, that is, the evaluation of the board if the player declines to make any more
        // captures. Since the player is not forced to capture and may have good non-capture moves available, they may
        // choose to stand pat. In that case, treat this node as a beta cut-off.
        int eval = evaluator.get();
//        log.info("{} {} {} q initial eval: {}", side(board), alphaBeta(alpha, beta), moveHistory(board), eval);
        if (eval >= beta) {
//            log.info("{} {} {} q cutoff {}", side(board), alphaBeta(alpha, beta), moveHistory(board), eval);
            return beta;
        }
        if (eval > alpha) {
            alpha = eval;
//            log.info("{} {} {} q raising alpha {}", side(board), alphaBeta(alpha, beta), moveHistory(board), eval);
        }

        // Generate all legal captures
        List<Move> moves = moveGenerator.generateMoves(board, true);
        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, false, 0);
//        log.info("{} {} {} q moves: {}", side(board), alphaBeta(alpha, beta), moveHistory(board), moves(orderedMoves));

        for (Move move : orderedMoves) {

            // Get the static exchange evaluation of the position ('see'). Equivalent to the human heuristic of 'counting
            // the attackers and defenders' - if an exchange is obviously bad, like for giving up the queen for a pawn, don't
            // bother searching that branch. This saves time at the expense of potentially missing some tactical combinations.
            int seeEval = see.evaluate(board, move);
            if ((depth <= 4 && seeEval < 0) || (depth > 4 && seeEval <= 0)) {
                // Up to depth 4 in quiescence, only considers captures with SEE eval >= 0.
                // Past depth 4 in quiescence, only consider captures with SEE eval > 0.
//                log.info("{} {} {} see skipping move: {}", side(board), alphaBeta(alpha, beta), moveHistory(board), NotationUtils.toNotation(move));
                continue;
            }

            board.makeMove(move);
            evaluator.makeMove(move);
            eval = -quiescenceSearch(-beta, -alpha, depth + 1);
            board.unmakeMove();
            evaluator.unmakeMove();
//            log.info("{} {} {} q eval {} {}", side(board), alphaBeta(alpha, beta), moveHistory(board), NotationUtils.toNotation(move), eval);

            if (eval >= beta) {
//                log.info("{} {} {} q cutoff {} {}", side(board), alphaBeta(alpha, beta), moveHistory(board), NotationUtils.toNotation(move), eval);
                return beta;
            }
            if (eval > alpha) {
//                log.info("{} {} {} q best move {} {}", side(board), alphaBeta(alpha, beta), moveHistory(board), NotationUtils.toNotation(move), eval);
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

    private String side(Board board) {
        return board.isWhiteToMove() ? "w" : "b";
    }

    private String alphaBeta(int alpha, int beta) {
        return String.format("[%s, %s]", alpha, beta);
    }

    private String moveHistory(Board board) {
        List<Move> moves = new ArrayList<>(board.getMoveHistory());
        Collections.reverse(moves);
        return moves(moves);
    }

    private String moves(List<Move> moves) {
        return moves.stream()
                .map(NotationUtils::toNotation)
                .toList()
                .toString();
    }

}
