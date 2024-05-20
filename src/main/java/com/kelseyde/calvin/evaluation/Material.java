package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

/**
 * Stores how much material one side has.
 */
public record Material(int pawns,
                       int knights,
                       int bishops,
                       int rooks,
                       int queens) {

    public static Material fromBoard(Board board, boolean white) {
        int pawns = Bitwise.countBits(board.getPawns(white));
        int knights = Bitwise.countBits(board.getKnights(white));
        int bishops = Bitwise.countBits(board.getBishops(white));
        int rooks = Bitwise.countBits(board.getRooks(white));
        int queens = Bitwise.countBits(board.getQueens(white));
        return new Material(pawns, knights, bishops, rooks, queens);
    }

    public int sum(EngineConfig config, float phase) {
        int middlegameScore = sum(config.getPieceValues()[0], config.getBishopPairBonus());
        int endgameScore = sum(config.getPieceValues()[1], config.getBishopPairBonus());
        return Phase.taperedEval(middlegameScore, endgameScore, phase);
    }

    /**
     * Based on a provided piece-value array, counts the material score in centipawns.
     */
    public int sum(int[] pieceValues, int bishopPairBonus) {
        return (pawns * pieceValues[Piece.PAWN.getIndex()]) +
                (knights * pieceValues[Piece.KNIGHT.getIndex()]) +
                (bishops * pieceValues[Piece.BISHOP.getIndex()]) +
                (rooks * pieceValues[Piece.ROOK.getIndex()]) +
                (queens * pieceValues[Piece.QUEEN.getIndex()]) +
                (bishops == 2 ? bishopPairBonus : 0);
    }

    public boolean hasPiecesRemaining() {
        return knights > 0 || bishops > 0 || rooks > 0 || queens > 0;
    }

}
