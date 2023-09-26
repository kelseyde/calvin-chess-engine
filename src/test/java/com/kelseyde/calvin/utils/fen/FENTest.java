package com.kelseyde.calvin.utils.fen;

import com.kelseyde.calvin.board.Board;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class FENTest {

    @Test
    public void testStartingPosition() {

        Board fenBoard = FEN.fromFEN(FEN.STARTING_POSITION);
        Board newBoard = new Board();
        Assertions.assertEquals(newBoard.getWhitePawns(), fenBoard.getWhitePawns());
        Assertions.assertEquals(newBoard.getWhiteKnights(), fenBoard.getWhiteKnights());
        Assertions.assertEquals(newBoard.getWhiteBishops(), fenBoard.getWhiteBishops());
        Assertions.assertEquals(newBoard.getWhiteRooks(), fenBoard.getWhiteRooks());
        Assertions.assertEquals(newBoard.getWhiteQueens(), fenBoard.getWhiteQueens());
        Assertions.assertEquals(newBoard.getWhiteKing(), fenBoard.getWhiteKing());
        Assertions.assertEquals(newBoard.getBlackPawns(), fenBoard.getBlackPawns());
        Assertions.assertEquals(newBoard.getBlackKnights(), fenBoard.getBlackKnights());
        Assertions.assertEquals(newBoard.getBlackBishops(), fenBoard.getBlackBishops());
        Assertions.assertEquals(newBoard.getBlackRooks(), fenBoard.getBlackRooks());
        Assertions.assertEquals(newBoard.getBlackQueens(), fenBoard.getBlackQueens());
        Assertions.assertEquals(newBoard.getBlackKing(), fenBoard.getBlackKing());

        Assertions.assertEquals(newBoard.getWhitePieces(), fenBoard.getWhitePieces());
        Assertions.assertEquals(newBoard.getBlackPieces(), fenBoard.getBlackPieces());
        Assertions.assertEquals(newBoard.getOccupied(), fenBoard.getOccupied());

        Assertions.assertEquals(newBoard.isWhiteToMove(), fenBoard.isWhiteToMove());
        Assertions.assertEquals(newBoard.getGameState(), fenBoard.getGameState());
        Assertions.assertEquals(new ArrayList<>(newBoard.getGameStateHistory()), new ArrayList<>(fenBoard.getGameStateHistory()));
        Assertions.assertEquals(new ArrayList<>(newBoard.getMoveHistory()), new ArrayList<>(fenBoard.getMoveHistory()));

    }

}