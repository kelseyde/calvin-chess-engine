package com.kelseyde.calvin.search.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;

import java.util.Random;

//@Service
public class RandomMoveEngine implements Engine {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Override
    public Move selectMove(Board board) {
        Move[] legalMoves = moveGenerator.generateLegalMoves(board);
        return legalMoves[new Random().nextInt(legalMoves.length)];
    }

}
