package com.kelseyde.calvin;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameTest {

    private final String rook = Piece.getPieceCode(Colour.WHITE, PieceType.ROOK);

    @BeforeEach
    public void beforeEach() {
    }

    @Test
    public void testFromPositionDoesNotCorruptBoard() {

        Game game = new Game(Board.emptyBoard());
        assertSinglePieceBoard(game, 0);
        assertSinglePieceBoard(game, 7);
        assertSinglePieceBoard(game, 12);
        assertSinglePieceBoard(game, 18);
        assertSinglePieceBoard(game, 25);
        assertSinglePieceBoard(game, 31);
        assertSinglePieceBoard(game, 38);
        assertSinglePieceBoard(game, 36);
        assertSinglePieceBoard(game, 43);
        assertSinglePieceBoard(game, 54);
        assertSinglePieceBoard(game, 59);
        assertSinglePieceBoard(game, 60);
        assertSinglePieceBoard(game, 63);

    }

    @Test
    public void testBoardHistoryPreservedMultipleMoves() {
        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e5"));
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("b8", "c6"));
        game.makeMove(move("f1", "b5"));
        game.makeMove(move("a7", "a6"));
        game.makeMove(move("b5", "a4"));

        Assertions.assertEquals(7, game.getBoardHistory().size());
        Assertions.assertEquals(7, game.getMoveHistory().size());

    }

    @Test
    public void testBoardHistoryPreservesMoveCounter() {

        Game game = new Game();
        Assertions.assertEquals(1, game.getBoard().getFullMoveCounter());

        game.makeMove(move("e2", "e3"));
        Assertions.assertEquals(1, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(1, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("e7", "e6"));
        Assertions.assertEquals(2, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(1, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("d2", "d3"));
        Assertions.assertEquals(2, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(2, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("d7", "d6"));
        Assertions.assertEquals(3, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(2, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("c2", "c3"));
        Assertions.assertEquals(3, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(3, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("c7", "c6"));
        Assertions.assertEquals(4, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(3, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("b2", "b3"));
        Assertions.assertEquals(4, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(4, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("b7", "b6"));
        Assertions.assertEquals(5, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(4, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("a2", "a3"));
        Assertions.assertEquals(5, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(5, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("a7", "a6"));
        Assertions.assertEquals(6, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(5, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("h2", "h3"));
        Assertions.assertEquals(6, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(6, game.getBoardHistory().peek().getFullMoveCounter());

        game.makeMove(move("h7", "h6"));
        Assertions.assertEquals(7, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(6, game.getBoardHistory().peek().getFullMoveCounter());
    }

    @Test
    public void testBoardHistoryPreservesCastlingRights() {
        Game game = new Game();

        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        game.makeMove(move("e2", "e3"));
        game.makeMove(move("e7", "e6"));

        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        game.makeMove(move("e1", "e2"));

        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isQueenSide());

        game.makeMove(move("f7", "f6"));

        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        Assertions.assertFalse(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertFalse(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isQueenSide());

    }

    @Test
    public void testUnmakeMoveRestoresCapturedPieces() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("d7", "d5"));
        game.makeMove(move("e4", "d5"));

        Set<Integer> whitePiecePositions = getPiecePositions(game.getBoard(), Colour.WHITE);
        Assertions.assertEquals(Set.of(35, 8, 9, 10, 11, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7), whitePiecePositions);

        Set<Integer> blackPiecePositions = getPiecePositions(game.getBoard(), Colour.BLACK);
        Assertions.assertEquals(Set.of(56, 57, 58, 59, 60, 61, 62, 63, 48, 49, 50, 52, 53, 54, 55), blackPiecePositions);

        game.unmakeMove();

        whitePiecePositions = getPiecePositions(game.getBoard(), Colour.WHITE);
        Assertions.assertEquals(Set.of(28, 8, 9, 10, 11, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7), whitePiecePositions);

        blackPiecePositions = getPiecePositions(game.getBoard(), Colour.BLACK);
        Assertions.assertEquals(Set.of(35, 56, 57, 58, 59, 60, 61, 62, 63, 48, 49, 50, 52, 53, 54, 55), blackPiecePositions);

    }

    @Test
    public void testUnmakeMoveRemovesCorrectMoveFromMoveHistory() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("d7", "d5"));
        game.makeMove(move("e4", "d5"));

        List<Move> moveHistory = game.getMoveHistory().stream().toList();
        Assertions.assertEquals(3, moveHistory.size());
        Assertions.assertTrue(move("e4", "d5").moveMatches(moveHistory.get(0)));
        Assertions.assertTrue(move("d7", "d5").moveMatches(moveHistory.get(1)));
        Assertions.assertTrue(move("e2", "e4").moveMatches(moveHistory.get(2)));

        game.unmakeMove();

        moveHistory = game.getMoveHistory().stream().toList();
        Assertions.assertEquals(2, moveHistory.size());
        Assertions.assertTrue(move("d7", "d5").moveMatches(moveHistory.get(0)));
        Assertions.assertTrue(move("e2", "e4").moveMatches(moveHistory.get(1)));

    }

    @Test
    public void testUnmakeMoveHandlesTurnSwitching() {

        Game game = new Game();
        Assertions.assertEquals(Colour.WHITE, game.getTurn());

        game.makeMove(move("e2", "e4"));
        Assertions.assertEquals(Colour.BLACK, game.getTurn());

        game.makeMove(move("d7", "d5"));
        Assertions.assertEquals(Colour.WHITE, game.getTurn());

        game.makeMove(move("e4", "d5"));
        Assertions.assertEquals(Colour.BLACK, game.getTurn());

        game.unmakeMove();
        Assertions.assertEquals(Colour.WHITE, game.getTurn());

    }

    @Test
    public void testUnmakeMoveHandlesCastling() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e6"));
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));
        game.makeMove(move("f1", "e2"));
        game.makeMove(move("f8", "e7"));
        // castles
        game.makeMove(move("e1", "g1"));
        Assertions.assertEquals(Colour.BLACK, game.getTurn());
        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());

        game.unmakeMove();
        Assertions.assertEquals(Colour.WHITE, game.getTurn());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());

    }

    @Test
    public void testUnmakeCheckmate() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e5"));
        game.makeMove(move("d1", "h5"));
        game.makeMove(move("b8", "c6"));
        game.makeMove(move("f1", "c4"));
        game.makeMove(move("g8", "f6"));
        // scholar's mate
        game.makeMove(move("h5", "f7"));
        Assertions.assertEquals(Colour.BLACK, game.getTurn());
        // todo

    }

    private Set<Integer> getPiecePositions(Board board, Colour colour) {
        Set<Integer> positions = new HashSet<>();
        if (colour.isWhite()) {
            long whitePieces = board.getWhitePieces();
            while (whitePieces != 0) {
                int position = BitBoard.scanForward(whitePieces);
                positions.add(position);
                whitePieces = BitBoard.popLSB(whitePieces);
            }
        } else {
            long blackPieces = board.getBlackPieces();
            while (blackPieces != 0) {
                int position = BitBoard.scanForward(blackPieces);
                positions.add(position);
                blackPieces = BitBoard.popLSB(blackPieces);
            }
        }
        return positions;
    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

    private void assertSinglePieceBoard(Game game, int startSquare) {
        game.getBoard().setPiece(startSquare, rook);
        Assertions.assertEquals(Set.of(startSquare), getPiecePositions(game.getBoard(), Colour.WHITE));
        Assertions.assertEquals(Set.of(), getPiecePositions(game.getBoard(), Colour.BLACK));
        game.getBoard().unsetPiece(startSquare);
    }

}
