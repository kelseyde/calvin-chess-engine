package com.kelseyde.calvin.utils;

import java.util.Set;

public class BoardUtils {

    public static boolean isValidSquareCoordinate(int square) {
        return square >=0 && square < 64;
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

}
