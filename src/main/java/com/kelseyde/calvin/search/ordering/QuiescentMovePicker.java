package com.kelseyde.calvin.search.ordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;

import java.util.List;

/**
 * Implementation of {@link MovePicker} for quiescence search. Discards all bad noisies which don't pass a SEE threshold.
 */
public class QuiescentMovePicker extends MovePicker {

    private ScoredMove[] goodNoisies;

    public QuiescentMovePicker(
            EngineConfig config, MoveGenerator movegen, SearchStack ss, SearchHistory history, Board board, int ply, Move ttMove, boolean inCheck) {
        super(config, movegen, history, ss, board, ply, ttMove, inCheck);
        this.stage = Stage.TT_MOVE;
    }

    @Override
    protected MoveScorer initMoveScorer(EngineConfig config, SearchHistory history, SearchStack ss) {
        final int seeDivisor = config.seeQsNoisyDivisor();
        final int seeOffset = config.seeQsNoisyOffset();
        return new MoveScorer(config, history, ss, seeDivisor, seeOffset);
    }

    @Override
    public ScoredMove next() {

        ScoredMove nextMove = null;
        MoveFilter filter = inCheck ? MoveFilter.ALL : MoveFilter.CAPTURES_ONLY;
        while (nextMove == null) {
            nextMove = switch (stage) {
                case TT_MOVE -> pickTTMove(Stage.QSEARCH_GEN_NOISY);
                case QSEARCH_GEN_NOISY -> generate(filter, Stage.QSEARCH_NOISY);
                case QSEARCH_NOISY -> pickMove(Stage.END);
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
            ScoredMove scoredMove = scorer.score(board, move, ply, stage);
            // In q-search, only consider good noisies
            // unless we are in check, in which case consider all moves.
            if (scoredMove.isGoodNoisy() || inCheck)
                goodNoisies[goodIndex++] = scoredMove;
        }
    }

    @Override
    protected boolean isSpecial(Move move) {
        return move.equals(ttMove);
    }

    @Override
    protected ScoredMove[] loadStagedMoves(Stage stage) {
        return goodNoisies;
    }

}
