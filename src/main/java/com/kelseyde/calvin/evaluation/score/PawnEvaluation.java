package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.board.bitboard.Bitwise;
import com.kelseyde.calvin.utils.BoardUtils;

public class PawnEvaluation {

    // The bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
    private static final int[] PASSED_PAWN_BONUS = { 0, 140, 100, 60, 30, 15, 15 };

    // The bonus for a passed pawn that is additionally protected by another pawn (multiplied by number of defending pawns).
    private static final int PROTECTED_PASSED_PAWN_BONUS = 25;

    // The penalties for isolated pawns, indexed by the number of isolated pawns.
    private static final int[] ISOLATED_PAWN_PENALTY = { 0, -10, -25, -50, -75, -75, -75, -75, -75 };

    // The penalties for doubled pawns, indexed by the number of doubled pawns (two pawns on the same rank are
    // treated as 'separate' doubled pawns).
    private static final int[] DOUBLED_PAWN_PENALTY = { 0, -5, -10, -20, -40, -60, -75, -85, -95};

    public static int score(long friendlyPawns, long opponentPawns, boolean isWhite) {

        int passedPawnsBonus = 0;
        int isolatedPawnCount = 0;
        int doubledPawnCount = 0;

        long pawnsIterator = friendlyPawns;
        while (pawnsIterator > 0) {
            int pawn = Bitwise.getNextBit(pawnsIterator);
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

            pawnsIterator = Bitwise.popBit(pawnsIterator);
        }
        int isolatedPawnPenalty = ISOLATED_PAWN_PENALTY[isolatedPawnCount];
        int doubledPawnPenalty =  DOUBLED_PAWN_PENALTY[doubledPawnCount];
        return passedPawnsBonus + isolatedPawnPenalty + doubledPawnPenalty;

    }

    private static boolean isPassedPawn(int pawn, long opponentPawns, boolean isWhite) {
        long passedPawnMask = isWhite ? Bits.WHITE_PASSED_PAWN_MASK[pawn] : Bits.BLACK_PASSED_PAWN_MASK[pawn];
        return (passedPawnMask & opponentPawns) == 0;
    }

    private static boolean isIsolatedPawn(int file, long friendlyPawns) {
        return (Bits.ADJACENT_FILE_MASK[file] & friendlyPawns) == 0;
    }

    private static boolean isDoubledPawn(int file, long friendlyPawns) {
        long fileMask = Bits.FILE_MASKS[file];
        return Bitwise.countBits(friendlyPawns & fileMask) > 1;
    }

    private static int calculatePassedPawnBonus(int pawn, boolean isWhite) {
        int rank = BoardUtils.getRank(pawn);
        int squaresFromPromotion = isWhite ? 7 - rank : rank;
        return PASSED_PAWN_BONUS[squaresFromPromotion];
    }

    private static int calculateProtectedPawnBonus(int pawn, long friendlyPawns, boolean isWhite) {
        long protectionMask = isWhite ? Bits.WHITE_PROTECTED_PAWN_MASK[pawn] : Bits.BLACK_PROTECTED_PAWN_MASK[pawn];
        return Bitwise.countBits(protectionMask & friendlyPawns) * PROTECTED_PASSED_PAWN_BONUS;
    }

}
