package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Evaluator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

@Disabled
public class TexelTunerTest {

    private final TexelTuner tuner = new TexelTuner("positions.txt");

    @Test
    public void tunePieceValues() throws IOException {

//        EngineConfiguration initialConfig = EngineConfiguration.builder().build();
//        int[] initialParams = new int[] {
//                100, 320, 330, 500, 900,
//                100, 320, 330, 500, 900,
//        };
//        Function<int[], Evaluator> createEvaluatorFunction = (params) ->
//                new Evaluator(new Board(), EngineConfiguration.builder()
//                        .pieceValuesMg(new int[] {params[0], params[1], params[2], params[3], params[4], 0})
//                        .pieceValuesEg(new int[] {params[5], params[6], params[7], params[8], params[9], 0})
//                        .build());
//
//        int[] bestParams = tuner.tune(initialParams, createEvaluatorFunction);
//        System.out.println(Arrays.toString(bestParams));

    }

}