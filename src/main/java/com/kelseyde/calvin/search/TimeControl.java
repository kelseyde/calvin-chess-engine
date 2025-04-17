package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
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
public record TimeControl(EngineConfig config, Instant start, Duration softTime, Duration hardTime, int softNodes, int hardNodes, int maxDepth) {

    public static TimeControl init(EngineConfig config, Board board, Instant start, GoCommand command) {

        double time;
        double inc;
        if (command.isMovetime()) {
            time = command.movetime();
            Duration movetime = Duration.ofMillis((long) time);
            return new TimeControl(config, start, movetime, movetime, -1, -1, -1);
        }
        else if (command.isTimeAndInc()) {
            boolean white = board.isWhite();
            time = white ? command.wtime() : command.btime();
            inc = white ? command.winc() : command.binc();
        }
        else {
            time = Double.MAX_VALUE;
            inc = 0;
        }

        // If we were sent negative time, just assume we have one second.
        if (time <= 0) time = 1000;
        if (inc < 0) inc = 0;

        double timeFactor = config.timeFactor() / 100.0;
        double incrementFactor = config.incrementFactor() / 100.0;
        double base = time * timeFactor + inc * incrementFactor;

        // Get the full move number from the board
        int fullMoveNumber = board.getPly();

        // Calculate dynamic soft and hard factors based on move number
        double softFactor = 0.025 + 0.05 * (1.0 - Math.exp(-0.034 * (double) fullMoveNumber));
        double hardFactor = 0.135 + 0.21 * (1.0 - Math.exp(-0.030 * (double) fullMoveNumber));

        Duration softLimit = Duration.ofMillis((int) (base * softFactor));
        Duration hardLimit = Duration.ofMillis((int) (base * hardFactor));

        return new TimeControl(config, start, softLimit, hardLimit, command.nodes(), -1, command.depth());
    }

    public boolean isHardLimitReached(int depth, int nodes) {
        if (nodes % 4096 != 0) return false;
        if (hardNodes > 0 && nodes >= hardNodes) return true;
        if (maxDepth > 0 && depth >= maxDepth) return true;
        final Duration expired = Duration.between(start, Instant.now());
        final Duration overhead = Duration.ofMillis(config.uciOverhead());
        return expired.compareTo(hardTime.minus(overhead)) > 0;
    }

    public boolean isSoftLimitReached(int depth, int nodes, int bestMoveNodes, int bestMoveStability, int evalStability) {
        if (maxDepth > 0 && depth >= maxDepth) return true;
        if (softNodes > 0 && nodes >= softNodes) return true;
        final Duration expired = Duration.between(start, Instant.now());
        final Duration adjustedSoftLimit = adjustSoftLimit(softTime, nodes, bestMoveNodes, bestMoveStability, evalStability, depth);
        return expired.compareTo(adjustedSoftLimit) > 0;
    }

    private Duration adjustSoftLimit(
            Duration softLimit, int nodes, int bestMoveNodes, int bestMoveStability, int scoreStability, int depth) {

        int overhead = config.uciOverhead();
        long hardLimit = hardTime.toMillis() - overhead;

        // Apply soft-limit scaling heuristics
        double scale = 1.0
                * bestMoveStabilityFactor(config, depth, bestMoveStability)
                * scoreStabilityFactor(config, depth, scoreStability)
                * nodeTmFactor(config, depth, bestMoveNodes, nodes);

        // Clamp the scale factor to the configured min/max values
        scale = clampScale(scale);

        // Clamp the scaled limit to the hard limit
        long limit = (long) Math.min(softLimit.toMillis() * scale, hardLimit);

        return Duration.ofMillis(limit);

    }

    // Scale the soft limit based on the stability of the best move. If the best move has remained stable for several
    // iterations, we can safely assume that we don't need to spend as much time searching further.
    private double bestMoveStabilityFactor(EngineConfig config, int depth, int bestMoveStability) {
        if (depth < config.bmStabilityMinDepth()) {
            return 1.0;
        }
        bestMoveStability = Math.min(bestMoveStability, config.bmStabilityFactor().length - 1);
        return config.bmStabilityFactor()[bestMoveStability] / 100.0;
    }

    // Scale the soft limit based on the stability of the search score. If the evaluation has remained stable for
    // several iterations, we can safely assume that we don't need to spend as much time searching further.
    private double scoreStabilityFactor(EngineConfig config, int depth, int scoreStability) {
        if (depth < config.scoreStabilityMinDepth()) {
            return 1.0;
        }
        scoreStability = Math.min(scoreStability, config.scoreStabilityFactor().length - 1);
        return config.scoreStabilityFactor()[scoreStability] / 100.0;
    }

    // Scale the soft limit based on the fraction of total nodes spent searching the best move. If a greater portion
    // of the search has been spent on the best move, we can assume that the best move is more likely to be correct,
    // and therefore we can spend less time searching further.
    private double nodeTmFactor(EngineConfig config, int depth, int bestMoveNodes, int nodes) {
        if (depth < config.nodeTmMinDepth()) {
            return 1.0;
        }
        double bestMoveNodeFraction = (double) bestMoveNodes / nodes;
        double nodeTmBase = (double) config.nodeTmBase() / 100;
        double nodeTmScale = (double) config.nodeTmScale() / 100;
        return (nodeTmBase - bestMoveNodeFraction) * nodeTmScale;
    }

    private double clampScale(double scale) {
        double scaleMin = config.softTimeScaleMin() / 100.0;
        double scaleMax = config.softTimeScaleMax() / 100.0;
        return Math.min(Math.max(scale, scaleMin), scaleMax);
    }

}
