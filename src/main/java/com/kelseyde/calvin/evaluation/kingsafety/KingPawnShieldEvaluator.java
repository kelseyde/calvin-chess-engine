package com.kelseyde.calvin.evaluation.kingsafety;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.evaluation.material.Material;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnBits;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementScore;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.Distance;

public class KingPawnShieldEvaluator {

    private static final int[] PAWN_SHIELD_DISTANCE_PENALTY = new int[] {0, 0, 10, 25, 50, 50, 50};
    private static final int SEMI_OPEN_KING_FILE_PENALTY = 15;
    private static final int SEMI_OPEN_ADJACENT_FILE_PENALTY = 10;
    private static final int OPEN_KING_FILE_PENALTY = 25;
    private static final int OPEN_ADJACENT_FILE_PENALTY = 15;

    public int evaluate(Board board, Material opponentMaterial, boolean isWhite) {

        if (opponentMaterial.phase() <= 0.5) {
            return 0;
        }
        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());
        int kingFile = BoardUtils.getFile(kingSquare);

        long friendlyPawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long opponentPawns = isWhite ? board.getBlackPawns() : board.getWhitePawns();

        int pawnShieldPenalty = 0;

        if (kingFile <= 2 || kingFile >= 5) {

            long tripleFileMask = PawnBits.TRIPLE_FILE_MASK[kingFile];

            // Add penalty for a castled king with pawns far away from their starting squares.
            long pawnShieldMask =  tripleFileMask & friendlyPawns;
            while (pawnShieldMask != 0) {
                int pawn = BitboardUtils.getLSB(pawnShieldMask);
                int distance = Distance.chebyshev(kingSquare, pawn);
                pawnShieldPenalty += PAWN_SHIELD_DISTANCE_PENALTY[distance];
                pawnShieldMask = BitboardUtils.popLSB(pawnShieldMask);
            }
        }

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

        float endgameWeight = opponentMaterial.phase();
        if (opponentMaterial.queens() == 0) {
            // King safety matters less without opponent queen
            endgameWeight *= 0.6f;
        }

        return (int) -((pawnShieldPenalty + openKingFilePenalty) * endgameWeight);

    }

}
