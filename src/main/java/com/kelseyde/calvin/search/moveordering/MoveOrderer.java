package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.evaluation.see.StaticExchangeEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;

@Slf4j
public class MoveOrderer {

    private static final int MILLION = 1000000;
    private static final int PREVIOUS_BEST_MOVE_BIAS = 100 * MILLION;
    private static final int PROMOTION_BIAS = 9 * MILLION;
    private static final int WINNING_CAPTURE_BIAS = 8 * MILLION;
    private static final int EQUAL_CAPTURE_BIAS = 7 * MILLION;
    private static final int KILLER_MOVE_BIAS = 6 * MILLION;
    private static final int CHECK_BIAS = 5 * MILLION;
    private static final int CASTLE_BIAS = 4 * MILLION;
    private static final int LOSING_CAPTURE_BIAS = 3 * MILLION;
    private static final int UNDER_PROMOTION_BIAS = 2 * MILLION;

    public static final int MAX_KILLER_MOVE_PLY_DEPTH = 32;
    private static final int MAX_KILLER_MOVES_PER_PLY = 2;

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final StaticExchangeEvaluator seeEvaluator = new StaticExchangeEvaluator();

    private Move[][] killerMoves = new Move[MAX_KILLER_MOVE_PLY_DEPTH][MAX_KILLER_MOVES_PER_PLY];

    public Move[] orderMoves(Board board, Move[] moves, Move previousBestMove, boolean includeKillers, int depth) {
        Arrays.sort(moves, Comparator.comparing(move -> -calculateMoveScore(board, move, previousBestMove, includeKillers, depth)));
        return moves;
    }

    private int calculateMoveScore(Board board, Move move, Move previousBestMove, boolean includeKillers, int depth) {

        int moveScore = 0;

        // Always search the best move from the previous iteration first.
        if (previousBestMove != null && move.matches(previousBestMove)) {
            moveScore += PREVIOUS_BEST_MOVE_BIAS;
        }

        // Give captures a high priority. 'Winning captures', where we capture a high-value piece with a low-value piece -
        // e.g. winning a queen with a pawn - should be given a high priority. 'Losing captures', where we capture a low
        // value piece with a high-value piece - e.g. going pawn-grabbing with the queen - are given a slightly lower priority.
        PieceType capturedPieceType = board.pieceAt(move.getEndSquare());
        boolean isCapture = capturedPieceType != null;
        if (isCapture) {
            int seeEval = seeEvaluator.evaluate(board, move);
            if (seeEval > 0) {
                moveScore += seeEval + WINNING_CAPTURE_BIAS;
            }
            else if (seeEval == 0) {
                moveScore += EQUAL_CAPTURE_BIAS;
            }
            else {
                moveScore += seeEval + LOSING_CAPTURE_BIAS;
            }
        }

        // Prioritising pawn promotion
        if (move.getMoveType().equals(MoveType.PROMOTION)) {
            int promotionBias = move.getPromotionPieceType().equals(PieceType.QUEEN) ? PROMOTION_BIAS : UNDER_PROMOTION_BIAS;
            moveScore += promotionBias;
        }

        // Prioritise killers
        if (!isCapture && includeKillers && isKillerMove(depth, move)) {
            moveScore += KILLER_MOVE_BIAS;
        }

        // Prioritise evaluating checks
        if (moveGenerator.isCheck(board, move)) {
            moveScore += CHECK_BIAS;
        }

        // Castling likely to be good (king safety)
        if (move.getMoveType().equals(MoveType.KINGSIDE_CASTLE) || move.getMoveType().equals(MoveType.QUEENSIDE_CASTLE)) {
            moveScore += CASTLE_BIAS;
        }

        return moveScore;

    }

    public void addKillerMove(int ply, Move newKiller) {
        Move firstKiller = killerMoves[ply][0];
        // By ensuring that the new killer is not the same as the first existing killer, we guarantee
        // that both killers at this ply are unique.
        if (firstKiller == null || !newKiller.matches(firstKiller)) {
            // Add the new killer at the start of the killer list for this ply.
            killerMoves[ply][1] = firstKiller;
            killerMoves[ply][0] = newKiller;
        }
    }

    public boolean isKillerMove(int ply, Move move) {
        for (Move killerMove : killerMoves[ply]) {
            if (killerMove != null && killerMove.matches(move)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        killerMoves = new Move[MAX_KILLER_MOVE_PLY_DEPTH][MAX_KILLER_MOVES_PER_PLY];
    }

}
