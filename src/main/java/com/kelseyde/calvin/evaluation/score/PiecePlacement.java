package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class PiecePlacement {

    public static int score(EngineConfig config, Board board, float phase, boolean isWhite) {
        long pawns = board.getPawns(isWhite);
        long knights = board.getKnights(isWhite);
        long bishops = board.getBishops(isWhite);
        long rooks = board.getRooks(isWhite);
        long queens = board.getQueens(isWhite);
        long king = board.getKing(isWhite);
        return score(isWhite, pawns, knights, bishops, rooks, queens, king, phase, config.getMiddlegameTables(), config.getEndgameTables());
    }

    private static int score(boolean isWhite, long pawns, long knights, long bishops, long rooks, long queens, long king, float phase, int[][] middlegameTables, int[][] endgameTables) {
        return scorePieces(pawns, isWhite, phase, middlegameTables[Piece.PAWN.getIndex()], endgameTables[Piece.PAWN.getIndex()]) +
                scorePieces(knights, isWhite, phase, middlegameTables[Piece.KNIGHT.getIndex()], endgameTables[Piece.KNIGHT.getIndex()]) +
                scorePieces(bishops, isWhite, phase, middlegameTables[Piece.BISHOP.getIndex()], endgameTables[Piece.BISHOP.getIndex()]) +
                scorePieces(rooks, isWhite, phase, middlegameTables[Piece.ROOK.getIndex()], endgameTables[Piece.ROOK.getIndex()]) +
                scorePieces(queens, isWhite, phase, middlegameTables[Piece.QUEEN.getIndex()], endgameTables[Piece.QUEEN.getIndex()]) +
                scorePieces(king, isWhite, phase, middlegameTables[Piece.KING.getIndex()], endgameTables[Piece.KING.getIndex()]);
    }

    private static int scorePieces(long pieces, boolean isWhite, float phase, int[] middlegameTable, int[] endgameTable) {
        int mgScore = 0;
        int egScore = 0;
        while (pieces != 0) {
            int square = Bitwise.getNextBit(pieces);
            int squareIndex = isWhite ? square ^ 56 : square;
            mgScore += middlegameTable[squareIndex];
            egScore += endgameTable[squareIndex];
            pieces = Bitwise.popBit(pieces);
        }
        return Phase.taperedEval(mgScore, egScore, phase);
    }

}
