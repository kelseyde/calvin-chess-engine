package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;
import com.kelseyde.calvin.tables.history.*;

import java.util.List;

public class SearchHistory {

    private static final int[] CONT_HIST_PLIES = { 1, 2 };

    private final KillerTable killerTable;
    private final QuietHistoryTable quietHistoryTable;
    private final ContinuationHistoryTable contHistTable;
    private final CaptureHistoryTable captureHistoryTable;
    private final CorrectionHistoryTable pawnCorrHistTable;
    private final CorrectionHistoryTable[] nonPawnCorrHistTables;
    private final CorrectionHistoryTable kingCorrHistTable;

    private int bestMoveStability = 0;
    private int bestScoreStability = 0;

    public SearchHistory(EngineConfig config) {
        this.killerTable = new KillerTable();
        this.quietHistoryTable = new QuietHistoryTable(config);
        this.contHistTable = new ContinuationHistoryTable(config);
        this.captureHistoryTable = new CaptureHistoryTable(config);
        this.pawnCorrHistTable = new CorrectionHistoryTable();
        this.nonPawnCorrHistTables = new CorrectionHistoryTable[] {
                new CorrectionHistoryTable(), new CorrectionHistoryTable()
        };
        this.kingCorrHistTable = new CorrectionHistoryTable();
    }

    public void updateHistory(
            PlayedMove bestMove, boolean white, int depth, int ply, SearchStack ss, boolean failHigh) {

        List<PlayedMove> playedMoves = ss.get(ply).searchedMoves;

        if (bestMove.isQuiet()) {
            killerTable.add(ply, bestMove.move);
        }

        for (PlayedMove playedMove : playedMoves) {
            if (bestMove.isQuiet() && playedMove.isQuiet()) {

                boolean good = bestMove.move.equals(playedMove.move);
                if (good || failHigh) {
                    quietHistoryTable.update(playedMove.move, playedMove.piece, depth, white, good);
                    for (int prevPly : CONT_HIST_PLIES) {
                        SearchStackEntry prevEntry = ss.get(ply - prevPly);
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

    public int correctEvaluation(Board board, int staticEval) {
        int pawn = pawnCorrHistTable.get(board.pawnKey(), board.isWhite());
        int white = nonPawnCorrHistTables[Colour.WHITE].get(board.nonPawnKeys()[Colour.WHITE], board.isWhite());
        int black = nonPawnCorrHistTables[Colour.BLACK].get(board.nonPawnKeys()[Colour.BLACK], board.isWhite());
        int king = kingCorrHistTable.get(board.kingKey(), board.isWhite());
        int correction = pawn + white + black + king;
        return staticEval + correction / CorrectionHistoryTable.SCALE;
    }

    public void updateCorrectionHistory(Board board, int depth, int score, int staticEval) {
        pawnCorrHistTable.update(board.pawnKey(), board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.WHITE].update(board.nonPawnKeys()[Colour.WHITE], board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.BLACK].update(board.nonPawnKeys()[Colour.BLACK], board.isWhite(), depth, score, staticEval);
        kingCorrHistTable.update(board.kingKey(), board.isWhite(), depth, score, staticEval);
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
        quietHistoryTable.ageScores(true);
        quietHistoryTable.ageScores(false);
    }

    public void clear() {
        killerTable.clear();
        quietHistoryTable.clear();
        contHistTable.clear();
        captureHistoryTable.clear();
        pawnCorrHistTable.clear();
        nonPawnCorrHistTables[Colour.WHITE].clear();
        nonPawnCorrHistTables[Colour.BLACK].clear();
        kingCorrHistTable.clear();
    }

}
