package com.kelseyde.calvin.evaluation.result;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.Arbiter;
import com.kelseyde.calvin.generation.MoveGenerator;

import java.util.List;

public class ResultCalculator {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public Result calculateResult(Board board) {
        List<Move> legalMoves = moveGenerator.generateMoves(board);
        if (legalMoves.isEmpty()) {
            if (moveGenerator.isCheck(board, board.isWhiteToMove())) {
                return board.isWhiteToMove() ? Result.BLACK_WINS_BY_CHECKMATE : Result.WHITE_WINS_BY_CHECKMATE;
            } else {
                return Result.DRAW_BY_STALEMATE;
            }
        }
        if (Arbiter.isThreefoldRepetition(board)) {
            return Result.DRAW_BY_REPETITION;
        }
        if (Arbiter.isInsufficientMaterial(board)) {
            return Result.DRAW_BY_INSUFFICIENT_MATERIAL;
        }
        if (Arbiter.isFiftyMoveRule(board)) {
            return Result.DRAW_BY_FIFTY_MOVE_RULE;
        }
        return Result.IN_PROGRESS;
    }

}
