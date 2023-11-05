package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.board.bitboard.Bitwise;

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
        return scorePieces(pawns, Piece.PAWN, isWhite, pieceSquareTables) +
                scorePieces(knights, Piece.KNIGHT, isWhite, pieceSquareTables) +
                scorePieces(bishops, Piece.BISHOP, isWhite, pieceSquareTables) +
                scorePieces(rooks, Piece.ROOK, isWhite, pieceSquareTables) +
                scorePieces(queens, Piece.QUEEN, isWhite, pieceSquareTables) +
                scorePieces(king, Piece.KING, isWhite, pieceSquareTables);
    }

    private int scorePieces(long pieces, Piece pieceType, boolean isWhite, int[][] pieceSquareTables) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = Bitwise.getNextBit(pieces);
            int pieceIndex = pieceType.getIndex();
            int squareIndex = isWhite ? square ^ 56 : square;
            pieceTypeScore += pieceSquareTables[pieceIndex][squareIndex];
            pieces = Bitwise.popBit(pieces);
        }
        return pieceTypeScore;
    }

}
