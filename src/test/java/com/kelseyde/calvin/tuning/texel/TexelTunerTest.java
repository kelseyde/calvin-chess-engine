package com.kelseyde.calvin.tuning.texel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.engine.EngineInitializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Disabled
public class TexelTunerTest {

    private final TexelTuner tuner = new TexelTuner("quiet_positions.epd");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void tuneEverything() throws IOException, ExecutionException, InterruptedException {
        List<Integer> weights = new ArrayList<>();
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        initialConfig.setPawnHashEnabled(false);
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameTables()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameTables()[1]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameTables()[2]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameTables()[3]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameTables()[4]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameTables()[5]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameTables()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameTables()[1]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameTables()[2]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameTables()[3]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameTables()[4]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameTables()[5]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getPieceValues()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getPieceValues()[1]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[1]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[2]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[3]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[4]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameMobilityBonus()[1]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameMobilityBonus()[2]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameMobilityBonus()[3]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getEndgameMobilityBonus()[4]).boxed().toList());
        weights.add(initialConfig.getBishopPairBonus());
        weights.add(initialConfig.getRookOpenFileBonus()[0]);
        weights.add(initialConfig.getRookOpenFileBonus()[1]);
        weights.add(initialConfig.getRookSemiOpenFileBonus()[0]);
        weights.add(initialConfig.getRookSemiOpenFileBonus()[1]);
        weights.addAll(Arrays.stream(initialConfig.getPassedPawnBonus()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getPassedPawnBonus()[1]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getDoubledPawnPenalty()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getDoubledPawnPenalty()[1]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getIsolatedPawnPenalty()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getIsolatedPawnPenalty()[1]).boxed().toList());
        weights.add(initialConfig.getProtectedPassedPawnBonus());
        weights.addAll(Arrays.stream(initialConfig.getKingPawnShieldPenalty()).boxed().toList());
        weights.add(initialConfig.getKingOpenFilePenalty());
        weights.add(initialConfig.getKingSemiOpenFilePenalty());
        weights.add(initialConfig.getKingOpenAdjacentFilePenalty());
        weights.add(initialConfig.getKingSemiOpenAdjacentFilePenalty());
        weights.addAll(Arrays.stream(initialConfig.getVirtualKingMobilityPenalty()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getVirtualKingMobilityPenalty()[1]).boxed().toList());
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.setPawnHashEnabled(false);
                    config.getMiddlegameTables()[0] = Arrays.stream(params, 0, 64).toArray();
                    config.getMiddlegameTables()[1] = Arrays.stream(params, 64, 128).toArray();
                    config.getMiddlegameTables()[2] = Arrays.stream(params, 128, 192).toArray();
                    config.getMiddlegameTables()[3] = Arrays.stream(params, 192, 256).toArray();
                    config.getMiddlegameTables()[4] = Arrays.stream(params, 256, 320).toArray();
                    config.getMiddlegameTables()[5] = Arrays.stream(params, 320, 384).toArray();
                    config.getEndgameTables()[0] = Arrays.stream(params, 384, 448).toArray();
                    config.getEndgameTables()[1] = Arrays.stream(params, 448, 512).toArray();
                    config.getEndgameTables()[2] = Arrays.stream(params, 512, 576).toArray();
                    config.getEndgameTables()[3] = Arrays.stream(params, 576, 640).toArray();
                    config.getEndgameTables()[4] = Arrays.stream(params, 640, 704).toArray();
                    config.getEndgameTables()[5] = Arrays.stream(params, 704, 768).toArray();
                    config.getPieceValues()[0] = Arrays.stream(params, 768, 774).toArray();
                    config.getPieceValues()[1] = Arrays.stream(params, 774, 780).toArray();
                    config.getMiddlegameMobilityBonus()[1] = Arrays.stream(params, 780, 789).toArray();
                    config.getMiddlegameMobilityBonus()[2] = Arrays.stream(params, 789, 803).toArray();
                    config.getMiddlegameMobilityBonus()[3] = Arrays.stream(params, 803, 818).toArray();
                    config.getMiddlegameMobilityBonus()[4] = Arrays.stream(params, 818, 846).toArray();
                    config.getEndgameMobilityBonus()[1] = Arrays.stream(params, 846, 855).toArray();
                    config.getEndgameMobilityBonus()[2] = Arrays.stream(params, 855, 869).toArray();
                    config.getEndgameMobilityBonus()[3] = Arrays.stream(params, 869, 884).toArray();
                    config.getEndgameMobilityBonus()[4] = Arrays.stream(params, 884, 912).toArray();
                    config.setBishopPairBonus(params[912]);
                    config.getRookOpenFileBonus()[0] = params[913];
                    config.getRookOpenFileBonus()[1] = params[914];
                    config.getRookSemiOpenFileBonus()[0] = params[915];
                    config.getRookSemiOpenFileBonus()[1] = params[916];
                    config.getPassedPawnBonus()[0] = Arrays.stream(params, 917, 924).toArray();
                    config.getPassedPawnBonus()[1] = Arrays.stream(params, 924, 931).toArray();
                    config.getDoubledPawnPenalty()[0] = Arrays.stream(params, 931, 940).toArray();
                    config.getDoubledPawnPenalty()[1] = Arrays.stream(params, 940, 949).toArray();
                    config.getIsolatedPawnPenalty()[0] = Arrays.stream(params, 949, 958).toArray();
                    config.getIsolatedPawnPenalty()[1] = Arrays.stream(params, 958, 967).toArray();
                    config.setProtectedPassedPawnBonus(params[967]);
                    config.setKingPawnShieldPenalty(Arrays.stream(params, 968, 975).toArray());
                    config.setKingOpenFilePenalty(params[975]);
                    config.setKingSemiOpenFilePenalty(params[976]);
                    config.setKingOpenAdjacentFilePenalty(params[977]);
                    config.setKingSemiOpenAdjacentFilePenalty(params[978]);
                    config.getVirtualKingMobilityPenalty()[0] = Arrays.stream(params, 978, 1006).toArray();
                    config.getVirtualKingMobilityPenalty()[1] = Arrays.stream(params, 1006, 1034).toArray();
                    return config;
                }
        );
    }

    @Test
    public void convertParamsToJson() throws JsonProcessingException {
        int[] params = new int[] {95, 0, 0, 0, 0, 0, 0, 0, -67, -85, -100, -68, -34, -61, -77, -190, -8, 4, 46, 23, 62, 116, 30, 5, -4, 3, 12, 30, 46, 44, 29, 3, -26, -12, 6, 34, 30, 45, 18, -26, -17, -15, 12, 17, 32, 29, 42, 13, -22, 6, -6, 8, 16, 53, 64, 7, 0, 0, 0, 0, 0, 0, 0, 0, -264, -58, -34, -30, 43, -107, -140, -141, -67, -30, 93, 36, 99, 81, 1, 7, 3, 35, 37, 80, 116, 149, 76, -1, 42, 39, 31, 74, 36, 55, 16, 61, 20, 13, 37, 28, 44, 45, 25, 3, 0, 16, 40, 38, 51, 34, 47, 11, 9, -5, 18, 22, 30, 42, 31, 32, -20, 11, -25, 5, 4, 19, 9, -45, -14, -53, -16, -70, -117, -14, -5, -81, -20, 8, -13, -4, 9, 65, -17, 10, -10, 18, 60, 9, 70, 64, 66, 41, -17, 11, 19, 26, 29, -18, 20, -3, 11, 13, 20, 38, 39, 18, 17, -6, 44, 43, 37, 19, 37, 40, 35, 42, 1, 51, 31, 19, 24, 56, 73, 31, 32, 7, 25, 1, -1, 13, 25, 32, 34, 44, 41, 55, 96, 140, 121, 20, -15, -41, 6, 46, 2, 70, 89, 77, -7, -19, -26, 1, 13, 41, 89, 37, -39, -15, -6, 5, -21, 1, 30, 33, -29, -38, -46, -32, -28, -14, 14, -4, -31, -29, -18, -8, -7, 0, 24, 14, -18, -20, -28, -14, -7, 12, 34, -27, -3, -6, -2, 3, 5, 12, -11, 20, -34, -3, 31, 60, 96, 24, 4, -39, -42, -58, -62, -96, -119, 9, -61, 98, -39, -31, -21, -30, -16, -17, 37, -12, -32, -17, -23, -45, -47, -61, -66, -29, -15, -15, -15, -28, -5, -19, -6, -35, -18, 8, -7, 0, -2, -2, 6, -17, -2, 6, 9, 10, 20, 34, 44, 28, -1, -13, -2, 18, -6, -16, -25, 34, 159, 108, 153, 147, 153, 138, 60, 5, 13, 166, 141, 105, 111, 155, 28, -64, 66, 216, 121, 134, 74, 151, 132, -50, 13, 68, 96, 71, 33, 23, 31, -83, 46, 84, 59, -15, -20, -69, -32, -139, 17, 45, -50, -41, -59, -48, -11, -80, 15, -8, -24, -48, -37, -31, 20, -15, -85, 35, 21, -32, 4, -17, 43, 7, 0, 0, 0, 0, 0, 0, 0, 0, 30, 28, 8, -33, -48, -30, 19, 25, 72, 55, 29, -6, -19, -8, 29, 37, 45, 36, 19, -8, -5, 2, 21, 18, 32, 27, 7, -2, 1, -1, 9, 9, 22, 19, 9, 6, 11, 10, 3, 1, 31, 17, 27, 16, 17, 12, -3, 2, 0, 0, 0, 0, 0, 0, 0, 0, -17, -48, -19, -14, -28, -32, -25, -105, -17, 1, -27, -8, -33, -28, -18, -39, -26, -14, 27, 17, -10, -6, -21, -13, -8, 17, 29, 40, 38, 30, 20, -17, -5, 4, 32, 35, 35, 18, -3, 0, -12, 1, 1, 24, 22, 6, -17, -29, -23, -13, -15, 5, -2, -12, -20, -30, -60, -32, -5, 2, -9, 0, -33, -36, -3, -10, -15, 3, 9, -10, -12, 13, -1, -3, 1, -9, -1, -17, 4, -22, 13, 3, -1, 18, -5, 14, -2, 0, 14, 14, 11, 22, 15, 12, -1, 3, -1, -2, 14, 8, 10, 10, -4, -3, -13, 3, 6, 15, 13, 5, -6, -11, 4, -10, -9, 4, 8, -6, -4, -17, -20, -2, -14, 12, 9, 9, -21, -15, 24, 18, 18, 8, -3, -16, -11, 14, 25, 45, 30, 15, 18, 7, 2, -4, 20, 22, 21, 15, 11, 5, -5, 0, 22, 15, 20, 15, 16, 14, 2, 0, 15, 19, 26, 26, 22, 10, -1, -6, 6, 11, 10, 8, 10, 1, -11, -17, 5, 9, 20, 12, 8, -1, -8, -2, 15, 11, 13, 10, 4, 8, -1, -13, 28, 25, 13, 9, -21, 39, 0, 68, 54, 51, 83, 143, 129, 112, 102, -54, 10, 28, 64, 57, 110, 59, 12, 34, 27, 47, 43, 90, 100, 99, 83, 44, 30, 47, 38, 78, 49, 33, 48, 39, 5, -14, 48, 22, 51, 39, 53, 38, 24, 20, 16, 36, 21, -35, -39, -10, 10, 5, -1, -43, 23, 4, -13, -47, -107, -50, -58, -42, -41, -33, -30, -61, -45, -12, -8, -2, 4, 14, 28, 1, -43, -11, 25, 26, 41, 41, 27, 5, -45, 1, 35, 66, 72, 56, 27, 6, -56, -6, 38, 77, 80, 62, 24, 5, -44, -1, 49, 59, 62, 52, 18, 3, -38, 5, 26, 40, 36, 32, 6, -16, -37, -33, -13, -5, -22, -9, -38, -70, 110, 437, 451, 659, 1359, 0, 82, 276, 300, 573, 1120, 0, -23, -10, 5, 10, 26, 29, 32, 30, 34, -34, -19, 4, 4, 9, 14, 23, 29, 33, 33, 43, 50, 51, 70, -116, -31, 11, 6, 13, 1, 5, 13, 18, 15, 19, 23, 11, 25, 34, -13, -53, -153, -14, -10, -12, 3, -9, 1, 4, 2, 2, 6, 8, 9, 8, 3, 1, 6, 3, -2, -5, 1, 7, 31, 53, 115, 341, 45, 44, 43, 35, 38, 51, 46, 50, 42, -24, 0, -17, 7, 17, 34, 39, 43, 52, 53, 52, 46, 55, 39, -53, -52, -10, 6, -5, 10, 12, 12, 17, 20, 24, 29, 44, 38, 32, -13, -226, -46, -192, -55, -117, -104, -39, -65, -48, -29, -15, -7, -4, 6, 8, 28, 34, 40, 40, 50, 54, 48, 37, 13, 16, -6, -153, 54, 48, -7, 13, 13, 0, 124, -10, -8, -28, -21, -3, 0, 241, 140, 62, 32, 9, 5, 0, -5, -13, -37, -19, -37, -61, 92, -95, -3, -15, -15, -38, -38, -90, -25, 2455, -110, 7, -15, -30, -55, -73, -49, -83, -77, -75, -2, -13, -20, -29, -37, -57, -59, -152, -80, 21, 0, -1, 4, 16, 17, -3, -50, 68, 15, 23, 14, 14, 0, -432, -650, 80, 53, 54, 11, 36, 30, 22, 1, 6, 6, -6, -12, -1, -7, -12, -26, -7, -7, -3, -5, -20, -38, -78, -50, -7, 0, 179, 18, -81, -2, -18, -3, -3, -5, 14, 12, 12, 15, 22, 20, 14, 14, 12, 7, -2, -12, -22, -29, -45, -52, -72, -81};
        System.out.println(params.length);
        EngineConfig config = EngineInitializer.loadDefaultConfig();
        config.getMiddlegameTables()[0] = Arrays.stream(params, 0, 64).toArray();
        config.getMiddlegameTables()[1] = Arrays.stream(params, 64, 128).toArray();
        config.getMiddlegameTables()[2] = Arrays.stream(params, 128, 192).toArray();
        config.getMiddlegameTables()[3] = Arrays.stream(params, 192, 256).toArray();
        config.getMiddlegameTables()[4] = Arrays.stream(params, 256, 320).toArray();
        config.getMiddlegameTables()[5] = Arrays.stream(params, 320, 384).toArray();
        config.getEndgameTables()[0] = Arrays.stream(params, 384, 448).toArray();
        config.getEndgameTables()[1] = Arrays.stream(params, 448, 512).toArray();
        config.getEndgameTables()[2] = Arrays.stream(params, 512, 576).toArray();
        config.getEndgameTables()[3] = Arrays.stream(params, 576, 640).toArray();
        config.getEndgameTables()[4] = Arrays.stream(params, 640, 704).toArray();
        config.getEndgameTables()[5] = Arrays.stream(params, 704, 768).toArray();
        config.getPieceValues()[0] = Arrays.stream(params, 768, 774).toArray();
        config.getPieceValues()[1] = Arrays.stream(params, 774, 780).toArray();
        config.getMiddlegameMobilityBonus()[1] = Arrays.stream(params, 780, 789).toArray();
        config.getMiddlegameMobilityBonus()[2] = Arrays.stream(params, 789, 803).toArray();
        config.getMiddlegameMobilityBonus()[3] = Arrays.stream(params, 803, 818).toArray();
        config.getMiddlegameMobilityBonus()[4] = Arrays.stream(params, 818, 846).toArray();
        config.getEndgameMobilityBonus()[1] = Arrays.stream(params, 846, 855).toArray();
        config.getEndgameMobilityBonus()[2] = Arrays.stream(params, 855, 869).toArray();
        config.getEndgameMobilityBonus()[3] = Arrays.stream(params, 869, 884).toArray();
        config.getEndgameMobilityBonus()[4] = Arrays.stream(params, 884, 912).toArray();
        config.setBishopPairBonus(params[912]);
        config.getRookOpenFileBonus()[0] = params[913];
        config.getRookOpenFileBonus()[1] = params[914];
        config.getRookSemiOpenFileBonus()[0] = params[915];
        config.getRookSemiOpenFileBonus()[1] = params[916];
        config.getPassedPawnBonus()[0] = Arrays.stream(params, 917, 924).toArray();
        config.getPassedPawnBonus()[1] = Arrays.stream(params, 924, 931).toArray();
        config.getDoubledPawnPenalty()[0] = Arrays.stream(params, 931, 940).toArray();
        config.getDoubledPawnPenalty()[1] = Arrays.stream(params, 940, 949).toArray();
        config.getIsolatedPawnPenalty()[0] = Arrays.stream(params, 949, 958).toArray();
        config.getIsolatedPawnPenalty()[1] = Arrays.stream(params, 958, 967).toArray();
        config.setProtectedPassedPawnBonus(params[967]);
        config.setKingPawnShieldPenalty(Arrays.stream(params, 968, 975).toArray());
        config.setKingOpenFilePenalty(params[975]);
        config.setKingSemiOpenFilePenalty(params[976]);
        config.setKingOpenAdjacentFilePenalty(params[977]);
        config.setKingSemiOpenAdjacentFilePenalty(params[978]);
        config.getVirtualKingMobilityPenalty()[0] = Arrays.stream(params, 978, 1006).toArray();
        config.getVirtualKingMobilityPenalty()[1] = Arrays.stream(params, 1006, 1034).toArray();
        System.out.println(objectMapper.writeValueAsString(config));
    }

    private void tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction) throws IOException, ExecutionException, InterruptedException {
        int[] bestParams = tuner.tune(initialParams, createConfigFunction);
        EngineConfig bestConfig = createConfigFunction.apply(bestParams);
        System.out.println("Best config: " + objectMapper.writeValueAsString(bestConfig));
    }


}