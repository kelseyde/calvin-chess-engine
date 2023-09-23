package com.kelseyde.calvin.search.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Service
public class RandomMoveEngine implements Engine {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Override
    public Move selectMove(Board board) {
        List<Move> legalMoves = new ArrayList<>(moveGenerator.generateLegalMoves(board));
        return legalMoves.get(new Random().nextInt(legalMoves.size()));
    }

}
