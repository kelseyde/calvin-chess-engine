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

        int score = 0;

        int pawns = Long.bitCount(isWhite ? board.getWhitePawns() : board.getBlackPawns());
        int knights = Long.bitCount(isWhite ? board.getWhiteKnights() : board.getBlackKnights());
        int bishops = Long.bitCount(isWhite ? board.getWhiteBishops() : board.getBlackBishops());
        int rooks = Long.bitCount(isWhite ? board.getWhiteRooks() : board.getBlackRooks());
        int queens = Long.bitCount(isWhite ? board.getWhiteQueens() : board.getBlackQueens());
        int king = Long.bitCount(isWhite ? board.getWhiteKing() : board.getBlackKing());

        score += pawns * PieceValues.PAWN;
        score += knights * PieceValues.KNIGHT;
        score += bishops * PieceValues.BISHOP;
        score += rooks * PieceValues.ROOK;
        score += queens * PieceValues.QUEEN;
        score += king * PieceValues.KING;

        if (bishops == 2) {
            score += PieceValues.BISHOP_PAIR;
        }

        return score;
    }

}
