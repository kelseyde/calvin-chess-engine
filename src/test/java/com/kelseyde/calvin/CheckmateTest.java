package com.kelseyde.calvin;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckmateTest {

    @Test
    public void testFoolsMate() {

        Game game = new Game();
        game.executeAction(move("f2", "f3"));
        game.executeAction(move("e7", "e5"));
        game.executeAction(move("g2", "g4"));

        ActionResult result = game.executeAction(move("d8", "h4"));
        Assertions.assertTrue(result.isWin());
        Assertions.assertEquals(WinType.CHECKMATE, result.getWinType());

    }

    @Test
    public void testScholarsMate() {

        Game game = new Game();
        game.executeAction(move("e2", "e4"));
        game.executeAction(move("e7", "e5"));
        game.executeAction(move("d1", "h5"));
        game.executeAction(move("b8", "c6"));
        game.executeAction(move("f1", "c4"));
        game.executeAction(move("d7", "d6"));

        ActionResult result = game.executeAction(move("h5", "f7"));
        Assertions.assertTrue(result.isWin());
        Assertions.assertEquals(WinType.CHECKMATE, result.getWinType());

    }

    @Test
    public void testDiscoveredCheckmate() {

        // Lasker vs Thomas, 1912
        Game game = new Game();
        game.executeAction(move("d2", "d4"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("e7", "e6"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("g1", "f3"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("f7", "f5"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("b1", "c3"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("g8", "f6"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("c1", "g5"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("f8", "e7"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("g5", "f6"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("e7", "f6"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("e2", "e4"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("f5", "e4"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("c3", "e4"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("b7", "b6"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("f3", "e5"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("e8", "g8"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("f1", "d3"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("c8", "b7"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("d1", "h5"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("d8", "e7"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        // now the fun begins
        game.executeAction(move("h5", "h7"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("g8", "h7"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("e4", "f6"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("h7", "h6"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("e5", "g4"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("h6", "g5"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("h2", "h4"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("g5", "f4"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("g2", "g3"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("f4", "f3"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("d3", "e2"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("f3", "g2"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("h1", "h2"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        game.executeAction(move("g2", "g1"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));

        ActionResult result = game.executeAction(move("e1", "d2"));
        System.out.println(BoardUtils.toFormattedBoardString(game.getBoard()));
        Assertions.assertTrue(result.isWin());
        Assertions.assertEquals(WinType.CHECKMATE, result.getWinType());

    }

    @Test
    public void testCastlesCheckmate() {

        // Lasker vs Thomas, 1912
        Game game = new Game();
        game.executeAction(move("d2", "d4"));
        game.executeAction(move("e7", "e6"));
        game.executeAction(move("g1", "f3"));
        game.executeAction(move("f7", "f5"));
        game.executeAction(move("b1", "c3"));
        game.executeAction(move("g8", "f6"));
        game.executeAction(move("c1", "g5"));
        game.executeAction(move("f8", "e7"));
        game.executeAction(move("g5", "f6"));
        game.executeAction(move("e7", "f6"));
        game.executeAction(move("e2", "e4"));
        game.executeAction(move("f5", "e4"));
        game.executeAction(move("c3", "e4"));
        game.executeAction(move("b7", "b6"));
        game.executeAction(move("f3", "e5"));
        game.executeAction(move("e8", "g8"));
        game.executeAction(move("f1", "d3"));
        game.executeAction(move("c8", "b7"));
        game.executeAction(move("d1", "h5"));
        game.executeAction(move("d8", "e7"));
        // now the fun begins
        game.executeAction(move("h5", "h7"));
        game.executeAction(move("g8", "h7"));
        game.executeAction(move("e4", "f6"));
        game.executeAction(move("h7", "h6"));
        game.executeAction(move("e5", "g4"));
        game.executeAction(move("h6", "g5"));
        game.executeAction(move("h2", "h4"));
        game.executeAction(move("g5", "f4"));
        game.executeAction(move("g2", "g3"));
        game.executeAction(move("f4", "f3"));
        game.executeAction(move("d3", "e2"));
        game.executeAction(move("f3", "g2"));
        game.executeAction(move("h1", "h2"));
        game.executeAction(move("g2", "g1"));

        // improving on Lasker's move, O-O-O#!
        ActionResult result = game.executeAction(move("e1", "c1"));
        Assertions.assertTrue(result.isWin());
        Assertions.assertEquals(WinType.CHECKMATE, result.getWinType());

    }

    @Test
    public void testEnPassantCheckmate() {

        Game game = new Game();
        game.executeAction(move("e2", "e4"));
        game.executeAction(move("f7", "f5"));
        game.executeAction(move("e4", "f5"));
        game.executeAction(move("e8", "f7"));
        game.executeAction(move("b2", "b3"));
        game.executeAction(move("d7", "d6"));
        game.executeAction(move("c1", "b2"));
        game.executeAction(move("h7", "h6"));
        game.executeAction(move("f1", "b5"));
        game.executeAction(move("a7", "a6"));
        game.executeAction(move("d1", "g4"));
        game.executeAction(move("g7", "g5"));

        ActionResult result = game.executeAction(move("f5", "g6"));
        Assertions.assertTrue(result.isWin());
        Assertions.assertEquals(WinType.CHECKMATE, result.getWinType());

    }

    @Test
    public void testKnightPromotionCheckmate() {

        Game game = new Game();
        game.executeAction(move("d2", "d3"));
        game.executeAction(move("e7", "e5"));
        game.executeAction(move("e1", "d2"));
        game.executeAction(move("e5", "e4"));
        game.executeAction(move("d2", "c3"));
        game.executeAction(move("e4", "d3"));
        game.executeAction(move("b2", "b3"));
        game.executeAction(move("d3", "e2"));
        game.executeAction(move("c3", "b2"));

        Move move = Move.builder()
                .startSquare(MoveUtils.fromNotation("e2"))
                .endSquare(MoveUtils.fromNotation("d1"))
                .promotionPieceType(PieceType.KNIGHT)
                .build();
        ActionResult result = game.executeAction(GameAction.builder().actionType(ActionType.MOVE).move(move).build());
        Assertions.assertTrue(result.isWin());
        Assertions.assertEquals(WinType.CHECKMATE, result.getWinType());
    }

    @Test
    public void testSimpleQueenCheckmate() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(48, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(42, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(1, new Piece(Colour.WHITE, PieceType.QUEEN));

        Game game = new Game(board);

        ActionResult result = game.executeAction(move("b1", "b7"));
        Assertions.assertTrue(result.isWin());
        Assertions.assertEquals(WinType.CHECKMATE, result.getWinType());

    }

    private GameAction move(String startSquare, String endSquare) {
        return GameAction.builder()
                .actionType(ActionType.MOVE)
                .move(MoveUtils.fromNotation(startSquare, endSquare))
                .build();
    }

}
