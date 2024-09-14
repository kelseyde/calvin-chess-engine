package com.kelseyde.calvin.search.picker;

public class MoveBonus {

    public static final int MILLION = 1000000;
    public static final int TT_MOVE_BONUS = 10 * MILLION;
    public static final int QUEEN_PROMOTION_BIAS = 9 * MILLION;
    public static final int WINNING_CAPTURE_BIAS = 8 * MILLION;
    public static final int KILLER_MOVE_BIAS = 7 * MILLION;
    public static final int LOSING_CAPTURE_BIAS = 6 * MILLION;
    public static final int QUIET_MOVE_BIAS = 5 * MILLION;
    public static final int UNDER_PROMOTION_BIAS = 4 * MILLION;
    public static final int CASTLING_BIAS = 3 * MILLION;

    public static final int MVV_OFFSET = 5000;
    public static final int KILLER_BONUS = 10000;

}
