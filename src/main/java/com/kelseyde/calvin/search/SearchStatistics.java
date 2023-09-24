package com.kelseyde.calvin.search;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchStatistics {

    int nodesSearched = 0;
    int nodesPruned = 0;
    int transpositions = 0;
    List<Duration> searchDurations = new ArrayList<>();

    public void incrementNodesSearched() {
        ++nodesSearched;
    }

    public void incrementNodesPruned() {
        ++nodesPruned;
    }

    public void incrementTranspositions() {
        ++transpositions;
    }

    public void addSearchDuration(Instant start, Instant end) {
        searchDurations.add(Duration.between(start, end));
    }

    public Duration getAverageSearchDurationMs() {
        return searchDurations.stream()
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(nodesSearched);
    }

}
