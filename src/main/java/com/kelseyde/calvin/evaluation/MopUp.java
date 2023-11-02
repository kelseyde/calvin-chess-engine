package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.utils.Distance;

public class MopUp {

    private static final int KING_MANHATTAN_DISTANCE_MULTIPLIER = 4;
    private static final int KING_CHEBYSHEV_DISTANCE_MULTIPLIER = 4;
    private static final int KING_CENTER_MANHATTAN_DISTANCE_MULTIPLIER = 10;

    public static int score(Board board, Material friendlyMaterial, Material opponentMaterial, float phase, boolean isWhite) {

        int friendlyMaterialScore = friendlyMaterial.sum(PieceValues.SIMPLE_VALUES);
        int opponentMaterialScore = opponentMaterial.sum(PieceValues.SIMPLE_VALUES);

        boolean twoPawnAdvantage = friendlyMaterialScore > (opponentMaterialScore + 2 * PieceValues.valueOf(PieceType.PAWN));

        if (twoPawnAdvantage && phase < 1) {

            int mopUpEval = 0;

            int friendlyKing = BitboardUtils.getLSB(board.getKing(isWhite));
            int opponentKing = BitboardUtils.getLSB(board.getKing(!isWhite));

            // Bonus for moving king closer to opponent king
            mopUpEval += (14 - Distance.manhattan(friendlyKing, opponentKing)) * KING_MANHATTAN_DISTANCE_MULTIPLIER;
            mopUpEval += (7 - Distance.chebyshev(friendlyKing, opponentKing)) * KING_CHEBYSHEV_DISTANCE_MULTIPLIER;

            // Bonus for pushing opponent king to the edges of the board
            mopUpEval += Distance.centerManhattan(opponentKing) * KING_CENTER_MANHATTAN_DISTANCE_MULTIPLIER;

            // TODO is this correct?
            // Taper the eval based on how much material is remaining
            return mopUpEval * (int) (1 - phase);

        }

        return 0;
    }

}
