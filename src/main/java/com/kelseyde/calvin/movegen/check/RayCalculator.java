package com.kelseyde.calvin.movegen.check;

import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Rank;
import com.kelseyde.calvin.board.Bits.Square;

public class RayCalculator {

    /**
     * Calculates the ray (bitboard) between two squares on the chessboard.
     *
     * @param from The starting square index (0-63).
     * @param to The ending square index (0-63).
     * @return A bitboard representing the ray between the start and end squares,
     *         or 0L if there is no valid ray.
     */
    public long rayBetween(int from, int to) {
        // Check for valid square indices and that the squares are not the same
        if (!Square.isValid(from) || !Square.isValid(to) || (from == to)) {
            return 0L;
        }

        int directionOffset = getDirectionOffset(from, to);

        // If there's no valid direction offset, return 0L
        if (directionOffset == 0) {
            return 0L;
        }

        long ray = 0L;
        int currentSquare = from + directionOffset;

        // Build the ray by setting bits for each square between from and to
        while (Square.isValid(currentSquare) && currentSquare != to) {
            ray |= 1L << currentSquare;
            currentSquare += directionOffset;
        }

        // Return the ray, which includes squares between from and to
        return ray;
    }

    /**
     * Determines the direction offset between two squares on the chessboard.
     *
     * @param from The starting square index (0-63).
     * @param to The ending square index (0-63).
     * @return The direction offset to traverse from the start square to the end square,
     *         or 0 if there is no valid direction.
     */
    private int getDirectionOffset(int from, int to) {
        int startRank = Rank.of(from);
        int endRank = Rank.of(to);
        int startFile = File.of(from);
        int endFile = File.of(to);

        // Check if the two squares are on the same rank (row)
        if (startRank == endRank) {
            return from > to ? -1 : 1;
        }
        // Check if the two squares are on the same file (column)
        else if (startFile == endFile) {
            return from > to ? -8 : 8;
        }
        // Check if the two squares are on the same diagonal
        else if (Math.abs(startRank - endRank) == Math.abs(startFile - endFile)) {
            return from > to ? (from - to) % 9 == 0 ? -9 : -7 : (to - from) % 9 == 0 ? 9 : 7;
        }
        // Check if the two squares are on the same anti-diagonal
        else if (startRank + startFile == endRank + endFile) {
            return from > to ? -9 : 9;
        }

        // No valid direction found
        return 0;
    }

}
