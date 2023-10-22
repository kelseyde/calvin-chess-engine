package com.kelseyde.calvin.movegeneration.check;

import com.kelseyde.calvin.utils.BoardUtils;

public class RayCalculator {

    public long rayBetween(int startSquare, int endSquare) {

        long ray = 0L;

        if (!BoardUtils.isValidIndex(startSquare) || !BoardUtils.isValidIndex(endSquare) || startSquare == endSquare) {
            return 0L;
        }

        int directionOffset = getDirectionOffset(startSquare, endSquare);

        if (directionOffset == 0) {
            return 0L;
        }

        int currentSquare = startSquare + directionOffset;

        while (BoardUtils.isValidIndex(currentSquare)) {
            if (currentSquare == endSquare) {
                break;
            }
            ray |= 1L << currentSquare;
            currentSquare += directionOffset;
        }

        return ray;

    }

    private int getDirectionOffset(int startSquare, int endSquare) {
        int directionOffset = 0;
        if (BoardUtils.getRank(startSquare) == BoardUtils.getRank(endSquare)) {
            directionOffset = startSquare > endSquare ? -1 : 1;
        }
        else if (BoardUtils.getFile(startSquare) == BoardUtils.getFile(endSquare)) {
            directionOffset = startSquare > endSquare ? -8 : 8;
        }
        else if (BoardUtils.getDiagonal(startSquare) == BoardUtils.getDiagonal(endSquare)) {
            directionOffset = startSquare > endSquare ? -7 : 7;
        }
        else if (BoardUtils.getAntiDiagonal(startSquare) == BoardUtils.getAntiDiagonal(endSquare)) {
            directionOffset = startSquare > endSquare ? -9 : 9;
        }
        return directionOffset;
    }

}
