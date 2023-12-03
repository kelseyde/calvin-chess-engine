package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;

/**
 * Piece square tables (PSTs) are a simple way to assign values to pieces placed on specific squares. Each piece has its
 * own PSTs - one for the middlegame and one for the endgame - and the evaluation score is tapered between the two based
 * on the game phase.
 * </p>
 * @see <a href="https://www.chessprogramming.org/Piece-Square_Tables">Chess Programming Wiki</a>
 */
public record PiecePlacement(long pawns, long knights, long bishops, long rooks, long queens, long king) {

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
        return scorePieces(pawns, isWhite, pieceSquareTables[Piece.PAWN.getIndex()]) +
                scorePieces(knights, isWhite, pieceSquareTables[Piece.KNIGHT.getIndex()]) +
                scorePieces(bishops, isWhite, pieceSquareTables[Piece.BISHOP.getIndex()]) +
                scorePieces(rooks, isWhite, pieceSquareTables[Piece.ROOK.getIndex()]) +
                scorePieces(queens, isWhite, pieceSquareTables[Piece.QUEEN.getIndex()]) +
                scorePieces(king, isWhite, pieceSquareTables[Piece.KING.getIndex()]);
    }

    private int scorePieces(long pieces, boolean isWhite, int[] pieceSquareTables) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = Bitwise.getNextBit(pieces);
            int squareIndex = isWhite ? square ^ 56 : square;
            pieceTypeScore += pieceSquareTables[squareIndex];
            pieces = Bitwise.popBit(pieces);
        }
        return pieceTypeScore;
    }

}
