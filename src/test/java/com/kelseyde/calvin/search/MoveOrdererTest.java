package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
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

        Assertions.assertEquals(10, table[Piece.PAWN.getIndex()][Piece.KING.getIndex()]);
        Assertions.assertEquals(11, table[Piece.PAWN.getIndex()][Piece.QUEEN.getIndex()]);
        Assertions.assertEquals(12, table[Piece.PAWN.getIndex()][Piece.ROOK.getIndex()]);
        Assertions.assertEquals(13, table[Piece.PAWN.getIndex()][Piece.BISHOP.getIndex()]);
        Assertions.assertEquals(14, table[Piece.PAWN.getIndex()][Piece.KNIGHT.getIndex()]);
        Assertions.assertEquals(15, table[Piece.PAWN.getIndex()][Piece.PAWN.getIndex()]);
        Assertions.assertEquals(20, table[Piece.KNIGHT.getIndex()][Piece.KING.getIndex()]);
        Assertions.assertEquals(21, table[Piece.KNIGHT.getIndex()][Piece.QUEEN.getIndex()]);
        Assertions.assertEquals(22, table[Piece.KNIGHT.getIndex()][Piece.ROOK.getIndex()]);
        Assertions.assertEquals(23, table[Piece.KNIGHT.getIndex()][Piece.BISHOP.getIndex()]);
        Assertions.assertEquals(24, table[Piece.KNIGHT.getIndex()][Piece.KNIGHT.getIndex()]);
        Assertions.assertEquals(25, table[Piece.KNIGHT.getIndex()][Piece.PAWN.getIndex()]);
        Assertions.assertEquals(30, table[Piece.BISHOP.getIndex()][Piece.KING.getIndex()]);
        Assertions.assertEquals(31, table[Piece.BISHOP.getIndex()][Piece.QUEEN.getIndex()]);
        Assertions.assertEquals(32, table[Piece.BISHOP.getIndex()][Piece.ROOK.getIndex()]);
        Assertions.assertEquals(33, table[Piece.BISHOP.getIndex()][Piece.BISHOP.getIndex()]);
        Assertions.assertEquals(34, table[Piece.BISHOP.getIndex()][Piece.KNIGHT.getIndex()]);
        Assertions.assertEquals(35, table[Piece.BISHOP.getIndex()][Piece.PAWN.getIndex()]);
        Assertions.assertEquals(40, table[Piece.ROOK.getIndex()][Piece.KING.getIndex()]);
        Assertions.assertEquals(41, table[Piece.ROOK.getIndex()][Piece.QUEEN.getIndex()]);
        Assertions.assertEquals(42, table[Piece.ROOK.getIndex()][Piece.ROOK.getIndex()]);
        Assertions.assertEquals(43, table[Piece.ROOK.getIndex()][Piece.BISHOP.getIndex()]);
        Assertions.assertEquals(44, table[Piece.ROOK.getIndex()][Piece.KNIGHT.getIndex()]);
        Assertions.assertEquals(45, table[Piece.ROOK.getIndex()][Piece.PAWN.getIndex()]);
        Assertions.assertEquals(50, table[Piece.QUEEN.getIndex()][Piece.KING.getIndex()]);
        Assertions.assertEquals(51, table[Piece.QUEEN.getIndex()][Piece.QUEEN.getIndex()]);
        Assertions.assertEquals(52, table[Piece.QUEEN.getIndex()][Piece.ROOK.getIndex()]);
        Assertions.assertEquals(53, table[Piece.QUEEN.getIndex()][Piece.BISHOP.getIndex()]);
        Assertions.assertEquals(54, table[Piece.QUEEN.getIndex()][Piece.KNIGHT.getIndex()]);
        Assertions.assertEquals(55, table[Piece.QUEEN.getIndex()][Piece.PAWN.getIndex()]);

    }

    @Test
    public void testLosingCaptureBias() {

        String fen = "7k/8/8/8/4r3/3P1Q2/8/7K w - - 0 1";
        Board board = FEN.toBoard(fen);

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
        Board board = FEN.toBoard(fen);

        List<Move> moves = new ArrayList<>(List.of(
                NotationUtils.fromNotation("g1", "h1"),
                NotationUtils.fromNotation("g1", "f1"),
                NotationUtils.fromNotation("f3", "f4"),
                NotationUtils.fromNotation("h2", "h4")));

        Move killerMove = TestUtils.getLegalMove(board, "f3", "f4");
        moveOrderer.addKillerMove(1, killerMove);

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves.get(0).matches(NotationUtils.fromNotation("f3", "f4")));

    }

    @Test
    public void testIgnoreKillersAtDifferentPly() {

        String fen = "1k6/8/8/6q1/8/5PP1/6BP/6K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

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
        Board board = FEN.toBoard(fen);

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
        Board board = FEN.toBoard(fen);

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
        Board board = FEN.toBoard(fen);

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
        Board board = FEN.toBoard(fen);

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

        String fen = "1k6/3p2pp/rnb1P3/8/N3Q3/1P5p/P1PPPpN1/1K6 b - - 0 1";
        Board board = FEN.toBoard(fen);

        List<Move> legalMoves = moveGenerator.generateMoves(board, false);

        // Previous best move
        Move previousBestMove = new Move(NotationUtils.fromNotation("b8"), NotationUtils.fromNotation("c7"));
        // Add killer move
        moveOrderer.addKillerMove(3, new Move(NotationUtils.fromNotation("d7"), NotationUtils.fromNotation("d5"), Move.PAWN_DOUBLE_MOVE_FLAG));
        // Add history moves
        moveOrderer.addHistoryMove(2, new Move(NotationUtils.fromNotation("h7"), NotationUtils.fromNotation("h5"), Move.PAWN_DOUBLE_MOVE_FLAG), false);
        moveOrderer.addHistoryMove(3, new Move(NotationUtils.fromNotation("g7"), NotationUtils.fromNotation("g5"), Move.PAWN_DOUBLE_MOVE_FLAG), false);
        // Add a white history move just to confirm it is not used
        moveOrderer.addHistoryMove(2, new Move(NotationUtils.fromNotation("h3"), NotationUtils.fromNotation("h2")), true);

        List<Move> orderedMoves = moveOrderer.orderMoves(board, legalMoves, previousBestMove, true, 3);

        Assertions.assertEquals(orderedMoves.get(0), new Move(NotationUtils.fromNotation("b8"), NotationUtils.fromNotation("c7")));
        Assertions.assertEquals(orderedMoves.get(1), new Move(NotationUtils.fromNotation("f2"), NotationUtils.fromNotation("f1"), Move.PROMOTE_TO_QUEEN_FLAG));
        Assertions.assertEquals(orderedMoves.get(2), new Move(NotationUtils.fromNotation("c6"), NotationUtils.fromNotation("e4")));
        Assertions.assertEquals(orderedMoves.get(3), new Move(NotationUtils.fromNotation("h3"), NotationUtils.fromNotation("g2")));
        Assertions.assertEquals(orderedMoves.get(4), new Move(NotationUtils.fromNotation("b6"), NotationUtils.fromNotation("a4")));
        Assertions.assertEquals(orderedMoves.get(5), new Move(NotationUtils.fromNotation("d7"), NotationUtils.fromNotation("e6")));
        Assertions.assertEquals(orderedMoves.get(6), new Move(NotationUtils.fromNotation("d7"), NotationUtils.fromNotation("d5"), Move.PAWN_DOUBLE_MOVE_FLAG));
        Assertions.assertEquals(orderedMoves.get(7), new Move(NotationUtils.fromNotation("c6"), NotationUtils.fromNotation("a4")));
        Assertions.assertEquals(orderedMoves.get(8), new Move(NotationUtils.fromNotation("a6"), NotationUtils.fromNotation("a4")));
        Assertions.assertEquals(orderedMoves.get(9), new Move(NotationUtils.fromNotation("f2"), NotationUtils.fromNotation("f1"), Move.PROMOTE_TO_KNIGHT_FLAG));
        Assertions.assertEquals(orderedMoves.get(10), new Move(NotationUtils.fromNotation("f2"), NotationUtils.fromNotation("f1"), Move.PROMOTE_TO_BISHOP_FLAG));
        Assertions.assertEquals(orderedMoves.get(11), new Move(NotationUtils.fromNotation("f2"), NotationUtils.fromNotation("f1"), Move.PROMOTE_TO_ROOK_FLAG));
        Assertions.assertEquals(orderedMoves.get(12), new Move(NotationUtils.fromNotation("g7"), NotationUtils.fromNotation("g5"), Move.PAWN_DOUBLE_MOVE_FLAG));
        Assertions.assertEquals(orderedMoves.get(13), new Move(NotationUtils.fromNotation("h7"), NotationUtils.fromNotation("h5"), Move.PAWN_DOUBLE_MOVE_FLAG));

    }

}