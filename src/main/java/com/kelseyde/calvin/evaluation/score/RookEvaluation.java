package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.BoardUtils;

/**
 * Gives rooks a small bonus for being placed on an open or semi-open file. A separate bonus for rooks on the seventh rank
 * is not needed, since this is captured in the rook's {@link PieceSquareTable}.
 */
public class RookEvaluation {

    public static int score(EngineConfig config, Board board, float phase, boolean isWhite) {

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
                middlegameScore += config.getRookOpenFileBonus()[0];
                endgameScore += config.getRookOpenFileBonus()[1];
            }
            else if (hasSemiOpenFile) {
                middlegameScore += config.getRookSemiOpenFileBonus()[0];
                endgameScore += config.getRookSemiOpenFileBonus()[1];
            }

            rooks = Bitwise.popBit(rooks);
        }

        return Phase.taperedEval(middlegameScore, endgameScore, phase);

    }

}
