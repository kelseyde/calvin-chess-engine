package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.generation.attacks.Attacks;

/**
 * Mobility evaluation gives bonuses for the number of possible moves for each piece. Possible moves are defined as moves
 * to squares that are not occupied by a friendly piece, or attacked by an enemy pawn.
 * <p>
 * The bonus is a non-linear value that is zero-centered, meaning a piece gets zero for having its 'average' number of possible moves,
 * a scaled penalty for fewer moves and a scaled bonus for more moves. The values are also weighted based on game phase.
 */
public class Mobility {

    private static final int[][] MG_PIECE_MOBILITY_BONUS = new int[][] {
            new int[] {},
            new int[] {-18, -14, -8, -4, 0, 4, 8, 12, 16},
            new int[] {-26, -21, -16, -12, -8, -4, 0, 4, 8, 12, 16, 16, 16, 16},
            new int[] {-14, -12, -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10, 12, 12},
            new int[] {-13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12, 12},
            new int[] {}
    };
    private static final int[][] EG_PIECE_MOBILITY_BONUS = new int[][] {
            new int[] {},
            new int[] {-18, -14, -8, -4, 0, 4, 8, 12, 16},
            new int[] {-26, -21, -16, -12, -8, -4, 0, 4, 8, 12, 16, 16, 16, 16},
            new int[] {-14, -12, -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10, 12, 12},
            new int[] {-13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12, 12},
            new int[] {}
    };

    public static int score(EngineConfig config, Board board, boolean isWhite, float phase) {

        long friendlyBlockers = board.getKing(isWhite) | board.getPawns(isWhite);
        long opponentBlockers = board.getPieces(!isWhite);
        long blockers = friendlyBlockers | opponentBlockers;
        long opponentPawnAttacks = Attacks.pawnAttacks(board.getPawns(!isWhite), !isWhite);

        int middlegameScore = 0;
        int endgameScore = 0;

        long knights = board.getKnights(isWhite);
        while (knights != 0) {
            int square = Bitwise.getNextBit(knights);
            long attacks = Attacks.knightAttacks(square);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            int index = Piece.KNIGHT.getIndex();
            middlegameScore += config.getMiddlegameMobilityBonus()[index][moveCount];
            endgameScore += config.getEndgameMobilityBonus()[index][moveCount];
            knights = Bitwise.popBit(knights);
        }

        long bishops = board.getBishops(isWhite);
        while (bishops != 0) {
            int square = Bitwise.getNextBit(bishops);
            long attacks = Attacks.bishopAttacks(square, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            int index = Piece.BISHOP.getIndex();
            middlegameScore += config.getMiddlegameMobilityBonus()[index][moveCount];
            endgameScore += config.getEndgameMobilityBonus()[index][moveCount];
            bishops = Bitwise.popBit(bishops);
        }

        long rooks = board.getRooks(isWhite);
        while (rooks != 0) {
            int square = Bitwise.getNextBit(rooks);
            long attacks = Attacks.rookAttacks(square, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            int index = Piece.ROOK.getIndex();
            middlegameScore += config.getMiddlegameMobilityBonus()[index][moveCount];
            endgameScore += config.getEndgameMobilityBonus()[index][moveCount];
            rooks = Bitwise.popBit(rooks);
        }

        long queens = board.getQueens(isWhite);
        while (queens != 0) {
            int square = Bitwise.getNextBit(queens);
            long attacks = Attacks.rookAttacks(square, blockers) | Attacks.bishopAttacks(square, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            int index = Piece.QUEEN.getIndex();
            middlegameScore += config.getMiddlegameMobilityBonus()[index][moveCount];
            endgameScore += config.getEndgameMobilityBonus()[index][moveCount];
            queens = Bitwise.popBit(queens);
        }

        return Phase.taperedEval(middlegameScore, endgameScore, phase);

    }

}
