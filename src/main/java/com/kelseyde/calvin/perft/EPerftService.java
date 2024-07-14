package com.kelseyde.calvin.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.NNUE;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;

import java.util.List;

public class EPerftService {

    private final MoveGenerator moveGenerator;
    private final Evaluation evaluator;

    public EPerftService(Board board) {
        moveGenerator = new MoveGenerator();
        evaluator = new NNUE(board);
        evaluator.evaluate(board);
    }

    public void ePerft(Board board, int depth) {
        if (depth == 0) {
            return;
        }
        List<Move> moves = moveGenerator.generateMoves(board);
        for (Move move : moves) {
            evaluator.makeMove(board, move);
            board.makeMove(move);
            if (evaluator.evaluate(board) != new NNUE(board).evaluate(board)) {
                System.out.println(board.getMoveHistory().stream().map(Notation::toNotation).toList());
                System.out.println(FEN.toFEN(board));
                throw new IllegalArgumentException();
            }
            ePerft(board, depth - 1);
            evaluator.unmakeMove();
            board.unmakeMove();
        }
    }

}
