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

    int nodesSearched = 0;
    int cutOffs = 0;
    int transpositions = 0;
    int killers = 0;

    public void incrementNodesSearched() {
        ++nodesSearched;
    }

    public void incrementCutoffs() {
        ++nodesSearched;
        ++cutOffs;
    }

    public void incrementKillers() {
        ++killers;
    }

    public void incrementTranspositions() {
        ++nodesSearched;
        ++transpositions;
    }

    public String generateReport() {
        Duration searchDuration = Duration.between(start, end);
        double millis = searchDuration.toMillis();
        double nodesPerSecond = nodesSearched / millis * 1000;
        return String.format(
                "Searched %s nodes in %s (%s nodes per second). Transpositions: %s, Cut-offs: %s, Killers: %s%n",
                nodesSearched, searchDuration, nodesPerSecond, transpositions, cutOffs, killers);
    }

}
