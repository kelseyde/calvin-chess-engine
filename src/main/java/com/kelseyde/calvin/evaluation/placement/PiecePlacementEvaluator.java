package com.kelseyde.calvin.evaluation.placement;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import org.springframework.stereotype.Service;

@Service
public class PiecePlacementEvaluator {

    public int evaluate(Board board, float gamePhase, boolean isWhite) {

        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long knights = isWhite ? board.getWhiteKnights() : board.getBlackKnights();
        long bishops = isWhite ? board.getWhiteBishops() : board.getBlackBishops();
        long rooks = isWhite ? board.getWhiteRooks() : board.getBlackRooks();
        long queens = isWhite ? board.getWhiteQueens() : board.getBlackQueens();
        long king = isWhite ? board.getWhiteKing() : board.getBlackKing();

        int[] pawnStartTable = isWhite ? PieceSquareTable.WHITE_PAWN_START_TABLE : PieceSquareTable.BLACK_PAWN_START_TABLE;
        int[] pawnEndTable = isWhite ? PieceSquareTable.WHITE_PAWN_END_TABLE : PieceSquareTable.BLACK_PAWN_END_TABLE;
        int[] knightTable = isWhite ? PieceSquareTable.WHITE_KNIGHT_TABLE : PieceSquareTable.BLACK_KNIGHT_TABLE;
        int[] bishopTable = isWhite ? PieceSquareTable.WHITE_BISHOP_TABLE : PieceSquareTable.BLACK_BISHOP_TABLE;
        int[] rookTable = isWhite ? PieceSquareTable.WHITE_ROOK_TABLE : PieceSquareTable.BLACK_ROOK_TABLE;
        int[] queenTable = isWhite ? PieceSquareTable.WHITE_QUEEN_TABLE : PieceSquareTable.BLACK_QUEEN_TABLE;
        int[] kingStartTable = isWhite ? PieceSquareTable.WHITE_KING_START_TABLE : PieceSquareTable.BLACK_KING_START_TABLE;
        int[] kingEndTable = isWhite ? PieceSquareTable.WHITE_KING_END_TABLE : PieceSquareTable.BLACK_KING_END_TABLE;

        return scoreMultiTablePiece(pawns, gamePhase, pawnStartTable, pawnEndTable)
                + scoreSingleTablePiece(knights, knightTable)
                + scoreSingleTablePiece(bishops, bishopTable)
                + scoreSingleTablePiece(rooks, rookTable)
                + scoreSingleTablePiece(queens, queenTable)
                + scoreMultiTablePiece(king, gamePhase, kingStartTable, kingEndTable);
    }

    private int scoreSingleTablePiece(long pieces, int[] pieceTable) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = BitboardUtils.getLSB(pieces);
            pieceTypeScore += pieceTable[square];
            pieces = BitboardUtils.popLSB(pieces);
        }
        return pieceTypeScore;
    }

    private int scoreMultiTablePiece(long pieces, float gamePhase, int[] openingTable, int[] endgameTable) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = BitboardUtils.getLSB(pieces);
            // gives a tapered eval based on what phase the game is in
            pieceTypeScore += gamePhase * openingTable[square];
            pieceTypeScore += (1 - gamePhase) * endgameTable[square];
            pieces = BitboardUtils.popLSB(pieces);
        }
        return pieceTypeScore;
    }

}
