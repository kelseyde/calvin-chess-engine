package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.PieceType;

public record Material(int pawns,
                       int knights,
                       int bishops,
                       int rooks,
                       int queens) {

    public static Material fromBoard(Board board, boolean isWhite) {
        int pawns = Long.bitCount(board.getPawns(isWhite));
        int knights = Long.bitCount(board.getKnights(isWhite));
        int bishops = Long.bitCount(board.getBishops(isWhite));
        int rooks = Long.bitCount(board.getRooks(isWhite));
        int queens = Long.bitCount(board.getQueens(isWhite));
        return new Material(pawns, knights, bishops, rooks, queens);
    }

    public int sum(int[] pieceValues) {
        return (pawns * pieceValues[PieceType.PAWN.getIndex()]) +
                (knights * pieceValues[PieceType.KNIGHT.getIndex()]) +
                (bishops * pieceValues[PieceType.BISHOP.getIndex()]) +
                (rooks * pieceValues[PieceType.ROOK.getIndex()]) +
                (queens * pieceValues[PieceType.QUEEN.getIndex()]) +
                bishopPairBonus(bishops);
    }

    public boolean hasPiecesRemaining() {
        return knights > 0 || bishops > 0 || rooks > 0 || queens > 0;
    }

    private int bishopPairBonus(int bishops) {
        return bishops == 2 ? PieceValues.BISHOP_PAIR_BONUS : 0;
    }

}
