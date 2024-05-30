package com.kelseyde.calvin.evaluation.hce;

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

    /**
     * Determine if the position is 'drawish', i.e. still technically winnable but with perfect play should be a draw.
     * Credit to Blunder chess engine (<a href="https://github.com/deanmchris/blunder">...</a>) for the formula.
     */
    public static boolean isDrawish(Material whiteMaterial, Material blackMaterial) {
        int pawns = whiteMaterial.pawns() + blackMaterial.pawns();
        if (pawns > 0) return false;

        int whiteKnights = whiteMaterial.knights();
        int blackKnights = blackMaterial.knights();
        int whiteBishops = whiteMaterial.bishops();
        int blackBishops = blackMaterial.bishops();
        int whiteRooks = whiteMaterial.rooks();
        int blackRooks = blackMaterial.rooks();
        int whiteQueens = whiteMaterial.queens();
        int blackQueens = blackMaterial.queens();

        int knights = whiteKnights + blackKnights;
        int bishops = whiteBishops + blackBishops;
        int rooks = whiteRooks + blackRooks;
        int queens = whiteQueens + blackQueens;

        int whiteMinors = whiteKnights + whiteBishops;
        int blackMinors = blackKnights + blackBishops;

        int minors = knights + bishops;
        int majors = rooks + queens;
        int all = majors + minors;

        return
            // KQ v KQ
            (all == 2 && blackQueens == 1 && whiteQueens == 1) ||
            // KR v KR
            (all == 2 && blackRooks == 1 && whiteRooks == 1) ||
            // KN v KB
            // KB v KB
            (all == 2 && whiteMinors == 1 && blackMinors == 1) ||
            // KQ v KRR
            (all == 3 && ((whiteQueens == 1 && blackRooks == 2) || (blackQueens == 1 && whiteRooks == 2))) ||
            // KQ vs KBB
            (all == 3 && ((whiteQueens == 1 && blackBishops == 2) || (blackQueens == 1 && whiteBishops == 2))) ||
            // KQ vs KNN
            (all == 3 && ((whiteQueens == 1 && blackKnights == 2) || (blackQueens == 1 && whiteKnights == 2))) ||
            // KNN v KN
            // KNN v KB
            // KNN v K
            (all <= 3 && ((whiteKnights == 2 && blackMinors <= 1) || (blackKnights == 2 && whiteMinors <= 1))) ||
            // KQ vs KRN
            // KQ vs KRB
            (all == 3 && ((whiteQueens == 1 && blackRooks == 1 && blackMinors == 1) || (blackQueens == 1 && whiteRooks == 1 && whiteMinors == 1))) ||
            // KR vs KRB
            // KR vs KRN
            (all == 3 && ((whiteRooks == 1 && blackRooks == 1 && blackMinors == 1) || (blackRooks == 1 && whiteRooks == 1 && whiteMinors == 1))) ||
            // KRR v KRB
            // KRR v KRN
            (all == 4 && ((whiteRooks == 2 && blackRooks == 1 && blackMinors == 1) || (blackRooks == 2 && whiteRooks == 1 && whiteMinors == 1)));

    }

    public static boolean isFiftyMoveRule(Board board) {
        return board.getGameState().getFiftyMoveCounter() >= 100;
    }


}
