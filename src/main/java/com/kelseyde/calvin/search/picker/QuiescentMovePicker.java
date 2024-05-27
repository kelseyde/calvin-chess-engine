package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import com.kelseyde.calvin.search.moveordering.StaticExchangeEvaluator;
import com.kelseyde.calvin.utils.notation.FEN;
import lombok.AccessLevel;
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
    final StaticExchangeEvaluator see;

    final Board board;
    final int ply;
    final boolean isInCheck;
    MoveFilter filter;
    Stage stage;

    List<Move> moves;
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
    public QuiescentMovePicker(MoveGeneration moveGenerator, StaticExchangeEvaluator see, Board board, boolean isInCheck, int ply) {
        this.moveGenerator = moveGenerator;
        this.see = see;
        this.board = board;
        this.stage = Stage.BEST_MOVE;
        this.isInCheck = isInCheck;
        this.ply = ply;
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
        sortMoves();
        Move move = moves.get(moveIndex);
        moveIndex++;
        if (move.equals(bestMove)) {
            // Skip to the next move
            return pickMove();
        }

//        if (!isInCheck) {
//            int score = scores[moveIndex];
//            // Static Exchange Evaluation - https://www.chessprogramming.org/Static_Exchange_Evaluation
//            // Evaluate the possible captures + recaptures on the target square, in order to filter out losing capture
//            // chains, such as capturing with the queen a pawn defended by another pawn.
//            if ((ply <= 3 && score < 0) || (ply > 3 && score <= 0)) {
//                moveIndex++;
//                return pickMove();
//            }
//        }

        return move;

    }

    /**
     * Moves are scored using the {@link MoveOrderer} MVV-LVA routine.
     */
    public void scoreMoves() {
        scores = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            //scores[i] = see.evaluate(board, moves.get(i));
            scores[i] = new MoveOrderer().mvvLva(board, moves.get(i), bestMove);
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

    public void setBestMove(Move bestMove) {
        this.bestMove = bestMove;
    }

    public void setFilter(MoveFilter filter) {
        this.filter = filter;
    }

}
