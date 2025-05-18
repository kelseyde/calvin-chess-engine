package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.scorer.MoveScorer;

import java.util.List;

public class EvasionMovePicker extends AbstractMovePicker {

    public EvasionMovePicker(EngineConfig config,
                             MoveGenerator movegen,
                             SearchHistory history,
                             SearchStack ss,
                             boolean inCheck,
                             Board board,
                             Move ttMove,
                             int ply) {
        super(config, movegen, history, ss, inCheck, board, ttMove, ply);
        this.stage = Stage.TT_MOVE;
    }

    @Override
    public ScoredMove next() {
        return null;
    }

    @Override
    protected void handleStagedMoves(List<Move> moves) {

    }

    @Override
    protected ScoredMove[] loadStagedMoves(Stage stage) {
        return new ScoredMove[0];
    }

    @Override
    protected boolean isSpecial(Move move) {
        return false;
    }

    @Override
    protected MoveScorer initMoveScorer(EngineConfig config, SearchStack ss) {
        return null;
    }
}
