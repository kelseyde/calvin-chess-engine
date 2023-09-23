package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.PositionEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
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
public class MinimaxSearch implements Engine {

    private final List<PositionEvaluator> positionEvaluators;

    private MoveGenerator legalMoveService = new MoveGenerator();

    @Override
    public Move selectMove(Board board) {
        MinimaxResult result = minimax(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        log.info("Minimax evaluation: {}, move: {}", result.eval(), NotationUtils.toNotation(result.move()));
        return result.move();
    }

    /**
     * @param board The current game state.
     * @param depth The search depth for this iteration of the algorithm.
     * @param alpha The best possible score for the maximising player. Used in alpha-beta pruning
     * @param beta The best possible score for the minimising player. Used in alpha-beta pruning
     * @param isMaximisingPlayer Whether the current player is the maximising or minimising player.
     * @return {@link MinimaxResult} containing the position eval, and the best move which reaches that eval.
     */
    public MinimaxResult minimax(Board board, int depth, int alpha, int beta, boolean isMaximisingPlayer) {
        // TODO make work
//        if (depth == 0 || game.isCheckmate() || game.isDraw()) {
//            int finalEval = evaluate(game);
//            return new MinimaxResult(finalEval, null);
//        }
        List<Move> legalMoves = new ArrayList<>(legalMoveService.generateLegalMoves(board));
        Move bestMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
        log.info("current best move {}, legal moves: {}", NotationUtils.toNotation(bestMove), legalMoves.stream().map(NotationUtils::toNotation).toList());
        if (isMaximisingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move legalMove : legalMoves) {
                board.makeMove(legalMove);
                MinimaxResult result = minimax(board, depth - 1, alpha, beta, false);
                board.unmakeMove();
                if (result.eval > maxEval) {
                    log.info("result eval {} > max eval {}, move {}", result.eval, maxEval, NotationUtils.toNotation(bestMove));
                    maxEval = result.eval;
                    log.info("max eval: {}", maxEval);
                    bestMove = legalMove;
                    alpha = Math.max(alpha, maxEval);
                    if (beta <= alpha) {
                        log.info("pruning for maximising player");
                        break;
                    }
                }
            }
            return new MinimaxResult(maxEval, bestMove);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move legalMove : legalMoves) {
                board.makeMove(legalMove);
                MinimaxResult result = minimax(board, depth - 1,alpha, beta, true);
                board.unmakeMove();
                if (result.eval < minEval) {
                    minEval = result.eval;
                    bestMove = legalMove;
                    log.info("result eval {} < min eval {}, move {}", result.eval, minEval, NotationUtils.toNotation(bestMove));
                    beta = Math.min(beta, minEval);
                    if (beta <= alpha) {
                        log.info("pruning for minimising player");
                        break;
                    }
                }
            }
            return new MinimaxResult(minEval, bestMove);
        }


    }

    private int evaluate(Board board) {
        return positionEvaluators.stream()
                .map(evaluator -> evaluator.evaluate(board))
                .reduce(0, Integer::sum);
    }


    public record MinimaxResult(int eval, Move move) { }

}
