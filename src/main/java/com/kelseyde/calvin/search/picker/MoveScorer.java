package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.SEE;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.picker.MovePicker.MoveType;
import com.kelseyde.calvin.search.picker.MovePicker.Stage;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;

/**
 * Assigns a score to a {@link Move} to determine the order in which moves are tried during search. The score is based on several
 * heuristics, and those heuristics differ depending on whether the move is a noisy move - such as a capture, check, or
 * promotion - or else a quieter, positional move.
 */
public class MoveScorer {

    private final EngineConfig config;
    private final SearchHistory history;
    private final SearchStack ss;
    private final int seeNoisyDivisor;
    private final int seeNoisyOffset;
    private Stage stage;

    public MoveScorer(EngineConfig config,
                      SearchHistory history,
                      SearchStack ss,
                      int seeNoisyDivisor,
                      int seeNoisyOffset) {
        this.config = config;
        this.history = history;
        this.ss = ss;
        this.seeNoisyDivisor = seeNoisyDivisor;
        this.seeNoisyOffset = seeNoisyOffset;
    }

    /**
     * Assign a {@link Move} a score and {@link MoveType}. The scoring heuristics used depend on the type of move, with
     * different heuristics for quiets, captures and promotions.
     * @return the {@link ScoredMove} containing the move, its score, and type.
     */
    public ScoredMove score(Board board, Move move, int ply, Stage stage) {

        this.stage = stage;
        Piece piece = board.pieceAt(move.from());
        Piece captured = board.captured(move);
        boolean capture = captured != null;
        boolean promotion = move.isPromotion();

        if (promotion)
            return scorePromotion(move, piece, captured);
        else if (capture)
            return scoreCapture(board, move, piece, captured);
        else
            return scoreQuiet(board, move, piece, ply);

    }

    /**
     * Promotions are considered noisy moves. They are scored based on the value of the promotion piece. Queen
     * promotions are considered 'good noisies', while under-promotions are considered 'bad noisies'.
     */
    private ScoredMove scorePromotion(Move move, Piece piece, Piece captured) {

        int score = SEE.value(move.promoPiece()) - SEE.value(Piece.PAWN);
        MoveType type = move.promoPiece() == Piece.QUEEN ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;
        return new ScoredMove(move, piece, captured, score, 0, type);

    }

    /**
     * Captures are scored based on the value of the captured piece (MVV, Most Valuable Victim), and their score in the
     * capture history table. They are separated into 'good' and 'bad' noisies based on whether they pass a SEE threshold
     * that is determined by their MVV + capthist score.
     */
    private ScoredMove scoreCapture(Board board, Move move, Piece piece, Piece captured) {

        int historyScore = history.captureHistory().get(piece, move.to(), captured, board.isWhite());
        int score = SEE.value(captured) + historyScore / 4;
        int threshold = -score / seeNoisyDivisor + seeNoisyOffset;
        MoveType type = SEE.see(board, move, threshold) ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;
        return new ScoredMove(move, piece, captured, score, historyScore, type);

    }

    /**
     * Quiets are scored based on their score in the quiet and continuation history tables. They are separated into 'good'
     * and 'bad' quiets based on whether their history score exceeds a configurable threshold - except for quiet checks
     * that are generated during the noisy stage, which are considered 'bad noisies' regardless of score.
     */
    private ScoredMove scoreQuiet(Board board, Move move, Piece piece, int ply) {

        int historyScore = history.quietHistory().get(move, piece, board.isWhite());
        int contHistScore = continuationHistoryScore(move, piece, board.isWhite(), ply);
        int score = historyScore + contHistScore;
        MoveType type = stage == Stage.GEN_NOISY
                ? MoveType.BAD_NOISY
                : (score >= config.goodQuietThreshold() ? MoveType.GOOD_QUIET : MoveType.BAD_QUIET);
        return new ScoredMove(move, piece, null, score, score, type);

    }

    /**
     * Continuation history is based on the history score indexed by the current move and the move played x plies ago.
     * Here we aggregate the conthist score for this move for all the configured conthist plies.
     */
    private int continuationHistoryScore(Move move, Piece piece, boolean white, int ply) {

        int contHistScore = 0;
        for (int contHistPly : config.contHistPlies()) {
            SearchStackEntry entry = ss.get(ply - contHistPly);
            if (entry != null)
                contHistScore += history.continuationHistory().get(entry.move, entry.piece, move, piece, white);
        }
        return contHistScore;

    }

}
