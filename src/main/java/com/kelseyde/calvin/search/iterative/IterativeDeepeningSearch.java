package com.kelseyde.calvin.search.iterative;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.evaluation.CombinedBoardEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.MoveOrderer;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.TimedSearch;
import com.kelseyde.calvin.search.transposition.TranspositionTable;

import java.time.Duration;

public class IterativeDeepeningSearch implements TimedSearch {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();
    private final BoardEvaluator boardEvaluator = new CombinedBoardEvaluator();
    private final TranspositionTable transpositionTable;

    private Board board;

    private SearchResult bestResultOverall;
    private SearchResult bestResultThisIteration;
    private int currentDepth = 0;

    public IterativeDeepeningSearch(Board board) {
        this.board = board;
        this.transpositionTable = new TranspositionTable(board);
    }

    @Override
    public SearchResult search(Duration duration) {
        return null;
    }

}
