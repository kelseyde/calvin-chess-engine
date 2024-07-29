package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
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
public class MovePicker implements MovePicking {

    public enum Stage {
        BEST_MOVE,
        NOISY,
        QUIET,
        END
    }

    final MoveGeneration moveGenerator;
    final MoveOrdering moveOrderer;
    final Board board;
    final int ply;

    Stage stage;
    List<Move> moves;
    @Setter
    Move bestMove;
    int moveIndex;
    int[] scores;

    /**
     * Constructs a MovePicker with the specified move generator, move orderer, board, and ply.
     *
     * @param moveGenerator the move generator to use for generating moves
     * @param moveOrderer   the move orderer to use for scoring and ordering moves
     * @param board         the current state of the board
     * @param ply           the number of ply from the root position
     */
    public MovePicker(MoveGeneration moveGenerator, MoveOrdering moveOrderer, Board board, int ply) {
        this.moveGenerator = moveGenerator;
        this.moveOrderer = moveOrderer;
        this.board = board;
        this.ply = ply;
        this.stage = Stage.BEST_MOVE;
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
                case BEST_MOVE -> pickBestMove();
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
    private Move pickBestMove() {
        stage = Stage.NOISY;
        return bestMove;
    }

    /**
     * Select the next move from the move list.
     * @param filter the move generation filter to use, if the moves are not yet generated
     * @param nextStage the next stage to move on to, if we have tried all moves in the current stage.
     */
    private Move pickMove(MoveFilter filter, Stage nextStage) {

        if (moves == null) {
            moves = moveGenerator.generateMoves(board, filter);
            moveIndex = 0;
            scoreMoves();
        }
        if (moveIndex >= moves.size()) {
            moves = null;
            stage = nextStage;
            return null;
        }
        sortMoves();
        Move move = moves.get(moveIndex);
        moveIndex++;
        if (move.equals(bestMove)) {
            // Skip to the next move
            return pickMove(filter, nextStage);
        }
        return move;

    }

    /**
     * Moves are scored using the {@link MoveOrderer}.
     */
    public void scoreMoves() {
        scores = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            scores[i] = moveOrderer.scoreMove(board, moves.get(i), bestMove, ply);
        }
    }

    /**
     * Select the move with the highest score and move it to the head of the move list.
     */
    public void sortMoves() {
        for (int j = moveIndex + 1; j < moves.size(); j++) {
            int firstScore = scores[moveIndex];
            int secondScore = scores[j];
            if (scores[j] > scores[moveIndex]) {
                Move firstMove = moves.get(moveIndex);
                Move secondMove = moves.get(j);
                scores[moveIndex] = secondScore;
                scores[j] = firstScore;
                moves.set(moveIndex, secondMove);
                moves.set(j, firstMove);
            }
        }
    }

}
