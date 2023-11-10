package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.board.bitboard.Bitwise;
import com.kelseyde.calvin.movegeneration.attacks.Attacks;

/**
 * Mobility evaluation gives bonuses for the number of possible moves for each piece. Possible moves are defined as moves
 * to squares that are not occupied by a friendly pawn/king, or attacked by an enemy pawn.
 * <p>
 * The bonus is a non-linear value that is zero-centered, meaning a piece gets zero for having its 'average' number of possible moves,
 * a scaled penalty for fewer moves and a scaled bonus for more moves. The values are also weighted based on game phase.
 * <p>
 * Values for the mobility bonuses are based on Erik Madsen's MadChess engine:
 * @see <a href="https://github.com/ekmadsen/MadChess">MadChess</a>
 */
public class Mobility {

    private static final int[][] MG_PIECE_MOBILITY_BONUS = new int[][] {
            new int[] {},
            new int[] {-32, -22, -14, -7, 0, 7, 14, 20, 27},
            new int[] {-31, -25, -19, -14, -9, -4, 0, 5, 9, 13, 17, 21, 25, 30},
            new int[] {-49, -40, -33, -26, -19, -12, -6, 0, 6, 12, 17, 23, 29, 34, 40},
            new int[] {-46, -41, -37, -33, -30, -26, -23, -19, -16, -13, -10, -6, -3, 0, 3, 6, 9, 11, 14, 17, 20, 23, 26, 29, 31, 34, 37, 40},
            new int[] {}
    };
    private static final int[][] EG_PIECE_MOBILITY_BONUS = new int[][] {
            new int[] {},
            new int[] {-21, -15, -10, -5, 0, 5, 9, 13, 18},
            new int[] {-83, -65, -51, -37, -24, -12, 0, 12, 23, 34, 45, 56, 67, 78},
            new int[] {-84, -68, -55, -43, -32, -21, -10, 0, 11, 21, 30, 40, 50, 59, 69},
            new int[] {-40, -36, -32, -29, -26, -23, -20, -17, 14, -11, -8, -6, -3, 0, 2, 5, 8, 10, 13, 15, 18, 20, 22, 25, 27, 30, 32, 35},
            new int[] {}
    };

    public static int score(Board board, boolean isWhite, float phase) {

        long friendlyBlockers = board.getKing(isWhite) | board.getPawns(isWhite);
        long opponentBlockers = board.getPieces(!isWhite);
        long blockers = friendlyBlockers | opponentBlockers;
        long opponentPawnAttacks = Attacks.pawnAttacks(board.getPawns(!isWhite), !isWhite);

        int middlegameScore = 0;
        int endgameScore = 0;

        long knights = board.getKnights(isWhite);
        while (knights != 0) {
            int square = Bitwise.getNextBit(knights);
            long moves = Attacks.knightAttacks(square) &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            middlegameScore += MG_PIECE_MOBILITY_BONUS[Piece.KNIGHT.getIndex()][moveCount];
            endgameScore += EG_PIECE_MOBILITY_BONUS[Piece.KNIGHT.getIndex()][moveCount];
            knights = Bitwise.popBit(knights);
        }

        long bishops = board.getBishops(isWhite);
        while (bishops != 0) {
            int square = Bitwise.getNextBit(bishops);
            long moves = Attacks.bishopAttacks(square, blockers) &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            middlegameScore += MG_PIECE_MOBILITY_BONUS[Piece.BISHOP.getIndex()][moveCount];
            endgameScore += EG_PIECE_MOBILITY_BONUS[Piece.BISHOP.getIndex()][moveCount];
            bishops = Bitwise.popBit(bishops);
        }

        long rooks = board.getRooks(isWhite);
        while (rooks != 0) {
            int square = Bitwise.getNextBit(rooks);
            long moves = Attacks.rookAttacks(square, blockers) &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            middlegameScore += MG_PIECE_MOBILITY_BONUS[Piece.ROOK.getIndex()][moveCount];
            endgameScore += EG_PIECE_MOBILITY_BONUS[Piece.ROOK.getIndex()][moveCount];
            rooks = Bitwise.popBit(rooks);
        }

        long queens = board.getQueens(isWhite);
        while (queens != 0) {
            int square = Bitwise.getNextBit(queens);
            long moves = (Attacks.rookAttacks(square, blockers) | Attacks.bishopAttacks(square, blockers))
                    &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            middlegameScore += MG_PIECE_MOBILITY_BONUS[Piece.QUEEN.getIndex()][moveCount];
            endgameScore += EG_PIECE_MOBILITY_BONUS[Piece.QUEEN.getIndex()][moveCount];
            queens = Bitwise.popBit(queens);
        }

        return GamePhase.taperedEval(middlegameScore, endgameScore, phase);

    }

    public static int bishopScore(Board board, boolean isWhite, float phase) {

        long bishops = board.getBishops(isWhite);
        if (bishops == 0) return 0;
        long friendlies = board.getPieces(isWhite);
        long blockers = board.getOccupied();
        int middlegameScore = 0;
        int endgameScore = 0;

        while (bishops != 0) {
            int square = Bitwise.getNextBit(bishops);
            long moves = Attacks.bishopAttacks(square, blockers) &~ friendlies;
            int moveCount = Bitwise.countBits(moves);
            middlegameScore += MG_PIECE_MOBILITY_BONUS[Piece.BISHOP.getIndex()][moveCount];
            endgameScore += EG_PIECE_MOBILITY_BONUS[Piece.BISHOP.getIndex()][moveCount];
            bishops = Bitwise.popBit(bishops);
        }

        return GamePhase.taperedEval(middlegameScore, endgameScore, phase);

    }

}
