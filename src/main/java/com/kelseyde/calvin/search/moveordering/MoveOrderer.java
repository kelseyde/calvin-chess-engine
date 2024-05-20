package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
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
public class MoveOrderer implements MoveOrdering {

    private static final int MILLION = 1000000;
    private static final int PREVIOUS_BEST_MOVE_BIAS = 10 * MILLION;
    private static final int QUEEN_PROMOTION_BIAS = 9 * MILLION;
    private static final int WINNING_CAPTURE_BIAS = 8 * MILLION;
    private static final int EQUAL_CAPTURE_BIAS = 7 * MILLION;
    private static final int KILLER_MOVE_BIAS = 6 * MILLION;
    private static final int LOSING_CAPTURE_BIAS = 5 * MILLION;
    private static final int HISTORY_MOVE_BIAS = 4 * MILLION;
    private static final int UNDER_PROMOTION_BIAS = 3 * MILLION;
    private static final int CASTLING_BIAS = 2 * MILLION;

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

    /**
     * Orders the given list of moves based on the defined move-ordering strategy.
     *
     * @param board The current board state.
     * @param moves The list of moves to be ordered.
     * @param previousBestMove The best move found at an earlier ply.
     * @param includeKillers Whether to include killer moves in the ordering.
     * @param depth The current search depth.
     * @return The ordered list of moves.
     */
    public List<Move> orderMoves(Board board, List<Move> moves, Move previousBestMove, boolean includeKillers, int depth) {
        List<Move> orderedMoves = new ArrayList<>(moves);
        // Sort moves based on their scores in descending order
        orderedMoves.sort(Comparator.comparingInt(move -> -scoreMove(board, move, previousBestMove, includeKillers, depth)));
        return orderedMoves;
    }

    /**
     * Scores a move based on various heuristics such as previous best move, MVV-LVA, killer moves, and history moves.
     *
     * @param board The current board state.
     * @param move The move to be scored.
     * @param previousBestMove The best move found at an earlier ply.
     * @param includeKillers Whether to include killer moves in the scoring.
     * @param depth The current search depth.
     * @return The score of the move.
     */
    public int scoreMove(Board board, Move move, Move previousBestMove, boolean includeKillers, int depth) {

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();

        int moveScore = 0;

        // Always prioritize the best move from the previous iteration
        if (move.equals(previousBestMove)) {
            moveScore += PREVIOUS_BEST_MOVE_BIAS;
        }

        // Evaluate promotions
        Piece promotionPiece = move.getPromotionPieceType();
        if (promotionPiece != null) {
            moveScore += Piece.QUEEN == promotionPiece ? QUEEN_PROMOTION_BIAS : UNDER_PROMOTION_BIAS;
        }

        // Evaluate captures using MVV-LVA
        Piece capturedPiece = board.pieceAt(endSquare);
        boolean isCapture = capturedPiece != null;
        if (isCapture) {
            // Captures are sorted using MVV-LVA
            Piece piece = board.pieceAt(startSquare);
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
            boolean isKiller = includeKillers && isKillerMove(depth, move);
            if (isKiller) {
                // TODO give first, second, third killers different scores, see Blunder
                moveScore += KILLER_MOVE_BIAS;
            }
            int colourIndex = BoardUtils.getColourIndex(board.isWhiteToMove());
            int historyScore = historyMoves[colourIndex][startSquare][endSquare];
            moveScore += historyScore;
            if (!isKiller && historyScore > 0) {
                moveScore += HISTORY_MOVE_BIAS;
            }
        }

        if (move.isCastling()) {
            moveScore += CASTLING_BIAS;
        }

        return moveScore;

    }

    @Override
    public int mvvLva(Board board, Move move, Move previousBestMove) {
        if (move.matches(previousBestMove)) return PREVIOUS_BEST_MOVE_BIAS;
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece capturedPiece = board.pieceAt(endSquare);
        if (capturedPiece == null) return 0;
        Piece piece = board.pieceAt(startSquare);
        return MVV_LVA_TABLE[capturedPiece.getIndex()][piece.getIndex()];
    }

    /**
     * Adds a new killer move for a given ply.
     *
     * @param ply The current ply depth.
     * @param newKiller The new killer move to be added.
     */
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

    /**
     * Checks if a move is a killer move at a given ply.
     *
     * @param ply The current ply depth.
     * @param move The move to be checked.
     * @return True if the move is a killer move, otherwise false.
     */
    private boolean isKillerMove(int ply, Move move) {
        return ply < MAX_KILLER_MOVE_PLY_DEPTH &&
                (move.equals(killerMoves[ply][0]) || move.equals(killerMoves[ply][1]) || move.equals(killerMoves[ply][2]));
    }

    /**
     * Adds a history move for a given ply and color.
     *
     * @param depth The remaining ply depth.
     * @param historyMove The history move to be added.
     * @param white Whether the move is for white pieces.
     */
    public void incrementHistoryScore(int depth, Move historyMove, boolean white) {
        // TODO age history moves? see Blunder
        // TODO decrement history moves if they don't cause a cutoff, see Blunder
        int colourIndex = BoardUtils.getColourIndex(white);
        int startSquare = historyMove.getStartSquare();
        int endSquare = historyMove.getEndSquare();
        int score = depth * depth;
        historyMoves[colourIndex][startSquare][endSquare] += score;
    }

    public void decrementHistoryScore(int depth, Move historyMove, boolean white) {
        int colourIndex = BoardUtils.getColourIndex(white);
        int startSquare = historyMove.getStartSquare();
        int endSquare = historyMove.getEndSquare();
        int score = depth * depth;
        historyMoves[colourIndex][startSquare][endSquare] -= score;
    }

    public void ageHistoryTable() {

    }

    public void clear() {
        killerMoves = new Move[MAX_KILLER_MOVE_PLY_DEPTH][MAX_KILLER_MOVES_PER_PLY];
    }

}
