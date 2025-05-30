package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.Bench;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PseudoLegalTest {

    private final MoveGenerator movegen = new MoveGenerator();

    @Test
    public void testIsLegalDebug() {

		// Test king side castling
        Board board = Board.from("r3k2r/p1p1qpb1/bn1ppnpB/3PN3/1p2P3/1PN2Q1p/P1P1BPPP/R3K2R b KQkq - 0 2");
        Move move = Move.fromUCI("e8g8", Move.CASTLE_FLAG);
        Assertions.assertTrue(movegen.isPseudoLegal(board, move));

		// Test queen side castling
        board = Board.from("r3k2r/2pb1ppp/2pp1q2/1Q6/pnP1B3/1P2P3/P2N1PPP/R3K2R b KQkq - 1 2");
		move = Move.fromUCI("e8c8", Move.CASTLE_FLAG);
		Assertions.assertTrue(movegen.isPseudoLegal(board, move));
    }

    @Test
    public void testPawnDoublePushNotOnStartingRank() {

        Board board = Board.from("5bk1/R5p1/8/1p1p3p/1P3B1P/P1r3P1/6K1/8 b - - 1 1");

        Move move1 = Move.fromUCI("d5d3");
        Assertions.assertFalse(movegen.isPseudoLegal(board, move1));

        Move move2 = Move.fromUCI("d5d3", Move.PAWN_DOUBLE_MOVE_FLAG);
        Assertions.assertFalse(movegen.isPseudoLegal(board, move2));

    }

    @Test
    public void testPawnDoublePushToOccupiedSquare() {

        Board board = Board.from("rnbqkbnr/pppp1ppp/8/8/4p3/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        Move move1 = Move.fromUCI("e2e4", Move.PAWN_DOUBLE_MOVE_FLAG);
        Assertions.assertFalse(movegen.isPseudoLegal(board, move1));

        Move move2 = Move.fromUCI("e2e4");
        Assertions.assertFalse(movegen.isPseudoLegal(board, move2));

    }

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
                Assertions.fail(FEN.fromBoard(board) + " " + Move.toUCI(move));
            }
            board.makeMove(move);
            totalMoveCount += pseudoLegalPerft(board, depth - 1);
            board.unmakeMove();
        }
        return totalMoveCount;
    }

}
