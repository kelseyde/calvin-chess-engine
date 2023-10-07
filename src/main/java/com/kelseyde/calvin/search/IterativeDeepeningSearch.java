package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.evaluation.see.StaticExchangeEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.transposition.NodeType;
import com.kelseyde.calvin.search.transposition.TranspositionNode;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

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
public class IterativeDeepeningSearch implements Search {

    private static final int MIN_EVAL = Integer.MIN_VALUE + 1;
    private static final int MAX_EVAL = Integer.MAX_VALUE - 1;
    private static final int CHECKMATE_EVAL = 1000000;

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final ResultCalculator resultCalculator = new ResultCalculator();
    private final MoveOrderer moveOrderer = new MoveOrderer();
    private final BoardEvaluator evaluator = new BoardEvaluator();
    private final StaticExchangeEvaluator see = new StaticExchangeEvaluator();
    private final TranspositionTable transpositionTable;

    private final @Getter Board board;

    private Instant timeout;
    private Move bestMove;
    private int bestEval;
    private Move bestMoveCurrentDepth;
    private int bestEvalCurrentDepth;
    private boolean hasSearchedAtLeastOneMove;
    private SearchStatistics statistics;

    public IterativeDeepeningSearch(Board board) {
        this.board = board;
        this.transpositionTable = new TranspositionTable(board);
    }

