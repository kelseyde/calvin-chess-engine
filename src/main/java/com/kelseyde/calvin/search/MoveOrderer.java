package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.evaluation.material.PieceValues;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.generator.PawnMoveGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;

@Slf4j
public class MoveOrderer {

    private static final int MILLION = 1000000;
    private static final int PREVIOUS_BEST_MOVE_BIAS = 100 * MILLION;
    private static final int CAPTURE_BIAS = 8 * MILLION;
    private static final int PROMOTION_BIAS = 7 * MILLION;
    private static final int CHECK_BIAS = 6 * MILLION;
    private static final int CASTLE_BIAS = 4 * MILLION;
    private static final int UNDER_PROMOTION_BIAS = 2 * MILLION;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    private final PawnMoveGenerator pawnMoveGenerator = new PawnMoveGenerator();

    public Move[] orderMoves(Board board, Move[] moves, Move previousBestMove) {
        Arrays.sort(moves, Comparator.comparing(move -> -calculateMoveScore(board, move, previousBestMove)));
        return moves;
    }

    private int calculateMoveScore(Board board, Move move, Move previousBestMove) {

        int moveScore = 0;

        if (previousBestMove != null && move.matches(previousBestMove)) {
            moveScore += PREVIOUS_BEST_MOVE_BIAS;
        }

        // Prioritise evaluating checks
        if (moveGenerator.isCheck(board, move)) {
            moveScore += CHECK_BIAS;
        }

        // Castling likely to be good (king safety)
        if (move.getMoveType().equals(MoveType.QUEENSIDE_CASTLE) || move.getMoveType().equals(MoveType.KINGSIDE_CASTLE)) {
            moveScore += CASTLE_BIAS;
        }

        PieceType capturedPieceType = board.pieceAt(move.getEndSquare());

        // Prioritising capturing most valuable opponent pieces with least valuable friendly pieces
        if (capturedPieceType != null) {
            int materialDelta = PieceValues.valueOf(capturedPieceType) - PieceValues.valueOf(move.getPieceType());
            moveScore += CAPTURE_BIAS + materialDelta;
        }

        // Prioritising pawn promotion
        if (move.getMoveType().equals(MoveType.PROMOTION)) {
            int promotionBias = move.getPromotionPieceType().equals(PieceType.QUEEN) ? PROMOTION_BIAS : UNDER_PROMOTION_BIAS;
            moveScore += promotionBias;
        }

        return moveScore;

    }



}
