package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.Distance;

public class KingSafety {

    private static final int[] PAWN_SHIELD_DISTANCE_PENALTY = new int[] {0, 0, 10, 25, 50, 50, 50};
    private static final int SEMI_OPEN_KING_FILE_PENALTY = 15;
    private static final int SEMI_OPEN_ADJACENT_FILE_PENALTY = 10;
    private static final int OPEN_KING_FILE_PENALTY = 25;
    private static final int OPEN_ADJACENT_FILE_PENALTY = 15;
    private static final int LOST_CASTLING_RIGHTS_PENALTY = 120;

    public static int score(Board board, Material opponentMaterial, float phase, boolean isWhite) {

        if (phase <= 0.5) {
            return 0;
        }
        int kingSquare = BitboardUtils.getLSB(board.getKing(isWhite));
        int kingFile = BoardUtils.getFile(kingSquare);

        long friendlyPawns = board.getPawns(isWhite);
        long opponentPawns = board.getPawns(!isWhite);

        int pawnShieldPenalty = calculatePawnShieldPenalty(kingSquare, kingFile, friendlyPawns);
        int openKingFilePenalty = calculateOpenKingFilePenalty(kingFile, friendlyPawns, opponentPawns, opponentMaterial);
        int lostCastlingRightsPenalty = calculateLostCastlingRightsPenalty(board, isWhite, kingFile);

        if (opponentMaterial.queens() == 0) {
            // King safety matters less without opponent queen
            phase *= 0.6f;
        }

        return (int) -((pawnShieldPenalty + openKingFilePenalty + lostCastlingRightsPenalty) * phase);

    }

    private static int calculatePawnShieldPenalty(int kingSquare, int kingFile, long pawns) {
        int pawnShieldPenalty = 0;
        if (kingFile <= 2 || kingFile >= 5) {

            long tripleFileMask = Bits.TRIPLE_FILE_MASK[kingFile];

            // Add penalty for a castled king with pawns far away from their starting squares.
            long pawnShieldMask =  tripleFileMask & pawns;
            while (pawnShieldMask != 0) {
                int pawn = BitboardUtils.getLSB(pawnShieldMask);
                int distance = Distance.chebyshev(kingSquare, pawn);
                pawnShieldPenalty += PAWN_SHIELD_DISTANCE_PENALTY[distance];
                pawnShieldMask = BitboardUtils.popLSB(pawnShieldMask);
            }
        }
        return pawnShieldPenalty;
    }

    private static int calculateOpenKingFilePenalty(int kingFile, long friendlyPawns, long opponentPawns, Material opponentMaterial) {
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
                    openKingFilePenalty += isKingFile ? SEMI_OPEN_KING_FILE_PENALTY : SEMI_OPEN_ADJACENT_FILE_PENALTY;
                }
                if (isFriendlyPawnMissing && isOpponentPawnMissing) {
                    // Add penalty for fully open file around king
                    openKingFilePenalty += isKingFile ? OPEN_KING_FILE_PENALTY : OPEN_ADJACENT_FILE_PENALTY;
                }
            }

        }
        return openKingFilePenalty;
    }

    private static int calculateLostCastlingRightsPenalty(Board board, boolean isWhite, int kingFile) {
        if (kingFile <= 2 || kingFile >= 5) {
            return 0;
        }
        boolean hasCastlingRights = board.getGameState().hasCastlingRights(isWhite);
        boolean opponentHasCastlingRights = board.getGameState().hasCastlingRights(!isWhite);
        if (!hasCastlingRights && opponentHasCastlingRights) {
            return LOST_CASTLING_RIGHTS_PENALTY;
        }
        return 0;
    }

}
