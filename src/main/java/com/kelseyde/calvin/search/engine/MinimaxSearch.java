package com.kelseyde.calvin.search.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.PositionEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public SearchResult search(Board board, int depth) {
        boolean isMaximisingPlayer = board.isWhiteToMove();
        SearchResult result = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximisingPlayer);
        log.info("Minimax evaluation: {}, move: {}", result.eval(), NotationUtils.toNotation(result.move()));
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
        List<Move> legalMoves = new ArrayList<>(moveGenerator.generateLegalMoves(board));
        GameResult currentResult = resultEvaluator.calculateResult(board, legalMoves);
        if (currentResult.isCheckmate()) {
            int modifier = board.isWhiteToMove() ? -1 : 1;
            return new SearchResult(modifier * Integer.MAX_VALUE, null); // TODO correct?
        }
        if (currentResult.isDraw()) {
            return new SearchResult(0, null);
        }
        if (depth == 0) {
            int finalEval = evaluate(board);
            return new SearchResult(finalEval, null);
        }
        Move bestMove = legalMoves.get(new Random().nextInt(legalMoves.size()));

        if (isMaximisingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move legalMove : legalMoves) {
                board.makeMove(legalMove);
                SearchResult result = minimax(board, depth - 1, alpha, beta, false);
                board.unmakeMove();
                if (result.eval() > maxEval) {
                    maxEval = result.eval();
                    bestMove = legalMove;
                }
                alpha = Math.max(alpha, result.eval());
                if (beta <= alpha) {
                    break;
                }
            }
            return new SearchResult(maxEval, bestMove);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move legalMove : legalMoves) {
                board.makeMove(legalMove);
                SearchResult result = minimax(board, depth - 1, alpha, beta, true);
                board.unmakeMove();
                if (result.eval() < minEval) {
                    minEval = result.eval();
                    bestMove = legalMove;
                }
                beta = Math.min(beta, result.eval());
                if (beta <= alpha) {
                    break;
                }
            }
            return new SearchResult(minEval, bestMove);
        }


    }

    private int evaluate(Board board) {
        return positionEvaluators.stream()
                .map(evaluator -> evaluator.evaluate(board))
                .reduce(0, Integer::sum);
    }

}
