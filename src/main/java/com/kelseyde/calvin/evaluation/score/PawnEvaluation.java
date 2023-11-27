package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.board.bitboard.Bitwise;
import com.kelseyde.calvin.utils.BoardUtils;

public class PawnEvaluation {

    // Bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
    private static final int[] PASSED_PAWN_MG_BONUS = { 0, 140, 100, 60, 30, 15, 15 };
    private static final int[] PASSED_PAWN_EG_BONUS = { 0, 250, 140, 85, 45, 25, 25 };

    // Bonus for a passed pawn that is additionally protected by another pawn (multiplied by number of defending pawns).
    private static final int PROTECTED_PASSED_PAWN_BONUS = 25;

    // Penalties for isolated pawns, indexed by the number of isolated pawns.
    private static final int[] ISOLATED_PAWN_MG_PENALTY = { 0, -10, -25, -50, -75, -75, -75, -75, -75 };
    private static final int[] ISOLATED_PAWN_EG_PENALTY = { 0, -20, -35, -65, -80, -80, -80, -80, -80 };

    // Penalties for doubled pawns, indexed by the number of doubled pawns
    private static final int[] DOUBLED_PAWN_MG_PENALTY = { 0, -5, -10, -20, -40, -60, -75, -85, -95};
    private static final int[] DOUBLED_PAWN_EG_PENALTY = { 0, -15, -25, -35, -55, -75, -90, -100, -110};

    public static int score(long friendlyPawns, long opponentPawns, float phase, boolean isWhite) {

        int passedPawnsMgBonus = 0;
        int passedPawnsEgBonus = 0;

        int isolatedPawnCount = 0;
        int doubledPawnCount = 0;

        long pawnsIterator = friendlyPawns;
        while (pawnsIterator > 0) {
            int pawn = Bitwise.getNextBit(pawnsIterator);
            int file = BoardUtils.getFile(pawn);

            if (isPassedPawn(pawn, opponentPawns, isWhite)) {
                passedPawnsMgBonus += calculatePassedPawnBonus(pawn, isWhite, PASSED_PAWN_MG_BONUS);
                passedPawnsMgBonus += calculateProtectedPawnBonus(pawn, friendlyPawns, isWhite);
                passedPawnsEgBonus += calculatePassedPawnBonus(pawn, isWhite, PASSED_PAWN_EG_BONUS);
                passedPawnsEgBonus += calculateProtectedPawnBonus(pawn, friendlyPawns, isWhite);
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
        int isolatedPawnMgPenalty = ISOLATED_PAWN_MG_PENALTY[isolatedPawnCount];
        int isolatedPawnEgPenalty = ISOLATED_PAWN_EG_PENALTY[isolatedPawnCount];

        int doubledPawnMgPenalty =  DOUBLED_PAWN_MG_PENALTY[doubledPawnCount];
        int doubledPawnEgPenalty =  DOUBLED_PAWN_EG_PENALTY[doubledPawnCount];

        int middlegameScore = passedPawnsMgBonus + isolatedPawnMgPenalty + doubledPawnMgPenalty;
        int endgameScore = passedPawnsEgBonus + isolatedPawnEgPenalty + doubledPawnEgPenalty;

        return GamePhase.taperedEval(middlegameScore, endgameScore, phase);

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

    private static int calculatePassedPawnBonus(int pawn, boolean isWhite, int[] passedPawnBonuses) {
        int rank = BoardUtils.getRank(pawn);
        int squaresFromPromotion = isWhite ? 7 - rank : rank;
        return passedPawnBonuses[squaresFromPromotion];
    }

    private static int calculateProtectedPawnBonus(int pawn, long friendlyPawns, boolean isWhite) {
        long protectionMask = isWhite ? Bits.WHITE_PROTECTED_PAWN_MASK[pawn] : Bits.BLACK_PROTECTED_PAWN_MASK[pawn];
        return Bitwise.countBits(protectionMask & friendlyPawns) * PROTECTED_PASSED_PAWN_BONUS;
    }

}
