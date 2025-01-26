package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;

public class QuiescentMovePicker extends MovePicker {

    private MoveFilter filter;

    public QuiescentMovePicker(
            EngineConfig config, MoveGenerator movegen, SearchStack ss, SearchHistory history, Board board, int ply, Move ttMove, boolean inCheck) {
        super(config, movegen, ss, history, board, ply, ttMove, inCheck);
        this.scorer.setSeeNoisyDivisor(config.seeQsNoisyDivisor.value);
        this.scorer.setSeeNoisyOffset(config.seeQsNoisyOffset.value);
        this.stage = ttMove != null ? Stage.TT_MOVE : Stage.QSEARCH_GEN_NOISY;
        this.skipQuiets = true;
    }

    @Override
    public ScoredMove next() {

        ScoredMove nextMove = null;
        while (nextMove == null) {
            nextMove = switch (stage) {
                case TT_MOVE -> pickTTMove(Stage.QSEARCH_GEN_NOISY);
                case QSEARCH_GEN_NOISY -> generate(filter, Stage.QSEARCH_NOISY);
                case QSEARCH_NOISY -> pickMove(Stage.END);
                case GEN_NOISY, GOOD_NOISY, KILLER, GEN_QUIET, QUIET, BAD_NOISY, END -> null;
            };
            if (stage == Stage.END) break;
        }
        return nextMove;

    }

    public void setFilter(MoveFilter filter) {
        this.filter = filter;
    }

}
