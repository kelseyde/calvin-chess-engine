package com.kelseyde.calvin.search.scorer;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SEE;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.picker.AbstractMovePicker.MoveType;
import com.kelseyde.calvin.search.picker.ScoredMove;

public class EvasionMoveScorer extends AbstractMoveScorer {

    public EvasionMoveScorer(EngineConfig config,
                              MoveGenerator movegen,
                              SearchHistory history,
                              SearchStack ss,
                              Board board,
                              int ply) {
        super(config, movegen, history, ss, board, ply);
    }

    @Override
    public ScoredMove score(Move move) {
        final Piece piece = board.pieceAt(move.from());
        final Piece captured = board.captured(move);
        final boolean givesCheck = movegen.givesCheck(board, move);

        return captured != null
                ? scoreCapture(move, piece, captured, givesCheck)
                : scoreQuiet(move, piece, ply, givesCheck);
    }

    private ScoredMove scoreCapture(Move move, Piece piece, Piece captured, boolean givesCheck) {
        int historyScore = captureHistoryScore(board, move, piece, captured);
        int score = SEE.value(piece) + historyScore;
        return new ScoredMove(move, piece, captured, score, historyScore, givesCheck, MoveType.BAD_NOISY);
    }

    private ScoredMove scoreQuiet(Move move, Piece piece, int ply, boolean givesCheck) {
        int historyScore = quietHistoryScore(board, move, piece);
        int contHistScore = continuationHistoryScore(move, piece, board.isWhite(), ply);
        int score = historyScore + contHistScore;
        return new ScoredMove(move, piece, null, score, score, givesCheck, MoveType.QUIET);
    }

}
