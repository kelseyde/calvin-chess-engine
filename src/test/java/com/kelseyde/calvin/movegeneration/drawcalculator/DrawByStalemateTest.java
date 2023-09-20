package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.Piece;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.board.result.DrawResult;
import com.kelseyde.calvin.board.result.DrawType;
import com.kelseyde.calvin.board.result.GameResult;
import com.kelseyde.calvin.utils.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByStalemateTest {

    @Test
    public void testSimpleQueenStalemate() {

        Board board = Board.emptyBoard();
        board.setPiece(56, Piece.getPieceCode(false, PieceType.KING), true);
        board.setPiece(42, Piece.getPieceCode(true, PieceType.KING), true);
        board.setPiece(1, Piece.getPieceCode(true, PieceType.QUEEN), true);

        Game game = new Game(board);
        GameResult result = game.makeMove(move("b1", "b6"));

        // king stalemated in the corner
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.STALEMATE, ((DrawResult) result).getDrawType());

    }

    @Test
    public void testSimpleKingAndPawnStalemate() {

        Board board = Board.emptyBoard();
        board.setPiece(60, Piece.getPieceCode(false, PieceType.KING), true);
        board.setPiece(43, Piece.getPieceCode(true, PieceType.KING), true);
        board.setPiece(52, Piece.getPieceCode(true, PieceType.PAWN), true);

        Game game = new Game(board);
        GameResult result = game.makeMove(move("d6", "e6"));

        // king stalemated by king and pawn
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.STALEMATE, ((DrawResult) result).getDrawType());

    }

    @Test
    public void testSimpleKingAndBishopStalemate() {

        Board board = Board.emptyBoard();
        board.setPiece(63, Piece.getPieceCode(false, PieceType.KING), true);
        board.setPiece(46, Piece.getPieceCode(true, PieceType.KING), true);
        board.setPiece(47, Piece.getPieceCode(true, PieceType.PAWN), true);
        board.setPiece(37, Piece.getPieceCode(true, PieceType.BISHOP), true);

        Game game = new Game(board);
        GameResult result = game.makeMove(move("f5", "e6"));

        // king stalemated in the corner
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.STALEMATE, ((DrawResult) result).getDrawType());

    }

    @Test
    public void testStalemateWithPinnedPawn() {

        Board board = Board.emptyBoard();
        board.setPiece(63, Piece.getPieceCode(false, PieceType.KING), true);
        board.setPiece(54, Piece.getPieceCode(false, PieceType.PAWN), true);
        board.setPiece(38, Piece.getPieceCode(true, PieceType.KING), true);
        board.setPiece(47, Piece.getPieceCode(true, PieceType.PAWN), true);
        board.setPiece(36, Piece.getPieceCode(true, PieceType.BISHOP), true);
        board.setPiece(37, Piece.getPieceCode(true, PieceType.BISHOP), true);
        board.setPiece(9, Piece.getPieceCode(true, PieceType.QUEEN), true);

        Game game = new Game(board);
        GameResult result = game.makeMove(move("b2", "a2"));

        // even though pawn could pseudo-legally capture on h6 with check, it is pinned, therefore stalemate
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.STALEMATE, ((DrawResult) result).getDrawType());

    }

    private Move move(String startSquare, String endSquare) {
        return NotationUtils.fromNotation(startSquare, endSquare);
    }

}
