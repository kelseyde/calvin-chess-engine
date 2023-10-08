package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.movegeneration.magic.Magics;

public class PinCalculator {

    private final RayCalculator rayCalculator = new RayCalculator();

    public long calculatePinMask(Board board, boolean isWhite) {

        long pinMask = 0L;
        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        // Calculate possible orthogonal (queen or rook) pins
        long opponentOrthogonalSliders = isWhite ? board.getBlackQueens() | board.getBlackRooks() : board.getWhiteQueens() | board.getWhiteRooks();
        if (opponentOrthogonalSliders != 0) {
            long possibleOrthogonalPinners = Magics.getRookAttacks(kingSquare, opponentOrthogonalSliders) & opponentOrthogonalSliders;
            pinMask += calculatePins(board, kingSquare, friendlies, possibleOrthogonalPinners);
        }

        // Calculate possible diagonal (queen or bishop) pins
        long opponentDiagonalSliders = isWhite ? board.getBlackQueens() | board.getBlackBishops() : board.getWhiteQueens() | board.getWhiteBishops();
        if (opponentDiagonalSliders != 0) {
            long possibleDiagonalPinners = Magics.getBishopAttacks(kingSquare, opponentDiagonalSliders) & opponentDiagonalSliders;
            pinMask += calculatePins(board, kingSquare, friendlies, possibleDiagonalPinners);
        }

        return pinMask;

    }

    private long calculatePins(Board board, int kingSquare, long friendlies, long possiblePinners) {

        long pinMask = 0L;

        while (possiblePinners != 0) {
            int possiblePinner = BitboardUtils.getLSB(possiblePinners);
            long friendliesBetween = rayCalculator.rayBetween(kingSquare, possiblePinner) & friendlies;
            boolean onlyPieceBetween = friendliesBetween > 0 && Long.bitCount(friendliesBetween) == 1;
            if (onlyPieceBetween) {
                pinMask |= friendliesBetween;
            }
            possiblePinners = BitboardUtils.popLSB(possiblePinners);
        }

        return pinMask;

    }



}
