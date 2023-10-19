package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.evaluation.material.PieceValues;
import com.kelseyde.calvin.evaluation.see.StaticExchangeEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class MoveOrderer {

    private static final int MILLION = 1000000;
    private static final int PREVIOUS_BEST_MOVE_BIAS = 100 * MILLION;
    private static final int PROMOTION_BIAS = 9 * MILLION;
    private static final int WINNING_CAPTURE_BIAS = 8 * MILLION;
    private static final int EQUAL_CAPTURE_BIAS = 7 * MILLION;
    private static final int KILLER_MOVE_BIAS = 6 * MILLION;
    private static final int CASTLE_BIAS = 4 * MILLION;
    private static final int LOSING_CAPTURE_BIAS = 3 * MILLION;
    private static final int UNDER_PROMOTION_BIAS = 2 * MILLION;

    public static final int MAX_KILLER_MOVE_PLY_DEPTH = 32;
    private static final int MAX_KILLER_MOVES_PER_PLY = 2;

    private Move[][] killerMoves = new Move[MAX_KILLER_MOVE_PLY_DEPTH][MAX_KILLER_MOVES_PER_PLY];
    private int[][][] historyMoves = new int[2][64][64];

    public List<Move> orderMoves(Board board, List<Move> moves, Move previousBestMove, boolean includeKillers, int depth) {
        List<Move> orderedMoves = new ArrayList<>(moves);
        orderedMoves.sort(Comparator.comparing(move -> -calculateMoveScore(board, move, previousBestMove, includeKillers, depth)));
        return orderedMoves;
    }

    private int calculateMoveScore(Board board, Move move, Move previousBestMove, boolean includeKillers, int depth) {

        int moveScore = 0;

        // Always search the best move from the previous iteration first.
        if (move.equals(previousBestMove)) {
            moveScore += PREVIOUS_BEST_MOVE_BIAS;
        }

        // Give captures a high priority. 'Winning captures', where we capture a high-value piece with a low-value piece -
        // e.g. winning a queen with a pawn - should be given a high priority. 'Losing captures', where we capture a low
        // value piece with a high-value piece - e.g. going pawn-grabbing with the queen - are given a slightly lower priority.
        PieceType capturedPieceType = board.pieceAt(move.getEndSquare());
        boolean isCapture = capturedPieceType != null;
        if (isCapture) {
            int materialDelta = PieceValues.valueOf(capturedPieceType) - PieceValues.valueOf(board.pieceAt(move.getStartSquare()));
            if (materialDelta > 0) {
                moveScore += WINNING_CAPTURE_BIAS;
            } else if (materialDelta == 0) {
                moveScore += EQUAL_CAPTURE_BIAS;
            } else {
                moveScore += LOSING_CAPTURE_BIAS;
            }
            // TODO test SEE in move ordering
//            int seeEval = seeEvaluator.evaluate(board, move);
//            if (seeEval > 0) {
//                moveScore += seeEval + WINNING_CAPTURE_BIAS;
//            }
//            else if (seeEval == 0) {
//                moveScore += EQUAL_CAPTURE_BIAS;
//            }
//            else {
//                moveScore += seeEval + LOSING_CAPTURE_BIAS;
//            }
        }

        // Prioritising pawn promotion
        if (move.isPromotion()) {
            int promotionBias = move.getPromotionPieceType().equals(PieceType.QUEEN) ? PROMOTION_BIAS : UNDER_PROMOTION_BIAS;
            moveScore += promotionBias;
        }

        // Prioritise killers + history moves
        if (!isCapture) {
            if (includeKillers && isKillerMove(depth, move)) {
                moveScore += KILLER_MOVE_BIAS;
            }
            moveScore += historyMoves[colourIndex(board.isWhiteToMove())][move.getStartSquare()][move.getEndSquare()];
        }

        // Castling likely to be good (king safety)
        if (move.isCastling()) {
            moveScore += CASTLE_BIAS;
        }

        return moveScore;

    }

    public void addKillerMove(int ply, Move newKiller) {
        if (ply >= MAX_KILLER_MOVE_PLY_DEPTH) {
            return;
        }
        Move firstKiller = killerMoves[ply][0];
        // By ensuring that the new killer is not the same as the first existing killer, we guarantee
        // that both killers at this ply are unique.
        if (!newKiller.equals(firstKiller)) {
            // Add the new killer at the start of the killer list for this ply.
            killerMoves[ply][1] = firstKiller;
            killerMoves[ply][0] = newKiller;
        }
    }

    private boolean isKillerMove(int ply, Move move) {
        return ply < MAX_KILLER_MOVE_PLY_DEPTH && Arrays.asList(killerMoves[ply]).contains(move);
    }

    public void addHistoryMove(int plyRemaining, Move historyMove, boolean isWhite) {
        int colourIndex = colourIndex(isWhite);
        int startSquare = historyMove.getStartSquare();
        int endSquare = historyMove.getEndSquare();
        int score = plyRemaining * plyRemaining;
        historyMoves[colourIndex][startSquare][endSquare] = score;
    }

    private int colourIndex(boolean isWhite) {
        return isWhite ? 1 : 0;
    }

    public void clear() {
        killerMoves = new Move[MAX_KILLER_MOVE_PLY_DEPTH][MAX_KILLER_MOVES_PER_PLY];
        historyMoves = new int[2][64][64];
    }

}
