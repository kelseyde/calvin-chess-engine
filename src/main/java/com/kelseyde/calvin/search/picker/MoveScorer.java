package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SEE;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.picker.MovePicker.MoveType;
import com.kelseyde.calvin.search.picker.MovePicker.Stage;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;

/**
 * Assigns a score to a move to determine the order in which moves are tried during search. The score is based on several
 * heuristics, and those heuristics differ depending on whether the move is a noisy move - such as a capture, check, or
 * promotion - or else a quieter, positional move.
 */
public class MoveScorer {

    private final EngineConfig config;
    private final MoveGenerator movegen;
    private final SearchHistory history;
    private final SearchStack ss;
    private final int seeNoisyDivisor;
    private final int seeNoisyOffset;
    private final boolean inCheck;

    public MoveScorer(EngineConfig config,
                      MoveGenerator movegen,
                      SearchHistory history,
                      SearchStack ss,
                      int seeNoisyDivisor,
                      int seeNoisyOffset,
                      boolean inCheck) {
        this.config = config;
        this.movegen = movegen;
        this.history = history;
        this.ss = ss;
        this.seeNoisyDivisor = seeNoisyDivisor;
        this.seeNoisyOffset = seeNoisyOffset;
        this.inCheck = inCheck;
    }

    public ScoredMove score(Board board, Move move, int ply, Stage stage) {

        final Piece piece = board.pieceAt(move.from());
        final Piece captured = board.captured(move);
        final boolean capture = captured != null;
        final boolean promotion = move.isPromotion();
        final boolean givesCheck = movegen.givesCheck(board, move);

        if (inCheck)
            return scoreEvasion(board, move, piece, captured, givesCheck, ply);
        else if (promotion)
            return scorePromotion(board, move, piece, captured, givesCheck);
        else if (capture)
            return scoreNoisy(board, move, piece, captured, givesCheck);
        else
            return scoreQuiet(board, move, piece, givesCheck, ply);

    }

    private ScoredMove scoreNoisy(Board board, Move move, Piece piece, Piece captured, boolean givesCheck) {

        int historyScore = history.captureHistory().get(piece, move.to(), captured, board.isWhite());
        int score = SEE.value(captured) + historyScore / 4;
        int threshold = -score / seeNoisyDivisor + seeNoisyOffset;
        MoveType type = SEE.see(board, move, threshold) ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;
        return new ScoredMove(move, piece, captured, score, historyScore, givesCheck, type);

    }

    private ScoredMove scoreQuiet(Board board, Move move, Piece piece, boolean givesCheck, int ply) {

        final int historyScore = history.quietHistory().get(move, piece, board.isWhite());
        final int contHistScore = continuationHistoryScore(move, piece, board.isWhite(), ply);
        final int score = historyScore + contHistScore;
        MoveType type = score >= config.goodQuietThreshold() ? MoveType.GOOD_QUIET : MoveType.BAD_QUIET;
        return new ScoredMove(move, piece, null, score, score, givesCheck, type);

    }

    private ScoredMove scorePromotion(Board board, Move move, Piece piece, Piece captured, boolean givesCheck) {

        final MoveType type = move.promoPiece() == Piece.QUEEN ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;
        int score = SEE.value(move.promoPiece()) - SEE.value(Piece.PAWN);
        return new ScoredMove(move, piece, captured, score, 0, givesCheck, type);

    }

    private ScoredMove scoreEvasion(Board board, Move move, Piece piece, Piece captured, boolean givesCheck, int ply) {

        final MoveType type = MoveType.BAD_NOISY;
        final int historyScore = history.quietHistory().get(move, piece, board.isWhite());
        final int contHistScore = continuationHistoryScore(move, piece, board.isWhite(), ply);
        int score = historyScore + contHistScore;
        return new ScoredMove(move, piece, captured, score, historyScore, givesCheck, type);

    }

    private int continuationHistoryScore(Move move, Piece piece, boolean white, int ply) {

        // Continuation history is based on the history score indexed by the current move and the move played x plies ago.
        int contHistScore = 0;
        for (int contHistPly : config.contHistPlies()) {
            SearchStackEntry entry = ss.get(ply - contHistPly);
            if (entry != null && entry.move != null) {
                Move prevMove = entry.move;
                Piece prevPiece = entry.piece;
                contHistScore += history.continuationHistory().get(prevMove, prevPiece, move, piece, white);
            }
        }
        return contHistScore;

    }

}
