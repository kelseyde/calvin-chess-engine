package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;

public record PiecePlacement(long pawns,
                             long knights,
                             long bishops,
                             long rooks,
                             long queens,
                             long king) {

    public static PiecePlacement fromBoard(Board board, boolean isWhite) {
        long pawns = board.getPawns(isWhite);
        long knights = board.getKnights(isWhite);
        long bishops = board.getBishops(isWhite);
        long rooks = board.getRooks(isWhite);
        long queens = board.getQueens(isWhite);
        long king = board.getKing(isWhite);
        return new PiecePlacement(pawns, knights, bishops, rooks, queens, king);
    }

    public int sum(int[][] pieceSquareTables, boolean isWhite) {
        return scorePieces(pawns, PieceType.PAWN, isWhite, pieceSquareTables) +
                scorePieces(knights, PieceType.KNIGHT, isWhite, pieceSquareTables) +
                scorePieces(bishops, PieceType.BISHOP, isWhite, pieceSquareTables) +
                scorePieces(rooks, PieceType.ROOK, isWhite, pieceSquareTables) +
                scorePieces(queens, PieceType.QUEEN, isWhite, pieceSquareTables) +
                scorePieces(king, PieceType.KING, isWhite, pieceSquareTables);
    }

    private int scorePieces(long pieces, PieceType pieceType, boolean isWhite, int[][] pieceSquareTables) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = BitboardUtils.getLSB(pieces);
            int pieceIndex = pieceType.getIndex();
            int squareIndex = isWhite ? square ^ 56 : square;
            pieceTypeScore += pieceSquareTables[pieceIndex][squareIndex];
            pieces = BitboardUtils.popLSB(pieces);
        }
        return pieceTypeScore;
    }

}
