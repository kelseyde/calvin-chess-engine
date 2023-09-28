package com.kelseyde.calvin.search;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchStatistics {

    int nodesSearched = 0;
    int cutOffs = 0;
    int transpositions = 0;

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

}
