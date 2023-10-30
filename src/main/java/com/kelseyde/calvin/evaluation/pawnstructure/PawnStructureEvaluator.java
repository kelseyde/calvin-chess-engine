package com.kelseyde.calvin.evaluation.pawnstructure;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.utils.BoardUtils;

/**
 * Evaluate certain characteristics of the remaining pawns: bonuses for passed pawns, connected pawns; penalties for
 * isolated and doubled pawns.
 */
public class PawnStructureEvaluator {

    // The bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
    private static final int[] PASSED_PAWN_BONUS = { 0, 140, 100, 60, 30, 15, 15 };

    // The bonus for a passed pawn that is additionally protected by another pawn (multiplied by number of defending pawns).
    private static final int PROTECTED_PASSED_PAWN_BONUS = 25;

    // The penalties for isolated pawns, indexed by the number of isolated pawns.
    private static final int[] ISOLATED_PAWN_PENALTY = { 0, -10, -25, -50, -75, -75, -75, -75, -75 };

    // The penalties for doubled pawns, indexed by the number of doubled pawns (two pawns on the same rank are
    // treated as 'separate' doubled pawns).
    private static final int[] DOUBLED_PAWN_PENALTY = { 0, -5, -10, -20, -40, -60, -75, -85, -95};

    public int evaluate(Board board, boolean isWhite) {

        long friendlyPawns = board.getPawns(isWhite);
        long opponentPawns = board.getPawns(!isWhite);

        int passedPawnsBonus = 0;
        int isolatedPawnCount = 0;
        int doubledPawnCount = 0;
        long friendlyPawnsIterator = friendlyPawns;
        while (friendlyPawnsIterator > 0) {
            int pawn = BitboardUtils.getLSB(friendlyPawnsIterator);
            int file = BoardUtils.getFile(pawn);

            if (isPassedPawn(pawn, opponentPawns, isWhite)) {
                passedPawnsBonus += calculatePassedPawnBonus(pawn, isWhite);
                passedPawnsBonus += calculateProtectedPawnBonus(pawn, friendlyPawns, isWhite);
            }
            // Passed pawns are not penalised for being isolated
            else if (isIsolatedPawn(file, friendlyPawns)) {
                isolatedPawnCount++;
            }
            if (isDoubledPawn(file, friendlyPawns)) {
                doubledPawnCount++;
            }

            friendlyPawnsIterator = BitboardUtils.popLSB(friendlyPawnsIterator);
        }
        int isolatedPawnPenalty = ISOLATED_PAWN_PENALTY[isolatedPawnCount];
        int doubledPawnPenalty =  DOUBLED_PAWN_PENALTY[doubledPawnCount];
        return passedPawnsBonus + isolatedPawnPenalty + doubledPawnPenalty;

    }

    private boolean isPassedPawn(int pawn, long opponentPawns, boolean isWhite) {
        long passedPawnMask = isWhite ? PawnBits.WHITE_PASSED_PAWN_MASK[pawn] : PawnBits.BLACK_PASSED_PAWN_MASK[pawn];
        return (passedPawnMask & opponentPawns) == 0;
    }

    private boolean isIsolatedPawn(int file, long friendlyPawns) {
        return (PawnBits.ADJACENT_FILE_MASK[file] & friendlyPawns) == 0;
    }

    private boolean isDoubledPawn(int file, long friendlyPawns) {
        long fileMask = Bits.FILE_MASKS[file];
        return Long.bitCount(friendlyPawns & fileMask) > 1;
    }

    private int calculatePassedPawnBonus(int pawn, boolean isWhite) {
        int rank = BoardUtils.getRank(pawn);
        int squaresFromPromotion = isWhite ? 7 - rank : rank;
        return PASSED_PAWN_BONUS[squaresFromPromotion];
    }

    private int calculateProtectedPawnBonus(int pawn, long friendlyPawns, boolean isWhite) {
        long protectionMask = isWhite ? PawnBits.WHITE_PROTECTED_PAWN_MASK[pawn] : PawnBits.BLACK_PROTECTED_PAWN_MASK[pawn];
        return Long.bitCount(protectionMask & friendlyPawns) * PROTECTED_PASSED_PAWN_BONUS;
    }

}
