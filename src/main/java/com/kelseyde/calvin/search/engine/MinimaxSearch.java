package com.kelseyde.calvin.search.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.PositionEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.MoveOrdering;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.SearchStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinimaxSearch implements Search {

    private final List<PositionEvaluator> positionEvaluators;

    private MoveGenerator moveGenerator = new MoveGenerator();

    private ResultCalculator resultEvaluator = new ResultCalculator();

    private MoveOrdering moveOrdering = new MoveOrdering();

    private SearchStatistics statistics;

    @Override
    public SearchResult search(Board board, int depth) {
        Instant start = Instant.now();
        statistics = new SearchStatistics();
        boolean isMaximisingPlayer = board.isWhiteToMove();
        SearchResult result = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximisingPlayer);
        Instant end = Instant.now();
//        log.info("Minimax evaluation: {}, move: {}", result.eval(), NotationUtils.toNotation(result.move()));
//        log.info("Total search time: {}, Nodes searched: {}, Nodes pruned: {}, Transpositions: {}, Average search duration: {}",
//                Duration.between(start, end), statistics.getNodesSearched(), statistics.getNodesPruned(), statistics.getTranspositions(), statistics.getAverageSearchDurationMs());
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
        List<Move> legalMoves = new ArrayList<>(moveGenerator.generateLegalMoves(board));
        List<Move> orderedMoves = moveOrdering.orderMoves(board, legalMoves);
        GameResult currentResult = resultEvaluator.calculateResult(board, orderedMoves);
        if (currentResult.isCheckmate()) {
            int modifier = board.isWhiteToMove() ? -1 : 1;
            statistics.incrementNodesSearched();
            statistics.addSearchDuration(start, Instant.now());
            return new SearchResult(modifier * Integer.MAX_VALUE, null); // TODO correct?
        }
        if (currentResult.isDraw()) {
            statistics.incrementNodesSearched();
            statistics.addSearchDuration(start, Instant.now());
            return new SearchResult(0, null);
        }
        if (depth == 0) {
            int finalEval = evaluate(board);
            statistics.incrementNodesSearched();
            statistics.addSearchDuration(start, Instant.now());
            return new SearchResult(finalEval, null);
        }
        Move bestMove = orderedMoves.get(new Random().nextInt(orderedMoves.size()));

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
                    statistics.incrementNodesSearched();
                    statistics.incrementNodesPruned();
                    statistics.addSearchDuration(start, Instant.now());
                    break;
                }
            }
            statistics.incrementNodesSearched();
            statistics.addSearchDuration(start, Instant.now());
            return new SearchResult(maxEval, bestMove);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : orderedMoves) {
                board.makeMove(move);
                SearchResult result = minimax(board, depth - 1, alpha, beta, true);
                board.unmakeMove();
                if (result.eval() < minEval) {
                    minEval = result.eval();
                    bestMove = move;
                }
                beta = Math.min(beta, result.eval());
                if (beta <= alpha) {
                    statistics.incrementNodesSearched();
                    statistics.incrementNodesPruned();
                    statistics.addSearchDuration(start, Instant.now());
                    break;
                }
            }
            Instant end = Instant.now();
            statistics.incrementNodesSearched();
            statistics.addSearchDuration(start, end);
            return new SearchResult(minEval, bestMove);
        }


    }

    private int evaluate(Board board) {
        return positionEvaluators.stream()
                .map(evaluator -> evaluator.evaluate(board))
                .reduce(0, Integer::sum);
    }

}
