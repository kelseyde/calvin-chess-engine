package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.evaluation.material.PieceValues;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
public class MoveOrdering {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public Move[] orderMoves(Board board, Move[] moves) {
        Arrays.sort(moves, Comparator.comparing(move -> -calculateMoveScore(board, move)));
        return moves;
    }

    private int calculateMoveScore(Board board, Move move) {

        int moveScore = 0;

        // Prioritise evaluating checks
        if (moveGenerator.isCheck(board, move)) {
            moveScore += 1000;
        }

        PieceType pieceType = move.getPieceType();
        Optional<PieceType> capturedPieceType = board.pieceAt(move.getEndSquare());

        // Prioritising capturing most valuable opponent pieces with least valuable friendly pieces
        if (capturedPieceType.isPresent()) {
            moveScore += 10 * PieceValues.valueOf(capturedPieceType.get()) - PieceValues.valueOf(pieceType);
        }

        // Prioritising pawn promotion
        if (move.getMoveType().equals(MoveType.PROMOTION)) {
            moveScore += 10 + PieceValues.valueOf(move.getPromotionPieceType());
        }

//        log.info("move: {}, score: {}, history: {}", NotationUtils.toNotation(move), moveScore, board.getMoveHistory().stream().map(NotationUtils::toNotation).toList());
        return moveScore;

    }

}
