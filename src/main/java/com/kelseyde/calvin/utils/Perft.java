package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Key;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.uci.UCI;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Perft, ('Performance Test') is a debugging function to walk the move generation tree of strictly legal moves to count
 * all the leaf nodes of a certain depth, which is compared to predetermined values and used to isolate bugs.
 */
public class Perft {

    private final MoveGenerator movegen = new MoveGenerator();

    public long nodesSearched = 0;
    private Map<Move, Long> nodesPerMove;

    public long perft(Board board, int depth) {
        nodesSearched = 0;
        nodesPerMove = new HashMap<>();

        long totalNodes = perft(board, depth, depth);

        nodesPerMove.entrySet().stream()
                .sorted(Comparator.comparing(entry -> Move.toUCI(entry.getKey())))
                .forEach(entry -> UCI.write(String.format("%s: %s", Move.toUCI(entry.getKey()), entry.getValue())));
        UCI.write(String.format("Nodes searched: %s", totalNodes));

        return totalNodes;
    }

    public long perft(Board board, int depth, int originalDepth) {
        nodesSearched++;
        List<Move> moves = movegen.generateMoves(board);
        if (depth == 1) {
            return moves.size();
        }
        long totalMoveCount = 0;
        for (Move move : moves) {
            board.makeMove(move);
            long moveCount = perft(board, depth - 1, originalDepth);
            if (depth == originalDepth) {
                nodesPerMove.put(move, moveCount);
            }
            totalMoveCount += moveCount;
            board.unmakeMove();
        }
        return totalMoveCount;
    }

}
