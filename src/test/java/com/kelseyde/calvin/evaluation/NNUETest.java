package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.NNUE.Network;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class NNUETest {

    @Test
    public void testBenchmark() {

        String startpos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        benchmark("startpos", startpos);

        String lostPos = "rnbqkbnr/pppppppp/8/8/8/8/8/3QK3 w kq - 0 1";
        benchmark("lostpos", lostPos);

        String wonPos = "rn2k1nr/ppp2ppp/8/4P3/2P3b1/8/PP1B1KPP/RN1q1BR1 b kq - 1 10";
        benchmark("wonpos", wonPos);

    }

    private void benchmark(String name, String fen) {
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        System.out.printf("%s %s nnue %s%n", name, fen, nnue.evaluate(board));
    }

    @Test
    @Disabled // TODO fix this when I am not stupid
    public void testStartingPositionActivations() {

        Set<Integer> whitePerspectiveActivations = Set.of(
                192,65,130,259,324,133,70,199,8,9,10,11,12,13,14,15,432,433,434,435,436,437,438,439,632,505,570,699,764,573,510,639);

        Set<Integer> blackPerspectiveActivations = Set.of(
                632,505,570,699,764,573,510,639,432,433,434,435,436,437,438,439,8,9,10,11,12,13,14,15,192,65,130,259,324,133,70,199);

        Board board = new Board();

        Assertions.assertEquals(NNUE.getFeatureActivations(board, true), whitePerspectiveActivations);
        Assertions.assertEquals(NNUE.getFeatureActivations(board, false), blackPerspectiveActivations);

    }

}