package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.piece.PieceValues;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaterialEvaluator implements PositionEvaluator {

    @Override
    public int evaluate(Board board) {

//        boolean isWhiteToMove = board.isWhiteToMove();
//        int modifier = isWhiteToMove ? 1 : 0;

        int whiteScore = calculateMaterialScore(board, true);
        int blackScore = calculateMaterialScore(board, false);

        int score = whiteScore - blackScore;
//        log.info("white score: {}, black score: {}, score: {}", whiteScore, blackScore, score);
        return score;

    }

    private int calculateMaterialScore(Board board, boolean isWhite) {

        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long knights = isWhite ? board.getWhiteKnights() : board.getBlackKnights();
        long bishops = isWhite ? board.getWhiteBishops() : board.getBlackBishops();
        long rooks = isWhite ? board.getWhiteRooks() : board.getBlackRooks();
        long queens = isWhite ? board.getWhiteQueens() : board.getBlackQueens();
        long king = isWhite ? board.getWhiteKing() : board.getBlackKing();

        return (Long.bitCount(pawns) * PieceValues.PAWN) +
                (Long.bitCount(knights) * PieceValues.KNIGHT) +
                (Long.bitCount(bishops) * PieceValues.BISHOP) +
                (Long.bitCount(rooks) * PieceValues.ROOK) +
                (Long.bitCount(queens) * PieceValues.QUEEN) +
                (Long.bitCount(king) * PieceValues.KING);

    }

}
