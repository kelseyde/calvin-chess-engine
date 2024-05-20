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

    private static final int KILLER_MOVE_ORDER_BONUS = 10000;
    private static final int MAX_KILLER_MOVE_PLY = 32;
    private static final int MAX_KILLER_MOVES_PER_PLY = 3;

    public static final int[][] MVV_LVA_TABLE = new int[][] {
            new int[] {15, 14, 13, 12, 11, 10},  // victim P, attacker P, N, B, R, Q, K
            new int[] {25, 24, 23, 22, 21, 20},  // victim N, attacker P, N, B, R, Q, K
            new int[] {35, 34, 33, 32, 31, 30},  // victim B, attacker P, N, B, R, Q, K
            new int[] {45, 44, 43, 42, 41, 40},  // victim R, attacker P, N, B, R, Q, K
            new int[] {55, 54, 53, 52, 51, 50},  // victim Q, attacker P, N, B, R, Q, K
    };

    private Move[][] killerMoves = new Move[MAX_KILLER_MOVE_PLY][MAX_KILLER_MOVES_PER_PLY];
    private final int[][][] historyMoves = new int[2][64][64];

    /**
     * Orders the given list of moves based on the defined move-ordering strategy.
     *
     * @param board             The current board state.
     * @param moves             The list of moves to be ordered.
     * @param previousBestMove  The best move found at an earlier ply.
     * @param ply               The number of ply from the root node.
     * @return                  The ordered list of moves.
     */
    public List<Move> orderMoves(Board board, List<Move> moves, Move previousBestMove, int ply) {
        List<Move> orderedMoves = new ArrayList<>(moves);
        // Sort moves based on their scores in descending order
        orderedMoves.sort(Comparator.comparingInt(move -> -scoreMove(board, move, previousBestMove, ply)));
        return orderedMoves;
    }

    /**
     * Scores a move based on various heuristics such as previous best move, MVV-LVA, killer moves, and history moves.
     *
     * @param board            The current board state.
     * @param move             The move to be scored.
     * @param previousBestMove The best move found at an earlier ply.
     * @param ply              The number of ply from the root node.
     * @return                 The score of the move.
     */
    public int scoreMove(Board board, Move move, Move previousBestMove, int ply) {

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        int moveScore = 0;

        // The previous best move from the transposition table is searched first.
        if (move.equals(previousBestMove)) {
            moveScore += PREVIOUS_BEST_MOVE_BIAS;
        }

        // Then any pawn promotions
        Piece promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            moveScore += Piece.QUEEN == promotionPiece ? QUEEN_PROMOTION_BIAS : UNDER_PROMOTION_BIAS;
        }

        // Then captures, sorted by MVV-LVA
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
        // Non-captures are sorted using killer score + history score
        else {
//            int killerScore = scoreKillerMove(move, ply);
//            int historyScore = scoreHistoryMove(board, startSquare, endSquare, killerScore);
//            moveScore += killerScore + historyScore;
            // Non-captures are sorted using history + killers
            boolean isKiller = isKillerMove(ply, move);
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

    private boolean isKillerMove(int ply, Move move) {
        return ply < MAX_KILLER_MOVE_PLY
                && (move.equals(killerMoves[ply][0])
                || move.equals(killerMoves[ply][1])
                || move.equals(killerMoves[ply][2]));
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

    private int scoreCapture(Board board, int startSquare, Piece capturedPiece) {
        Piece piece = board.pieceAt(startSquare);
        int captureScore = 0;
        captureScore += MVV_LVA_TABLE[capturedPiece.getIndex()][piece.getIndex()];
        int materialDelta = capturedPiece.getValue() - piece.getValue();
        if (materialDelta > 0) {
            captureScore += WINNING_CAPTURE_BIAS;
        } else if (materialDelta == 0) {
            captureScore += EQUAL_CAPTURE_BIAS;
        } else {
            captureScore += LOSING_CAPTURE_BIAS;
        }
        return captureScore;
    }

    private int scorePromotion(Piece promotionPiece) {
        return Piece.QUEEN == promotionPiece ? QUEEN_PROMOTION_BIAS : UNDER_PROMOTION_BIAS;
    }

    private int scoreKillerMove(Move move, int ply) {

        if (ply < MAX_KILLER_MOVE_PLY
            && (move.equals(killerMoves[ply][0])
                || move.equals(killerMoves[ply][1])
                || move.equals(killerMoves[ply][2]))) {
            return KILLER_MOVE_BIAS;
        }
        return 0;
//        if (ply >= MAX_KILLER_MOVE_PLY) {
//            return 0;
//        }
//        else if (move.matches(killerMoves[ply][0])) {
//            return KILLER_MOVE_BIAS + (KILLER_MOVE_ORDER_BONUS * 3);
//        }
//        else if (move.matches(killerMoves[ply][1])) {
//            return KILLER_MOVE_BIAS + (KILLER_MOVE_ORDER_BONUS * 2);
//        }
//        else if (move.matches(killerMoves[ply][2])) {
//            return KILLER_MOVE_BIAS + (KILLER_MOVE_ORDER_BONUS);
//        }
//        else {
//            return 0;
//        }
    }

    private int scoreHistoryMove(Board board, int startSquare, int endSquare, int killerScore) {
        int colourIndex = BoardUtils.getColourIndex(board.isWhiteToMove());
        int historyScore = historyMoves[colourIndex][startSquare][endSquare];
        if (killerScore == 0 && historyScore > 0) {
            historyScore += HISTORY_MOVE_BIAS;
        }
        return historyScore;
    }

    /**
     * Adds a new killer move for a given ply.
     *
     * @param ply The current ply from root.
     * @param newKiller The new killer move to be added.
     */
    public void addKillerMove(int ply, Move newKiller) {
        if (ply >= MAX_KILLER_MOVE_PLY) {
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
     * Adds a history move for a given ply and color.
     *
     * @param depth The current search depth.
     * @param historyMove The history move to be added.
     * @param white Whether the move is for white pieces.
     */
    public void incrementHistoryScore(int depth, Move historyMove, boolean white) {
        int colourIndex = BoardUtils.getColourIndex(white);
        int startSquare = historyMove.getStartSquare();
        int endSquare = historyMove.getEndSquare();
        int score = depth * depth;
        historyMoves[colourIndex][startSquare][endSquare] += score;
    }

    public void ageHistoryTable(boolean white) {
        int colourIndex = BoardUtils.getColourIndex(white);
        for (int startSquare = 0; startSquare < 64; startSquare++) {
            for (int endSquare = 0; endSquare < 64; endSquare++) {
                historyMoves[colourIndex][startSquare][endSquare] /= 2;
            }
        }
    }

    public void clear() {
        killerMoves = new Move[MAX_KILLER_MOVE_PLY][MAX_KILLER_MOVES_PER_PLY];
    }

}
