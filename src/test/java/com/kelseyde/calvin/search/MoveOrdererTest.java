package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Not;

public class MoveOrdererTest {

    private MoveOrderer moveOrderer;

    private MoveGenerator moveGenerator = new MoveGenerator();

    @BeforeEach
    public void beforeEach() {
        moveOrderer = new MoveOrderer();
    }

    @Test
    public void testUnderpromotionBias() {

        String fen = "7k/2P5/8/8/8/8/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move underPromotionMove = Move.builder()
                .startSquare(50)
                .endSquare(58)
                .pieceType(PieceType.PAWN)
                .moveType(MoveType.PROMOTION)
                .promotionPieceType(PieceType.KNIGHT)
                .build();

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("h1", "h2", PieceType.KING),
                NotationUtils.fromNotation("h1", "g1", PieceType.KING),
                NotationUtils.fromNotation("h1", "g2", PieceType.KING),
                underPromotionMove
        };

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(underPromotionMove));

    }

    @Test
    public void testLosingCaptureBias() {

        String fen = "7k/8/8/8/4r3/3P1Q2/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("h1", "h2", PieceType.KING),
                NotationUtils.fromNotation("h1", "g1", PieceType.KING),
                NotationUtils.fromNotation("h1", "g2", PieceType.KING),
                NotationUtils.fromNotation("f3", "e4", PieceType.QUEEN),
//                NotationUtils.fromNotation("d3", "e4", PieceType.PAWN),
        };

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(NotationUtils.fromNotation("f3", "e4", PieceType.QUEEN)));

    }

    @Test
    public void testCastleBias() {

        String fen = "4k2r/5ppp/8/8/8/8/5PPP/4K2R b Kk - 0 1";
        Board board = FEN.fromFEN(fen);

        Move castlingMove = Move.builder()
                .startSquare(60)
                .endSquare(62)
                .pieceType(PieceType.KING)
                .moveType(MoveType.KINGSIDE_CASTLE)
                .build();

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("h7", "h5", PieceType.KING),
                NotationUtils.fromNotation("g7", "g5", PieceType.KING),
                NotationUtils.fromNotation("f7", "f5", PieceType.KING),
                NotationUtils.fromNotation("e8", "f8", PieceType.QUEEN),
                NotationUtils.fromNotation("e8", "g8", PieceType.PAWN),
                castlingMove
        };

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(castlingMove));

    }

    @Test
    public void testCheckBias() {

        String fen = "6k1/5ppp/8/4Q3/8/8/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("h1", "h2", PieceType.KING),
                NotationUtils.fromNotation("h1", "g1", PieceType.KING),
                NotationUtils.fromNotation("h1", "g2", PieceType.KING),
                NotationUtils.fromNotation("e6", "e8", PieceType.QUEEN),
        };

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(NotationUtils.fromNotation("e6", "e8", PieceType.QUEEN)));

    }

    @Test
    public void testKillerMoveBias() {

        String fen = "k7/8/8/6q1/8/5PP1/6BP/6K1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("g1", "h1", PieceType.KING),
                NotationUtils.fromNotation("g1", "f1", PieceType.KING),
                NotationUtils.fromNotation("f3", "f4", PieceType.PAWN),
                NotationUtils.fromNotation("h2", "h4", PieceType.PAWN),
        };

        Move killerMove = Move.builder()
                .startSquare(21)
                .endSquare(29)
                .pieceType(PieceType.PAWN)
                .build();
        moveOrderer.addKillerMove(1, killerMove);

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(NotationUtils.fromNotation("f3", "f4", PieceType.PAWN)));

    }

    @Test
    public void testIgnoreKillersAtDifferentPly() {

        String fen = "1k6/8/8/6q1/8/5PP1/6BP/6K1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("g1", "h1", PieceType.KING),
                NotationUtils.fromNotation("g1", "f1", PieceType.KING),
                NotationUtils.fromNotation("f3", "f4", PieceType.PAWN),
                NotationUtils.fromNotation("h2", "h4", PieceType.PAWN),
        };

        Move killerMove = Move.builder()
                .startSquare(21)
                .endSquare(29)
                .pieceType(PieceType.PAWN)
                .build();
        moveOrderer.addKillerMove(2, killerMove);

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertFalse(orderedMoves[0].matches(NotationUtils.fromNotation("f3", "f4", PieceType.PAWN)));

    }

    @Test
    public void testPromotionBias() {

        String fen = "7k/2P5/8/8/8/8/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move underPromotionMove = Move.builder()
                .startSquare(50)
                .endSquare(58)
                .pieceType(PieceType.PAWN)
                .moveType(MoveType.PROMOTION)
                .promotionPieceType(PieceType.QUEEN)
                .build();

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("h1", "h2", PieceType.KING),
                NotationUtils.fromNotation("h1", "g1", PieceType.KING),
                NotationUtils.fromNotation("h1", "g2", PieceType.KING),
                underPromotionMove
        };

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(underPromotionMove));

    }

    @Test
    public void testWinningCaptureBias() {

        String fen = "7k/8/8/8/4r3/3P1Q2/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("h1", "h2", PieceType.KING),
                NotationUtils.fromNotation("h1", "g1", PieceType.KING),
                NotationUtils.fromNotation("h1", "g2", PieceType.KING),
//                NotationUtils.fromNotation("f3", "e4", PieceType.QUEEN),
                NotationUtils.fromNotation("d3", "e4", PieceType.PAWN),
        };

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(NotationUtils.fromNotation("d3", "e4", PieceType.PAWN)));

    }

    @Test
    public void testWinningCaptureBeatsLosingCapture() {

        String fen = "7k/8/8/8/4r3/3P1Q2/8/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("h1", "h2", PieceType.KING),
                NotationUtils.fromNotation("h1", "g1", PieceType.KING),
                NotationUtils.fromNotation("h1", "g2", PieceType.KING),
                NotationUtils.fromNotation("f3", "e4", PieceType.QUEEN),
                NotationUtils.fromNotation("d3", "e4", PieceType.PAWN),
        };

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, null, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(NotationUtils.fromNotation("d3", "e4", PieceType.PAWN)));
        Assertions.assertTrue(orderedMoves[1].matches(NotationUtils.fromNotation("f3", "e4", PieceType.QUEEN)));

    }

    @Test
    public void testPreviousBestMoveBias() {

        String fen = "2b1r1k1/5pp1/B5Np/8/8/8/3R1PPP/6K1 b - - 0 1";
        Board board = FEN.fromFEN(fen);

        Move[] moves = new Move[] {
                NotationUtils.fromNotation("c8", "a6", PieceType.BISHOP),
                NotationUtils.fromNotation("g8", "h7", PieceType.KING),
                NotationUtils.fromNotation("f7", "f5", PieceType.PAWN),
                NotationUtils.fromNotation("h6", "h5", PieceType.PAWN),
                NotationUtils.fromNotation("e8", "e1", PieceType.ROOK),
        };

        Move previousBestMove = NotationUtils.fromNotation("e8", "e1", PieceType.ROOK);

        Move[] orderedMoves = moveOrderer.orderMoves(board, moves, previousBestMove, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(NotationUtils.fromNotation("e8", "e1", PieceType.ROOK)));

    }

    @Test
    public void testComplexPosition() {

        // Kiwipete
        String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";
        Board board = FEN.fromFEN(fen);

        Move[] legalMoves = moveGenerator.generateLegalMoves(board, false);

        Move killerMove = NotationUtils.fromNotation("e1", "c1", PieceType.KING);
        killerMove.setMoveType(MoveType.QUEENSIDE_CASTLE);
        moveOrderer.addKillerMove(1, killerMove);

        Move prevBestMove = NotationUtils.fromNotation("e2", "a6", PieceType.BISHOP);

        Move[] orderedMoves = moveOrderer.orderMoves(board, legalMoves, prevBestMove, true, 1);

        Assertions.assertTrue(orderedMoves[0].matches(NotationUtils.fromNotation("e2", "a6", PieceType.BISHOP)));
        Assertions.assertTrue(orderedMoves[1].matches(NotationUtils.fromNotation("e1", "c1", PieceType.KING)));
        Assertions.assertTrue(orderedMoves[2].matches(NotationUtils.fromNotation("g2", "h3", PieceType.PAWN))
                || orderedMoves[3].matches(NotationUtils.fromNotation("g2", "h3", PieceType.PAWN)));
        Assertions.assertTrue(orderedMoves[3].matches(NotationUtils.fromNotation("d5", "e6", PieceType.PAWN))
                || orderedMoves[2].matches(NotationUtils.fromNotation("d5", "e6", PieceType.PAWN)));
        Assertions.assertTrue(orderedMoves[4].matches(NotationUtils.fromNotation("e1", "g1", PieceType.KING)));

    }

}