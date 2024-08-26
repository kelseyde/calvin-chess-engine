package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;

import java.time.Duration;
import java.time.Instant;

public record TimeControl(Duration softLimit, Duration hardLimit) {

    static final double SOFT_TIME_FACTOR = 0.6666;
    static final double HARD_TIME_FACTOR = 2.0;
    static final double[] BEST_MOVE_STABILITY_FACTOR = new double[] { 2.50, 1.20, 0.90, 0.80, 0.75 };

    public static TimeControl init(Board board, int timeWhiteMs, int timeBlackMs, int incWhiteMs, int incBlackMs) {

        boolean white = board.isWhiteToMove();
        double time = white ? timeWhiteMs : timeBlackMs;
        double inc = white ? incWhiteMs : incBlackMs;

        double base = time / 20 + inc * 0.75;
        Duration soft = Duration.ofMillis((int) (base * SOFT_TIME_FACTOR));
        Duration hard = Duration.ofMillis((int) (base * HARD_TIME_FACTOR));

        return new TimeControl(soft, hard);

    }

    public boolean isSoftLimitExceeded(Instant start, int bestMoveStability) {
        bestMoveStability = Math.min(bestMoveStability, BEST_MOVE_STABILITY_FACTOR.length - 1);
        Duration expired = Duration.between(start, Instant.now());
        Duration adjustedSoftLimit = Duration.ofMillis((long) (softLimit.toMillis() * BEST_MOVE_STABILITY_FACTOR[bestMoveStability]));
        return expired.compareTo(adjustedSoftLimit) > 0;
    }

    public boolean isHardLimitExceeded(Instant start) {
        Duration expired = Duration.between(start, Instant.now());
        return expired.compareTo(hardLimit) > 0;
    }

}
