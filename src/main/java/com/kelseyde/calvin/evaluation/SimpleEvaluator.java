package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.material.Material;

public class SimpleEvaluator implements Evaluation {

    private static final int KNIGHT_PHASE = 1;
    private static final int BISHOP_PHASE = 1;
    private static final int ROOK_PHASE = 2;
    private static final int QUEEN_PHASE = 4;
    private static final float TOTAL_PHASE =
            (KNIGHT_PHASE * 4) + (BISHOP_PHASE * 4) + (ROOK_PHASE * 4) + (QUEEN_PHASE * 2);

    private Board board;

    public SimpleEvaluator(Board board) {
        init(board);
    }

    @Override
    public void init(Board board) {

        boolean isWhite = board.isWhiteToMove();
        float phase =

    }

    @Override
    public void makeMove(Move move) {

    }

    @Override
    public void unmakeMove() {

    }

    @Override
    public int get() {
        return 0;
    }

    private Material countMaterial(boolean isWhite) {
        int pawns = Long.bitCount(board.getPawns(isWhite));
        int knights = Long.bitCount(board.getKnights(isWhite));
        int bishops = Long.bitCount(board.getBishops(isWhite));
        int rooks = Long.bitCount(board.getRooks(isWhite));
        int queens = Long.bitCount(board.getQueens(isWhite));
        return null;
    }

    private float calculatePhase(Board board) {
        int whiteKnights = Long.bitCount(board.getKnights(true));
        int blackKnights = Long.bitCount(board.getKnights(false));
        int whiteBishops = Long.bitCount(board.getBishops(true));
        int blackBishops = Long.bitCount(board.getBishops(false));
        int whiteRooks = Long.bitCount(board.getRooks(true));
        int blackRooks = Long.bitCount(board.getRooks(false));
        int whiteQueens = Long.bitCount(board.getQueens(true));
        int blackQueens = Long.bitCount(board.getQueens(false));

        float phase = TOTAL_PHASE;
        phase -= (whiteKnights * KNIGHT_PHASE);
        phase -= (blackKnights * KNIGHT_PHASE);
        phase -= (whiteBishops * BISHOP_PHASE);
        phase -= (blackBishops * BISHOP_PHASE);
        phase -= (whiteRooks * ROOK_PHASE);
        phase -= (blackRooks * ROOK_PHASE);
        phase -= (whiteQueens * QUEEN_PHASE);
        phase -= (blackQueens * QUEEN_PHASE);

        return (phase * 256 + (TOTAL_PHASE / 2)) / TOTAL_PHASE;
    }

}
