package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MoveOrdererTest {

    private MoveOrderer moveOrderer;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @BeforeEach
    public void beforeEach() {
        moveOrderer = new MoveOrderer();
    }

    @Test
    public void testMvvLvaScores() {

        int[][] table = MoveOrderer.MVV_LVA_TABLE;

        Assertions.assertEquals(10, table[PieceType.PAWN.getIndex()][PieceType.KING.getIndex()]);
        Assertions.assertEquals(11, table[PieceType.PAWN.getIndex()][PieceType.QUEEN.getIndex()]);
        Assertions.assertEquals(12, table[PieceType.PAWN.getIndex()][PieceType.ROOK.getIndex()]);
        Assertions.assertEquals(13, table[PieceType.PAWN.getIndex()][PieceType.BISHOP.getIndex()]);
        Assertions.assertEquals(14, table[PieceType.PAWN.getIndex()][PieceType.KNIGHT.getIndex()]);
        Assertions.assertEquals(15, table[PieceType.PAWN.getIndex()][PieceType.PAWN.getIndex()]);
        Assertions.assertEquals(20, table[PieceType.KNIGHT.getIndex()][PieceType.KING.getIndex()]);
        Assertions.assertEquals(21, table[PieceType.KNIGHT.getIndex()][PieceType.QUEEN.getIndex()]);
        Assertions.assertEquals(22, table[PieceType.KNIGHT.getIndex()][PieceType.ROOK.getIndex()]);
        Assertions.assertEquals(23, table[PieceType.KNIGHT.getIndex()][PieceType.BISHOP.getIndex()]);
        Assertions.assertEquals(24, table[PieceType.KNIGHT.getIndex()][PieceType.KNIGHT.getIndex()]);
        Assertions.assertEquals(25, table[PieceType.KNIGHT.getIndex()][PieceType.PAWN.getIndex()]);
        Assertions.assertEquals(30, table[PieceType.BISHOP.getIndex()][PieceType.KING.getIndex()]);
        Assertions.assertEquals(31, table[PieceType.BISHOP.getIndex()][PieceType.QUEEN.getIndex()]);
        Assertions.assertEquals(32, table[PieceType.BISHOP.getIndex()][PieceType.ROOK.getIndex()]);
        Assertions.assertEquals(33, table[PieceType.BISHOP.getIndex()][PieceType.BISHOP.getIndex()]);
        Assertions.assertEquals(34, table[PieceType.BISHOP.getIndex()][PieceType.KNIGHT.getIndex()]);
        Assertions.assertEquals(35, table[PieceType.BISHOP.getIndex()][PieceType.PAWN.getIndex()]);
        Assertions.assertEquals(40, table[PieceType.ROOK.getIndex()][PieceType.KING.getIndex()]);
        Assertions.assertEquals(41, table[PieceType.ROOK.getIndex()][PieceType.QUEEN.getIndex()]);
        Assertions.assertEquals(42, table[PieceType.ROOK.getIndex()][PieceType.ROOK.getIndex()]);
        Assertions.assertEquals(43, table[PieceType.ROOK.getIndex()][PieceType.BISHOP.getIndex()]);
        Assertions.assertEquals(44, table[PieceType.ROOK.getIndex()][PieceType.KNIGHT.getIndex()]);
        Assertions.assertEquals(45, table[PieceType.ROOK.getIndex()][PieceType.PAWN.getIndex()]);
        Assertions.assertEquals(50, table[PieceType.QUEEN.getIndex()][PieceType.KING.getIndex()]);
        Assertions.assertEquals(51, table[PieceType.QUEEN.getIndex()][PieceType.QUEEN.getIndex()]);
        Assertions.assertEquals(52, table[PieceType.QUEEN.getIndex()][PieceType.ROOK.getIndex()]);
        Assertions.assertEquals(53, table[PieceType.QUEEN.getIndex()][PieceType.BISHOP.getIndex()]);
        Assertions.assertEquals(54, table[PieceType.QUEEN.getIndex()][PieceType.KNIGHT.getIndex()]);
        Assertions.assertEquals(55, table[PieceType.QUEEN.getIndex()][PieceType.PAWN.getIndex()]);

    }

    @Test
    public void testLosingCaptureBias() {

        String fen = "7k/8/8/8/4r3/3P1Q2/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("h1", "h2"),
                NotationUtils.fromNotation("h1", "g1"),
                NotationUtils.fromNotation("h1", "g2"),
                NotationUtils.fromNotation("f3", "e4")
//                NotationUtils.fromNotation("d3", "e4", PieceType.PAWN),
        ));

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("f3", "e4")));

    }

    @Test
    public void testKillerMoveBias() {

        String fen = "k7/8/8/6q1/8/5PP1/6BP/6K1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("g1", "h1"),
                NotationUtils.fromNotation("g1", "f1"),
                NotationUtils.fromNotation("f3", "f4"),
                NotationUtils.fromNotation("h2", "h4")));

        Move killerMove = new Move(21, 29);
        moveOrderer.addKillerMove(1, killerMove);

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("f3", "f4")));

    }

    @Test
    public void testIgnoreKillersAtDifferentPly() {

        String fen = "1k6/8/8/6q1/8/5PP1/6BP/6K1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("g1", "h1"),
                NotationUtils.fromNotation("g1", "f1"),
                NotationUtils.fromNotation("f3", "f4"),
                NotationUtils.fromNotation("h2", "h4")
                ));

        Move killerMove = new Move(21, 29);
        moveOrderer.addKillerMove(2, killerMove);

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertFalse(orderedMoves.get(0).matches(NotationUtils.fromNotation("f3", "f4")));

    }

    @Test
    public void testHistoryHeuristicOrdering() {

        String fen = "5rk1/5ppp/8/8/8/8/4QPPP/5RK1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("g1", "h1"),
                NotationUtils.fromNotation("f2", "f4"),
                NotationUtils.fromNotation("g2", "g4"),
                NotationUtils.fromNotation("h2", "h4"),
                NotationUtils.fromNotation("f2", "f3"),
                NotationUtils.fromNotation("g2", "g3"),
                NotationUtils.fromNotation("h2", "h3"),
                NotationUtils.fromNotation("f1", "e1"),
                NotationUtils.fromNotation("e2", "e7")
                ));

        moveOrderer.addHistoryMove(5, NotationUtils.fromNotation("f1", "e1"), true);

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("f1", "e1")));

    }

    @Test
    public void testWinningCaptureBias() {

        String fen = "7k/8/8/8/4r3/3P1Q2/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("h1", "h2"),
                NotationUtils.fromNotation("h1", "g1"),
                NotationUtils.fromNotation("h1", "g2"),
//                NotationUtils.fromNotation("f3", "e4", PieceType.QUEEN),
                NotationUtils.fromNotation("d3", "e4")
                ));

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("d3", "e4")));

    }

    @Test
    public void testWinningCaptureBeatsLosingCapture() {

        String fen = "7k/8/8/5p2/4r3/3P1Q2/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("h1", "h2"),
                NotationUtils.fromNotation("h1", "g1"),
                NotationUtils.fromNotation("h1", "g2"),
                NotationUtils.fromNotation("f3", "e4"),
                NotationUtils.fromNotation("d3", "e4")
                ));

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("d3", "e4")));
        Assertions.assertTrue(orderedMoves.get(1).matches(NotationUtils.fromNotation("f3", "e4")));

    }

    @Test
    public void testPreviousBestMoveBias() {

        String fen = "2b1r1k1/5pp1/B5Np/8/8/8/3R1PPP/6K1 b - - 0 1";
        Board board = FEN.fromFEN(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("c8", "a6"),
                NotationUtils.fromNotation("g8", "h7"),
                NotationUtils.fromNotation("f7", "f5"),
                NotationUtils.fromNotation("h6", "h5"),
                NotationUtils.fromNotation("e8", "e1")
                ));

        Move previousBestMove = NotationUtils.fromNotation("e8", "e1");

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, previousBestMove, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("e8", "e1")));

    }

    // TODO re-write
    @Disabled
    @Test
    public void testComplexPosition() {

        // Kiwipete
        String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";
        Board board = FEN.fromFEN(fen);

        List<Move> legalMoves = moveGenerator.generateMoves(board, false);

        Move killerMove = NotationUtils.fromNotation("e1", "c1", Move.CASTLE_FLAG);
        moveOrderer.addKillerMove(1, killerMove);

        Move prevBestMove = NotationUtils.fromNotation("e2", "a6");

        List<Move> orderedMoves = moveOrderer.orderMoves(board, legalMoves, prevBestMove, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("e2", "a6")));
        Assertions.assertTrue(orderedMoves.get(1).matches(NotationUtils.fromNotation("e1", "c1")));
        Assertions.assertTrue(orderedMoves.get(2).matches(NotationUtils.fromNotation("g2", "h3"))
                || orderedMoves.get(3).matches(NotationUtils.fromNotation("g2", "h3")));
        Assertions.assertTrue(orderedMoves.get(3).matches(NotationUtils.fromNotation("d5", "e6"))
                || orderedMoves.get(2).matches(NotationUtils.fromNotation("d5", "e6")));
        Assertions.assertTrue(orderedMoves.get(4).matches(NotationUtils.fromNotation("e1", "g1")));

    }

}