    @Override
    public SearchResult search(Duration duration) {

        timeout = Instant.now().plus(duration);
        int currentDepth = 1;
        bestMove = null;
        bestMoveCurrentDepth = null;
        statistics = new SearchStatistics();
        statistics.setStart(Instant.now());

        while (!isTimeoutExceeded()) {

            Instant depthStart = Instant.now();

            hasSearchedAtLeastOneMove = false;

            search(currentDepth, 0, MIN_EVAL, MAX_EVAL);

            if (isTimeoutExceeded()) {
                if (hasSearchedAtLeastOneMove) {
                    bestMove = bestMoveCurrentDepth;
                    bestEval = bestEvalCurrentDepth;
//                    log.trace("({}) {} {} Timeout reached, best move {}, eval {}", board.isWhiteToMove() ? "White" : "Black", currentDepth, NotationUtils.toNotation(board.getMoveHistory()), bestMove, bestEval);
                    break;
                }
            } else {
                bestMove = bestMoveCurrentDepth;
                bestEval = bestEvalCurrentDepth;

                if (isCheckmateFoundAtCurrentDepth(bestEval, currentDepth)) {
                    // Exit early if we have found the fastest mate at the current depth
                    break;
                }

            }

            statistics.incrementDepth(currentDepth, depthStart, Instant.now());
            currentDepth++;
        }

        statistics.setEnd(Instant.now());
//        System.out.println(statistics.generateReport());
        transpositionTable.logTableSize();
        return new SearchResult(bestEval, bestMove);

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth. Since this function is called
     * recursively until the depth limit is reached, 'depth' needs to be split into two parameters: 'ply remaining' and
     * 'ply from root'.
     * @param plyRemaining The number of ply deeper left to go in the current search
     * @param plyFromRoot The number of ply already examined in this iteration of the search.
     * @param alpha the lower bound for child nodes at the current search depth.
     * @param beta the upper bound for child nodes at the current search depth.
     */
     int search(int plyRemaining, int plyFromRoot, int alpha, int beta) {
         if (isTimeoutExceeded()) {
             return 0;
         }
         if (plyFromRoot > 0) {
             // TODO detect draw
             // Exit early if we have already found a forced mate at an earlier ply
             alpha = Math.max(alpha, -CHECKMATE_EVAL + plyFromRoot);
             beta = Math.min(beta, CHECKMATE_EVAL - plyFromRoot);
             if (alpha >= beta) {
                 return alpha;
             }
         }
         Move previousBestMove = plyFromRoot == 0 ? bestMove : null;

         // Handle possible transposition
         TranspositionNode transposition = transpositionTable.get();
         if (transposition != null) {
             if (transposition.getBestMove() != null) {
                previousBestMove = transposition.getBestMove();
             }
             if (transposition.getDepth() >= plyRemaining) {
                 statistics.incrementTranspositions();
                 NodeType type = transposition.getType();
                 if ((type.equals(NodeType.EXACT))
                         || (type.equals(NodeType.LOWER_BOUND) && transposition.getValue() <= alpha)
                         || (type.equals(NodeType.UPPER_BOUND) && transposition.getValue() >= beta)) {
                     if (plyFromRoot == 0) {
                         bestMoveCurrentDepth = transposition.getBestMove();
                         bestEvalCurrentDepth = transposition.getValue();
                     }
                     return transposition.getValue();
                 }
             }
         }

         Move[] legalMoves = moveGenerator.generateLegalMoves(board, false);
         GameResult gameResult = resultCalculator.calculateResult(board, legalMoves);

         // Handle terminal nodes, where search is ended either due to checkmate, draw, or reaching max depth.
         if (gameResult.isCheckmate()) {
             statistics.incrementNodesSearched();
//             log.trace("({}) {} Found checkmate", board.isWhiteToMove() ? "WHITE" : "BLACK", plyFromRoot);
             return -CHECKMATE_EVAL + plyFromRoot;
         }
         if (gameResult.isDraw()) {
             statistics.incrementNodesSearched();
//             log.trace("({}) {} Found draw", board.isWhiteToMove() ? "WHITE" : "BLACK", plyFromRoot);
             return 0;
         }
         if (plyRemaining == 0) {
             // In the case that max depth is reached, begin the quiescence search
             statistics.incrementNodesSearched();
             int finalEval = quiescenceSearch(alpha, beta, 1);
//             log.trace("{} {} ({}, {}) Final eval: {}", board.isWhiteToMove() ? "WHITE" : "BLACK", NotationUtils.toNotation(board.getMoveHistory()), alpha, beta, finalEval);
             return finalEval;
         }

         Move[] orderedMoves = moveOrderer.orderMoves(board, legalMoves, previousBestMove, true, plyFromRoot);
//         log.trace("{} {} ({}, {}) Ordered moves: {}", board.isWhiteToMove() ? "WHITE" : "BLACK", NotationUtils.toNotation(board.getMoveHistory()), alpha, beta, NotationUtils.toNotation(Arrays.asList(orderedMoves)));

         Move bestMoveInThisPosition = null;
         int originalAlpha = alpha;

         for (int i = 0; i < orderedMoves.length; i++) {

             Move move = orderedMoves[i];
             boolean isCapture = board.pieceAt(move.getEndSquare()) != null;
             board.makeMove(move);

             int extensions = 0;
             // Search extensions: if the move meets particular criteria (e.g. is a check), then extend the search depth by one ply.
             if (moveGenerator.isCheck(board, board.isWhiteToMove())) {
                 extensions = 1;
             }

             // Search reductions: if the move is ordered late in the list, so less likely to be good, reduce the search depth by one ply.
             int reductions = 0;
             if (extensions == 0 && plyRemaining >= 3 && i >= 3 && !isCapture) {
                 reductions = 1;
             }

             int eval = -search(plyRemaining - 1 + extensions - reductions, plyFromRoot + 1, -beta, -alpha);

             if (reductions > 0 && eval > alpha) {
                 // In case we reduced the search but the move beat alpha, do a full-depth search to get a more accurate eval
                 eval = -search(plyRemaining - 1 + extensions, plyFromRoot + 1, -beta, -alpha);
             }
             board.unmakeMove();
//             log.trace("{} {} ({}, {}) Eval for {}: {}", board.isWhiteToMove() ? "WHITE" : "BLACK", NotationUtils.toNotation(board.getMoveHistory()), alpha, beta, NotationUtils.toNotation(move), eval);

             if (isTimeoutExceeded()) {
                 return 0;
             }

             if (eval >= beta) {
                 // This is a beta cut-off, meaning the move is too good - the opponent won't let us get here as they
                 // already have other options which will prevent us from reaching this position.
                 transpositionTable.put(NodeType.LOWER_BOUND, plyRemaining, move, beta);

                 if (!isCapture && plyFromRoot <= MoveOrderer.MAX_KILLER_MOVE_PLY_DEPTH) {
                     // Non-captures which cause a beta cut-off are 'killer moves', and are stored so that in our move
                     // ordering we can prioritise examining them early, on the basis that they are likely to be similarly
                     // effective in sibling nodes.
                     moveOrderer.addKillerMove(plyFromRoot, move);
                     statistics.incrementKillers();
                 }
//                 log.trace("{} {} ({}, {}) Pruning {} as eval {} >= beta {}",
//                         board.isWhiteToMove() ? "WHITE" : "BLACK", NotationUtils.toNotation(board.getMoveHistory()), alpha, beta, NotationUtils.toNotation(move), eval, beta);
                 statistics.incrementNodesSearched();
                 statistics.incrementCutoffs();
                 return beta;
             }

             if (eval > alpha) {
                 // We have found a new best move
                 bestMoveInThisPosition = move;
//                 log.trace("{} {} ({}, {}) New best move {} as eval {} > alpha {}",
//                         board.isWhiteToMove() ? "WHITE" : "BLACK", NotationUtils.toNotation(board.getMoveHistory()), alpha, beta, NotationUtils.toNotation(move), eval, alpha);
                 alpha = eval;
                 if (plyFromRoot == 0) {
                     bestMoveCurrentDepth = move;
                     bestEvalCurrentDepth = eval;
                     hasSearchedAtLeastOneMove = true;
                 }
             }

         }

         NodeType transpositionType = alpha <= originalAlpha ? NodeType.UPPER_BOUND : NodeType.EXACT;
         transpositionTable.put(transpositionType, plyRemaining, bestMoveInThisPosition, alpha);
         statistics.incrementNodesSearched();


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
        // In the case where there are only 'bad' captures available, just return the static evaluation of the board,
        // since the player is not forced to capture and may have good non-capture moves available.
        int eval = evaluator.evaluate(board);
        alpha = Math.max(alpha, eval);
        if (eval >= beta) {
            statistics.incrementNodesSearched();
            statistics.incrementQuiescents();
            statistics.incrementCutoffs();
            return beta;
        }

        // Generate only legal captures
        Move[] moves = moveGenerator.generateLegalMoves(board, true);

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, false, 0);

        for (Move move : orderedMoves) {

            int seeEval = see.evaluate(board, move);
            if ((depth <= 4 && seeEval < 0) || (depth > 4 && seeEval <= 0)) {
                // Up to depth 4 in quiescence, only considers captures with SEE eval >= 0.
                // Past depth 4 in quiescence, only consider captures with SEE eval > 0.
                continue;
            }

            board.makeMove(move);
            eval = -quiescenceSearch(-beta, -alpha, depth + 1);
            board.unmakeMove();
        }
        if (eval >= beta) {
            statistics.incrementNodesSearched();
            statistics.incrementQuiescents();
            statistics.incrementCutoffs();
            return beta;
        }
        if (eval > alpha) {
            alpha = eval;
        }
        statistics.incrementNodesSearched();
        statistics.incrementQuiescents();

        return alpha;

    }

    @Override
    public void clearHistory() {
        moveOrderer.clear();
        transpositionTable.clear();
    }

    private boolean isCheckmateFoundAtCurrentDepth(int bestEval, int currentDepth) {
        return Math.abs(bestEval) >= CHECKMATE_EVAL - currentDepth;
    }

    private boolean isTimeoutExceeded() {
        return !Instant.now().isBefore(timeout);
    }

}
