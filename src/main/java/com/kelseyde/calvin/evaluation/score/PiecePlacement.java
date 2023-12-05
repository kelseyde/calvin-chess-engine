package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class PiecePlacement {

    public static int score(EngineConfig config, Board board, float phase, boolean isWhite) {
        int[][] mgTables = config.getMiddlegameTables();
        int[][] egTables = config.getEndgameTables();
        return scorePieces(board.getPawns(isWhite), isWhite, phase, mgTables[Piece.PAWN.getIndex()], egTables[Piece.PAWN.getIndex()]) +
                scorePieces(board.getKnights(isWhite), isWhite, phase, mgTables[Piece.KNIGHT.getIndex()], egTables[Piece.KNIGHT.getIndex()]) +
                scorePieces(board.getBishops(isWhite), isWhite, phase, mgTables[Piece.BISHOP.getIndex()], egTables[Piece.BISHOP.getIndex()]) +
                scorePieces(board.getRooks(isWhite), isWhite, phase, mgTables[Piece.ROOK.getIndex()], egTables[Piece.ROOK.getIndex()]) +
                scorePieces(board.getQueens(isWhite), isWhite, phase, mgTables[Piece.QUEEN.getIndex()], egTables[Piece.QUEEN.getIndex()]) +
                scorePieces(board.getKing(isWhite), isWhite, phase, mgTables[Piece.KING.getIndex()], egTables[Piece.KING.getIndex()]);
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
