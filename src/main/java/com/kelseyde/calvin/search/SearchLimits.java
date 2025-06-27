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
public record SearchLimits(EngineConfig config,
                           Instant start,
                           Duration softTime,
                           Duration hardTime,
                           int softNodes,
                           int hardNodes,
                           int maxDepth) {

    // Initialise search limits for a new search based on the content of the Go command from UCI.
    // The command may specify a movetime, or a time and increment, or a node limit, or a depth limit.
    public static SearchLimits init(EngineConfig config,
                                    Board board,
                                    Instant start,
                                    GoCommand command) {

        if (command.isMovetime())
            return moveTimeLimit(config, start, command);
        
        double base = baseTime(config, board, command);
        Duration softLimit = softTimeLimit(config, base);
        Duration hardLimit = hardTimeLimit(config, base);

        return new SearchLimits(config, start, softLimit, hardLimit, command.nodes(), -1, command.depth());

    }

    // The hard limit is the final limit that the engine will respect during search. Exceeding this limit
    // will likely result in the game being adjudicated as a forfeit by the match runner or GUI.
    public boolean isHardLimitReached(int depth, int nodes) {

        return nodes % 4096 == 0
            && (isHardNodeLimitReached(nodes)
                || isMaxDepthReached(depth)
                || isHardTimeLimitReached());

    }

    // The soft limit is a limit that the engine will check at the start of each iterative deepening loop. If the limit
    // is exceeded, the engine will not start a new search iteration, but will instead return the best move found so far.
    // The idea is to not waste time on a new search iteration if it is unlikely to finish before the hard limit is reached.
    public boolean isSoftLimitReached(int depth,
                                      int nodes,
                                      int bestMoveNodes,
                                      int bestMoveStability,
                                      int scoreStability,
                                      int rootCorrplexity) {

        return isMaxDepthReached(depth)
            || isSoftNodeLimitReached(nodes)
            || isSoftTimeLimitReached(depth, nodes, bestMoveNodes, bestMoveStability, scoreStability, rootCorrplexity);

    }

    private boolean isHardNodeLimitReached(int nodes) {
        return hardNodes > 0 && nodes >= hardNodes;
    }

    private boolean isSoftNodeLimitReached(int nodes) {
        return softNodes > 0 && nodes >= softNodes;
    }

    private boolean isMaxDepthReached(int depth) {
        return maxDepth > 0 && depth >= maxDepth;
    }

    private boolean isHardTimeLimitReached() {

        Duration expired = Duration.between(start, Instant.now());
        Duration overhead = Duration.ofMillis(config.uciOverhead());
        return expired.compareTo(hardTime.minus(overhead)) > 0;

    }

    private boolean isSoftTimeLimitReached(int depth,
                                           int nodes,
                                           int bestMoveNodes,
                                           int bestMoveStability,
                                           int scoreStability,
                                           int rootCorrplexity) {

        Duration expired = Duration.between(start, Instant.now());
        Duration adjustedSoftLimit = adjustSoftLimit(softTime, nodes, bestMoveNodes, bestMoveStability, scoreStability, rootCorrplexity, depth);
        return expired.compareTo(adjustedSoftLimit) > 0;

    }

    private Duration adjustSoftLimit(Duration softLimit,
                                     int nodes,
                                     int bestMoveNodes,
                                     int bestMoveStability,
                                     int scoreStability,
                                     int rootCorrplexity,
                                     int depth) {

        int overhead = config.uciOverhead();
        long hardLimit = hardTime.toMillis() - overhead;

        // Apply soft-limit scaling heuristics
        double scale = 1.0
                * bestMoveStabilityFactor(config, depth, bestMoveStability)
                * scoreStabilityFactor(config, depth, scoreStability)
                * rootCorrplexityFactor(config, depth, rootCorrplexity)
                * nodeTmFactor(config, depth, bestMoveNodes, nodes);

        // Clamp the scale factor to the configured min/max values
        scale = clampScale(scale);

        // Clamp the scaled limit to the hard limit
        long limit = (long) Math.min(softLimit.toMillis() * scale, hardLimit);

        return Duration.ofMillis(limit);

    }

    // Scale the soft limit based on the stability of the best move. If the best move has remained stable for several
    // iterations, we can safely assume that we don't need to spend as much time searching further.
    private double bestMoveStabilityFactor(EngineConfig config,
                                           int depth,
                                           int bestMoveStability) {

        if (depth < config.bmStabilityMinDepth())
            return 1.0;
        bestMoveStability = Math.min(bestMoveStability, config.bmStabilityFactor().length - 1);
        return config.bmStabilityFactor()[bestMoveStability] / 100.0;

    }

    // Scale the soft limit based on the stability of the search score. If the evaluation has remained stable for
    // several iterations, we can safely assume that we don't need to spend as much time searching further.
    private double scoreStabilityFactor(EngineConfig config,
                                        int depth,
                                        int scoreStability) {

        if (depth < config.scoreStabilityMinDepth())
            return 1.0;
        scoreStability = Math.min(scoreStability, config.scoreStabilityFactor().length - 1);
        return config.scoreStabilityFactor()[scoreStability] / 100.0;

    }

    // Scale the soft limit based on the corrplexity of the root position. If the position is complex, we can assume
    // that the best move is less likely to be correct, and therefore we should spend more time searching.
    private double rootCorrplexityFactor(EngineConfig config, int depth, int corrplexity) {

        if (depth < config.rootCorrplexityMinDepth())
            return 1.0;
        corrplexity = Math.min(Math.abs(corrplexity) / config.rootCorrplexityDivisor(), config.rootCorrplexityFactor().length - 1);
        return config.rootCorrplexityFactor()[corrplexity] / 100.0;

    }

    // Scale the soft limit based on the fraction of total nodes spent searching the best move. If a greater portion
    // of the search has been spent on the best move, we can assume that the best move is more likely to be correct,
    // and therefore we can spend less time searching further.
    private double nodeTmFactor(EngineConfig config,
                                int depth,
                                int bestMoveNodes,
                                int nodes) {

        if (depth < config.nodeTmMinDepth())
            return 1.0;
        double bestMoveNodeFraction = (double) bestMoveNodes / nodes;
        double nodeTmBase = (double) config.nodeTmBase() / 100;
        double nodeTmScale = (double) config.nodeTmScale() / 100;
        return (nodeTmBase - bestMoveNodeFraction) * nodeTmScale;

    }
    
    private static double baseTime(EngineConfig config, Board board, GoCommand command) {

        double time;
        double inc;
        if (command.isTimeAndInc()) {
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
        return time * timeFactor + inc * incrementFactor;

    }

    private static SearchLimits moveTimeLimit(EngineConfig config, Instant start, GoCommand command) {
        Duration movetime = Duration.ofMillis(command.movetime());
        return new SearchLimits(config, start, movetime, movetime, -1, -1, -1);
    }

    private static Duration hardTimeLimit(EngineConfig config, double base) {
        return Duration.ofMillis((int) (base * (config.hardTimeFactor() / 100.0)));
    }

    private static Duration softTimeLimit(EngineConfig config, double base) {
        return Duration.ofMillis((int) (base * (config.softTimeFactor() / 100.0)));
    }

    private double clampScale(double scale) {
        double scaleMin = config.softTimeScaleMin() / 100.0;
        double scaleMax = config.softTimeScaleMax() / 100.0;
        return Math.min(Math.max(scale, scaleMin), scaleMax);
    }

}
