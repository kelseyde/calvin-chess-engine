package com.kelseyde.calvin;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.result.GameResult;
import com.kelseyde.calvin.model.result.WinResult;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckmateTest {

    @Test
    public void testFoolsMate() {

        Game game = new Game();
        game.makeMove(move("f2", "f3"));
        game.makeMove(move("e7", "e5"));
        game.makeMove(move("g2", "g4"));

        GameResult result = game.makeMove(move("d8", "h4"));
        Assertions.assertEquals(GameResult.ResultType.WIN, result.getResultType());
        WinResult winResult = (WinResult) result;
        Assertions.assertEquals(WinType.CHECKMATE, winResult.getWinType());

    }

    @Test
    public void testScholarsMate() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e5"));
        game.makeMove(move("d1", "h5"));
        game.makeMove(move("b8", "c6"));
        game.makeMove(move("f1", "c4"));
        game.makeMove(move("d7", "d6"));

        GameResult result = game.makeMove(move("h5", "f7"));
        Assertions.assertEquals(GameResult.ResultType.WIN, result.getResultType());
        WinResult winResult = (WinResult) result;
        Assertions.assertEquals(WinType.CHECKMATE, winResult.getWinType());

    }

    @Test
    public void testDiscoveredCheckmate() {

        // Lasker vs Thomas, 1912
        Game game = new Game();
        game.makeMove(move("d2", "d4"));
        game.makeMove(move("e7", "e6"));
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("f7", "f5"));
        game.makeMove(move("b1", "c3"));
        game.makeMove(move("g8", "f6"));
        game.makeMove(move("c1", "g5"));
        game.makeMove(move("f8", "e7"));
        game.makeMove(move("g5", "f6"));
        game.makeMove(move("e7", "f6"));
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("f5", "e4"));
        game.makeMove(move("c3", "e4"));
        game.makeMove(move("b7", "b6"));
        game.makeMove(move("f3", "e5"));
        game.makeMove(move("e8", "g8"));
        game.makeMove(move("f1", "d3"));
        game.makeMove(move("c8", "b7"));
        game.makeMove(move("d1", "h5"));
        game.makeMove(move("d8", "e7"));
        // now the fun begins
        game.makeMove(move("h5", "h7"));
        game.makeMove(move("g8", "h7"));
        game.makeMove(move("e4", "f6"));
        game.makeMove(move("h7", "h6"));
        game.makeMove(move("e5", "g4"));
        game.makeMove(move("h6", "g5"));
        game.makeMove(move("h2", "h4"));
        game.makeMove(move("g5", "f4"));
        game.makeMove(move("g2", "g3"));
        game.makeMove(move("f4", "f3"));
        game.makeMove(move("d3", "e2"));
        game.makeMove(move("f3", "g2"));
        game.makeMove(move("h1", "h2"));
        game.makeMove(move("g2", "g1"));

        GameResult result = game.makeMove(move("e1", "d2"));
        Assertions.assertEquals(GameResult.ResultType.WIN, result.getResultType());
        WinResult winResult = (WinResult) result;
        Assertions.assertEquals(WinType.CHECKMATE, winResult.getWinType());

    }

    @Test
    public void testCastlesCheckmate() {

        // Lasker vs Thomas, 1912
        Game game = new Game();
        game.makeMove(move("d2", "d4"));
        game.makeMove(move("e7", "e6"));
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("f7", "f5"));
        game.makeMove(move("b1", "c3"));
        game.makeMove(move("g8", "f6"));
        game.makeMove(move("c1", "g5"));
        game.makeMove(move("f8", "e7"));
        game.makeMove(move("g5", "f6"));
        game.makeMove(move("e7", "f6"));
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("f5", "e4"));
        game.makeMove(move("c3", "e4"));
        game.makeMove(move("b7", "b6"));
        game.makeMove(move("f3", "e5"));
        game.makeMove(move("e8", "g8"));
        game.makeMove(move("f1", "d3"));
        game.makeMove(move("c8", "b7"));
        game.makeMove(move("d1", "h5"));
        game.makeMove(move("d8", "e7"));
        // now the fun begins
        game.makeMove(move("h5", "h7"));
        game.makeMove(move("g8", "h7"));
        game.makeMove(move("e4", "f6"));
        game.makeMove(move("h7", "h6"));
        game.makeMove(move("e5", "g4"));
        game.makeMove(move("h6", "g5"));
        game.makeMove(move("h2", "h4"));
        game.makeMove(move("g5", "f4"));
        game.makeMove(move("g2", "g3"));
        game.makeMove(move("f4", "f3"));
        game.makeMove(move("d3", "e2"));
        game.makeMove(move("f3", "g2"));
        game.makeMove(move("h1", "h2"));
        game.makeMove(move("g2", "g1"));

        // improving on Lasker's move, O-O-O#!
        GameResult result = game.makeMove(move("e1", "c1"));
        Assertions.assertEquals(GameResult.ResultType.WIN, result.getResultType());
        WinResult winResult = (WinResult) result;
        Assertions.assertEquals(WinType.CHECKMATE, winResult.getWinType());

    }

    @Test
    public void testEnPassantCheckmate() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("f7", "f5"));
        game.makeMove(move("e4", "f5"));
        game.makeMove(move("e8", "f7"));
        game.makeMove(move("b2", "b3"));
        game.makeMove(move("d7", "d6"));
        game.makeMove(move("c1", "b2"));
        game.makeMove(move("h7", "h6"));
        game.makeMove(move("f1", "b5"));
        game.makeMove(move("a7", "a6"));
        game.makeMove(move("d1", "g4"));
        game.makeMove(move("g7", "g5"));

        GameResult result = game.makeMove(move("f5", "g6"));
        Assertions.assertEquals(GameResult.ResultType.WIN, result.getResultType());
        WinResult winResult = (WinResult) result;
        Assertions.assertEquals(WinType.CHECKMATE, winResult.getWinType());
    }

    @Test
    public void testKnightPromotionCheckmate() {

        Game game = new Game();
        game.makeMove(move("d2", "d3"));
        game.makeMove(move("e7", "e5"));
        game.makeMove(move("e1", "d2"));
        game.makeMove(move("e5", "e4"));
        game.makeMove(move("d2", "c3"));
        game.makeMove(move("e4", "d3"));
        game.makeMove(move("b2", "b3"));
        game.makeMove(move("d3", "e2"));
        game.makeMove(move("c3", "b2"));

        Move move = Move.builder()
                .startSquare(MoveUtils.fromNotation("e2"))
                .endSquare(MoveUtils.fromNotation("d1"))
                .promotionPieceType(PieceType.KNIGHT)
                .build();
        GameResult result = game.makeMove(move);
        Assertions.assertEquals(GameResult.ResultType.WIN, result.getResultType());
        WinResult winResult = (WinResult) result;
        Assertions.assertEquals(WinType.CHECKMATE, winResult.getWinType());
    }

    @Test
    public void testSimpleQueenCheckmate() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(48, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(42, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(1, new Piece(Colour.WHITE, PieceType.QUEEN));

        Game game = new Game(board);

        GameResult result = game.makeMove(move("b1", "b7"));
        Assertions.assertEquals(GameResult.ResultType.WIN, result.getResultType());
        WinResult winResult = (WinResult) result;
        Assertions.assertEquals(WinType.CHECKMATE, winResult.getWinType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
