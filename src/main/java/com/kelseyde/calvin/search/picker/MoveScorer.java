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
    private final SEE see;
    private int seeNoisyDivisor;
    private int seeNoisyOffset;

    public MoveScorer(EngineConfig config,
                      SearchHistory history,
                      SearchStack ss,
                      int seeNoisyDivisor,
                      int seeNoisyOffset) {
        this.config = config;
        this.history = history;
        this.ss = ss;
        this.see = new SEE(config);
        this.seeNoisyDivisor = seeNoisyDivisor;
        this.seeNoisyOffset = seeNoisyOffset;
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
                scoreNoisy(board, move, piece, captured, quietCheck, ply) :
                scoreQuiet(board, move, piece, ply);

    }

    private ScoredMove scoreNoisy(Board board, Move move, Piece piece, Piece captured, boolean quietCheck, int ply) {

        final boolean white = board.isWhite();

        int score = 0;

        boolean promotion = move.promoPiece() != null;
        if (promotion) {
            // Queen promos are treated as 'good noisies', under promotions as 'bad noisies'
            final MoveType type = move.promoPiece() == Piece.QUEEN ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;
            score += see.valueOf(move.promoPiece()) - see.valueOf(Piece.PAWN);
            return new ScoredMove(move, piece, captured, score, 0, type);
        }

        if (quietCheck) {
            // Quiet checks are treated as 'bad noisies' and scored using quiet history heuristics
            final MoveType type = MoveType.BAD_NOISY;
            final int historyScore = history.getQuietHistoryTable().get(move, piece, white);
            final int contHistScore = continuationHistoryScore(move, piece, white, ply);
            score = historyScore + contHistScore;
            return new ScoredMove(move, piece, captured, score, historyScore, type);
        }

        score += see.valueOf(captured);

        final int historyScore = history.getCaptureHistoryTable().get(piece, move.to(), captured, board.isWhite());
        score += historyScore / 4;

        final int threshold = -score / seeNoisyDivisor + seeNoisyOffset;

        // Separate good and bad noisies based on the material won or lost once all pieces are swapped off.
        final MoveType type = see.see(board, move, threshold) ? MoveType.GOOD_NOISY : MoveType.BAD_NOISY;

        return new ScoredMove(move, piece, captured, score, historyScore, type);
    }

    private ScoredMove scoreQuiet(Board board, Move move, Piece piece, int ply) {

        // Quiet moves are scored using the quiet history and continuation history heuristics.
        final int historyScore = history.getQuietHistoryTable().get(move, piece, board.isWhite());
        final int contHistScore = continuationHistoryScore(move, piece, board.isWhite(), ply);
        final int score = historyScore + contHistScore;

        return new ScoredMove(move, piece, null, score, score, MoveType.QUIET);

    }

    private int continuationHistoryScore(Move move, Piece piece, boolean white, int ply) {

        // Continuation history is based on the history score indexed by the current move and the move played x plies ago.
        int contHistScore = 0;
        for (int contHistPly : config.contHistPlies()) {
            SearchStackEntry entry = ss.get(ply - contHistPly);
            if (entry != null && entry.move != null) {
                Move prevMove = entry.move;
                Piece prevPiece = entry.piece;
                contHistScore += history.getContHistTable().get(prevMove, prevPiece, move, piece, white);
            }
        }
        return contHistScore;

    }

    public void setSeeNoisyDivisor(int seeNoisyDivisor) {
        this.seeNoisyDivisor = seeNoisyDivisor;
    }

    public void setSeeNoisyOffset(int seeNoisyOffset) {
        this.seeNoisyOffset = seeNoisyOffset;
    }

}
