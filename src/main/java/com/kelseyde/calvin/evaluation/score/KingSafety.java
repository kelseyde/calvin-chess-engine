package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.Distance;

/**
 * King safety evaluation gives bonuses for a castled king with a secure 'pawn shield' - that is the pawns infront
 */
public class KingSafety {

    public static int score(EngineConfig config, Board board, Material opponentMaterial, float phase, boolean isWhite) {

        if (phase <= 0.5) {
            return 0;
        }
        int kingSquare = Bitwise.getNextBit(board.getKing(isWhite));
        int kingFile = BoardUtils.getFile(kingSquare);

        long friendlyPawns = board.getPawns(isWhite);
        long opponentPawns = board.getPawns(!isWhite);

        int pawnShieldPenalty = calculatePawnShieldPenalty(config, kingSquare, kingFile, friendlyPawns);
        int openKingFilePenalty = calculateOpenKingFilePenalty(config, kingFile, friendlyPawns, opponentPawns, opponentMaterial);
        int lostCastlingRightsPenalty = calculateLostCastlingRightsPenalty(config, board, isWhite, kingFile);

        if (opponentMaterial.queens() == 0) {
            // King safety matters less without opponent queen
            phase *= 0.6f;
        }

        return (int) -((pawnShieldPenalty + openKingFilePenalty + lostCastlingRightsPenalty) * phase);

    }

    private static int calculatePawnShieldPenalty(EngineConfig config, int kingSquare, int kingFile, long pawns) {
        int pawnShieldPenalty = 0;
        if (kingFile <= 2 || kingFile >= 5) {
            long tripleFileMask = Bits.TRIPLE_FILE_MASK[kingFile];

            // Add penalty for a castled king with pawns far away from their starting squares.
            long pawnShieldMask =  tripleFileMask & pawns;
            while (pawnShieldMask != 0) {
                int pawn = Bitwise.getNextBit(pawnShieldMask);
                int distance = Distance.chebyshev(kingSquare, pawn);
                pawnShieldPenalty += config.getKingPawnShieldPenalty()[distance];
                pawnShieldMask = Bitwise.popBit(pawnShieldMask);
            }
        }
        return pawnShieldPenalty;
    }

    private static int calculateOpenKingFilePenalty(EngineConfig config, int kingFile, long friendlyPawns, long opponentPawns, Material opponentMaterial) {
        int openKingFilePenalty = 0;
        if (opponentMaterial.rooks() > 0 || opponentMaterial.queens() > 0) {

            for (int attackFile = kingFile - 1; attackFile <= kingFile + 1; attackFile++) {
                if (attackFile < 0 || attackFile > 7) {
                    continue;
                }
                long fileMask = Bits.FILE_MASKS[attackFile];
                boolean isKingFile = attackFile == kingFile;
                boolean isFriendlyPawnMissing = (friendlyPawns & fileMask) == 0;
                boolean isOpponentPawnMissing = (opponentPawns & fileMask) == 0;
                if (isFriendlyPawnMissing || isOpponentPawnMissing) {
                    // Add penalty for semi-open file around the king
                    openKingFilePenalty += isKingFile ? config.getKingSemiOpenFilePenalty() : config.getKingSemiOpenAdjacentFilePenalty();
                }
                if (isFriendlyPawnMissing && isOpponentPawnMissing) {
                    // Add penalty for fully open file around king
                    openKingFilePenalty += isKingFile ? config.getKingOpenFilePenalty() : config.getKingSemiOpenFilePenalty();
                }
            }

        }
        return openKingFilePenalty;
    }

    private static int calculateLostCastlingRightsPenalty(EngineConfig config, Board board, boolean isWhite, int kingFile) {
        if (kingFile <= 2 || kingFile >= 5) {
            return 0;
        }
        boolean hasCastlingRights = board.getGameState().hasCastlingRights(isWhite);
        boolean opponentHasCastlingRights = board.getGameState().hasCastlingRights(!isWhite);
        if (!hasCastlingRights && opponentHasCastlingRights) {
            return config.getKingLostCastlingRightsPenalty();
        }
        return 0;
    }

}
