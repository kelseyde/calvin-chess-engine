package com.kelseyde.calvin.service.engine.minimax;

import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.engine.evaluator.PositionEvaluator;
import com.kelseyde.calvin.service.engine.evaluator.SimplePositionEvaluator;
import com.kelseyde.calvin.service.game.LegalMoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinimaxEvaluator {

    private final LegalMoveGenerator legalMoveService = new LegalMoveGenerator();

    private final PositionEvaluator positionEvaluator = new SimplePositionEvaluator();

    /**
     *
     * @param game The current game state.
     * @param depth The search depth for this iteration of the algorithm.
     * @param alpha The best possible score for the maximising player. Used in alpha-beta pruning
     * @param beta The best possible score for the minimising player. Used in alpha-beta pruning
     * @param isMaximisingPlayer Whether the current player is the maximising or minimising player.
     * @return {@link MinimaxResult} containing the position eval, and the best move which reaches that eval.
     */
    public MinimaxResult minimax(Game game, int depth, int alpha, int beta, boolean isMaximisingPlayer) {
        if (depth == 0 || game.isCheckmate() || game.isDraw()) {
            int finalEval = positionEvaluator.evaluate(game);
            return new MinimaxResult(finalEval, null);
        }
        List<Move> legalMoves = new ArrayList<>(legalMoveService.generateLegalMoves(game.getBoard()));
        Move bestMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
        if (isMaximisingPlayer) {
            int maxEval = -100000;
            for (Move legalMove : legalMoves) {
                game.makeMove(legalMove);
                MinimaxResult result = minimax(game, depth - 1, alpha, beta, false);
                game.unmakeMove();
                if (result.eval > maxEval) {
                    maxEval = result.eval;
                    bestMove = result.move;
                    alpha = Math.max(alpha, maxEval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return new MinimaxResult(maxEval, bestMove);
        } else {
            int minEval = 100000;
            for (Move legalMove : legalMoves) {
                game.makeMove(legalMove);
                MinimaxResult result = minimax(game, depth - 1,alpha, beta, true);
                game.unmakeMove();
                if (result.eval < minEval) {
                    minEval = result.eval;
                    bestMove = result.move;
                    beta = Math.min(beta, minEval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return new MinimaxResult(minEval, bestMove);
        }


    }


    public record MinimaxResult(int eval, Move move) { }

}
