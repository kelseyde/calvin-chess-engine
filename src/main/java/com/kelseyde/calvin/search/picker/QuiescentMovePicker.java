package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

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
    MoveFilter filter;
    Stage stage;

    Move[] moves;
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
        if (moveIndex >= moves.length) {
            moves = null;
            stage = Stage.END;
            return null;
        }
        sortMoves();
        Move move = moves[moveIndex];
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
        scores = new int[moves.length];
        for (int i = 0; i < moves.length; i++) {
            scores[i] = moveOrderer.mvvLva(board, moves[i], bestMove);
        }
    }

    /**
     * Select the move with the highest score and move it to the head of the move list.
     */
    public void sortMoves() {
        for (int j = moveIndex + 1; j < moves.length; j++) {
            int firstScore = scores[moveIndex];
            int secondScore = scores[j];
            if (scores[j] > scores[moveIndex]) {
                Move firstMove = moves[moveIndex];
                Move secondMove = moves[j];
                scores[moveIndex] = secondScore;
                scores[j] = firstScore;
                moves[moveIndex] = secondMove;
                moves[j] = firstMove;
            }
        }
    }

    public void setBestMove(Move bestMove) {
        this.bestMove = bestMove;
    }

    public void setFilter(MoveFilter filter) {
        this.filter = filter;
    }

}
