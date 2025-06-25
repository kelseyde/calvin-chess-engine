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
 * Implementation of {@link MovePicker} for standard PVS search. Splits noisy moves into separate 'good' and 'bad' stages,
 * based on whether they pass a SEE threshold, with the bad noisies pushed to the end after quiets. Also handles killer
 * moves in a separate stage between good noisies and quiets.
 */
public class StandardMovePicker extends MovePicker {

    int killerIndex;

    ScoredMove[] goodNoisies;
    ScoredMove[] badNoisies;
    ScoredMove[] goodQuiets;
    ScoredMove[] badQuiets;

    public StandardMovePicker(EngineConfig config,
                              MoveGenerator movegen,
                              SearchStack ss,
                              SearchHistory history,
                              Board board,
                              int ply,
                              Move ttMove,
                              boolean inCheck) {
        super(config, movegen, history, ss, board, ply, ttMove, inCheck);
        this.stage = ttMove != null ? Stage.TT_MOVE : Stage.GEN_NOISY;
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
                case TT_MOVE ->     pickTTMove(Stage.GEN_NOISY);
                case GEN_NOISY ->   generate(MoveFilter.NOISY, Stage.GOOD_NOISY);
                case GOOD_NOISY ->  pickMove(Stage.KILLER);
                case KILLER ->      pickKiller(Stage.GEN_QUIET);
                case GEN_QUIET ->   generate(MoveFilter.QUIET, Stage.GOOD_QUIET);
                case GOOD_QUIET ->  pickMove(Stage.BAD_NOISY);
                case BAD_NOISY ->   pickMove(Stage.BAD_QUIET);
                case BAD_QUIET ->   pickMove(Stage.END);
                default -> null;
            };
            if (stage == Stage.END) break;
        }
        return nextMove;

    }

    @Override
    protected void handleStagedMoves(List<Move> moves) {
        if (stage == Stage.GEN_NOISY) {
            int goodIndex = 0;
            int badIndex = 0;
            goodNoisies = new ScoredMove[moves.size()];
            badNoisies = new ScoredMove[moves.size()];
            for (Move move : moves) {
                ScoredMove scoredMove = scorer.score(board, move, ply, stage);
                if (scoredMove.isGoodNoisy())
                    goodNoisies[goodIndex++] = scoredMove;
                else
                    badNoisies[badIndex++] = scoredMove;
            }
        }
        else if (stage == Stage.GEN_QUIET) {
            int goodIndex = 0;
            int badIndex = 0;
            goodQuiets = new ScoredMove[moves.size()];
            badQuiets = new ScoredMove[moves.size()];
            for (Move move : moves) {
                ScoredMove scoredMove = scorer.score(board, move, ply, stage);
                if (scoredMove.isGoodQuiet())
                    goodQuiets[goodIndex++] = scoredMove;
                else
                    badQuiets[badIndex++] = scoredMove;
            }
        }
    }

    @Override
    protected boolean isSpecial(Move move) {
        if (move.equals(ttMove))
            return true;
        for (Move killer : history.killerTable().getKillers(ply)) {
            if (move.equals(killer) && !board.isNoisy(killer))
                return true;
        }
        return false;
    }

    @Override
    protected ScoredMove[] loadStagedMoves(Stage stage) {
        return switch (stage) {
            case GOOD_NOISY -> goodNoisies;
            case BAD_NOISY -> badNoisies;
            case GOOD_QUIET -> goodQuiets;
            case BAD_QUIET -> badQuiets;
            default -> throw new IllegalArgumentException("Invalid stage: " + stage);
        };
    }

    protected ScoredMove pickKiller(Stage nextStage) {

        Move[] killers = history.killerTable().getKillers(ply);
        if (killerIndex >= killers.length)
            return nextStage(nextStage);

        Move killer = killers[killerIndex++];

        // Skip the killer if it's null, the same as the TT move, noisy, or illegal
        if (killer == null
                || killer.equals(ttMove)
                || board.isNoisy(killer)
                || !movegen.isLegal(board, killer))
            return pickKiller(nextStage);

        return scorer.score(board, killer, ply, stage);
    }

}
