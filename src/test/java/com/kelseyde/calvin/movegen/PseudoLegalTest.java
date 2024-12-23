package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.Bench;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PseudoLegalTest {

    private final MoveGenerator movegen = new MoveGenerator();

    @Test
    public void testIsLegalDebug() {

        Board board = Board.from("r3k2r/p1p1qpb1/bn1ppnpB/3PN3/1p2P3/1PN2Q1p/P1P1BPPP/R3K2R b KQkq - 0 2");
        Move move = Move.fromUCI("e8g8", Move.CASTLE_FLAG);
        Assertions.assertTrue(movegen.isPseudoLegal(board, move));

    }

    // Disabled as it takes a long time - used for debugging
    @Test
    @Disabled
    public void testBenchSuite() {
        Bench.FENS.stream()
                .map(Board::from)
                .forEach(board -> pseudoLegalPerft(board, 5));
    }


    public long pseudoLegalPerft(Board board, int depth) {
        List<Move> moves = movegen.generateMoves(board);
        if (depth == 1) {
            return moves.size();
        }
        long totalMoveCount = 0;
        for (Move move : moves) {
            if (!movegen.isPseudoLegal(board, move)) {
                Assertions.fail(FEN.toFEN(board) + " " + Move.toUCI(move));
            }
            board.makeMove(move);
            totalMoveCount += pseudoLegalPerft(board, depth - 1);
            board.unmakeMove();
        }
        return totalMoveCount;
    }

}
