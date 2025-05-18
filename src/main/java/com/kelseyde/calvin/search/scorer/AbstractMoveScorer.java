package com.kelseyde.calvin.search.scorer;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;
import com.kelseyde.calvin.search.picker.ScoredMove;

public abstract class AbstractMoveScorer {

    protected final EngineConfig config;
    protected final MoveGenerator movegen;
    protected final SearchHistory history;
    protected final SearchStack ss;
    protected final Board board;
    protected final int ply;

    protected AbstractMoveScorer(EngineConfig config,
                                 MoveGenerator movegen,
                                 SearchHistory history,
                                 SearchStack ss,
                                 Board board,
                                 int ply) {
        this.config = config;
        this.movegen = movegen;
        this.history = history;
        this.ss = ss;
        this.board = board;
        this.ply = ply;
    }

    public abstract ScoredMove score(Move move);

    protected int quietHistoryScore(Board board, Move move, Piece piece) {
        return history.getQuietHistoryTable().get(move, piece, board.isWhite());
    }

    protected int captureHistoryScore(Board board, Move move, Piece piece, Piece captured) {
        return history.getCaptureHistoryTable().get(piece, move.to(), captured, board.isWhite());
    }

    protected int continuationHistoryScore(Move move, Piece piece, boolean white, int ply) {
        int contHistScore = 0;
        for (int contHistPly : config.contHistPlies()) {
            SearchStackEntry entry = ss.get(ply - contHistPly);
            contHistScore += history.getContHistTable().get(entry, move, piece, white);
        }
        return contHistScore;
    }

}
