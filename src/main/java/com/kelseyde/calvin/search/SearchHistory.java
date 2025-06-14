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

    private short bonus(int depth, short scale, short max) {
        return (short) Math.min(scale * depth, max);
    }

    private short malus(int depth, short scale, short max) {
        return (short) -Math.min(scale * depth, max);
    }

    public void updateQuietHistories(Board board, Move quiet, boolean white, int depth, int ply, boolean good) {
        // For quiet moves we update both the standard quiet and continuation history tables
        if (quiet == null)
            return;
        Piece piece = board.pieceAt(quiet.from());
        updateQuietHistory(quiet, piece, white, depth, good);
        updateContHistory(quiet, piece, white, depth, ply, good);
    }

    public void updateQuietHistory(Move move, Piece piece, boolean white, int depth, boolean good) {
        short scale = good ? (short) config.quietHistBonusScale() : (short) config.quietHistMalusScale();
        short max = good ? (short) config.quietHistBonusMax() : (short) config.quietHistMalusMax();
        short bonus = good ? bonus(depth, scale, max) : malus(depth, scale, max);
        quietHistoryTable.add(move, piece, white, bonus);
    }

    public void updateContHistory(Move move, Piece piece, boolean white, int depth, int ply, boolean good) {
        short scale = good ? (short) config.contHistBonusScale() : (short) config.contHistMalusScale();
        short max = good ? (short) config.contHistBonusMax() : (short) config.contHistMalusMax();
        short bonus = good ? bonus(depth, scale, max) : malus(depth, scale, max);
        for (int prevPly : config.contHistPlies()) {
            SearchStackEntry prevEntry = ss.get(ply - prevPly);
            if (prevEntry != null && prevEntry.move != null) {
                Move prevMove = prevEntry.move;
                Piece prevPiece = prevEntry.piece;
                contHistTable.add(prevMove, prevPiece, move, piece, white, bonus);
            }
        }
    }

    public void updateCaptureHistory(Board board, Move capture, boolean white, int depth, boolean good) {
        if (capture == null)
            return;
        Piece piece = board.pieceAt(capture.from());
        Piece captured = board.captured(capture);
        short scale = good ? (short) config.captHistBonusScale() : (short) config.captHistMalusScale();
        short max = good ? (short) config.captHistBonusMax() : (short) config.captHistMalusMax();
        short bonus = good ? bonus(depth, scale, max) : malus(depth, scale, max);
        captureHistoryTable.add(piece, capture.to(), captured, white, bonus);
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
