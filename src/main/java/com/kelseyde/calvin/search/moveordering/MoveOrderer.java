package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.tables.history.CaptureHistoryTable;
import com.kelseyde.calvin.tables.history.HistoryTable;
import com.kelseyde.calvin.tables.history.KillerTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static com.kelseyde.calvin.search.moveordering.MoveBonus.*;

/**
 * Using the following move-ordering strategy:
 *  1. Previous best move found at an earlier ply
 *  2. Queen promotions
 *  3. Winning captures (sub-ordered using MVV-LVA)
 *  4. Equal captures (sub-ordered using MVV-LVA)
 *  5. Killer moves
 *  6. Losing captures (sub-ordered using MVV-LVA)
 *  7. Under-promotions
 *  8. History moves
 *  9. Everything else.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoveOrderer {

    @Getter final KillerTable killerTable = new KillerTable();

    @Getter final HistoryTable historyTable = new HistoryTable();

    @Getter final CaptureHistoryTable captureHistoryTable = new CaptureHistoryTable();

    public int scoreMove(Board board, Move move, Move ttMove, int ply) {

        int startSquare = move.getFrom();
        int endSquare = move.getTo();

        if (move.equals(ttMove)) {
            return TT_MOVE_BONUS;
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


    private int scoreCapture(Board board, int startSquare, int endSquare, Piece capturedPiece) {
        Piece piece = board.pieceAt(startSquare);
        int captureScore = 0;

        // Separate captures into winning and losing
        int materialDelta = capturedPiece.getValue() - piece.getValue();
        captureScore += materialDelta >= 0 ? WINNING_CAPTURE_BIAS : LOSING_CAPTURE_BIAS;

        // Add MVV score to the capture score
        captureScore += MVV_OFFSET * capturedPiece.getIndex();

        // Tie-break with capture history
        captureScore += captureHistoryTable.get(piece, endSquare, capturedPiece, board.isWhiteToMove());

        return captureScore;
    }

    private int scoreQuiet(Board board, Move move, int ply) {
        int killerIndex = killerTable.getIndex(move, ply);
        int killerScore = killerIndex >= 0 ? KILLER_MOVE_BIAS + (KILLER_BONUS * KillerTable.KILLERS_PER_PLY - killerIndex) : 0;
        int historyScore = historyTable.get(move, board.isWhiteToMove());
        int historyBase = killerScore == 0 ? QUIET_MOVE_BIAS : 0;
        return killerScore + historyBase + historyScore;
    }

    private int scorePromotion(Move move) {
        return move.getPromotionPiece() == Piece.QUEEN ? QUEEN_PROMOTION_BIAS : UNDER_PROMOTION_BIAS;
    }


}
