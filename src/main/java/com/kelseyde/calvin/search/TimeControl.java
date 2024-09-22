package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.uci.UCICommand.GoCommand;

import java.time.Duration;
import java.time.Instant;

/**
 * The amount of time the engine chooses to search is split into to two limits: hard and soft. The hard limit is checked
 * constantly during search, and the search is aborted as soon as it is reached. The soft limit is checked at the start
 * of each iterative deepening loop, and the engine does not bother starting a new search if it is reached.
 * </p>
 * The idea is that if the engine is unlikely to finish a new iteration before hitting the hard limit, then there's no
 * point starting the iteration, since the time spent doing so is mostly wasted. That time can therefore be saved for
 * subsequent moves.
 */
public record TimeControl(Duration softTime, Duration hardTime, int softNodes, int hardNodes, int maxDepth) {

    static final double SOFT_TIME_FACTOR = 0.6666;
    static final double HARD_TIME_FACTOR = 2.0;

    static final double SOFT_TIME_SCALE_MIN = 0.125;
    static final double SOFT_TIME_SCALE_MAX = 2.5;

    static final double[] BEST_MOVE_STABILITY_FACTOR = new double[] { 2.50, 1.20, 0.90, 0.80, 0.75 };
    static final double[] SCORE_STABILITY_FACTOR = new double[] { 1.25, 1.15, 1.00, 0.94, 0.88 };

    static final int NODE_TM_MIN_DEPTH = 5;
    static final double NODE_TM_BASE = 1.5;
    static final double NODE_TM_SCALE = 1.35;

    static final int UCI_OVERHEAD = 50;

    public static TimeControl init(Board board, GoCommand command) {

        double time;
        double inc;
        if (command.isMovetime()) {
            time = command.movetime();
            Duration movetime = Duration.ofMillis((long) time);
            return new TimeControl(movetime, movetime, -1, -1, -1);
        } else if (command.isTimeAndInc()) {
            boolean white = board.isWhite();
            time = white ? command.wtime() : command.btime();
            inc = white ? command.winc() : command.binc();
        } else {
            time = Double.MAX_VALUE;
            inc = 0;
        }

        // If we were sent negative time, just assume we have one second.
        if (time <= 0) time = 1000;
        if (inc < 0) inc = 0;

        double base = time / 20 + inc * 0.75;
        Duration soft = Duration.ofMillis((int) (base * SOFT_TIME_FACTOR));
        Duration hard = Duration.ofMillis((int) (base * HARD_TIME_FACTOR));

        return new TimeControl(soft, hard, command.nodes(), -1, command.depth());

    }

    public boolean isHardLimitReached(Instant start, int depth, int nodes) {
        if (nodes % 4096 != 0) return false;
        if (hardNodes > 0 && nodes >= hardNodes) return true;
        if (maxDepth > 0 && depth >= maxDepth) return true;
        Duration expired = Duration.between(start, Instant.now());
        return expired.compareTo(hardTime) > 0;
    }

    public boolean isSoftLimitReached(
            Instant start, int depth, int nodes, int bestMoveNodes, int bestMoveStability, int evalStability) {
        if (maxDepth > 0 && depth >= maxDepth) return true;
        if (softNodes > 0 && nodes >= softNodes) return true;
        Duration expired = Duration.between(start, Instant.now());
        Duration adjustedSoftLimit = adjustSoftLimit(softTime, nodes, bestMoveNodes, bestMoveStability, evalStability, depth);
        return expired.compareTo(adjustedSoftLimit) > 0;
    }

    private Duration adjustSoftLimit(
            Duration softLimit, int nodes, int bestMoveNodes, int bestMoveStability, int scoreStability, int depth) {

        double scale = 1.0;

        // Scale the soft limit based on the stability of the best move. If the best move has remained stable for several
        // iterations, we can safely assume that we don't need to spend as much time searching further.
        bestMoveStability = Math.min(bestMoveStability, BEST_MOVE_STABILITY_FACTOR.length - 1);
        scale *= BEST_MOVE_STABILITY_FACTOR[bestMoveStability];

        // Scale the soft limit based on the stability of the search score. If the evaluation has remained stable for
        // several iterations, we can safely assume that we don't need to spend as much time searching further.
        scoreStability = Math.min(scoreStability, SCORE_STABILITY_FACTOR.length - 1);
        scale *= SCORE_STABILITY_FACTOR[scoreStability];

        // Scale the soft limit based on the fraction of total nodes spent searching the best move. If a greater portion
        // of the search has been spent on the best move, we can assume that the best move is more likely to be correct,
        // and therefore we can spend less time searching further.
        if (depth > NODE_TM_MIN_DEPTH && bestMoveNodes > 0) {
            double bestMoveNodeFraction = (double) bestMoveNodes / nodes;
            scale *= (NODE_TM_BASE - bestMoveNodeFraction) * NODE_TM_SCALE;
        }

        // Clamp the scale factor to a reasonable range.
        scale = Math.min(Math.max(scale, SOFT_TIME_SCALE_MIN), SOFT_TIME_SCALE_MAX);

        long scaled = (long) (softLimit.toMillis() * scale);

        // Ensure the scaled limit is at least the hard limit plus some overhead to account for UCI communication.
        scaled = Math.min(scaled, hardTime.toMillis() - UCI_OVERHEAD);

        return Duration.ofMillis(scaled);

    }

}
