package com.kelseyde.calvin.search.iterative;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.evaluation.CombinedBoardEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.MoveOrderer;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.TimedSearch;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class IterativeDeepeningSearch implements TimedSearch {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();
    private final BoardEvaluator boardEvaluator = new CombinedBoardEvaluator();
    private final TranspositionTable transpositionTable;

    private Board board;
    private Instant timeout;

    SearchResult bestMove;
    SearchResult bestMoveCurrentDepth;
    private int currentDepth = 0;

    public IterativeDeepeningSearch(Board board) {
        this.board = board;
        this.transpositionTable = new TranspositionTable(board);
    }

    @Override
    public SearchResult search(Duration duration) {

        int maxDepth = 0;
        while (!isTimeoutExceeded()) {



        }

        return null;
    }

    private boolean isTimeoutExceeded() {
        return !Instant.now().isBefore(timeout);
    }


//    @Override
//    public SearchResult search(Duration duration) {
//
//        log.info("Executing iterative deepening search with a timeout of {}", duration);
//        timeout =  Instant.now().plus(duration);
//        bestResultCurrentDepth = null;
//        bestResult = null;
//        currentDepth = 0;
//
//        for (int depth = 1; depth < 256; depth++) {
//
//            currentDepth = depth;
//            int alpha = Integer.MIN_VALUE;
//            int beta = Integer.MAX_VALUE;
//            searchDepth(currentDepth, alpha, beta);
//
//            if (isTimeoutExceeded()) {
//                if (bestResultCurrentDepth != null) {
//                    bestResult = bestResultCurrentDepth;
//                    return bestResult;
//                }
//                break;
//            }
//            else {
//                bestResult = bestResultCurrentDepth;
//                bestResultCurrentDepth = null;
//            }
//
//        }
//
//
//        return bestResult;
//
//    }
//
//    private void searchDepth(int depth, int alpha, int beta) {
//
//    }

}
