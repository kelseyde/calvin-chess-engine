package com.kelseyde.calvin.generation.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovePicker implements MovePicking {

    public enum Stage {
        PREVIOUS_BEST_MOVE,
        NOISY,
        QUIET,
        END
    }

    final MoveGeneration moveGenerator;
    final MoveOrdering moveOrderer;

    Board board;
    int ply;

    Stage stage;

    List<Move> moves;
    Move previousBestMove;
    int moveIndex;
    int[] scores;

    public MovePicker(MoveGeneration moveGenerator, MoveOrdering moveOrderer, Board board, int ply) {
        this.moveGenerator = moveGenerator;
        this.moveOrderer = moveOrderer;
        this.board = board;
        this.ply = ply;
        this.stage = Stage.PREVIOUS_BEST_MOVE;
    }

    public void reset(Board board, int ply) {
        this.board = board;
        this.ply = ply;
        this.stage = Stage.PREVIOUS_BEST_MOVE;
        this.previousBestMove = null;
        this.moves = null;
        this.scores = null;
        this.moveIndex = 0;
    }

    @Override
    public Move pickNextMove() {

        Move nextMove = null;

        while (nextMove == null) {
            nextMove = switch (stage) {
                case PREVIOUS_BEST_MOVE -> pickPreviousBestMove();
                case NOISY -> pickMove(MoveFilter.NOISY, Stage.QUIET);
                case QUIET -> pickMove(MoveFilter.QUIET, Stage.END);
                case END -> null;
            };
            if (stage == Stage.END) break;
        }

        return nextMove;

    }

    private Move pickPreviousBestMove() {
        stage = Stage.NOISY;
        return previousBestMove;
    }

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
        if (move.equals(previousBestMove)) {
            // Skip to the next move
            return pickMove(filter, nextStage);
        }
        return move;

    }

    public void scoreMoves() {
        scores = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            scores[i] = moveOrderer.scoreMove(board, moves.get(i), previousBestMove, ply);
        }
    }

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

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
        scoreMoves();
    }

    public void setPreviousBestMove(Move previousBestMove) {
        this.previousBestMove = previousBestMove;
    }

}
