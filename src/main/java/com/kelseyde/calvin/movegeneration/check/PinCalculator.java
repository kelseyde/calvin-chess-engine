package com.kelseyde.calvin.movegeneration.check;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.movegeneration.attacks.Attacks;

public class PinCalculator {

    private final RayCalculator rayCalculator = new RayCalculator();

    private long pinMask;
    private long[] pinRayMasks;

    public record PinData (long pinMask, long[] pinRayMasks) {};

    public PinData calculatePinMask(Board board, boolean isWhite) {

        pinMask = 0L;
        pinRayMasks = new long[64];

        int kingSquare = BitboardUtils.getLSB(board.getKing(isWhite));
        long friendlies = board.getPieces(isWhite);
        long opponents = board.getPieces(!isWhite);

        // Calculate possible orthogonal (queen or rook) pins
        long opponentOrthogonalSliders = board.getRooks(!isWhite) | board.getQueens(!isWhite);
        if (opponentOrthogonalSliders != 0) {
            long possibleOrthogonalPinners = Attacks.rookAttacks(kingSquare, opponentOrthogonalSliders) & opponentOrthogonalSliders;
            calculatePins(kingSquare, friendlies, opponents, possibleOrthogonalPinners);
        }

        // Calculate possible diagonal (queen or bishop) pins
        long opponentDiagonalSliders = board.getBishops(!isWhite) | board.getQueens(!isWhite);
        if (opponentDiagonalSliders != 0) {
            long possibleDiagonalPinners = Attacks.bishopAttacks(kingSquare, opponentDiagonalSliders) & opponentDiagonalSliders;
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
