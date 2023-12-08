package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.GameState;
import com.kelseyde.calvin.search.Search;

import java.util.Iterator;

public class Result {

    /**
     * Check for an 'effective' draw, which treats a single repetition of the position as a draw.
     * This is used during {@link Search} to quickly check for a draw; it will lead to some errors in edge cases, but the
     * gamble is that the boost in search speed is worth the potential cost.
     */
    public static boolean isEffectiveDraw(Board board) {
        return isDoubleRepetition(board) || isFiftyMoveRule(board) || isInsufficientMaterial(board);
    }

    public static boolean isThreefoldRepetition(Board board) {

        int repetitionCount = 0;
        long zobrist = board.getGameState().getZobristKey();
        Iterator<GameState> iterator = board.getGameStateHistory().descendingIterator();
        while (iterator.hasNext()) {
            GameState gameState = iterator.next();
            if (gameState.getZobristKey() == zobrist) {
                repetitionCount += 1;
            }
        }
        return repetitionCount >= 2;

    }

    public static boolean isDoubleRepetition(Board board) {

        long zobrist = board.getGameState().getZobristKey();
        Iterator<GameState> iterator = board.getGameStateHistory().descendingIterator();
        while (iterator.hasNext()) {
            GameState gameState = iterator.next();
            if (gameState.getZobristKey() == zobrist) {
                return true;
            }
        }
        return false;

    }

    public static boolean isInsufficientMaterial(Board board) {
        if (board.getWhitePawns() != 0 || board.getWhiteRooks() != 0 || board.getWhiteQueens() != 0
                || board.getBlackPawns() != 0 || board.getBlackRooks() != 0 || board.getBlackQueens() != 0) {
            return false;
        }
        long whitePieces = board.getWhiteKnights() | board.getWhiteBishops();
        long blackPieces = board.getBlackKnights() |  board.getBlackBishops();

        return (Bitwise.countBits(whitePieces) == 0 || Bitwise.countBits(whitePieces) == 1)
                && (Bitwise.countBits(blackPieces) == 0 || Bitwise.countBits(blackPieces) == 1);
    }

    public static boolean isFiftyMoveRule(Board board) {
        return board.getGameState().getFiftyMoveCounter() >= 100;
    }


}
