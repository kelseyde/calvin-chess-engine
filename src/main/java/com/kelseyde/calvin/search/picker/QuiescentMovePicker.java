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
 * Selects the next move to try in a position during quiescence search. First the move from the transposition table is
 * tried before any moves are generated. Then, all the noisy moves are generated and tried in order of their MVV-LVA score.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuiescentMovePicker implements MovePicking {

    public enum Stage {
        BEST_MOVE,
        NOISY,
        END
    }

    final MoveGeneration moveGenerator;
    final MoveOrdering moveOrderer;

    final Board board;
    @Setter
    MoveFilter filter;
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
     */
    public QuiescentMovePicker(MoveGeneration moveGenerator, MoveOrdering moveOrderer, Board board) {
        this.moveGenerator = moveGenerator;
        this.moveOrderer = moveOrderer;
        this.board = board;
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
                case NOISY -> pickMove();
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
     */
    private Move pickMove() {

        if (moves == null) {
            moves = moveGenerator.generateMoves(board, filter);
            moveIndex = 0;
            scoreMoves();
        }
        if (moveIndex >= moves.size()) {
            moves = null;
            stage = Stage.END;
            return null;
        }
        Move move = sortMoves();
        moveIndex++;
        if (move.equals(bestMove)) {
            // Skip to the next move
            return pickMove();
        }
        return move;

    }

    /**
     * Moves are scored using the {@link MoveOrderer} MVV-LVA routine.
     */
    public void scoreMoves() {
        scores = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            scores[i] = moveOrderer.mvvLva(board, moves.get(i), bestMove);
        }
    }

    /**
     * Select the move with the highest score and move it to the head of the move list.
     */
    public Move sortMoves() {

        int bestIndex = moveIndex;
        int bestScore = scores[moveIndex];

        for (int i = moveIndex + 1; i < moves.size(); i++) {
            int newScore = scores[i];
            if (newScore > bestScore) {
                bestIndex = i;
                bestScore = newScore;
            }
        }

        Move bestMove = moves.get(bestIndex);
        swapMoves(moveIndex, bestIndex);
        return bestMove;

    }

    private void swapMoves(int i, int j) {
        Move temp = moves.get(i);
        moves.set(i, moves.get(j));
        moves.set(j, temp);
        int tempScore = scores[i];
        scores[i] = scores[j];
        scores[j] = tempScore;
    }

}
