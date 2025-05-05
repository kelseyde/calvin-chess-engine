package com.kelseyde.calvin.search.picker2;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.picker.ScoredMove;

public class AlphaBetaMovePicker extends MovePicker2<AlphaBetaMovePicker.AlphaBetaStage> {

    public enum AlphaBetaStage implements Stage {
        TT_MOVE,
        GEN_NOISY,
        GOOD_NOISY,
        KILLER,
        GEN_QUIET,
        QUIET,
        BAD_NOISY,
        END
    }

    int killerIndex;

    ScoredMove[] goodNoisies;
    ScoredMove[] badNoisies;
    ScoredMove[] quiets;

    boolean skipQuiets;
    boolean inCheck;

    @Override
    public ScoredMove next() {
        ScoredMove nextMove = null;
        while (nextMove == null) {
            nextMove = switch (stage) {
                case TT_MOVE ->     pickTTMove(AlphaBetaStage.GEN_NOISY);
                case GEN_NOISY ->   generate(MoveFilter.NOISY, AlphaBetaStage.GOOD_NOISY);
                case GOOD_NOISY ->  pickMove(AlphaBetaStage.KILLER);
                case KILLER ->      pickKiller(AlphaBetaStage.GEN_QUIET);
                case GEN_QUIET ->   generate(MoveFilter.QUIET, AlphaBetaStage.QUIET);
                case QUIET ->       pickMove(AlphaBetaStage.BAD_NOISY);
                case BAD_NOISY ->   pickMove(AlphaBetaStage.END);
                case END -> null;
            };
            if (stage == AlphaBetaStage.END) break;
        }
        return nextMove;
    }

    @Override
    protected ScoredMove generate(MoveFilter filter, AlphaBetaStage nextStage) {

    }

    @Override
    protected boolean isSpecial(Move move) {
        return false;
    }

    protected ScoredMove pickMove(AlphaBetaStage nextStage) {

        final ScoredMove[] moves = switch (stage) {
            case GOOD_NOISY -> goodNoisies;
            case BAD_NOISY -> badNoisies;
            case QUIET -> quiets;
            default -> throw new IllegalArgumentException("Invalid stage: " + stage);
        };

        // If we're in check then all evasions have been tried in the noisy stage
        if (stage == AlphaBetaStage.QUIET && (skipQuiets || inCheck))
            return nextStage(nextStage);

        // If we've tried all moves in this stage, move on to the next stage
        if (moveIndex >= moves.length)
            return nextStage(nextStage);

        ScoredMove move = pick(moves);

        if (move == null)
            return nextStage(nextStage);

        moveIndex++;
        return move;

    }

    protected ScoredMove pickKiller(AlphaBetaStage nextStage) {

        Move[] killers = history.getKillerTable().getKillers(ply);
        if (killerIndex >= killers.length) {
            return nextStage(nextStage);
        }

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
