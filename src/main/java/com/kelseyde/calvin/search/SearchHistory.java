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
    private final KillerTable killerTable;
    private final QuietHistoryTable quietHistoryTable;
    private final ContinuationHistoryTable contHistTable;
    private final CaptureHistoryTable captureHistoryTable;
    private final HashCorrectionTable pawnCorrHistTable;
    private final HashCorrectionTable[] nonPawnCorrHistTables;
    private final PieceToCorrectionTable countermoveCorrHistTable;

    private int bestMoveStability = 0;
    private int bestScoreStability = 0;

    public SearchHistory(EngineConfig config) {
        this.config = config;
        this.killerTable = new KillerTable();
        this.quietHistoryTable = new QuietHistoryTable(config);
        this.contHistTable = new ContinuationHistoryTable(config);
        this.captureHistoryTable = new CaptureHistoryTable(config);
        this.pawnCorrHistTable = new HashCorrectionTable();
        this.nonPawnCorrHistTables = new HashCorrectionTable[] { new HashCorrectionTable(), new HashCorrectionTable() };
        this.countermoveCorrHistTable = new PieceToCorrectionTable();
    }

    public void updateHistory(
            Board board, Move bestMove, Move[] quiets, Move[] captures, boolean white, int depth, int ply, SearchStack ss) {

        // When the best move causes a beta cut-off, we want to update the various history tables to reward the best move
        // and punish the other moves that were searched. Doing so will hopefully improve move ordering in future searches.

        boolean bestMoveCapture = board.isCapture(bestMove);
        if (!bestMoveCapture) {
            killerTable.add(ply, bestMove);

            for (Move quiet : quiets) {
                // If the best move was quiet, give it a boost in the quiet history table, and penalise all other quiets.
                updateQuietHistory(board, quiet, bestMove, ss, white, depth, ply);
            }
        }

        for (Move capture : captures) {
            // If the best move was a capture, give it a boost in the capture history table. Regardless of whether the
            // best move was quiet or a capture, penalise all other captures.
            updateCaptureHistory(board, capture, bestMove, white, depth);
        }

    }

    public void updateQuietHistory(Board board, Move quietMove, Move bestMove, SearchStack ss, boolean white, int depth, int ply) {
        // For quiet moves we update both the standard quiet and continuation history tables
        if (quietMove == null)
            return;
        boolean good = quietMove.equals(bestMove);
        Piece piece = board.pieceAt(quietMove.from());
        quietHistoryTable.update(quietMove, piece, depth, white, good);
        for (int prevPly : config.contHistPlies()) {
            SearchStackEntry prevEntry = ss.get(ply - prevPly);
            if (prevEntry != null && prevEntry.move != null) {
                Move prevMove = prevEntry.move;
                Piece prevPiece = prevEntry.piece;
                contHistTable.update(prevMove, prevPiece, quietMove, piece, depth, white, good);
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

    public void updateBestMoveStability(Move bestMovePrevious, Move bestMoveCurrent) {
        if (bestMovePrevious == null || bestMoveCurrent == null) {
            return;
        }
        bestMoveStability = bestMovePrevious.equals(bestMoveCurrent) ? bestMoveStability + 1 : 0;
    }

    public void updateBestScoreStability(int scorePrevious, int scoreCurrent) {
        bestScoreStability = scoreCurrent >= scorePrevious - 10 && scoreCurrent <= scorePrevious + 10 ? bestScoreStability + 1 : 0;
    }

    public int evalCorrection(Board board, SearchStack ss, int ply) {
        int pawn    = pawnCorrHistTable.get(board.pawnKey(), board.isWhite());
        int white   = nonPawnCorrHistTables[Colour.WHITE].get(board.nonPawnKeys()[Colour.WHITE], board.isWhite());
        int black   = nonPawnCorrHistTables[Colour.BLACK].get(board.nonPawnKeys()[Colour.BLACK], board.isWhite());
        int counter = getContCorrHistEntry(ss, ply, board.isWhite());
        int correction = pawn + white + black + counter;
        return correction / CorrectionHistoryTable.SCALE;
    }

    public void updateCorrectionHistory(Board board, SearchStack ss, int ply, int depth, int score, int staticEval) {
        pawnCorrHistTable.update(board.pawnKey(), board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.WHITE].update(board.nonPawnKeys()[Colour.WHITE], board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.BLACK].update(board.nonPawnKeys()[Colour.BLACK], board.isWhite(), depth, score, staticEval);
        updateContCorrHistEntry(ss, ply, board.isWhite(), depth, score, staticEval);
    }

    private int getContCorrHistEntry(SearchStack ss, int ply, boolean white) {
        SearchStackEntry sse = ss.get(ply - 1);
        if (sse == null || sse.move == null) {
            return 0;
        }
        return countermoveCorrHistTable.get(white, sse.move, sse.piece);
    }

    private void updateContCorrHistEntry(SearchStack ss, int ply, boolean white, int depth, int score, int staticEval) {
        SearchStackEntry sse = ss.get(ply - 1);
        if (sse == null || sse.move == null) {
            return;
        }
        countermoveCorrHistTable.update(sse.move, sse.piece, white, staticEval, score, depth);
    }

    public int getBestMoveStability() {
        return bestMoveStability;
    }

    public int getBestScoreStability() {
        return bestScoreStability;
    }

    public KillerTable getKillerTable() {
        return killerTable;
    }

    public QuietHistoryTable getQuietHistoryTable() {
        return quietHistoryTable;
    }

    public ContinuationHistoryTable getContHistTable() {
        return contHistTable;
    }

    public CaptureHistoryTable getCaptureHistoryTable() {
        return captureHistoryTable;
    }

    public void reset() {
        bestMoveStability = 0;
        bestScoreStability = 0;
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
