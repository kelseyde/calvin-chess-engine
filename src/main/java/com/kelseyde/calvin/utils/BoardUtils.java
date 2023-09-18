package com.kelseyde.calvin.utils;

import java.util.List;

public class BoardUtils {

    public static final List<Integer> A_FILE = List.of(0, 8, 16, 24, 32, 40, 48, 56);
    public static final List<Integer> B_FILE = List.of(1, 9, 17, 25, 33, 41, 49, 57);
    public static final List<Integer> C_FILE = List.of(2, 10, 18, 26, 34, 42, 50, 58);
    public static final List<Integer> D_FILE = List.of(3, 11, 19, 27, 35, 43, 51, 59);
    public static final List<Integer> E_FILE = List.of(4, 12, 20, 28, 36, 44, 52, 60);
    public static final List<Integer> F_FILE = List.of(5, 13, 21, 29, 37, 45, 53, 61);
    public static final List<Integer> G_FILE = List.of(6, 14, 22, 30, 38, 46, 54, 62);
    public static final List<Integer> H_FILE = List.of(7, 15, 23, 31, 39, 47, 55, 63);

    public static final List<Integer> FIRST_RANK = List.of(0, 1, 2, 3, 4, 5, 6, 7);
    public static final List<Integer> SECOND_RANK = List.of(8, 9, 10, 11, 12, 13, 14, 15);
    public static final List<Integer> THIRD_RANK = List.of(16, 17, 18, 19, 20, 21, 22, 23);
    public static final List<Integer> FOURTH_RANK = List.of(24, 25, 26, 27, 28, 29, 30, 31);
    public static final List<Integer> FIFTH_RANK = List.of(32, 33, 34, 35, 36, 37, 38, 39);
    public static final List<Integer> SIXTH_RANK = List.of(40, 41, 42, 43, 44, 45, 46, 47);
    public static final List<Integer> SEVENTH_RANK = List.of(48, 49, 50, 51, 52, 53, 54, 55);
    public static final List<Integer> EIGHTH_RANK = List.of(56, 57, 58, 59, 60, 61, 62, 63);

    public static String getRank(int square) {
        if (FIRST_RANK.contains(square)) {
            return "1";
        } else if (SECOND_RANK.contains(square)) {
            return "2";
        } else if (THIRD_RANK.contains(square)) {
            return "3";
        } else if (FOURTH_RANK.contains(square)) {
            return "4";
        } else if (FIFTH_RANK.contains(square)) {
            return "5";
        } else if (SIXTH_RANK.contains(square)) {
            return "6";
        } else if (SEVENTH_RANK.contains(square)) {
            return "7";
        } else if (EIGHTH_RANK.contains(square)) {
            return "8";
        } else {
            // TODO rewrite this mess
            throw new IllegalArgumentException();
        }
    }

    public static String getFile(int square) {
        if (A_FILE.contains(square)) {
            return "a";
        } else if (B_FILE.contains(square)) {
            return "b";
        } else if (C_FILE.contains(square)) {
            return "c";
        } else if (D_FILE.contains(square)) {
            return "d";
        } else if (E_FILE.contains(square)) {
            return "e";
        } else if (F_FILE.contains(square)) {
            return "f";
        } else if (G_FILE.contains(square)) {
            return "g";
        } else if (H_FILE.contains(square)) {
            return "h";
        } else {
            // TODO rewrite this mess
            throw new IllegalArgumentException();
        }
    }


}
