package com.kelseyde.calvin.search.picker;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class MoveBonus {

    static final int MILLION = 1000000;

    static final int TT_MOVE_BONUS = 10 * MILLION;
    static final int QUEEN_PROMO_BONUS = 9 * MILLION;
    static final int WINNING_CAPTURE_BONUS = 8 * MILLION;
    static final int KILLER_MOVE_BONUS = 7 * MILLION;
    static final int LOSING_CAPTURE_BONUS = 6 * MILLION;
    static final int QUIET_MOVE_BONUS = 5 * MILLION;
    static final int UNDER_PROMO_BONUS = 4 * MILLION;

    static final int MVV_OFFSET = 5000;
    static final int KILLER_OFFSET = 10000;

}
