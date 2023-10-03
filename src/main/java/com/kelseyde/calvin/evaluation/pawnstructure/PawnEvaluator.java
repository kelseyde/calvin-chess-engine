package com.kelseyde.calvin.evaluation.pawnstructure;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.utils.BoardUtils;

/**
 * Evaluate certain characteristics of the remaining pawns: bonuses for passed pawns, connected pawns; penalties for
 * isolated and doubled pawns.
 */
public class PawnEvaluator implements BoardEvaluator {

    // The bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
    private static final int[] PASSED_PAWN_BONUS = { 0, 120, 80, 50, 30, 15, 15 };

    // The penalties for isolated pawns, indexed by the number of isolated pawns.
    private static final int[] ISOLATED_PAWN_PENALTY = { 0, -10, -25, -50, -75, -75, -75, -75, -75 };

    // The penalties for doubled pawns, indexed by the number of doubled pawns (two pawns on the same rank are
    // treated as 'separate' doubled pawns.
    private static final int[] DOUBLED_PAWN_PENALTY = { 0, -5, -10, -20, -40, -60, -75, -85, -95};

    @Override
    public int evaluate(Board board) {
        int colourModifier = board.isWhiteToMove() ? 1 : -1;
        int whiteScore = calculatePawnScore(board, true);
        int blackScore = calculatePawnScore(board, false);
        return colourModifier * (whiteScore - blackScore);
    }

    private int calculatePawnScore(Board board, boolean isWhite) {

        long friendlyPawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long opponentPawns = isWhite ? board.getBlackPawns() : board.getWhitePawns();

        int score = 0;
        while (friendlyPawns > 0) {
            int pawn = BitBoardUtils.scanForward(friendlyPawns);
            int rank = BoardUtils.getRank(pawn);
            int file = BoardUtils.getFile(pawn);

            int passedPawnBonus = 0;
            long passedPawnMask = isWhite ? PawnBits.WHITE_PASSED_PAWN_MASK[pawn] : PawnBits.BLACK_PASSED_PAWN_MASK[pawn];
            if ((passedPawnMask & opponentPawns) == 0) {
                int squaresFromPromotion = isWhite ? 7 - rank : rank;
                passedPawnBonus += PASSED_PAWN_BONUS[squaresFromPromotion];
            }

            int isolatedPawnCount = 0;
            long adjacentPawnMask = PawnBits.ADJACENT_FILE_MASK[file];
            if ((adjacentPawnMask & friendlyPawns) == 0) {
                isolatedPawnCount++;
            }


            friendlyPawns = BitBoardUtils.popLSB(friendlyPawns);
        }
        return score;

    }

}
