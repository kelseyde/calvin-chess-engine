package com.kelseyde.calvin.movegeneration.quiescent;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.board.bitboard.Bitwise;
import com.kelseyde.calvin.movegeneration.MoveGeneration;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Not;

import java.util.List;

public class QuiescentTest {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Test
    public void testGenerateKnightMoves() {

        // Includes a pinned knight and a friendly blocker
        String fen = "1k1r1N1n/pppb3p/P2b4/4N3/1N6/6K1/8/8 w - - 0 1";
        Board board = FEN.toBoard(fen);

        List<Move> moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_ONLY);

        List<Move> expected = List.of(
                NotationUtils.fromNotation("a6", "b7"),
                NotationUtils.fromNotation("f8", "d7"),
                NotationUtils.fromNotation("f8", "h7")
        );
        assertMoves(expected, moves);

        moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_AND_CHECKS);

        expected = List.of(
                NotationUtils.fromNotation("a6", "b7"),
                NotationUtils.fromNotation("f8", "d7"),
                NotationUtils.fromNotation("f8", "h7"),
                NotationUtils.fromNotation("b4", "c6")
        );
        assertMoves(expected, moves);

    }

    @Test
    public void testGenerateBishopMoves() {

        String fen = "6k1/4bpp1/7p/8/p6b/1B4B1/2B2K2/8 w - - 0 1";
        Board board = FEN.toBoard(fen);

        List<Move> moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_ONLY);

        List<Move> expected = List.of(
                NotationUtils.fromNotation("g3", "h4"),
                NotationUtils.fromNotation("b3", "f7"),
                NotationUtils.fromNotation("b3", "a4")
        );
        assertMoves(expected, moves);

        moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_AND_CHECKS);
        expected = List.of(
                NotationUtils.fromNotation("g3", "h4"),
                NotationUtils.fromNotation("b3", "f7"),
                NotationUtils.fromNotation("b3", "a4"),
                NotationUtils.fromNotation("c2", "h7")
        );
        assertMoves(expected, moves);

    }

    @Test
    public void testGenerateQueenMoves() {

        String fen = "8/8/1KQr4/2Q5/6p1/6pk/2Q4N/4r3 w - - 0 1";
        Board board = FEN.toBoard(fen);

        List<Move> moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_ONLY);
        List<Move> expected = List.of(
                NotationUtils.fromNotation("h2", "g4"),
                NotationUtils.fromNotation("c6", "d6"),
                NotationUtils.fromNotation("c5", "d6")
        );
        assertMoves(expected, moves);

        moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_AND_CHECKS);
        for (Move move : moves) {
            System.out.println(NotationUtils.toNotation(move));
        }
        expected = List.of(
                NotationUtils.fromNotation("h2", "g4"),
                NotationUtils.fromNotation("c6", "d6"),
                NotationUtils.fromNotation("c5", "d6"),
                NotationUtils.fromNotation("c2", "g2"),
                NotationUtils.fromNotation("c2", "h7"),
                NotationUtils.fromNotation("c5", "h5")
        );
        assertMoves(expected, moves);

    }

    @Test
    public void testKiwipete() {

        String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";

        Board board = FEN.toBoard(fen);

        List<Move> moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_ONLY);
        List<Move> expected = List.of(
                NotationUtils.fromNotation("e2", "a6"),
                NotationUtils.fromNotation("f3", "f6"),
                NotationUtils.fromNotation("d5", "e6"),
                NotationUtils.fromNotation("f3", "h3"),
                NotationUtils.fromNotation("g2", "h3"),
                NotationUtils.fromNotation("e5", "g6"),
                NotationUtils.fromNotation("e5", "f7"),
                NotationUtils.fromNotation("e5", "d7")
        );
        assertMoves(expected, moves);

        moves = moveGenerator.generateMoves(board, MoveGeneration.MoveFilter.CAPTURES_AND_CHECKS);
        expected = List.of(
                NotationUtils.fromNotation("e2", "a6"),
                NotationUtils.fromNotation("f3", "f6"),
                NotationUtils.fromNotation("d5", "e6"),
                NotationUtils.fromNotation("f3", "h3"),
                NotationUtils.fromNotation("g2", "h3"),
                NotationUtils.fromNotation("e5", "g6"),
                NotationUtils.fromNotation("e5", "f7"),
                NotationUtils.fromNotation("e5", "d7")
        );
        assertMoves(expected, moves);
    }

    private void assertMoves(List<Move> expected, List<Move> actual) {
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(expected.stream().allMatch(move -> actual.stream().anyMatch(move::matches)));
    }

}
