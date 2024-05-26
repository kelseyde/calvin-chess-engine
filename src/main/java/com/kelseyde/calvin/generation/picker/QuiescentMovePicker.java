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
public class QuiescentMovePicker implements MovePicking {

    public enum Stage {
        PREVIOUS_BEST_MOVE,
        NOISY,
        END
    }

    final MoveGeneration moveGenerator;
    final MoveOrdering moveOrderer;

    final Board board;
    MoveFilter filter;
    Stage stage;

    List<Move> moves;
    Move previousBestMove;
    int moveIndex;
    int[] scores;

    public QuiescentMovePicker(MoveGeneration moveGenerator, MoveOrdering moveOrderer, Board board) {
        this.moveGenerator = moveGenerator;
        this.moveOrderer = moveOrderer;
        this.board = board;
        this.stage = Stage.PREVIOUS_BEST_MOVE;
    }

    @Override
    public Move pickNextMove() {

        Move nextMove = null;

        while (nextMove == null) {
            nextMove = switch (stage) {
                case PREVIOUS_BEST_MOVE -> pickPreviousBestMove();
                case NOISY -> pickMove();
                case END -> null;
            };
            if (stage == Stage.END) break;
        }

        return nextMove;

    }

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
        sortMoves();
        Move move = moves.get(moveIndex);
        moveIndex++;
        if (move.equals(previousBestMove)) {
            // Skip to the next move
            return pickMove();
        }
        return move;

    }

    private Move pickPreviousBestMove() {
        stage = Stage.NOISY;
        return previousBestMove;
    }

    public void scoreMoves() {
        scores = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            scores[i] = moveOrderer.mvvLva(board, moves.get(i), previousBestMove);
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


    public void setPreviousBestMove(Move previousBestMove) {
        this.previousBestMove = previousBestMove;
    }

    public void setFilter(MoveFilter filter) {
        this.filter = filter;
    }

}
