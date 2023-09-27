package com.kelseyde.calvin.search.minimax;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.DepthSearch;
import com.kelseyde.calvin.search.MoveOrderer;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.SearchStatistics;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Slf4j
public class MinimaxSearch implements DepthSearch {

    private final List<BoardEvaluator> positionEvaluators = List.of(
            new MaterialEvaluator(),
            new PiecePlacementEvaluator()
    );

    private final Board board;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    private final ResultCalculator resultEvaluator = new ResultCalculator();

    private final MoveOrderer moveOrdering = new MoveOrderer();

    private TranspositionTable transpositionTable;

    private SearchStatistics statistics;

    public MinimaxSearch(Board board) {

        this.board = board;
        this.transpositionTable = new TranspositionTable(board);

    }

    @Override
    public SearchResult search(int depth) {
        Instant start = Instant.now();
        statistics = new SearchStatistics();
        boolean isMaximisingPlayer = board.isWhiteToMove();
        SearchResult result = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximisingPlayer);
        Instant end = Instant.now();
        log.info("Engine eval: {}, thinking time: {} move: {}",
                result.eval() / 100, Duration.between(start, end), NotationUtils.toNotation(result.move()));
        return result;
    }

    /**
     * @param board The current game state.
     * @param depth The search depth for this iteration of the algorithm.
     * @param alpha The best possible score for the maximising player. Used in alpha-beta pruning
     * @param beta The best possible score for the minimising player. Used in alpha-beta pruning
     * @param isMaximisingPlayer Whether the current player is the maximising or minimising player.
     * @return {@link SearchResult} containing the position eval, and the best move which reaches that eval.
     */
    public SearchResult minimax(Board board, int depth, int alpha, int beta, boolean isMaximisingPlayer) {
        Instant start = Instant.now();
        Move[] legalMoves = moveGenerator.generateLegalMoves(board);
        Move[] orderedMoves = moveOrdering.orderMoves(board, legalMoves, null);
        GameResult currentResult = resultEvaluator.calculateResult(board, orderedMoves);
        if (currentResult.isCheckmate()) {
            int modifier = board.isWhiteToMove() ? -1 : 1;
            statistics.incrementNodesSearched();
            return new SearchResult(modifier * Integer.MAX_VALUE, null); // TODO correct?
        }
        if (currentResult.isDraw()) {
            statistics.incrementNodesSearched();
            return new SearchResult(0, null);
        }
        if (depth == 0) {
            int finalEval = evaluate(board);
            statistics.incrementNodesSearched();
            return new SearchResult(finalEval, null);
        }
        Move bestMove = orderedMoves[new Random().nextInt(orderedMoves.length)];

        if (isMaximisingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : orderedMoves) {
                board.makeMove(move);
                SearchResult result = minimax(board, depth - 1, alpha, beta, false);
                board.unmakeMove();
                if (result.eval() > maxEval) {
                    maxEval = result.eval();
                    bestMove = move;
                }
                alpha = Math.max(alpha, result.eval());
                if (beta <= alpha) {
                    // A 'beta cut-off', where the minimizing (opponent) player's worst-possible score exceeds the maximising
                    // player's worst-possible score. This node can be pruned and no further child nodes need be searched, as
                    // the maximising player knows there are better alternatives elsewhere in the tree.
                    statistics.incrementNodesSearched();
                    statistics.incrementCutoffs();
                    break;
                }
            }
            statistics.incrementNodesSearched();
            return new SearchResult(maxEval, bestMove);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : orderedMoves) {
                board.makeMove(move);
                SearchResult result = minimax(board, depth - 1, alpha, beta, true);
                board.unmakeMove();
                if (result.eval() < minEval) {
                    minEval = result.eval();
                    log.info("New winner at depth {}: {} (previous winner {})", depth, NotationUtils.toNotation(move), NotationUtils.toNotation(bestMove));
                    bestMove = move;
                }
                beta = Math.min(beta, result.eval());
                if (beta <= alpha) {
                    // A 'beta cut-off', where the minimizing (opponent) player's worst-possible score exceeds the maximising
                    // player's worst-possible score. This node can be pruned and no further child nodes need be searched, as
                    // the maximising player knows there are better alternatives elsewhere in the tree.
                    statistics.incrementNodesSearched();
                    statistics.incrementCutoffs();
                    break;
                }
            }
            statistics.incrementNodesSearched();
            return new SearchResult(minEval, bestMove);
        }


    }

    private int evaluate(Board board) {
        return positionEvaluators.stream()
                .map(evaluator -> evaluator.evaluate(board))
                .reduce(0, Integer::sum);
    }

}
