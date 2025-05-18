package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.scorer.StandardMoveScorer;

import java.util.List;

public class QuiescentMovePicker extends MovePicker<StandardMoveScorer> {

    private ScoredMove[] goodNoisies;

    public QuiescentMovePicker(EngineConfig config,
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
                case TT_MOVE ->            pickTTMove(Stage.QSEARCH_GEN_NOISY);
                case QSEARCH_GEN_NOISY ->  generate(MoveFilter.CAPTURES_ONLY, Stage.QSEARCH_NOISY);
                case QSEARCH_NOISY ->      pickMove(Stage.END);
                default -> null;
            };
            if (stage == Stage.END) break;
        }
        return nextMove;

    }

    @Override
    protected void handleStagedMoves(List<Move> moves) {
        // In quiescent movegen all moves are treated as 'good noisies'
        goodNoisies = new ScoredMove[moves.size()];
        int goodIndex = 0;
        for (Move move : moves) {
            ScoredMove scoredMove = scorer.score(move);
            // In q-search, only consider good noisies
            if (scoredMove.isGoodNoisy())
                goodNoisies[goodIndex++] = scoredMove;
        }
    }

    @Override
    protected boolean isSpecial(Move move) {
        return move.equals(ttMove);
    }

    @Override
    protected StandardMoveScorer initMoveScorer(EngineConfig config, SearchStack ss) {
        return new StandardMoveScorer(config, movegen, history, ss, board, ply, config.seeQsNoisyDivisor(), config.seeQsNoisyOffset());
    }

    @Override
    protected ScoredMove[] loadStagedMoves(Stage stage) {
        return goodNoisies;
    }

}
