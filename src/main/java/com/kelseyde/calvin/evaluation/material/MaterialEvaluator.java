package com.kelseyde.calvin.evaluation.material;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MaterialEvaluator implements BoardEvaluator {

    @Override
    public int evaluate(Board board) {
        int colourModifier = board.isWhiteToMove() ? 1 : -1;
        int whiteScore = calculateMaterialScore(board, true);
        int blackScore = calculateMaterialScore(board, false);
        return colourModifier * (whiteScore - blackScore);
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
