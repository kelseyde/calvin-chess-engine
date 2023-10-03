package com.kelseyde.calvin.evaluation.pawnstructure;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.utils.BoardUtils;

/**
 * Evaluate certain characteristics of the remaining pawns: bonuses for passed pawns, connected pawns; penalties for
 * isolated and doubled pawns.
 */
public class PawnEvaluator implements BoardEvaluator {

    // The bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
    private static final int[] PASSED_PAWN_BONUS = { 0, 120, 80, 50, 30, 15, 15 };

    // The bonus for a passed pawn that is additionally protected by another pawn (multiplied by number of defending pawns).
    private static final int PROTECTED_PASSED_PAWN_BONUS = 25;

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

        int passedPawnsBonus = 0;
        int isolatedPawnCount = 0;
        int doubledPawnCount = 0;
        long friendlyPawnsIterator = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        while (friendlyPawnsIterator > 0) {
            int pawn = BitBoardUtils.scanForward(friendlyPawnsIterator);
            int rank = BoardUtils.getRank(pawn);
            int file = BoardUtils.getFile(pawn);

            long passedPawnMask = isWhite ? PawnBits.WHITE_PASSED_PAWN_MASK[pawn] : PawnBits.BLACK_PASSED_PAWN_MASK[pawn];
            if ((passedPawnMask & opponentPawns) == 0) {
                int squaresFromPromotion = isWhite ? 7 - rank : rank;
                int passedPawnBonus = PASSED_PAWN_BONUS[squaresFromPromotion];
                passedPawnsBonus += passedPawnBonus;

                long protectionMask = isWhite ? PawnBits.WHITE_PROTECTED_PAWN_MASK[pawn] : PawnBits.BLACK_PROTECTED_PAWN_MASK[pawn];
                int protectingPawnsBonus = Long.bitCount(protectionMask & friendlyPawns) * PROTECTED_PASSED_PAWN_BONUS;
                passedPawnsBonus += protectingPawnsBonus;
            }
            // Passed pawns are not penalised for being isolated
            else if ((PawnBits.ADJACENT_FILE_MASK[file] & friendlyPawns) == 0) {
                isolatedPawnCount++;
            }

            long fileMask = Bits.FILE_MASKS[file];
            if (Long.bitCount(friendlyPawns & fileMask) > 1) {
                doubledPawnCount++;
            }

            friendlyPawnsIterator = BitBoardUtils.popLSB(friendlyPawnsIterator);
        }
        return passedPawnsBonus + ISOLATED_PAWN_PENALTY[isolatedPawnCount] + DOUBLED_PAWN_PENALTY[doubledPawnCount];

    }

}
