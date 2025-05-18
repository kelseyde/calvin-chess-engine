package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.scorer.MoveScorer;

import java.util.List;

/**
 * Selects the next move to try in a given position. Moves are selected in stages. First, the 'best' move from the
 * transposition table is tried before any moves are generated. Then, the noisy moves are generated and separated into
 * 'good' and 'bad' noisies. Finally, the quiet moves are generated.
 * </p>
 * Within each stage, the order in which moves are tried is determined by several heuristics in the {@link MoveScorer}.
 * </p>
 * The idea behind generating and selecting moves in stages is to save time. Since in most positions only a few moves -
 * or even only a single move - will be tried, the time spent generating all the other moves is essentially wasted.
 */
public class StandardMovePicker extends AbstractMovePicker {

    int killerIndex;

    ScoredMove[] goodNoisies;
    ScoredMove[] badNoisies;
    ScoredMove[] quiets;

    public StandardMovePicker(EngineConfig config,
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

        ScoredMove nextMove = null;
        while (nextMove == null) {
            nextMove = switch (stage) {
                case TT_MOVE ->     pickTTMove(Stage.GEN_NOISY);
                case GEN_NOISY ->   generate(MoveFilter.NOISY, Stage.GOOD_NOISY);
                case GOOD_NOISY ->  pickMove(Stage.KILLER);
                case KILLER ->      pickKiller(Stage.GEN_QUIET);
                case GEN_QUIET ->   generate(MoveFilter.QUIET, Stage.QUIET);
                case QUIET ->       pickMove(Stage.BAD_NOISY);
                case BAD_NOISY ->   pickMove(Stage.END);
                default -> null;
            };
            if (stage == Stage.END) break;
        }
        return nextMove;

    }

    @Override
    protected void handleStagedMoves(List<Move> moves) {
        if (stage == Stage.GEN_NOISY) {
            // In noisy movegen we separate the moves into 'good' and 'bad' noisies
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
            // In quiet movegen everything is treated as a 'quiet' move
            int quietIndex = 0;
            quiets = new ScoredMove[moves.size()];
            for (Move move : moves) {
                ScoredMove scoredMove = scorer.score(board, move, ply, stage);
                quiets[quietIndex++] = scoredMove;
            }
        }
    }

    @Override
    protected boolean isSpecial(Move move) {
        if (move.equals(ttMove))
            return true;
        for (Move killer : history.getKillerTable().getKillers(ply)) {
            if (move.equals(killer) && !board.isNoisy(killer))
                return true;
        }
        return false;
    }

    @Override
    protected MoveScorer initMoveScorer(EngineConfig config, SearchStack ss) {
        return new MoveScorer(config, history, ss, config.seeNoisyDivisor(), config.seeNoisyOffset());
    }

    @Override
    protected ScoredMove[] loadStagedMoves(Stage stage) {
        return switch (stage) {
            case GOOD_NOISY -> goodNoisies;
            case BAD_NOISY -> badNoisies;
            case QUIET -> quiets;
            default -> throw new IllegalArgumentException("Invalid stage: " + stage);
        };
    }

    protected ScoredMove pickKiller(Stage nextStage) {

        Move[] killers = history.getKillerTable().getKillers(ply);
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
