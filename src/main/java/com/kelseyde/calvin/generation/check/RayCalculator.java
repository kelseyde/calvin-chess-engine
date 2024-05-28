package com.kelseyde.calvin.generation.check;

import com.kelseyde.calvin.utils.BoardUtils;

public class RayCalculator {

    /**
     * Calculates the ray (bitboard) between two squares on the chessboard.
     *
     * @param startSquare The starting square index (0-63).
     * @param endSquare The ending square index (0-63).
     * @return A bitboard representing the ray between the start and end squares,
     *         or 0L if there is no valid ray.
     */
    public long rayBetween(int startSquare, int endSquare) {
        // Check for valid square indices and that the squares are not the same
        if (!BoardUtils.isValidIndex(startSquare) || !BoardUtils.isValidIndex(endSquare) || startSquare == endSquare) {
            return 0L;
        }

        int directionOffset = getDirectionOffset(startSquare, endSquare);

        // If there's no valid direction offset, return 0L
        if (directionOffset == 0) {
            return 0L;
        }

        long ray = 0L;
        int currentSquare = startSquare + directionOffset;

        // Build the ray by setting bits from startSquare to endSquare
        while (BoardUtils.isValidIndex(currentSquare) && currentSquare != endSquare) {
            ray |= 1L << currentSquare;
            currentSquare += directionOffset;
        }

        // Return the ray, which includes squares between startSquare and endSquare
        return ray;
    }

    /**
     * Determines the direction offset between two squares on the chessboard.
     *
     * @param startSquare The starting square index (0-63).
     * @param endSquare The ending square index (0-63).
     * @return The direction offset to traverse from startSquare to endSquare,
     *         or 0 if there is no valid direction.
     */
    private int getDirectionOffset(int startSquare, int endSquare) {
        int startRank = BoardUtils.getRank(startSquare);
        int endRank = BoardUtils.getRank(endSquare);
        int startFile = BoardUtils.getFile(startSquare);
        int endFile = BoardUtils.getFile(endSquare);

        // Check if the two squares are on the same rank (row)
        if (startRank == endRank) {
            return startSquare > endSquare ? -1 : 1;
        }
        // Check if the two squares are on the same file (column)
        else if (startFile == endFile) {
            return startSquare > endSquare ? -8 : 8;
        }
        // Check if the two squares are on the same diagonal
        else if (BoardUtils.getDiagonal(startSquare) == BoardUtils.getDiagonal(endSquare)) {
            return startSquare > endSquare ? -7 : 7;
        }
        // Check if the two squares are on the same anti-diagonal
        else if (BoardUtils.getAntiDiagonal(startSquare) == BoardUtils.getAntiDiagonal(endSquare)) {
            return startSquare > endSquare ? -9 : 9;
        }

        // No valid direction found
        return 0;
    }

}
