package com.kelseyde.calvin.search;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class SearchStatistics {

    private Instant start;
    private Instant end;

    int maxDepth = 0;
    int nodesSearched = 0;
    int cutOffs = 0;
    int transpositions = 0;
    int quiescents = 0;
    int killers = 0;
    Map<Integer, Duration> depthDurationMap = new HashMap<>();

    public void incrementDepth(int depth, Instant depthStart, Instant depthEnd) {
        depthDurationMap.put(depth, Duration.between(depthStart, depthEnd));
        maxDepth++;
    }

    public void incrementNodesSearched() {
        ++nodesSearched;
    }

    public void incrementCutoffs() {
        ++cutOffs;
    }

    public void incrementTranspositions() {
        ++transpositions;
    }

    public void incrementQuiescents() {
        ++quiescents;
    }

    public void incrementKillers() {
        ++killers;
    }

    public String generateReport() {
        Duration searchDuration = Duration.between(start, end);
        double millis = searchDuration.toMillis();
        double nodesPerSecond = nodesSearched / millis * 1000;
        return String.format(
                "Searched %s nodes in %s (%s nodes per second). " +
                        "Max depth %s, Transpositions: %s, Cut-offs: %s, Quiescents: %s, Killers: %s, " +
                                "Depth durations: %s%n",
                nodesSearched, searchDuration, nodesPerSecond, maxDepth, transpositions, cutOffs, quiescents, killers, depthDurationMap);
    }

}
