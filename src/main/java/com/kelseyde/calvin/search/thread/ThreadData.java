package com.kelseyde.calvin.search.thread;

import com.kelseyde.calvin.board.Move;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ThreadData {

    Instant start;
    int nodes;
    int currentDepth;

    int bestMoveStability;
    int previousEval;
    int evalStability;

    Move bestMoveCurrentDepth;
    int bestScoreCurrentDepth;

    public void incrementDepth() {
        bestMoveCurrentDepth = null;
        bestScoreCurrentDepth = 0;
        currentDepth++;
    }

    public void reset() {
        start = Instant.now();
        nodes = 0;
        currentDepth = 1;
        bestMoveStability = 0;
        previousEval = 0;
        evalStability = 0;
        bestMoveCurrentDepth = null;
        bestScoreCurrentDepth = 0;
    }

}
