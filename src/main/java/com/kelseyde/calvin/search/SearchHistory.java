package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.SearchStack.PlayedMove;
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
    private final TtMoveCorrectionHistoryTable ttMoveCorrectionHistoryTable;

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
        this.ttMoveCorrectionHistoryTable = new TtMoveCorrectionHistoryTable();
    }

    public void updateHistory(
            PlayedMove bestMove, boolean white, int depth, int ply, SearchStack ss, List<PlayedMove> quiets, List<PlayedMove> captures) {

        if (bestMove.isQuiet()) {

            killerTable.add(ply, bestMove.move());
            for (PlayedMove quiet : quiets) {
                boolean good = bestMove.move().equals(quiet.move());
                quietHistoryTable.update(quiet.move(), quiet.piece(), depth, white, good);

                for (int prevPly : CONT_HIST_PLIES) {
                    Move prevMove = ss.getMove(ply - prevPly);
                    Piece prevPiece = ss.getMovedPiece(ply - prevPly);
                    contHistTable.update(prevMove, prevPiece, quiet.move(), quiet.piece(), depth, white, good);
                }
            }

        }

        for (PlayedMove capture : captures) {
            boolean good = bestMove.equals(capture);
            Piece piece = capture.piece();
            int to = capture.move().to();
            Piece captured = capture.captured();
            captureHistoryTable.update(piece, to, captured, depth, white, good);
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

    public int correctEvaluation(Board board, Move ttMove, int staticEval) {
        int pawn = pawnCorrHistTable.get(board.pawnKey(), board.isWhite());
        int white = nonPawnCorrHistTables[Colour.WHITE].get(board.nonPawnKeys()[Colour.WHITE], board.isWhite());
        int black = nonPawnCorrHistTables[Colour.BLACK].get(board.nonPawnKeys()[Colour.BLACK], board.isWhite());
        int correction = pawn + white + black;
        if (ttMove != null && board.isQuiet(ttMove)) {
            Piece piece = board.pieceAt(ttMove.from());
            correction += ttMoveCorrectionHistoryTable.get(ttMove, piece, board.isWhite());
        }
        return staticEval + correction / CorrectionHistoryTable.SCALE;
    }

    public void updateCorrectionHistory(Board board, int depth, Move bestMove, int score, int staticEval) {
        pawnCorrHistTable.update(board.pawnKey(), board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.WHITE].update(board.nonPawnKeys()[Colour.WHITE], board.isWhite(), depth, score, staticEval);
        nonPawnCorrHistTables[Colour.BLACK].update(board.nonPawnKeys()[Colour.BLACK], board.isWhite(), depth, score, staticEval);
        if (bestMove != null && board.isQuiet(bestMove)) {
            Piece piece = board.pieceAt(bestMove.from());
            ttMoveCorrectionHistoryTable.update(bestMove, piece, board.isWhite(), depth, score, staticEval);
        }
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

    public QuietHistoryTable getHistoryTable() {
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
        ttMoveCorrectionHistoryTable.clear();
    }

}
