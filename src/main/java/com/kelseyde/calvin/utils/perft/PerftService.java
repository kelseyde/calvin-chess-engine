package com.kelseyde.calvin.utils.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.picker.MovePicker;
import com.kelseyde.calvin.uci.UCI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PerftService {

    private final MoveGenerator movegen = new MoveGenerator();

    public long nodesSearched = 0;
    private Map<Move, Long> nodesPerMove;
    SearchStack ss = new SearchStack();
    SearchHistory history = new SearchHistory();

    public long perft(Board board, int depth) {
        nodesSearched = 0;
        nodesPerMove = new HashMap<>();

        long totalNodes = perft(board, depth, depth);

        nodesPerMove.forEach((move, nodes) ->
                UCI.write(String.format("%s: %s", Move.toUCI(move), nodes)));
        UCI.write(String.format("Nodes searched: %s", totalNodes));

        return totalNodes;
    }

    public long perft(Board board, int depth, int originalDepth) {
        nodesSearched++;

        if (depth == 1) {
            return  movegen.generateMoves(board).size();
        }

        MovePicker movePicker = new MovePicker(movegen, ss, history, board, originalDepth - depth, null, false);
        long totalMoveCount = 0;
        while (true) {
            Move move = movePicker.pickNextMove();
            if (move == null) {
                break;
            }
            board.makeMove(move);
            totalMoveCount += perft(board, depth - 1, originalDepth);
            board.unmakeMove();
        }
//        for (Move move : moves) {
//            board.makeMove(move);
//            totalMoveCount += perft(board, depth - 1, originalDepth);
//            if (depth == originalDepth) {
//                nodesPerMove.put(move, totalMoveCount);
//            }
//            board.unmakeMove();
//        }
        return totalMoveCount;
    }

    private void log(Board board, int depth, Set<Move> moves) {
        List<String> moveHistory = board.getMoves().stream().map(Move::toUCI).toList();
        List<String> legalMoves = moves.stream().map(Move::toUCI).toList();
        System.out.printf("perft(%s) -- %s: %s -- %s%n", depth, moveHistory, legalMoves.size(), legalMoves);
    }

    private void log(Board board, int depth, int count) {
        List<String> moveHistory = board.getMoves().stream().map(Move::toUCI).toList();
        System.out.printf("perft(%s) -- %s: %s %n", depth, moveHistory, count);
    }

}
