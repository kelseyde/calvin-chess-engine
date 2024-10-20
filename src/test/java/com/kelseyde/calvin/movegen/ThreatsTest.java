package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.Bench;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ThreatsTest {

    private static final MoveGenerator MOVEGEN = new MoveGenerator();

//    @Test
//    public void testThreats() {
//
//        for (String fen : Bench.FENS) {
//
//            System.out.println("fen: " + fen);
//            Board board = Board.from(fen);
//
//            long threats = MOVEGEN.calculateThreats(board, !board.isWhite());
//            board.setWhite(!board.isWhite());
//            List<Move> moves = MOVEGEN.generateMoves(board);
//            Bits.print(threats);
//            for (Move move : moves) {
//                System.out.println(Move.toUCI(move));
//                Assertions.assertTrue(Bits.contains(threats, move.to()));
//            }
////            Bits.print(expectedThreats);
////            Bits.print(threats);
////            Assertions.assertEquals(expectedThreats, threats);
//
//        }
//
//    }

}
