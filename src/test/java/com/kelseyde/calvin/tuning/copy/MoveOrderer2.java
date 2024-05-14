package com.kelseyde.calvin.tuning.copy;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import com.kelseyde.calvin.utils.BoardUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of {@link MoveOrdering} using the following move-ordering strategy:
 *  1. Previous best move found at an earlier ply
 *  2. Queen promotions
 *  3. Winning captures (sub-ordered using MVV-LVA)
 *  4. Equal captures (sub-ordered using MVV-LVA)
 *  5. Killer moves
 *  6. Losing captures (sub-ordered using MVV-LVA)
 *  7. Under-promotions
 *  8. History moves
 *  9. Everything else.
 */
public class MoveOrderer2 implements MoveOrdering {

    private static final int MILLION = 1000000;
    private static final int PREVIOUS_BEST_MOVE_BIAS = 10 * MILLION;
    private static final int QUEEN_PROMOTION_BIAS = 9 * MILLION;
    private static final int WINNING_CAPTURE_BIAS = 8 * MILLION;
    private static final int EQUAL_CAPTURE_BIAS = 7 * MILLION;
    private static final int KILLER_MOVE_BIAS = 6 * MILLION;
    private static final int LOSING_CAPTURE_BIAS = 5 * MILLION;
    private static final int UNDER_PROMOTION_BIAS = 4 * MILLION;
    private static final int CASTLING_BIAS = 3 * MILLION;

    private static final int MAX_KILLER_MOVE_PLY_DEPTH = 32;
    private static final int MAX_KILLER_MOVES_PER_PLY = 3;

    public static final int[][] MVV_LVA_TABLE = new int[][] {
            new int[] {15, 14, 13, 12, 11, 10},  // victim P, attacker P, N, B, R, Q, K
            new int[] {25, 24, 23, 22, 21, 20},  // victim N, attacker P, N, B, R, Q, K
            new int[] {35, 34, 33, 32, 31, 30},  // victim B, attacker P, N, B, R, Q, K
            new int[] {45, 44, 43, 42, 41, 40},  // victim R, attacker P, N, B, R, Q, K
            new int[] {55, 54, 53, 52, 51, 50},  // victim Q, attacker P, N, B, R, Q, K
    };

    private Move[][] killerMoves = new Move[MAX_KILLER_MOVE_PLY_DEPTH][MAX_KILLER_MOVES_PER_PLY];
    private int[][][] historyMoves = new int[2][64][64];

    public List<Move> orderMoves(Board board, List<Move> moves, Move previousBestMove, boolean includeKillers, int depth) {
        List<Move> orderedMoves = new ArrayList<>(moves);
        orderedMoves.sort(Comparator.comparingInt(move -> -scoreMove(board, move, previousBestMove, includeKillers, depth)));
        return orderedMoves;
    }

    public int scoreMove(Board board, Move move, Move previousBestMove, boolean includeKillers, int depth) {

        int moveScore = 0;

        // Always search the best move from the previous iteration first.
        if (move.equals(previousBestMove)) {
            moveScore += PREVIOUS_BEST_MOVE_BIAS;
        }

        // Sort captures according to MVV-LVA (most valuable victim, least valuable attacker)
        Piece piece = board.pieceAt(move.getStartSquare());
        Piece capturedPiece = board.pieceAt(move.getEndSquare());
        boolean isCapture = capturedPiece != null;
        if (isCapture) {
            // Captures are sorted using MVV-LVA
            moveScore += MVV_LVA_TABLE[capturedPiece.getIndex()][piece.getIndex()];
            int materialDelta = capturedPiece.getValue() - piece.getValue();
            if (materialDelta > 0) {
                moveScore += WINNING_CAPTURE_BIAS;
            } else if (materialDelta == 0) {
                moveScore += EQUAL_CAPTURE_BIAS;
            } else {
                moveScore += LOSING_CAPTURE_BIAS;
            }
        }
        else {
            // Non-captures are sorted using history + killers
            if (includeKillers && isKillerMove(depth, move)) {
                moveScore += KILLER_MOVE_BIAS;
            }
            int colourIndex = BoardUtils.getColourIndex(board.isWhiteToMove());
            moveScore += historyMoves[colourIndex][move.getStartSquare()][move.getEndSquare()];
        }

        if (move.isCastling()) {
            moveScore += CASTLING_BIAS;
        }

        return moveScore;

    }

    public void addKillerMove(int ply, Move newKiller) {
        if (ply >= MAX_KILLER_MOVE_PLY_DEPTH) {
            return;
        }
        Move firstKiller = killerMoves[ply][0];
        Move secondKiller = killerMoves[ply][1];
        Move thirdKiller = killerMoves[ply][2];
        if (!newKiller.equals(firstKiller) && !newKiller.equals(secondKiller) && !newKiller.equals(thirdKiller)) {
            killerMoves[ply][2] = secondKiller;
            killerMoves[ply][1] = firstKiller;
            killerMoves[ply][0] = newKiller;
        }
    }

    private boolean isKillerMove(int ply, Move move) {
        return ply < MAX_KILLER_MOVE_PLY_DEPTH &&
                (move.equals(killerMoves[ply][0]) || move.equals(killerMoves[ply][1]) || move.equals(killerMoves[ply][2]));
    }

    public void addHistoryMove(int plyRemaining, Move historyMove, boolean isWhite) {
        int colourIndex = BoardUtils.getColourIndex(isWhite);
        int startSquare = historyMove.getStartSquare();
        int endSquare = historyMove.getEndSquare();
        int score = plyRemaining * plyRemaining;
        historyMoves[colourIndex][startSquare][endSquare] = score;
    }

    public void clear() {
        killerMoves = new Move[MAX_KILLER_MOVE_PLY_DEPTH][MAX_KILLER_MOVES_PER_PLY];
    }

}
