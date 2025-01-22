package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.SEE;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;
import com.kelseyde.calvin.search.picker.MovePicker.Stage;

/**
 * Assigns a score to a move to determine the order in which moves are tried during search. The score is based on several
 * heuristics, and those heuristics differ depending on whether the move is a noisy move - such as a capture, check, or
 * promotion - or else a quieter, positional move.
 */
public class MoveScorer {

    private final EngineConfig config;
    private final SearchHistory history;
    private final SearchStack ss;

    public MoveScorer(EngineConfig config, SearchHistory history, SearchStack ss) {
        this.config = config;
        this.history = history;
        this.ss = ss;
    }

    public ScoredMove score(Board board, Move move, int ply, Stage stage) {

        final Piece piece = board.pieceAt(move.from());
        final Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.to());

        final boolean capture = captured != null;
        final boolean promotion = move.isPromotion();
        final boolean quietCheck = stage == Stage.GEN_NOISY && !promotion && !capture;

        // Noisy moves are captures, promotions, and quiet checks (meaning checks that are not captures or promotions).
        final boolean noisy = quietCheck || capture || promotion;

        return noisy ?
                scoreNoisy(board, move, piece, captured, quietCheck) :
                scoreQuiet(board, move, piece, ply);

    }

    private ScoredMove scoreNoisy(Board board, Move move, Piece piece, Piece captured, boolean quietCheck) {

        final boolean white = board.isWhite();
        final boolean capture = captured != null;
        final boolean promotion = move.promoPiece() != null;

        int score = 0;

        final int historyScore = history.getNoisyHistoryTable().get(move, piece, captured, white);
        score += historyScore / 8;

        MoveType type = MoveType.GOOD_NOISY;

        if (capture) {
            // Captures are separated into good and bad based on the SEE score, with a threshold based on the history score.
            score += SEE.value(captured);
            final int threshold = -score / 4 + config.seeNoisyOffset.value;
            type = SEE.see(board, move, threshold) ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;

        } else if (promotion) {
            // Queen promos are treated as 'good noisies', under promotions as 'bad noisies'
            score += SEE.value(move.promoPiece()) - SEE.value(Piece.PAWN);
            type = move.promoPiece() == Piece.QUEEN ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;

        } else if (quietCheck) {
            // Quiet checks are separated into good and bad based simply on the history score.
            type = historyScore > 0 ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;

        }

        return new ScoredMove(move, piece, captured, score, historyScore, type);

    }

    private ScoredMove scoreQuiet(Board board, Move move, Piece piece, int ply) {

        // Quiet moves are scored using the quiet history and continuation history heuristics.
        final int historyScore = history.getQuietHistoryTable().get(move, piece, board.isWhite());
        final int contHistScore = continuationHistoryScore(move, piece, board.isWhite(), ply);
        final int score = historyScore + contHistScore;

        return new ScoredMove(move, piece, null, score, historyScore, MoveType.QUIET);

    }

    private int continuationHistoryScore(Move move, Piece piece, boolean white, int ply) {

        // Continuation history is based on the history score indexed by the current move and the move played x plies ago.
        int contHistScore = 0;
        for (int contHistPly : config.contHistPlies) {
            SearchStackEntry entry = ss.get(ply - contHistPly);
            if (entry != null && entry.currentMove != null) {
                SearchHistory.PlayedMove prevMove = entry.currentMove;
                contHistScore += history.getContHistTable().get(prevMove.move(), prevMove.piece(), move, piece, white);
            }
        }
        return contHistScore;

    }

}
