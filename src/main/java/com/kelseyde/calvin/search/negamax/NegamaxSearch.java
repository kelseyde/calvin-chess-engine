package com.kelseyde.calvin.search.negamax;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class NegamaxSearch implements Search {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    private final ResultCalculator resultCalculator = new ResultCalculator();

    private final List<PositionEvaluator> positionEvaluators;

    public SearchResult search(Board board, int depth) {
        log.info("Starting negamax search");
        SearchResult result = negamax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        log.info("Negamax result: {}", result);
        return result;
    }

    private SearchResult negamax(Board board, int depth, int alpha, int beta) {

        log.info("Negamax depth {}, isWhite {}, alpha {}, beta {}", depth, board.isWhiteToMove(), alpha, beta);
        int modifier = board.isWhiteToMove() ? 1 : -1;
        List<Move> legalMoves = new ArrayList<>(moveGenerator.generateLegalMoves(board));
        GameResult currentResult = resultCalculator.calculateResult(board, legalMoves);
        if (currentResult.isCheckmate()) {
            return new SearchResult(modifier * Integer.MAX_VALUE, null);
        }
        if (currentResult.isDraw()) {
            return new SearchResult(0, null);
        }
        if (depth == 0) {
            int evaluate = evaluate(board);
            log.info("Returning terminal result {}", evaluate);
            return new SearchResult(evaluate, null);
        }

        int eval = Integer.MIN_VALUE + 1;
        Move bestMove = legalMoves.get(new Random().nextInt(legalMoves.size() - 1));

        for (Move move : legalMoves) {

            log.info("Considering move {}", NotationUtils.toNotation(move));
            board.makeMove(move);
            SearchResult result = negamax(board, depth - 1, -beta, -alpha);
            board.unmakeMove();

            if (result.eval() > eval) {
                if (result.move() != null) {
                    log.info("New winner: {} {}", NotationUtils.toNotation(result.move()), result.eval());
                    bestMove = result.move();
                }
                eval = result.eval();
            }

            if (eval > alpha) {
                alpha = eval;
            }

            if (eval >= beta) {
                break;
            }

        }
        return new SearchResult(eval, bestMove);

    }

    private int evaluate(Board board) {
        return positionEvaluators.stream()
                .map(evaluator -> evaluator.evaluate(board))
                .reduce(0, Integer::sum);
    }

}
