package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.board.bitboard.Bitwise;
import com.kelseyde.calvin.utils.Distance;

public class MopUp {

    private static final int KING_MANHATTAN_DISTANCE_MULTIPLIER = 10;
    private static final int KING_CHEBYSHEV_DISTANCE_MULTIPLIER = 10;
    private static final int KING_CENTER_MANHATTAN_DISTANCE_MULTIPLIER = 14;

    public static int score(Board board, Material friendlyMaterial, Material opponentMaterial, boolean isWhite) {

        int friendlyMaterialScore = friendlyMaterial.sum(PieceValues.SIMPLE_VALUES);
        int opponentMaterialScore = opponentMaterial.sum(PieceValues.SIMPLE_VALUES);

        boolean twoPawnAdvantage = friendlyMaterialScore > (opponentMaterialScore + 2 * PieceValues.valueOf(Piece.PAWN));

        if (twoPawnAdvantage) {

            int mopUpEval = 0;

            int friendlyKing = Bitwise.getNextBit(board.getKing(isWhite));
            int opponentKing = Bitwise.getNextBit(board.getKing(!isWhite));

            // Bonus for moving king closer to opponent king
            mopUpEval += (14 - Distance.manhattan(friendlyKing, opponentKing)) * KING_MANHATTAN_DISTANCE_MULTIPLIER;
            mopUpEval += (7 - Distance.chebyshev(friendlyKing, opponentKing)) * KING_CHEBYSHEV_DISTANCE_MULTIPLIER;

            // Bonus for pushing opponent king to the edges of the board
            mopUpEval += Distance.centerManhattan(opponentKing) * KING_CENTER_MANHATTAN_DISTANCE_MULTIPLIER;

            // Taper the eval based on how much material the opponent has remaining
            return (int) (mopUpEval * (1 - GamePhase.fromMaterial(opponentMaterial)));
        }

        return 0;
    }

}
