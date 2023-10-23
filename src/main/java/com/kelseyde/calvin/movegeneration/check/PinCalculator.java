package com.kelseyde.calvin.movegeneration.check;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.movegeneration.magic.Magics;

public class PinCalculator {

    private final RayCalculator rayCalculator = new RayCalculator();

    private long pinMask;
    private long[] pinRayMasks;

    public record PinData (long pinMask, long[] pinRayMasks) {};

    public PinData calculatePinMask(Board board, boolean isWhite) {

        pinMask = 0L;
        pinRayMasks = new long[64];

        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        long opponents = isWhite ? board.getBlackPieces() : board.getWhitePieces();

        // Calculate possible orthogonal (queen or rook) pins
        long opponentOrthogonalSliders = isWhite ? board.getBlackQueens() | board.getBlackRooks() : board.getWhiteQueens() | board.getWhiteRooks();
        if (opponentOrthogonalSliders != 0) {
            long possibleOrthogonalPinners = Magics.getRookAttacks(kingSquare, opponentOrthogonalSliders) & opponentOrthogonalSliders;
            calculatePins(kingSquare, friendlies, opponents, possibleOrthogonalPinners);
        }

        // Calculate possible diagonal (queen or bishop) pins
        long opponentDiagonalSliders = isWhite ? board.getBlackQueens() | board.getBlackBishops() : board.getWhiteQueens() | board.getWhiteBishops();
        if (opponentDiagonalSliders != 0) {
            long possibleDiagonalPinners = Magics.getBishopAttacks(kingSquare, opponentDiagonalSliders) & opponentDiagonalSliders;
            calculatePins(kingSquare, friendlies, opponents, possibleDiagonalPinners);
        }

        return new PinData(pinMask, pinRayMasks);

    }


    private void calculatePins(int kingSquare, long friendlies, long opponents, long possiblePinners) {

        while (possiblePinners != 0) {
            int possiblePinner = BitboardUtils.getLSB(possiblePinners);
            long ray = rayCalculator.rayBetween(kingSquare, possiblePinner);
            long opponentsBetween = ray & opponents;
            if (opponentsBetween > 0) {
                possiblePinners = BitboardUtils.popLSB(possiblePinners);
                continue;
            }
            long friendliesBetween = ray & friendlies;
            boolean onlyPieceBetween = friendliesBetween > 0 && Long.bitCount(friendliesBetween) == 1;
            if (onlyPieceBetween) {
                int friendlySquare = BitboardUtils.getLSB(friendliesBetween);
                pinMask |= friendliesBetween;
                pinRayMasks[friendlySquare] = ray | 1L << possiblePinner;
            }
            possiblePinners = BitboardUtils.popLSB(possiblePinners);
        }

    }

}
