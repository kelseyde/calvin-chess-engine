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
            new int[] {-16, -11, -7, -3, 0, 3, 7, 10, 13},
            new int[] {-15, -12, -9, -7, -4, -2, 0, 2, 4, 6, 8, 10, 12, 14},
            new int[] {-24, -20, -16, -13, -8, -6, -3, 0, 3, 6, 8, 11, 14, 16, 20},
            new int[] {-23, -20, -18, -16, -15, -13, -11, -9, -8, -6, -5, -3, -1, 0, 1, 3, 4, 5, 7, 8, 10, 11, 13, 14, 15, 17, 18, 20},
            new int[] {}
    };
    private static final int[][] EG_PIECE_MOBILITY_BONUS = new int[][] {
            new int[] {},
            new int[] {-10, -7, -5, -2, 0, 2, 4, 6, 9},
            new int[] {-41, -32, -25, -18, -12, -6, 0, 6, 11, 16, 22, 27, 33, 37},
            new int[] {-42, -34, -27, -21, -16, -10, -5, 0, 5, 10, 15, 20, 25, 29, 34},
            new int[] {-20, -18, -16, -14, -13, -11, -10, -8, -7, -5, -4, -3, -1, 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 13, 15, 16, 17},
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
