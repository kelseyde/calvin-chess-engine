package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.scorer.EvasionMoveScorer;

import java.util.List;

public class EvasionMovePicker extends MovePicker<EvasionMoveScorer> {

    ScoredMove[] evasions;

    public EvasionMovePicker(EngineConfig config,
                             MoveGenerator movegen,
                             SearchHistory history,
                             SearchStack ss,
                             Board board,
                             Move ttMove,
                             int ply) {
        super(config, movegen, history, ss, board, ttMove, ply);
        this.stage = Stage.TT_MOVE;
    }

    @Override
    public ScoredMove next() {
        ScoredMove nextMove = null;
        while (nextMove == null) {
            nextMove = switch (stage) {
                case TT_MOVE ->       pickTTMove(Stage.GEN_EVASIONS);
                case GEN_EVASIONS ->  generate(MoveFilter.ALL, Stage.EVASIONS);
                case EVASIONS ->      pickMove(Stage.END);
                default -> null;
            };
            if (stage == Stage.END) break;
        }
        return nextMove;
    }

    @Override
    protected void handleStagedMoves(List<Move> moves) {
        int index = 0;
        evasions = new ScoredMove[moves.size()];
        for (Move move : moves)
            evasions[index++] = scorer.score(move);
    }

    @Override
    protected ScoredMove[] loadStagedMoves(Stage stage) {
        return evasions;
    }

    @Override
    protected boolean isSpecial(Move move) {
        return move.equals(ttMove);
    }

    @Override
    protected EvasionMoveScorer initMoveScorer(EngineConfig config, SearchStack ss) {
        return new EvasionMoveScorer(config, movegen, history, ss, board, ply);
    }
}
