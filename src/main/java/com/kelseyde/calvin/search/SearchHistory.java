package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;
import com.kelseyde.calvin.tables.correction.CorrectionHistoryTable;
import com.kelseyde.calvin.tables.correction.HashCorrectionTable;
import com.kelseyde.calvin.tables.correction.PieceToCorrectionTable;
import com.kelseyde.calvin.tables.history.*;

import java.util.List;

public class SearchHistory {

    private static final int[] CONT_HIST_PLIES = { 1, 2 };

    private final KillerTable killerTable;
    private final CounterMoveTable counterMoveTable;
    private final QuietHistoryTable quietHistoryTable;
    private final ContinuationHistoryTable contHistTable;
    private final CaptureHistoryTable captureHistoryTable;
    private final HashCorrectionTable pawnCorrHistTable;
    private final HashCorrectionTable[] nonPawnCorrHistTables;
    private final PieceToCorrectionTable countermoveCorrHistTable;

    private int bestMoveStability = 0;
    private int bestScoreStability = 0;

    public SearchHistory(EngineConfig config) {
        this.killerTable = new KillerTable();
        this.counterMoveTable = new CounterMoveTable();
        this.quietHistoryTable = new QuietHistoryTable(config);
        this.contHistTable = new ContinuationHistoryTable(config);
        this.captureHistoryTable = new CaptureHistoryTable(config);
        this.pawnCorrHistTable = new HashCorrectionTable();
        this.nonPawnCorrHistTables = new HashCorrectionTable[] { new HashCorrectionTable(), new HashCorrectionTable() };
        this.countermoveCorrHistTable = new PieceToCorrectionTable();
    }

    public void updateHistory(
            PlayedMove bestMove, boolean white, int depth, int ply, SearchStack ss, boolean failHigh) {

        List<PlayedMove> playedMoves = ss.get(ply).searchedMoves;

        SearchStackEntry prevEntry = ss.get(ply - 1);

        if (bestMove.isQuiet()) {
            killerTable.add(ply, bestMove.move);
            if (prevEntry != null && prevEntry.currentMove != null) {
                counterMoveTable.add(prevEntry.currentMove.piece, prevEntry.currentMove.move, white, bestMove.move);
            }
        }

        for (PlayedMove playedMove : playedMoves) {
            if (bestMove.isQuiet() && playedMove.isQuiet()) {

                boolean good = bestMove.move.equals(playedMove.move);
                if (good || failHigh) {
                    quietHistoryTable.update(playedMove.move, playedMove.piece, depth, white, good);
                    for (int prevPly : CONT_HIST_PLIES) {
                        prevEntry = ss.get(ply - prevPly);
                        if (prevEntry != null && prevEntry.currentMove != null) {
                            PlayedMove prevMove = prevEntry.currentMove;
                            contHistTable.update(prevMove.move, prevMove.piece, playedMove.move, playedMove.piece, depth, white, good);
                        }
                    }
                }
            }
            else if (playedMove.isCapture()) {
                boolean good = bestMove.move.equals(playedMove.move);
                if (good || failHigh) {
                    captureHistoryTable.update(playedMove.piece, playedMove.move.to(), playedMove.captured, depth, white, good);
                }
            }
        }

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
        return countermoveCorrHistTable.get(white, sse.currentMove.move, sse.currentMove.piece);
    }

    private void updateContCorrHistEntry(SearchStack ss, int ply, boolean white, int depth, int score, int staticEval) {
        SearchStackEntry sse = ss.get(ply - 1);
        if (sse == null || sse.currentMove == null) {
            return;
        }
        countermoveCorrHistTable.update(sse.currentMove.move, sse.currentMove.piece, white, staticEval, score, depth);
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

    public CounterMoveTable getCounterMoveTable() {
        return counterMoveTable;
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
        quietHistoryTable.ageScores(true);
        quietHistoryTable.ageScores(false);
    }

    public void clear() {
        killerTable.clear();
        counterMoveTable.clear();
        quietHistoryTable.clear();
        contHistTable.clear();
        captureHistoryTable.clear();
        pawnCorrHistTable.clear();
        nonPawnCorrHistTables[Colour.WHITE].clear();
        nonPawnCorrHistTables[Colour.BLACK].clear();
        countermoveCorrHistTable.clear();
    }

}
