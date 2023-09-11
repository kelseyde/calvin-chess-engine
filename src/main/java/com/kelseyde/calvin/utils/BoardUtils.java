package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Piece;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

public class BoardUtils {

    private static final String BOLD = "\033[0;1m";

    public static Board fromCharArray(Character[] chars) {
        return new Board(Arrays.stream(chars)
                .map(Piece::fromChar)
                .toArray(Piece[]::new));
    }

    public static Character[] toCharArray(Board board) {
        return Arrays.stream(board.getSquares())
                .map(Optional::ofNullable)
                .map(piece -> piece.map(Piece::toChar).orElse('x'))
                .toArray(Character[]::new);
    }

    public static String toFormattedBoardString(Board board) {
        Character[] pieceCharArray = toCharArray(board);
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, 64)
                .boxed()
                .sorted(Collections.reverseOrder())
                .forEach(square -> {
                    sb.append(" ").append(pieceCharArray[square]).append(" ");
                    if (isAFile(square)) {
                        sb.append("\n");
                    }
        });
        return sb.toString();
    }

    public static boolean isValidSquareCoordinate(int square) {
        return square >= 0 && square < 64;
    }

    public static boolean isAFile(int square) {
        return Set.of(0, 8, 16, 24, 32, 40, 48, 56).contains(square);
    }

    public static boolean isBFile(int square) {
        return Set.of(1, 9, 17, 25, 33, 41, 49, 57).contains(square);
    }

    public static boolean isGFile(int square) {
        return Set.of(6, 14, 22, 30, 38, 46, 54, 62).contains(square);
    }

    public static boolean isHFile(int square) {
        return Set.of(7, 15, 23, 31, 39, 47, 55, 63).contains(square);
    }

    public static boolean isSecondRank(int square) {
        return Set.of(8, 9, 10, 11, 12, 13, 14, 15).contains(square);
    }

    public static boolean isSeventhRank(int square) {
        return Set.of(48, 49, 50, 51, 52, 53, 54, 55).contains(square);
    }

}
