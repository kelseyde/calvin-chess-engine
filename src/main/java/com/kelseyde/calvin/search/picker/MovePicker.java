package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.generation.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.tables.history.KillerTable;
import lombok.Data;

import java.util.List;

/**
 * Selects the next move to try in a given position. Moves are selected in stages. First, the 'best' move from the
 * transposition table is tried before any moves are generated. Then, all the 'noisy' moves are tried (captures,
 * checks and promotions). Finally, we generate the remaining quiet moves.
 */
@Data
public class MovePicker {

    public enum Stage {
        TT_MOVE,
        NOISY,
        QUIET,
        END
    }

    final MoveGeneration movegen;
    final SearchHistory history;
    final SearchStack ss;

    final Board board;
    final int ply;

    Stage stage;
    Move ttMove;
    boolean skipQuiets;
    boolean inCheck;

    int moveIndex;
    ScoredMove[] moves;

    public MovePicker(MoveGeneration movegen, SearchStack ss, SearchHistory history, Board board, int ply) {
        this.movegen = movegen;
        this.history = history;
        this.board = board;
        this.ss = ss;
        this.ply = ply;
        this.stage = Stage.TT_MOVE;
    }

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
     * Select the next move from the move list.
     * @param filter the move generation filter to use, if the moves are not yet generated
     * @param nextStage the next stage to move on to, if we have tried all moves in the current stage.
     */
    protected Move pickMove(MoveFilter filter, Stage nextStage) {

        if (stage == Stage.QUIET && (skipQuiets || inCheck)) {
            stage = nextStage;
            moves = null;
            return null;
        }

        if (moves == null) {
            List<Move> stagedMoves = movegen.generateMoves(board, filter);
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

    /**
     * Select the best move from the transposition table and advance to the next stage.
     */
    protected Move pickTTMove() {
        stage = Stage.NOISY;
        return ttMove;
    }

    protected void scoreMoves(List<Move> stagedMoves) {
        moves = new ScoredMove[stagedMoves.size()];
        for (int i = 0; i < stagedMoves.size(); i++) {
            Move move = stagedMoves.get(i);
            int score = scoreMove(board, move, ttMove, ply);
            moves[i] = new ScoredMove(move, score);
        }
    }

    protected int scoreMove(Board board, Move move, Move ttMove, int ply) {

        int startSquare = move.getFrom();
        int endSquare = move.getTo();

        if (move.equals(ttMove)) {
            return MoveBonus.TT_MOVE_BONUS;
        }
        if (move.isPromotion()) {
            return scorePromotion(move);
        }

        Piece capturedPiece = board.pieceAt(endSquare);
        if (capturedPiece != null) {
            return scoreCapture(board, startSquare, endSquare, capturedPiece);
        }
        else {
            return scoreQuiet(board, move, ply);
        }

    }

    protected int scoreCapture(Board board, int startSquare, int endSquare, Piece capturedPiece) {
        Piece piece = board.pieceAt(startSquare);
        int captureScore = 0;

        // Separate captures into winning and losing
        int materialDelta = capturedPiece.getValue() - piece.getValue();
        captureScore += materialDelta >= 0 ? MoveBonus.WINNING_CAPTURE_BIAS : MoveBonus.LOSING_CAPTURE_BIAS;

        // Add MVV score to the capture score
        captureScore += MoveBonus.MVV_OFFSET * capturedPiece.getIndex();

        // Tie-break with capture history
        captureScore += history.getCaptureHistoryTable().get(piece, endSquare, capturedPiece, board.isWhiteToMove());

        return captureScore;
    }

    protected int scoreQuiet(Board board, Move move, int ply) {
        int killerIndex = history.getKillerTable().getIndex(move, ply);
        int killerScore = killerIndex >= 0 ? MoveBonus.KILLER_BONUS * KillerTable.KILLERS_PER_PLY - killerIndex : 0;
        int historyScore = history.getHistoryTable().get(move, board.isWhiteToMove());
        int base = killerScore > 0 ? MoveBonus.KILLER_MOVE_BIAS : MoveBonus.QUIET_MOVE_BIAS;
        return base + killerScore + historyScore;
    }

    protected int scorePromotion(Move move) {
        return move.getPromotionPiece() == Piece.QUEEN ? MoveBonus.QUEEN_PROMOTION_BIAS : MoveBonus.UNDER_PROMOTION_BIAS;
    }

    /**
     * Select the move with the highest score and move it to the head of the move list.
     */
    protected Move pick() {
        for (int j = moveIndex + 1; j < moves.length; j++) {
            if (moves[j].score() > moves[moveIndex].score()) {
                swap(moveIndex, j);
            }
        }
        return moves[moveIndex].move();
    }

    protected void swap(int i, int j) {
        ScoredMove temp = moves[i];
        moves[i] = moves[j];
        moves[j] = temp;
    }

}
