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

import java.util.List;

public class SearchHistory {

    public record PlayedMove(Move move, Piece piece, Piece captured) {}

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

    public void updateHistory(PlayedMove bestMove, boolean white, int depth, int ply, SearchStack ss) {

        // When the best move causes a beta cut-off, we want to update the various history tables to reward the best move
        // and punish the other moves that were searched. Doing so will hopefully improve move ordering in future searches.

        List<PlayedMove> playedMoves = ss.get(ply).searchedMoves;

        if (bestMove.captured() == null) {
            killerTable.add(ply, bestMove.move());
        }

        for (PlayedMove playedMove : playedMoves) {
            if (bestMove.captured() == null && playedMove.captured() == null) {
                // If the best move was quiet, give it a boost in the quiet history table, and penalise all other quiets.
                updateQuietHistory(playedMove, bestMove, ss, white, depth, ply);
            }
            else if (playedMove.captured() != null) {
                // If the best move was a capture, give it a boost in the capture history table. Regardless of whether the
                // best move was quiet or a capture, penalise all other captures.
                updateCaptureHistory(playedMove, bestMove, white, depth);
            }
        }

    }

    private void updateQuietHistory(PlayedMove quietMove, PlayedMove bestMove, SearchStack ss, boolean white, int depth, int ply) {
        // For quiet moves we update both the standard quiet and continuation history tables
        boolean good = quietMove.move().equals(bestMove.move());
        quietHistoryTable.update(quietMove.move(), quietMove.piece(), depth, white, good);
        for (int prevPly : config.contHistPlies()) {
            SearchStackEntry prevEntry = ss.get(ply - prevPly);
            if (prevEntry != null && prevEntry.currentMove != null) {
                PlayedMove prevMove = prevEntry.currentMove;
                contHistTable.update(prevMove.move(), prevMove.piece(), quietMove.move(), quietMove.piece(), depth, white, good);
            }
        }
    }

    private void updateCaptureHistory(PlayedMove captureMove, PlayedMove bestMove, boolean white, int depth) {
        boolean good = captureMove.move().equals(bestMove.move());
        captureHistoryTable.update(captureMove.piece(), captureMove.move().to(), captureMove.captured(), depth, white, good);
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

    public int correctEvaluation(Board board, SearchStack ss, int ply, int staticEval) {
        int pawn    = pawnCorrHistTable.get(board.pawnKey(), board.isWhite());
        int white   = nonPawnCorrHistTables[Colour.WHITE].get(board.nonPawnKeys()[Colour.WHITE], board.isWhite());
        int black   = nonPawnCorrHistTables[Colour.BLACK].get(board.nonPawnKeys()[Colour.BLACK], board.isWhite());
        int counter = getContCorrHistEntry(ss, ply, board.isWhite());
        int correction = pawn + white + black + counter;
        return staticEval + correction / CorrectionHistoryTable.SCALE;
    }

    public void updateCorrectionHistory(Board board, SearchStack ss, int ply, int depth, int score, int staticEval) {
        pawnCorrHistTable.update(board.pawnKey(), board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.WHITE].update(board.nonPawnKeys()[Colour.WHITE], board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.BLACK].update(board.nonPawnKeys()[Colour.BLACK], board.isWhite(), depth, score, staticEval);
        updateContCorrHistEntry(ss, ply, board.isWhite(), depth, score, staticEval);
    }

    private int getContCorrHistEntry(SearchStack ss, int ply, boolean white) {
        SearchStackEntry sse = ss.get(ply - 1);
        if (sse == null || sse.currentMove == null) {
            return 0;
        }
        return countermoveCorrHistTable.get(white, sse.currentMove.move(), sse.currentMove.piece());
    }

    private void updateContCorrHistEntry(SearchStack ss, int ply, boolean white, int depth, int score, int staticEval) {
        SearchStackEntry sse = ss.get(ply - 1);
        if (sse == null || sse.currentMove == null) {
            return;
        }
        countermoveCorrHistTable.update(sse.currentMove.move(), sse.currentMove.piece(), white, staticEval, score, depth);
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
