package com.kelseyde.calvin.evaluation.mopup;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.evaluation.material.Material;
import com.kelseyde.calvin.evaluation.material.PieceValues;
import com.kelseyde.calvin.utils.Distance;

/**
 * When one side has a material advantage, they start to receive a bonus for pushing the enemy king to the edge of the board.
 * This is useful for endgame checkmates, where the checkmating line is beyond the horizon of the search algorithm.
 * The mop up bonus will direct the engine to corner the enemy king, up to the point that the checkmate is found during search.
 */
public class MopUpEvaluator {

    private static final int KING_MANHATTAN_DISTANCE_MULTIPLIER = 4;
    private static final int KING_CHEBYSHEV_DISTANCE_MULTIPLIER = 4;
    private static final int KING_CENTER_MANHATTAN_DISTANCE_MULTIPLIER = 10;

    public int evaluate(Board board, Material friendlyMaterial, Material opponentMaterial, boolean isWhite) {

        boolean twoPawnAdvantage = friendlyMaterial.eval() > (opponentMaterial.eval() + 2 * PieceValues.PAWN);

        if (twoPawnAdvantage && opponentMaterial.phase() < 1) {

            int mopUpEval = 0;

            int friendlyKing = BitboardUtils.getLSB(isWhite ? board.getWhiteKing() : board.getBlackKing());
            int opponentKing = BitboardUtils.getLSB(isWhite ? board.getBlackKing() : board.getWhiteKing());

            // Bonus for moving king closer to opponent king
            mopUpEval += (14 - Distance.manhattan(friendlyKing, opponentKing)) * KING_MANHATTAN_DISTANCE_MULTIPLIER;
            mopUpEval += (7 - Distance.chebyshev(friendlyKing, opponentKing)) * KING_CHEBYSHEV_DISTANCE_MULTIPLIER;

            // Bonus for pushing opponent king to the edges of the board
            mopUpEval += Distance.centerManhattan(opponentKing) * KING_CENTER_MANHATTAN_DISTANCE_MULTIPLIER;

            // Taper the eval based on how much material the opponent has remaining
            return mopUpEval * (int) (1 - opponentMaterial.phase());

        }

        return 0;
    }

}
