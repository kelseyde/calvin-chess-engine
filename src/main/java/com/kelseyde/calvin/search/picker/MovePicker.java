package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;

import java.util.List;

/**
 * Selects the next move to try in a given position.
 * </p>
 * We save time during search by trying moves in stages. For example, by trying the TT move before generating any moves,
 * if the TT move then leads to a cut-off then we have saved the time spent generating moves. Similarly, if a noisy move
 * generates a cut-off, we save the time spent generating quiet moves.
 * </p>
 * Within each stage, the order in which moves are tried is determined by several heuristics in the {@link MoveScorer}.
 * Which stages we use and in what order depends on the type of search, with different heuristics being used for standard
 * PVS search, quiescence search, and search done when in check.
 */
public abstract class MovePicker {

    public enum Stage {
        // PVS stages
        TT_MOVE, GEN_NOISY, GOOD_NOISY, KILLER, GEN_QUIET, QUIET, BAD_NOISY,

        // Q-search stages
        QSEARCH_GEN_NOISY, QSEARCH_NOISY,

        END
    }

    public enum MoveType {
        TT_MOVE, GOOD_NOISY, KILLER, QUIET, BAD_NOISY
    }

    final MoveGenerator movegen;
    final MoveScorer scorer;
    final SearchHistory history;

    final Move ttMove;
    final Board board;
    final boolean inCheck;
    final int ply;

    Stage stage;
    boolean skipQuiets;

    int moveIndex;

    protected MovePicker(
            EngineConfig config, MoveGenerator movegen, SearchHistory history, SearchStack ss, Board board, int ply, Move ttMove, boolean inCheck) {
        this.movegen = movegen;
        this.history = history;
        this.board = board;
        this.ply = ply;
        this.ttMove = ttMove;
        this.inCheck = inCheck;
        this.scorer = initMoveScorer(config, history, ss);
    }

    /**
     * Select the next {@link ScoredMove} to try. The implementation should take into account the current stage in
     * determining which move to try next.
     */
    public abstract ScoredMove next();

    /**
     * Handle newly-generated moves, potentially distributing them to different lists to be tried in different
     * stages during search. The implementation is left up to the move picker subclass.
     */
    protected abstract void handleStagedMoves(List<Move> moves);

    /**
     * Check if a move is 'special', in that it is tried in a dedicated stage and should therefore be skipped during
     * normal move generation. This is used to avoid trying the same move multiple times in different stages.
     */
    protected abstract boolean isSpecial(Move move);

    /**
     * Retrieve the moves that should be tried in the current stage.
     */
    protected abstract ScoredMove[] loadStagedMoves(Stage stage);

    /**
     * Initialize the move scorer for this move picker. This is used to score moves during generation.
     */
    protected abstract MoveScorer initMoveScorer(EngineConfig config, SearchHistory history, SearchStack ss);

    /**
     * Select the next move from the move list.
     * @param nextStage the next stage to move on to, if we have tried all moves in the current stage.
     */
    protected ScoredMove pickMove(MovePicker.Stage nextStage) {

        final ScoredMove[] moves = loadStagedMoves(stage);

        // If we're in check then all evasions have been tried in the noisy stage
        if (stage == Stage.QUIET && (skipQuiets || inCheck))
            return nextStage(nextStage);

        // If we have no moves to try, move on to the next stage.
        if (moveIndex >= moves.length)
            return nextStage(nextStage);

        ScoredMove move = selectionSort(moves);

        if (move == null)
            return nextStage(nextStage);

        moveIndex++;
        return move;

    }

    protected ScoredMove generate(MoveFilter filter, Stage nextStage) {
        // Generate moves based on the current stage and filter.
        List<Move> stagedMoves = movegen.generateMoves(board, filter);

        // How the generated moves are handled depends on the move picker implementation.
        handleStagedMoves(stagedMoves);

        // Reset the move index and move to the next stage.
        return nextStage(nextStage);
    }

    /**
     * Select the next move to try from a given list of moves. We use an incremental selection sort algorithm to only
     * move the best move to the front of the list each time. This is faster than doing a full sort, since we typically
     * only try a few moves in each node.
     */
    protected ScoredMove selectionSort(ScoredMove[] moves) {
        if (moveIndex >= moves.length)
            return null;
        ScoredMove best = moves[moveIndex];
        if (best == null)
            return null;
        int bestScore = best.score();
        int bestIndex = moveIndex;
        for (int j = moveIndex + 1; j < moves.length; j++) {
            ScoredMove current = moves[j];
            if (current == null)
                break;
            if (current.score() > bestScore) {
                bestScore = current.score();
                bestIndex = j;
            }
        }
        if (bestIndex != moveIndex)
            swap(moves, moveIndex, bestIndex);
        ScoredMove scoredMove = moves[moveIndex];
        if (scoredMove == null || isSpecial(scoredMove.move())) {
            moveIndex++;
            return selectionSort(moves);
        }
        return scoredMove;
    }

    protected void swap(ScoredMove[] moves, int i, int j) {
        ScoredMove temp = moves[i];
        moves[i] = moves[j];
        moves[j] = temp;
    }

    protected ScoredMove pickTTMove(Stage nextStage) {
        if (ttMove == null)
            return nextStage(nextStage);
        stage = nextStage;
        final Piece piece = board.pieceAt(ttMove.from());
        final Piece captured = board.captured(ttMove);
        return new ScoredMove(ttMove, piece, captured, 0, 0, MoveType.TT_MOVE);
    }

    public void skipQuiets(boolean skipQuiets) {
        this.skipQuiets = skipQuiets;
    }

    protected ScoredMove nextStage(Stage nextStage) {
        moveIndex = 0;
        stage = nextStage;
        return null;
    }

}
