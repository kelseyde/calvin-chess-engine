package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;

public record Material(int pawns,
                       int knights,
                       int bishops,
                       int rooks,
                       int queens) {

    public static Material fromBoard(Board board, boolean isWhite) {
        int pawns = Bitwise.countBits(board.getPawns(isWhite));
        int knights = Bitwise.countBits(board.getKnights(isWhite));
        int bishops = Bitwise.countBits(board.getBishops(isWhite));
        int rooks = Bitwise.countBits(board.getRooks(isWhite));
        int queens = Bitwise.countBits(board.getQueens(isWhite));
        return new Material(pawns, knights, bishops, rooks, queens);
    }

    public int sum(int[] pieceValues) {
        return (pawns * pieceValues[Piece.PAWN.getIndex()]) +
                (knights * pieceValues[Piece.KNIGHT.getIndex()]) +
                (bishops * pieceValues[Piece.BISHOP.getIndex()]) +
                (rooks * pieceValues[Piece.ROOK.getIndex()]) +
                (queens * pieceValues[Piece.QUEEN.getIndex()]) +
                bishopPairBonus(bishops);
    }

    public boolean hasPiecesRemaining() {
        return knights > 0 || bishops > 0 || rooks > 0 || queens > 0;
    }

    private int bishopPairBonus(int bishops) {
        return bishops == 2 ? PieceValues.BISHOP_PAIR_BONUS : 0;
    }

}
