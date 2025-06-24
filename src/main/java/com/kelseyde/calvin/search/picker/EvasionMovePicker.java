package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;

import java.util.List;

/**
 * Implementation of {@link MovePicker} for evasions - positions where the player is in check. Splits the moves into
 * noisies and quiets, trying all noisies first before generating the quiets.
 */
public class EvasionMovePicker extends MovePicker {

    ScoredMove[] noisies;
    ScoredMove[] quiets;

    public EvasionMovePicker(EngineConfig config,
                             MoveGenerator movegen,
                             SearchHistory history,
                             SearchStack ss,
                             Board board,
                             Move ttMove,
                             int ply) {
        super(config, movegen, history, ss, board, ttMove, ply, true);
        this.stage = ttMove != null ? Stage.TT_MOVE : Stage.EVASION_GEN_NOISY;
    }

    @Override
    protected MoveScorer initMoveScorer(EngineConfig config, SearchHistory history, SearchStack ss) {
        final int seeDivisor = config.seeNoisyDivisor();
        final int seeOffset = config.seeNoisyOffset();
        return new MoveScorer(config, history, ss, seeDivisor, seeOffset);
    }

    @Override
    public ScoredMove next() {

        ScoredMove nextMove = null;
        while (nextMove == null) {
            nextMove = switch (stage) {
                case TT_MOVE ->             pickTTMove(Stage.EVASION_GEN_NOISY);
                case EVASION_GEN_NOISY ->   generate(MoveFilter.NOISY, Stage.EVASION_NOISY);
                case EVASION_NOISY ->       pickMove(Stage.EVASION_GEN_QUIET);
                case EVASION_GEN_QUIET ->   generate(MoveFilter.QUIET, Stage.EVASION_QUIET);
                case EVASION_QUIET ->       pickMove(Stage.END);
                default -> null;
            };
            if (stage == Stage.END) break;
        }
        return nextMove;

    }

    @Override
    protected void handleStagedMoves(List<Move> moves) {
        if (stage == Stage.EVASION_GEN_NOISY) {
            noisies = new ScoredMove[moves.size()];
            int index = 0;
            for (Move move : moves)
                noisies[index++] = scorer.score(board, move, ply, stage);
        }
        else if (stage == Stage.EVASION_GEN_QUIET) {
            quiets = new ScoredMove[moves.size()];
            int index = 0;
            for (Move move : moves)
                quiets[index++] = scorer.score(board, move, ply, stage);
        }
    }

    @Override
    protected boolean isSpecial(Move move) {
        return move.equals(ttMove);
    }

    @Override
    protected ScoredMove[] loadStagedMoves(Stage stage) {
        return switch (stage) {
            case EVASION_NOISY -> noisies;
            case EVASION_QUIET -> quiets;
            default -> throw new IllegalArgumentException("Invalid stage: " + stage);
        };
    }

}
