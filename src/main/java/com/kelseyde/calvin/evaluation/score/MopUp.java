package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.Distance;

/**
 * A simple heuristic for positions when the side-to-move is up a decisive amount of material. Since simple mating sequences
 * (such as queen mate or rook mate) are often beyond the horizon of the search algorithm, this function gives the winning
 * side a small bonus for escorting the opponent king to the sides or corners of the board.
 * Eventually, the search algorithm will find the mating sequence and will take over.
 *
 * @see <a href="https://www.chessprogramming.org/Mop-up_Evaluation">Chess Programming Wiki</a>
 */
public class MopUp {

    public static int score(EngineConfig config, Board board, int whiteMaterialScore, int blackMaterialScore, float phase, boolean isWhite) {

        int friendlyMaterialScore = isWhite ? whiteMaterialScore : blackMaterialScore;
        int opponentMaterialScore = isWhite ? blackMaterialScore : whiteMaterialScore;

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
        return Phase.taperedEval(0, mopUpEval, phase);

    }

}
