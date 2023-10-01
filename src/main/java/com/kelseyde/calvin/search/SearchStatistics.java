package com.kelseyde.calvin.search;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

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

    public void incrementMaxDepth() {
        maxDepth++;
    }

    public void incrementNodesSearched() {
        ++nodesSearched;
    }

    public void incrementCutoffs() {
        ++nodesSearched;
        ++cutOffs;
    }

    public void incrementTranspositions() {
        ++nodesSearched;
        ++transpositions;
    }

    public void incrementQuiescents() {
        ++nodesSearched;
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
                        "Max depth %s, Transpositions: %s, Cut-offs: %s, Quiescents: %s, Killers: %s%n",
                nodesSearched, searchDuration, nodesPerSecond, maxDepth, transpositions, cutOffs, quiescents, killers);
    }

}
