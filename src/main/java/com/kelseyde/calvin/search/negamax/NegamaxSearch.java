package com.kelseyde.calvin.search.negamax;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.MoveOrdering;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.SearchStatistics;
import com.kelseyde.calvin.search.tt.NodeType;
import com.kelseyde.calvin.search.tt.TranspositionEntry;
import com.kelseyde.calvin.search.tt.TranspositionTable;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Slf4j
public class NegamaxSearch implements Search {

    private final Board board;

    private final List<BoardEvaluator> positionEvaluators = List.of(
            new MaterialEvaluator(),
            new PiecePlacementEvaluator()
    );

    private final MoveGenerator moveGenerator = new MoveGenerator();

    private final ResultCalculator resultEvaluator = new ResultCalculator();

    private final MoveOrdering moveOrdering = new MoveOrdering();

    private final TranspositionTable transpositionTable;

    private SearchStatistics statistics;

    public NegamaxSearch(Board board) {

        this.board = board;
        this.transpositionTable = new TranspositionTable(board);

    }

    public SearchResult search(int depth) {
        log.info("Starting negamax search");
        Instant start = Instant.now();
        statistics = new SearchStatistics();
        SearchResult result = negamax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Instant end = Instant.now();
        log.info("Engine eval: {}, thinking time: {} move: {}",
                result.eval() / 100, Duration.between(start, end), NotationUtils.toNotation(result.move()));
        log.info("Search statistics: {}", statistics);
        return result;
    }

    private SearchResult negamax(int depth, int alpha, int beta) {

        int originalAlpha = alpha;

        // Handle possible transposition
        TranspositionEntry ttEntry = transpositionTable.get(depth, alpha, beta);
        if (ttEntry != null && ttEntry.getDepth() >= depth) {
            statistics.incrementNodesSearched();
            statistics.incrementTranspositions();
            if (NodeType.EXACT.equals(ttEntry.getType())) {
                return new SearchResult(ttEntry.getValue(), ttEntry.getBestMove());
            }
            else if (NodeType.LOWER_BOUND.equals(ttEntry.getType())) {
                alpha = Math.max(alpha, ttEntry.getValue());
            }
            else if (NodeType.UPPER_BOUND.equals(ttEntry.getType())) {
                beta = Math.min(beta, ttEntry.getValue());
            }
            if (alpha >= beta) {
                return new SearchResult(ttEntry.getValue(), ttEntry.getBestMove());
            }
        }

        // TODO determine checkmate prior to search; then legal moves need not be generated for terminal nodes.
        Move[] legalMoves = moveGenerator.generateLegalMoves(board);
        GameResult gameResult = resultEvaluator.calculateResult(board, legalMoves);

        // Handle terminal nodes, where search is ended either due to checkmate, draw, or reaching max depth.
        if (gameResult.isCheckmate()) {
            statistics.incrementNodesSearched();
            int checkmateEval = -Integer.MAX_VALUE;
            return new SearchResult(checkmateEval, null);
        }
        if (gameResult.isDraw()) {
            statistics.incrementNodesSearched();
            int drawEval = 0;
            return new SearchResult(drawEval, null);
        }
        if (depth == 0) {
            // In the case that max depth is reached, return the static heuristic evaluation of the position.
            statistics.incrementNodesSearched();
            int finalEval = evaluate(board);
            return new SearchResult(finalEval, null);
        }

        Move[] orderedMoves = moveOrdering.orderMoves(board, legalMoves);

        int eval = Integer.MIN_VALUE + 1;
        Move bestMove = legalMoves[new Random().nextInt(legalMoves.length)];

        for (Move move : orderedMoves) {

            board.makeMove(move);
            SearchResult searchResult = negamax(depth - 1, -beta, -alpha);
            board.unmakeMove();
            statistics.incrementNodesSearched();

            eval = -searchResult.eval();
            if (eval > alpha) {
                bestMove = move;
                alpha = eval;
            }
            if (eval >= beta) {
                statistics.incrementCutoffs();
                break;
            }

        }

        NodeType type;
        if (eval <= originalAlpha) {
            type = NodeType.UPPER_BOUND;
        }
        else if (eval >= beta) {
            type = NodeType.LOWER_BOUND;
        }
        else {
            type = NodeType.EXACT;
        }
        transpositionTable.put(type, bestMove, depth, eval);

        return new SearchResult(eval, bestMove);

    }

    private int evaluate(Board board) {
        return positionEvaluators.stream()
                .map(evaluator -> evaluator.evaluate(board))
                .reduce(0, Integer::sum);
    }

}
