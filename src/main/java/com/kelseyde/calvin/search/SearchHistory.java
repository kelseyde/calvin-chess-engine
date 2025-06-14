package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;
import com.kelseyde.calvin.tables.correction.CorrectionHistoryTable;
import com.kelseyde.calvin.tables.correction.HashCorrectionTable;
import com.kelseyde.calvin.tables.correction.PieceToCorrectionTable;
import com.kelseyde.calvin.tables.history.CaptureHistoryTable;
import com.kelseyde.calvin.tables.history.ContinuationHistoryTable;
import com.kelseyde.calvin.tables.history.KillerTable;
import com.kelseyde.calvin.tables.history.QuietHistoryTable;

public class SearchHistory {

    private final EngineConfig config;
    private final SearchStack ss;
    private final KillerTable killerTable;
    private final QuietHistoryTable quietHistoryTable;
    private final ContinuationHistoryTable contHistTable;
    private final CaptureHistoryTable captureHistoryTable;
    private final HashCorrectionTable pawnCorrHistTable;
    private final HashCorrectionTable[] nonPawnCorrHistTables;
    private final PieceToCorrectionTable countermoveCorrHistTable;

    public SearchHistory(EngineConfig config, SearchStack ss) {
        this.config = config;
        this.ss = ss;
        this.killerTable = new KillerTable();
        this.quietHistoryTable = new QuietHistoryTable(config);
        this.contHistTable = new ContinuationHistoryTable(config);
        this.captureHistoryTable = new CaptureHistoryTable(config);
        this.pawnCorrHistTable = new HashCorrectionTable();
        this.nonPawnCorrHistTables = new HashCorrectionTable[] { new HashCorrectionTable(), new HashCorrectionTable() };
        this.countermoveCorrHistTable = new PieceToCorrectionTable();
    }

    public void updateHistory(
            Board board, Move bestMove, Move[] quiets, Move[] captures, boolean white, int depth, int ply) {

        // When the best move causes a beta cut-off, we want to update the various history tables to reward the best move
        // and punish the other moves that were searched. Doing so will hopefully improve move ordering in future searches.

        boolean bestMoveCapture = board.isCapture(bestMove);
        if (!bestMoveCapture) {
            killerTable.add(ply, bestMove);

            // If the best move was quiet, give it a boost in the quiet history table, and penalise all other quiets.
            for (Move quiet : quiets)
                updateQuietHistory(board, quiet, bestMove, white, depth, ply);
        }

        // If the best move was a capture, give it a boost in the capture history table. Regardless of whether the
        // best move was quiet or a capture, penalise all other captures.
        for (Move capture : captures)
            updateCaptureHistory(board, capture, bestMove, white, depth);

    }

    public void updateQuietHistory(Board board, Move quietMove, Move bestMove, boolean white, int depth, int ply) {
        // For quiet moves we update both the standard quiet and continuation history tables
        if (quietMove == null)
            return;
        boolean good = quietMove.equals(bestMove);
        Piece piece = board.pieceAt(quietMove.from());
        quietHistoryTable.update(quietMove, piece, depth, white, good);
        updateContHist(quietMove, piece, white, good, depth, ply);
    }

    public void updateContHist(Move move, Piece piece, boolean white, boolean good, int depth, int ply) {
        for (int prevPly : config.contHistPlies()) {
            SearchStackEntry prevEntry = ss.get(ply - prevPly);
            if (prevEntry != null && prevEntry.move != null) {
                Move prevMove = prevEntry.move;
                Piece prevPiece = prevEntry.piece;
                contHistTable.update(prevMove, prevPiece, move, piece, depth, white, good);
            }
        }
    }

    public void updateCaptureHistory(Board board, Move captureMove, Move bestMove, boolean white, int depth) {
        if (captureMove == null)
            return;
        boolean good = captureMove.equals(bestMove);
        Piece piece = board.pieceAt(captureMove.from());
        Piece captured = captureMove.isEnPassant() ? Piece.PAWN : board.pieceAt(captureMove.to());
        captureHistoryTable.update(piece, captureMove.to(), captured, depth, white, good);
    }

    public int evalCorrection(Board board, int ply) {
        int pawn    = pawnCorrHistTable.get(board.pawnKey(), board.isWhite());
        int white   = nonPawnCorrHistTables[Colour.WHITE].get(board.nonPawnKeys()[Colour.WHITE], board.isWhite());
        int black   = nonPawnCorrHistTables[Colour.BLACK].get(board.nonPawnKeys()[Colour.BLACK], board.isWhite());
        int counter = getContCorrHistEntry(ply, board.isWhite());
        int correction = pawn + white + black + counter;
        return correction / CorrectionHistoryTable.SCALE;
    }

    public int squaredCorrectionTerms(Board board, int ply) {
        int pawn    = pawnCorrHistTable.get(board.pawnKey(), board.isWhite());
        int white   = nonPawnCorrHistTables[Colour.WHITE].get(board.nonPawnKeys()[Colour.WHITE], board.isWhite());
        int black   = nonPawnCorrHistTables[Colour.BLACK].get(board.nonPawnKeys()[Colour.BLACK], board.isWhite());
        int counter = getContCorrHistEntry(ply, board.isWhite());
        return pawn * pawn + white * white + black * black + counter * counter;
    }

    public void updateCorrectionHistory(Board board, int ply, int depth, int score, int staticEval) {
        pawnCorrHistTable.update(board.pawnKey(), board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.WHITE].update(board.nonPawnKeys()[Colour.WHITE], board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.BLACK].update(board.nonPawnKeys()[Colour.BLACK], board.isWhite(), depth, score, staticEval);
        updateContCorrHistEntry(ss, ply, board.isWhite(), depth, score, staticEval);
    }

    private int getContCorrHistEntry(int ply, boolean white) {
        SearchStackEntry sse = ss.get(ply - 1);
        if (sse == null || sse.move == null)
            return 0;
        return countermoveCorrHistTable.get(white, sse.move, sse.piece);
    }

    private void updateContCorrHistEntry(SearchStack ss, int ply, boolean white, int depth, int score, int staticEval) {
        SearchStackEntry sse = ss.get(ply - 1);
        if (sse == null || sse.move == null)
            return;
        countermoveCorrHistTable.update(sse.move, sse.piece, white, staticEval, score, depth);
    }

    public KillerTable killerTable() {
        return killerTable;
    }

    public QuietHistoryTable quietHistory() {
        return quietHistoryTable;
    }

    public ContinuationHistoryTable continuationHistory() {
        return contHistTable;
    }

    public CaptureHistoryTable captureHistory() {
        return captureHistoryTable;
    }

    public void clear() {
        killerTable.clear();
        quietHistoryTable.clear();
        contHistTable.clear();
        captureHistoryTable.clear();
        pawnCorrHistTable.clear();
        nonPawnCorrHistTables[Colour.WHITE].clear();
        nonPawnCorrHistTables[Colour.BLACK].clear();
        countermoveCorrHistTable.clear();
    }

}
