package com.kelseyde.calvin.search.scorer;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SEE;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.picker.MovePicker.MoveType;
import com.kelseyde.calvin.search.picker.ScoredMove;

public class StandardMoveScorer extends MoveScorer {

    private final int seeNoisyDivisor;
    private final int seeNoisyOffset;

    public StandardMoveScorer(EngineConfig config,
                              MoveGenerator movegen,
                              SearchHistory history,
                              SearchStack ss,
                              Board board,
                              int ply,
                              int seeNoisyDivisor,
                              int seeNoisyOffset) {
        super(config, movegen, history, ss, board, ply);
        this.seeNoisyDivisor = seeNoisyDivisor;
        this.seeNoisyOffset = seeNoisyOffset;
    }

    @Override
    public ScoredMove score(Move move) {
        final Piece piece = board.pieceAt(move.from());
        final Piece captured = board.captured(move);
        final boolean givesCheck = movegen.givesCheck(board, move);

        if (move.isPromotion())
            return scorePromo(move, piece, captured, givesCheck);
        else if (captured != null)
            return scoreCapture(move, piece, captured, givesCheck);
        else
            return scoreQuiet(move, piece, ply, givesCheck);
    }

    /**
     * Queen promotions are treated as 'good noisies', under-promotions are treated as 'bad noisies'.
     */
    private ScoredMove scorePromo(Move move, Piece piece, Piece captured, boolean givesCheck) {
        MoveType type = move.promoPiece() == Piece.QUEEN ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;
        int score = SEE.value(move.promoPiece()) - SEE.value(Piece.PAWN);
        return new ScoredMove(move, piece, captured, score, 0, givesCheck, type);
    }

    /**
     * Captures are scored using their MVV + capture history score, and separated into 'good' and 'bad' noisies based
     * on whether they pass a SEE threshold.
     */
    private ScoredMove scoreCapture(Move move, Piece piece, Piece captured, boolean givesCheck) {
        int historyScore = captureHistoryScore(board, move, piece, captured);
        int score = SEE.value(captured) + historyScore / 4;
        int threshold = -score / seeNoisyDivisor + seeNoisyOffset;
        MoveType type = SEE.see(board, move, threshold) ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;
        return new ScoredMove(move, piece, captured, score, historyScore, givesCheck, type);
    }

    /**
     * Quiet moves are scored using the quiet history and continuation history tables.
     */
    private ScoredMove scoreQuiet(Move move, Piece piece, int ply, boolean givesCheck) {
        int historyScore = quietHistoryScore(board, move, piece);
        int contHistScore = continuationHistoryScore(move, piece, board.isWhite(), ply);
        int score = historyScore + contHistScore;
        MoveType type = givesCheck ? MoveType.BAD_NOISY : MoveType.QUIET;
        return new ScoredMove(move, piece, null, score, score, givesCheck, type);
    }

}
