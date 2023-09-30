package com.kelseyde.calvin.search.negamax;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.evaluation.CombinedBoardEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.DepthSearch;
import com.kelseyde.calvin.search.MoveOrderer;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.SearchStatistics;
import com.kelseyde.calvin.search.transposition.NodeType;
import com.kelseyde.calvin.search.transposition.TranspositionEntry;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Data
public class NegamaxSearch implements DepthSearch {

    // + 1 because the minimum integer cannot be negated (as is requried in negamax), due to numeric overflow.
    private static final int MIN_EVAL = Integer.MIN_VALUE + 1;
    private static final int MAX_EVAL = Integer.MAX_VALUE - 1;

    private final Board board;

    private final BoardEvaluator evaluator = new CombinedBoardEvaluator();

    private final MoveGenerator moveGenerator = new MoveGenerator();

    private final ResultCalculator resultCalculator = new ResultCalculator();

    private final MoveOrderer moveOrderer = new MoveOrderer();

    private final TranspositionTable transpositionTable;

    private SearchStatistics statistics;

    private List<Move> currentLine = new ArrayList<>();

    public NegamaxSearch(Board board) {
        this.board = board;
        this.transpositionTable = new TranspositionTable(board);
    }

    public SearchResult search(int depth) {
        log.info("Starting negamax search");
        statistics = new SearchStatistics();
        SearchResult result = negamax(depth, MIN_EVAL, MAX_EVAL);
        log.info("Engine eval: {}, move: {}", result.eval() / 100f, NotationUtils.toNotation(result.move()));
        log.info("Search statistics: {}", statistics);
        return result;
    }

    private SearchResult negamax(int depth, int alpha, int beta) {

        int originalAlpha = alpha;

        // Handle possible transposition
        TranspositionEntry ttEntry = transpositionTable.get();
        if (ttEntry != null && ttEntry.getDepth() >= depth) {
            statistics.incrementNodesSearched();
            statistics.incrementTranspositions();
            if (NodeType.EXACT.equals(ttEntry.getType())) {
                return new SearchResult(ttEntry.getValue(), ttEntry.getBestMove());
            }
            else if (NodeType.LOWER_BOUND.equals(ttEntry.getType()) && ttEntry.getValue() <= alpha) {
                return new SearchResult(ttEntry.getValue(), ttEntry.getBestMove());
            }
            else if (NodeType.UPPER_BOUND.equals(ttEntry.getType()) && ttEntry.getValue() >= beta) {
                return new SearchResult(ttEntry.getValue(), ttEntry.getBestMove());
            }
        }

        // TODO determine checkmate prior to search; then legal moves need not be generated for terminal nodes.
        Move[] legalMoves = moveGenerator.generateLegalMoves(board, false);
        GameResult gameResult = resultCalculator.calculateResult(board, legalMoves);

        // Handle terminal nodes, where search is ended either due to checkmate, draw, or reaching max depth.
        if (gameResult.isCheckmate()) {
            statistics.incrementNodesSearched();
            return new SearchResult(MIN_EVAL, null);
        }
        if (gameResult.isDraw()) {
            statistics.incrementNodesSearched();
            return new SearchResult(0, null);
        }
        if (depth == 0) {
            // In the case that max depth is reached, return the static heuristic evaluation of the position.
            statistics.incrementNodesSearched();
            int finalEval = evaluator.evaluate(board);
            System.out.printf("Colour %s, %s, alpha: %s, beta: %s, FINAL eval: %s%n",
                    !board.isWhiteToMove() ? "Black" : "White", currentLine.stream().map(NotationUtils::toNotation).toList(), alpha, beta, finalEval);
            return new SearchResult(finalEval, null);
        }

        Move[] orderedMoves = moveOrderer.orderMoves(board, legalMoves, null, true, 0);

        int bestEval = MIN_EVAL;
        Move bestMove = legalMoves[new Random().nextInt(legalMoves.length)];

        for (Move move : orderedMoves) {

            board.makeMove(move);
            currentLine.add(move);
//            System.out.printf("depth %s alpha/beta BEFORE recursive call: %s / %s%n", depth, alpha, beta);
            SearchResult searchResult = negamax(depth - 1, -beta, -alpha);
//            System.out.printf("depth %s alpha/beta AFTER recursive call: %s / %s%n", depth, alpha, beta);
            board.unmakeMove();
            statistics.incrementNodesSearched();

            int eval = -searchResult.eval();
            System.out.printf("Colour %s, %s, alpha: %s, beta: %s, eval: %s%n",
                    !board.isWhiteToMove() ? "Black" : "White", currentLine.stream().map(NotationUtils::toNotation).toList(), alpha, beta, eval);
            currentLine.remove(move);
            if (eval > alpha) {
                System.out.printf("Colour %s choosing %s since eval %s > alpha %s%n", !board.isWhiteToMove() ? "Black" : "White", NotationUtils.toNotation(move), eval, alpha);
                bestEval = eval;
                bestMove = move;
                alpha = eval;
            }
            if (alpha >= beta) {
                System.out.printf("Colour %s pruning %s since alpha %s >= beta %s%n",!board.isWhiteToMove() ? "Black" : "White", NotationUtils.toNotation(move), alpha, beta);
                statistics.incrementCutoffs();
                break;
            }

        }

        NodeType type;
        if (bestEval <= originalAlpha) {
            type = NodeType.UPPER_BOUND;
        }
        else if (bestEval >= beta) {
            type = NodeType.LOWER_BOUND;
        }
        else {
            type = NodeType.EXACT;
        }
        transpositionTable.put(type, depth, bestMove, bestEval);

        System.out.printf("Colour %s, %s, alpha: %s, beta: %s, choosing best move %s, eval %s END %n",
                !board.isWhiteToMove() ? "Black" : "White", currentLine.stream().map(NotationUtils::toNotation).toList(), alpha, beta, NotationUtils.toNotation(bestMove), bestEval);
        return new SearchResult(bestEval, bestMove);

    }


}
