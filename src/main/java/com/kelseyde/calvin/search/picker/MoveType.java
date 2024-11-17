package com.kelseyde.calvin.search.picker;

public enum MoveType {

    TT_MOVE     (5_000_000),
    GOOD_NOISY  (4_000_000),
    KILLER      (3_000_000),
    QUIET       (2_000_000),
    BAD_NOISY   (1_000_000);

    public static final int MVV_OFFSET = 5000;
    public static final int KILLER_OFFSET = 10000;

    final int bonus;

    MoveType(int bonus) {
        this.bonus = bonus;
    }

}
