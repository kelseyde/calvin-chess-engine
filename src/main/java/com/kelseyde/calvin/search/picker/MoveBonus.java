package com.kelseyde.calvin.search.picker;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class MoveBonus {

    static final int MILLION = 1000000;

    static final int TT_MOVE_BONUS = 10 * MILLION;
    static final int QUEEN_PROMOTION_BIAS = 9 * MILLION;
    static final int WINNING_CAPTURE_BIAS = 8 * MILLION;
    static final int KILLER_MOVE_BIAS = 7 * MILLION;
    static final int LOSING_CAPTURE_BIAS = 6 * MILLION;
    static final int QUIET_MOVE_BIAS = 5 * MILLION;
    static final int UNDER_PROMOTION_BIAS = 4 * MILLION;

    static final int MVV_OFFSET = 5000;
    static final int KILLER_BONUS = 10000;

}
