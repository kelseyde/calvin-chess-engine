package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.BoardUtils;

public class PawnEvaluation {

    public static int score(EngineConfig config, long friendlyPawns, long opponentPawns, float phase, boolean isWhite) {

        int passedPawnsMgBonus = 0;
        int passedPawnsEgBonus = 0;

        int isolatedPawnCount = 0;
        int doubledPawnCount = 0;

        long pawnsIterator = friendlyPawns;
        while (pawnsIterator > 0) {
            int pawn = Bitwise.getNextBit(pawnsIterator);
            int file = BoardUtils.getFile(pawn);

            // Bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
            // Bonus for a passed pawn that is additionally protected by another pawn (multiplied by number of defending pawns).
            if (isPassedPawn(pawn, opponentPawns, isWhite)) {
                passedPawnsMgBonus += calculatePassedPawnBonus(pawn, isWhite, config.getPassedPawnBonus()[0]);
                passedPawnsMgBonus += calculateProtectedPawnBonus(config, pawn, friendlyPawns, isWhite);
                passedPawnsEgBonus += calculatePassedPawnBonus(pawn, isWhite, config.getPassedPawnBonus()[1]);
                passedPawnsEgBonus += calculateProtectedPawnBonus(config, pawn, friendlyPawns, isWhite);
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
        // Penalties for isolated pawns, indexed by the number of isolated pawns.
        int isolatedPawnMgPenalty = config.getIsolatedPawnPenalty()[0][isolatedPawnCount];
        int isolatedPawnEgPenalty = config.getIsolatedPawnPenalty()[1][isolatedPawnCount];

        // Penalties for doubled pawns, indexed by the number of doubled pawns
        int doubledPawnMgPenalty =  config.getDoubledPawnPenalty()[0][doubledPawnCount];
        int doubledPawnEgPenalty =  config.getDoubledPawnPenalty()[1][doubledPawnCount];

        int middlegameScore = passedPawnsMgBonus + isolatedPawnMgPenalty + doubledPawnMgPenalty;
        int endgameScore = passedPawnsEgBonus + isolatedPawnEgPenalty + doubledPawnEgPenalty;

        return Phase.taperedEval(middlegameScore, endgameScore, phase);

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

    private static int calculateProtectedPawnBonus(EngineConfig config, int pawn, long friendlyPawns, boolean isWhite) {
        long protectionMask = isWhite ? Bits.WHITE_PROTECTED_PAWN_MASK[pawn] : Bits.BLACK_PROTECTED_PAWN_MASK[pawn];
        return Bitwise.countBits(protectionMask & friendlyPawns) * config.getProtectedPassedPawnBonus();
    }

}
