package com.kelseyde.calvin.evaluation.placement;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import org.springframework.stereotype.Service;

@Service
public class PiecePlacementEvaluator implements BoardEvaluator {

    @Override
    public int evaluate(Board board) {
        int colourModifier = board.isWhiteToMove() ? 1 : -1;
        int whiteScore = calculatePlacementScore(board, true);
        int blackScore = calculatePlacementScore(board, false);
        return colourModifier * (whiteScore - blackScore);
    }

    private int calculatePlacementScore(Board board, boolean isWhite) {

        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long knights = isWhite ? board.getWhiteKnights() : board.getBlackKnights();
        long bishops = isWhite ? board.getWhiteBishops() : board.getBlackBishops();
        long rooks = isWhite ? board.getWhiteRooks() : board.getBlackRooks();
        long queens = isWhite ? board.getWhiteQueens() : board.getBlackQueens();
        long king = isWhite ? board.getWhiteKing() : board.getBlackKing();

        return scorePieceType(PieceType.PAWN, pawns, isWhite)
                + scorePieceType(PieceType.KNIGHT, knights, isWhite)
                + scorePieceType(PieceType.BISHOP, bishops, isWhite)
                + scorePieceType(PieceType.ROOK, rooks, isWhite)
                + scorePieceType(PieceType.QUEEN, queens, isWhite)
                + scorePieceType(PieceType.KING, king, isWhite);
    }

    private int scorePieceType(PieceType type, long pieceBB, boolean isWhite) {
        int pieceTypeScore = 0;
        while (pieceBB != 0) {
            int piece = BitBoardUtils.scanForward(pieceBB);
            pieceTypeScore += PieceSquareTable.scorePiece(type, piece, isWhite);
            pieceBB = BitBoardUtils.popLSB(pieceBB);
        }
        return pieceTypeScore;
    }

}
