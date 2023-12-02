package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.Distance;

public class MopUp {

    public static int score(EngineConfig config, Board board, Material friendlyMaterial, Material opponentMaterial, boolean isWhite) {

        int friendlyMaterialScore = friendlyMaterial.sum(Score.SIMPLE_PIECE_VALUES, config.getBishopPairBonus());
        int opponentMaterialScore = opponentMaterial.sum(Score.SIMPLE_PIECE_VALUES, config.getBishopPairBonus());

        boolean twoPawnAdvantage = friendlyMaterialScore > (opponentMaterialScore + 2 * Piece.PAWN.getValue());
        if (!twoPawnAdvantage) {
            return 0;
        }

        int mopUpEval = 0;
        int friendlyKing = Bitwise.getNextBit(board.getKing(isWhite));
        int opponentKing = Bitwise.getNextBit(board.getKing(!isWhite));

        // Bonus for moving king closer to opponent king
        mopUpEval += (14 - Distance.manhattan(friendlyKing, opponentKing)) * config.getKingManhattanDistanceMultiplier();
        mopUpEval += (7 - Distance.chebyshev(friendlyKing, opponentKing)) * config.getKingChebyshevDistanceMultiplier();

        // Bonus for pushing opponent king to the edges of the board
        mopUpEval += Distance.centerManhattan(opponentKing) * config.getKingCenterManhattanDistanceMultiplier();

        // Taper the eval based on how much material the opponent has remaining
        return (int) (mopUpEval * (1 - Phase.fromMaterial(opponentMaterial)));

    }

}
