package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.moveordering.tables.HistoryTable;
import com.kelseyde.calvin.search.moveordering.tables.KillerTable;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Selects the next move to try in a given position. Moves are selected in stages. First, the 'best' move from the
 * transposition table is tried before any moves are generated. Then, all the 'noisy' moves are tried (captures,
 * checks and promotions). Finally, the remaining quiet moves are generated.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlphaBetaMovePicker implements MovePicker {

    public enum Stage {
        TT_MOVE,
        NOISY,
        QUIET,
        END
    }

    final MoveGeneration moveGenerator;
    final KillerTable killerTable;
    final HistoryTable historyTable;
    final Board board;
    final int ply;

    Stage stage;
    @Setter
    Move ttMove;
    int moveIndex;
    ScoredMove[] moves;

    public AlphaBetaMovePicker(MoveGeneration moveGenerator, KillerTable killerTable, HistoryTable historyTable, Board board, int ply) {
        this.moveGenerator = moveGenerator;
        this.killerTable = killerTable;
        this.historyTable = historyTable;
        this.board = board;
        this.ply = ply;
        this.stage = Stage.TT_MOVE;
    }

    /**
     * Picks the next move to make, cycling through the different stages until a move is found.
     *
     * @return the next move, or null if no moves are available
     */
    @Override
    public Move pickNextMove() {

        Move nextMove = null;
        while (nextMove == null) {
            nextMove = switch (stage) {
                case TT_MOVE -> pickTTMove();
                case NOISY -> pickMove(MoveFilter.NOISY, Stage.QUIET);
                case QUIET -> pickMove(MoveFilter.QUIET, Stage.END);
                case END -> null;
            };
            if (stage == Stage.END) break;
        }
        return nextMove;

    }

    /**
     * Select the best move from the transposition table and advance to the next stage.
     */
    private Move pickTTMove() {
        stage = Stage.NOISY;
        return ttMove;
    }

    /**
     * Select the next move from the move list.
     * @param filter the move generation filter to use, if the moves are not yet generated
     * @param nextStage the next stage to move on to, if we have tried all moves in the current stage.
     */
    private Move pickMove(MoveFilter filter, Stage nextStage) {

        if (moves == null) {
            List<Move> stagedMoves = moveGenerator.generateMoves(board, filter);
            scoreMoves(stagedMoves);
            moveIndex = 0;
        }
        if (moveIndex >= moves.length) {
            moves = null;
            stage = nextStage;
            return null;
        }
        Move move = pick();
        moveIndex++;
        if (move.equals(ttMove)) {
            // Skip to the next move
            return pickMove(filter, nextStage);
        }
        return move;

    }

    public void scoreMoves(List<Move> stagedMoves) {
        moves = new ScoredMove[stagedMoves.size()];
        for (int i = 0; i < stagedMoves.size(); i++) {
            Move move = stagedMoves.get(i);
            int score = scoreMove(board, killerTable, historyTable, move, ttMove, ply);
            moves[i] = new ScoredMove(move, score);
        }
    }

    /**
     * Select the move with the highest score and move it to the head of the move list.
     */
    public Move pick() {
        for (int j = moveIndex + 1; j < moves.length; j++) {
            if (moves[j].score() > moves[moveIndex].score()) {
                swap(moveIndex, j);
            }
        }
        return moves[moveIndex].move();
    }

    private void swap(int i, int j) {
        ScoredMove temp = moves[i];
        moves[i] = moves[j];
        moves[j] = temp;
    }

}
