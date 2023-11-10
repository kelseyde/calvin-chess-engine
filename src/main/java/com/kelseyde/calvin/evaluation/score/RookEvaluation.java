package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.board.bitboard.Bitwise;
import com.kelseyde.calvin.utils.BoardUtils;

/**
 * Gives rooks a small bonus for being placed on an open or semi-open file. A separate bonus for rooks on the seventh rank
 * is not needed, since this is captured in the rook's {@link PieceSquareTable}.
 */
public class RookEvaluation {

    private static final int ROOK_OPEN_FILE_MG_BONUS = 42;
    private static final int ROOK_OPEN_FILE_EG_BONUS = 22;

    private static final int ROOK_SEMI_OPEN_FILE_MG_BONUS = 18;
    private static final int ROOK_SEMI_OPEN_FILE_EG_BONUS = 16;

    public static int score(Board board, float phase, boolean isWhite) {

        long rooks = board.getRooks(isWhite);
        if (rooks == 0) return 0;
        long friendlyPawns = board.getPawns(isWhite);
        long opponentPawns = board.getPawns(!isWhite);
        int middlegameScore = 0;
        int endgameScore = 0;

        while (rooks != 0) {
            int rook = Bitwise.getNextBit(rooks);
            int file = BoardUtils.getFile(rook);
            long fileMask = Bits.FILE_MASKS[file];

            boolean hasFriendlyPawn = (fileMask & friendlyPawns) != 0;
            boolean hasOpponentPawn = (fileMask & opponentPawns) != 0;
            boolean hasOpenFile = !hasFriendlyPawn && !hasOpponentPawn;
            boolean hasSemiOpenFile = !hasFriendlyPawn && hasOpponentPawn;

            if (hasOpenFile) {
                middlegameScore += ROOK_OPEN_FILE_MG_BONUS;
                endgameScore += ROOK_OPEN_FILE_EG_BONUS;
            }
            else if (hasSemiOpenFile) {
                middlegameScore += ROOK_SEMI_OPEN_FILE_MG_BONUS;
                endgameScore += ROOK_SEMI_OPEN_FILE_EG_BONUS;
            }

            rooks = Bitwise.popBit(rooks);
        }

        return GamePhase.taperedEval(middlegameScore, endgameScore, phase);

    }

}
