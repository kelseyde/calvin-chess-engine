package com.kelseyde.calvin.search.iterative;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.evaluation.CombinedBoardEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.MoveOrderer;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.SearchStatistics;
import com.kelseyde.calvin.search.TimedSearch;
import com.kelseyde.calvin.search.transposition.NodeType;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class IterativeDeepeningSearch implements TimedSearch {

    private static final int MIN_EVAL = Integer.MIN_VALUE + 1;
    private static final int MAX_EVAL = Integer.MAX_VALUE - 1;
    private static final int IMMEDIATE_MATE_SCORE = -1000000;

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final ResultCalculator resultCalculator = new ResultCalculator();
    private final MoveOrderer moveOrderer = new MoveOrderer();
    private final BoardEvaluator boardEvaluator = new CombinedBoardEvaluator();
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
        int currentDepth = 0;
        bestMove = null;
        bestMoveCurrentDepth = null;
        statistics = new SearchStatistics();

        SearchResult result;

        while (!isTimeoutExceeded()) {

            System.out.println("At depth " + currentDepth);
            hasSearchedAtLeastOneMove = false;
            search(currentDepth, 0, MIN_EVAL, MAX_EVAL);

            if (isTimeoutExceeded()) {
                if (hasSearchedAtLeastOneMove) {
                    System.out.printf("Statistics -- nodes searched: %s, cutoffs: %s, transpositions: %s%n",
                            statistics.getNodesSearched(), statistics.getCutOffs(), statistics.getTranspositions());;
                    return new SearchResult(bestEvalCurrentDepth, bestMoveCurrentDepth);
                }
            } else {
                bestMove = bestMoveCurrentDepth;
                bestEval = bestEvalCurrentDepth;
            }

            currentDepth++;
        }

        System.out.printf("Statistics -- nodes searched: %s, cutoffs: %s, transpositions: %s%n",
                statistics.getNodesSearched(), statistics.getCutOffs(), statistics.getTranspositions());;
        return new SearchResult(bestEval, bestMove);

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth. Since this function is called
     * recursively until the depth limit is reached, 'depth' needs to be split into two parameters: ply remaining and
     * ply from root.
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
            // detect draw
            // detect previous mate
        }

        Move previousBestMove = plyFromRoot == 0 ? bestMove : null;

        // Handle possible transposition
//        TranspositionEntry transposition = transpositionTable.get();
//        if (transposition != null) {
//            if (transposition.getBestMove() != null) {
//                previousBestMove = transposition.getBestMove();
//            }
//            if (transposition.getDepth() >= plyRemaining) {
//                statistics.incrementTranspositions();
//                NodeType type = transposition.getType();
//                if ((type.equals(NodeType.EXACT))
//                        || (type.equals(NodeType.LOWER_BOUND) && transposition.getValue() <= alpha)
//                        || (type.equals(NodeType.UPPER_BOUND) && transposition.getValue() >= beta)) {
//                    if (plyFromRoot == 0) {
//                        bestMoveCurrentDepth = transposition.getBestMove();
//                        bestEvalCurrentDepth = transposition.getValue();
//                    }
//                    return transposition.getValue();
//                }
//            }
//        }

        Move[] legalMoves = moveGenerator.generateLegalMoves(board, false);
        GameResult gameResult = resultCalculator.calculateResult(board, legalMoves);

        // Handle terminal nodes, where search is ended either due to checkmate, draw, or reaching max depth.
        if (gameResult.isCheckmate()) {
            statistics.incrementNodesSearched();
            return IMMEDIATE_MATE_SCORE - plyFromRoot;
        }
        if (gameResult.isDraw()) {
            statistics.incrementNodesSearched();
            return bestEvalCurrentDepth = 0;
        }
        if (plyRemaining == 0) {
            // In the case that max depth is reached, return the static heuristic evaluation of the position.
            statistics.incrementNodesSearched();
            return quiescenceSearch(alpha, beta);
        }

        Move[] orderedMoves = moveOrderer.orderMoves(board, legalMoves, previousBestMove);

        Move bestMoveInThisPosition = null;
        int originalAlpha = alpha;

        for (Move move : orderedMoves) {

            // TODO search extensions
            // TODO late move reductions
            board.makeMove(move);
            int eval = -search(plyRemaining - 1, plyFromRoot + 1, -beta, -alpha);
            board.unmakeMove();

            if (isTimeoutExceeded()) {
                return 0;
            }

            if (eval >= beta) {
                // Move is too good, opponent won't let us get here.
                transpositionTable.put(NodeType.LOWER_BOUND, plyRemaining, move, beta);
                statistics.incrementCutoffs();
                return beta;
            }

            if (eval > alpha) {
                // We have found a new best move
                bestMoveInThisPosition = move;
                alpha = eval;
                if (plyFromRoot == 0)
                {
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
     * @see <a href="https://www.chessprogramming.org/Quiescence_Search">Chess Programming Wiki</a>
     */
    int quiescenceSearch(int alpha, int beta) {
        if (isTimeoutExceeded()) {
            return 0;
        }
        // In the case where there are only 'bad' captures available, just return the static evaluation of the board,
        // since the player is not forced to capture and may have good non-capture moves available.
        int eval = boardEvaluator.evaluate(board);
        if (eval >= beta) {
            return beta;
        }
        if (eval > alpha) {
            alpha = eval;
        }

        // Generate only legal captures
        Move[] moves = moveGenerator.generateLegalMoves(board, true);

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null);

        for (Move move : orderedMoves) {
            board.makeMove(move);
            eval = -quiescenceSearch(-beta, -alpha);
            board.unmakeMove();
        }
        if (eval >= beta) {
            statistics.incrementCutoffs();
            return beta;
        }
        if (eval > alpha) {
            alpha = eval;
        }
        statistics.incrementNodesSearched();

        return alpha;

    }

    private boolean isTimeoutExceeded() {
        return !Instant.now().isBefore(timeout);
    }

}
