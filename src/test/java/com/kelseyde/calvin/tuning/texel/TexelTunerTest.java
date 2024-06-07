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
        int[] params = new int[] {0, 0, 0, 0, 0, 0, 0, 0, -76, -83, -104, -73, -29, -62, -78, -181, -8, 4, 44, 22, 55, 117, 33, 4, -6, 4, 11, 29, 45, 46, 27, 4, -29, -12, 6, 33, 30, 44, 18, -25, -19, -15, 13, 16, 32, 28, 41, 14, -23, 5, -6, 9, 16, 54, 64, 6, 0, 0, 0, 0, 0, 0, 0, 0, -268, -57, -34, -36, 42, -122, -145, -136, -67, -27, 92, 35, 102, 78, 1, 5, 5, 31, 40, 78, 120, 151, 77, 2, 41, 38, 30, 73, 37, 54, 16, 61, 19, 12, 36, 28, 43, 44, 26, 5, -1, 16, 40, 38, 51, 34, 46, 10, 12, -8, 17, 22, 28, 40, 31, 32, -21, 11, -26, 8, 4, 19, 8, -40, -23, -54, -21, -73, -119, -19, -14, -80, -20, 4, -15, -6, 4, 59, -17, 12, -6, 17, 58, 8, 69, 64, 68, 42, -22, 9, 19, 24, 31, -17, 19, -2, 8, 13, 19, 37, 38, 17, 16, -5, 44, 42, 34, 17, 36, 37, 34, 41, -2, 49, 29, 18, 22, 54, 72, 29, 33, 4, 25, 3, 2, 12, 22, 31, 35, 53, 37, 53, 98, 151, 106, 29, -13, -36, 3, 47, 5, 70, 90, 77, -5, -17, -26, 1, 13, 43, 90, 40, -38, -14, -7, 6, -21, 2, 28, 34, -28, -36, -47, -32, -26, -14, 11, -4, -31, -28, -17, -5, -4, 0, 21, 8, -18, -21, -29, -12, -6, 13, 34, -29, -3, -6, -2, 3, 6, 12, -12, 17, -35, -7, 34, 56, 97, 29, -3, -37, -39, -56, -61, -93, -116, 10, -60, 98, -35, -28, -23, -30, -18, -15, 37, -10, -32, -17, -21, -46, -48, -58, -67, -27, -15, -17, -13, -28, -6, -20, -5, -33, -19, 9, -7, -1, -3, -1, 7, -16, -3, 7, 9, 11, 19, 34, 44, 24, 0, -13, -1, 18, -8, -13, -24, 32, 152, 98, 161, 142, 149, 147, 69, 3, 3, 167, 142, 107, 112, 161, 20, -65, 66, 212, 122, 133, 61, 154, 124, -51, 8, 63, 100, 70, 38, 18, 31, -83, 45, 84, 66, -18, -15, -75, -33, -138, 13, 45, -49, -51, -60, -48, -16, -83, 15, -13, -23, -50, -37, -35, 17, -16, -87, 35, 21, -37, 3, -18, 43, 8, 0, 0, 0, 0, 0, 0, 0, 0, 34, 29, 8, -33, -50, -29, 23, 24, 71, 53, 31, -6, -18, -9, 30, 37, 46, 35, 18, -7, -4, 2, 21, 18, 32, 24, 7, -1, 2, -2, 8, 9, 21, 18, 9, 5, 10, 11, 2, 0, 30, 16, 26, 14, 16, 11, -4, 1, 0, 0, 0, 0, 0, 0, 0, 0, -14, -50, -19, -13, -31, -26, -26, -105, -17, -1, -25, -8, -37, -26, -19, -40, -28, -15, 26, 20, -12, -6, -23, -14, -9, 19, 30, 40, 37, 31, 18, -15, -5, 6, 30, 38, 35, 18, -3, 1, -15, 1, 2, 24, 21, 6, -14, -29, -30, -10, -13, 4, -1, -11, -16, -29, -60, -31, -8, 1, -11, -2, -32, -38, 0, -10, -14, 3, 11, -5, -7, 12, 0, 2, 3, -8, -2, -14, 6, -25, 12, 6, -1, 17, -5, 13, -2, 0, 19, 15, 11, 23, 14, 13, -1, 2, -4, -1, 14, 9, 12, 11, -3, -2, -14, 3, 7, 16, 12, 6, -5, -12, 5, -9, -11, 5, 11, -4, -3, -16, -19, 0, -14, 8, 9, 9, -19, -13, 24, 15, 20, 8, -4, -20, -7, 9, 25, 45, 30, 14, 18, 7, 2, -5, 20, 22, 20, 15, 11, 5, -5, -1, 22, 13, 20, 12, 15, 16, 4, 0, 15, 18, 28, 25, 22, 10, -1, -5, 7, 12, 12, 7, 6, 1, -12, -14, 5, 9, 20, 14, 8, -2, -8, -2, 15, 11, 13, 10, 4, 8, 1, -13, 31, 26, 11, 10, -17, 36, 4, 66, 54, 49, 82, 139, 124, 110, 106, -53, 9, 30, 63, 53, 109, 54, 11, 33, 28, 46, 42, 89, 100, 99, 85, 42, 31, 46, 37, 77, 49, 31, 47, 41, 3, -14, 48, 24, 50, 39, 52, 38, 27, 17, 16, 36, 22, -30, -40, -6, 12, 8, -1, -43, 30, 1, -14, -47, -111, -51, -62, -44, -42, -38, -34, -62, -44, -14, -8, -2, 2, 11, 25, -1, -45, -11, 25, 28, 43, 41, 27, 4, -47, 1, 33, 68, 72, 57, 27, 4, -55, -7, 38, 77, 80, 63, 23, 4, -46, -2, 49, 61, 63, 53, 17, 2, -38, 5, 25, 39, 36, 32, 7, -16, -38, -33, -13, -6, -22, -9, -38, -70, 110, 437, 451, 659, 1359, 0, 82, 276, 300, 573, 1120, 0, -24, -9, 5, 10, 26, 29, 32, 30, 34, -34, -19, 3, 4, 9, 14, 23, 29, 33, 33, 44, 50, 50, 67, -113, -31, 10, 5, 12, 1, 5, 13, 18, 15, 19, 23, 11, 25, 34, -13, -47, -149, -8, -9, -12, 5, -9, 1, 3, 2, 2, 6, 7, 9, 8, 3, 1, 6, 3, -2, -5, 1, 9, 35, 52, 124, 331, 45, 44, 43, 35, 38, 51, 46, 50, 41, -24, -1, -16, 8, 18, 34, 39, 43, 52, 53, 51, 46, 55, 38, -56, -53, -10, 6, -5, 10, 12, 12, 17, 20, 24, 29, 44, 38, 33, -13, -235, -48, -196, -59, -118, -104, -38, -65, -47, -28, -14, -7, -5, 6, 7, 28, 35, 41, 40, 50, 55, 49, 37, 10, 16, -10, -146, 54, 48, -7, 12, 13, 0, 124, -10, -7, -28, -21, -2, 0, 242, 140, 62, 33, 9, 5, 0, -5, -13, -41, -19, -39, -64, -68, -95, -3, -15, -15, -39, -37, -88, -22, 4045, -110, 7, -15, -30, -55, -74, -49, -87, -79, -75, -2, -13, -20, -29, -36, -56, -56, -150, -80, 21, 0, -1, 5, 19, 18, 0, -45, 69, 15, 24, 14, 0, 0, -431, -684, 60, 44, 42, 9, 30, 21, 15, -1, 3, 5, -9, -12, -2, -7, -11, -30, -10, -2, -3, -12, -11, -64, -50, -7, 0, 0, 177, 32, -61, 0, -13, -5, -5, -1, 15, 7, 14, 15, 23, 16, 11, 12, 10, 4, -4, -16, -25, -32, -51, -52, -81, -36};
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

    @Test
    public void tunePieceValuesAndPSTs() throws IOException, ExecutionException, InterruptedException {
        List<Integer> weights = new ArrayList<>();
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
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
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    if (params.length != 780) return null;
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
                    return config;
                }
        );
    }

    @Test
    public void tuneMobilityWeights() throws IOException, ExecutionException, InterruptedException {
        List<Integer> initialParams = new ArrayList<>();
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[1]).boxed().toList());
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[2]).boxed().toList());
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[3]).boxed().toList());
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[4]).boxed().toList());
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[1]).boxed().toList());
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[2]).boxed().toList());
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[3]).boxed().toList());
        initialParams.addAll(Arrays.stream(initialConfig.getMiddlegameMobilityBonus()[4]).boxed().toList());
        tune(
                initialParams.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.getMiddlegameMobilityBonus()[1] = Arrays.stream(params, 0, 9).toArray();
                    config.getMiddlegameMobilityBonus()[2] = Arrays.stream(params, 9, 23).toArray();
                    config.getMiddlegameMobilityBonus()[3] = Arrays.stream(params, 23, 38).toArray();
                    config.getMiddlegameMobilityBonus()[4] = Arrays.stream(params, 38, 66).toArray();
                    config.getEndgameMobilityBonus()[1] = Arrays.stream(params, 66, 75).toArray();
                    config.getEndgameMobilityBonus()[2] = Arrays.stream(params, 75, 89).toArray();
                    config.getEndgameMobilityBonus()[3] = Arrays.stream(params, 89, 104).toArray();
                    config.getEndgameMobilityBonus()[4] = Arrays.stream(params, 104, 132).toArray();
                    return config;
                }
        );
    }

    @Test
    public void tuneBishopAndRookWeights() throws IOException, ExecutionException, InterruptedException {
        tune(
                new int[] { 50, 42, 22, 18, 16 },
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.setBishopPairBonus(params[0]);
                    config.getRookOpenFileBonus()[0] = params[1];
                    config.getRookOpenFileBonus()[1] = params[2];
                    config.getRookSemiOpenFileBonus()[0] = params[3];
                    config.getRookSemiOpenFileBonus()[1] = params[4];
                    return config;
                }
        );
    }

    @Test
    public void tuneKingSafetyWeights() throws IOException, ExecutionException, InterruptedException {
        tune(
                new int[] { 0, 0, 10, 25, 50, 50, 50, 15, 10, 25, 15, 120 },
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.getKingPawnShieldPenalty()[0] = params[0];
                    config.getKingPawnShieldPenalty()[1] = params[1];
                    config.getKingPawnShieldPenalty()[2] = params[2];
                    config.getKingPawnShieldPenalty()[3] = params[3];
                    config.getKingPawnShieldPenalty()[4] = params[4];
                    config.getKingPawnShieldPenalty()[5] = params[5];
                    config.getKingPawnShieldPenalty()[6] = params[6];
                    config.setKingSemiOpenFilePenalty(params[7]);
                    config.setKingSemiOpenAdjacentFilePenalty(params[8]);
                    config.setKingOpenFilePenalty(params[9]);
                    config.setKingOpenAdjacentFilePenalty(params[10]);
                    config.setKingLostCastlingRightsPenalty(params[11]);
                    return config;
                }
        );
    }

    @Test
    public void tunePieceValues() throws IOException, ExecutionException, InterruptedException {
        tune(
                new int[] { 92, 393, 400, 544, 1119, 0, 78, 254, 280, 535, 1072, 0 },
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.getPieceValues()[0] = Arrays.stream(params, 0, 6).toArray();
                    config.getPieceValues()[1] = Arrays.stream(params, 6, 12).toArray();
                    return config;
                }
        );

    }

    @Test
    public void tunePiecePhases() throws IOException, ExecutionException, InterruptedException {
        tune(
                new int[] {0, 10, 10, 20, 45, 0},
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.setPiecePhases(params);
                    return config;
                }
        );
    }

    @Test
    public void testOutpostWeights() throws IOException, ExecutionException, InterruptedException {
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        int[] initialParams = new int[4];
        initialParams[0] = initialConfig.getKnightOutpostBonus()[1][0];
        initialParams[1] = initialConfig.getKnightOutpostBonus()[1][1];
        initialParams[2] = initialConfig.getKnightOutpostBonus()[2][0];
        initialParams[3] = initialConfig.getKnightOutpostBonus()[2][1];
//        initialParams[4] = initialConfig.getBishopOutpostBonus()[1][0];
//        initialParams[5] = initialConfig.getBishopOutpostBonus()[1][1];
//        initialParams[6] = initialConfig.getBishopOutpostBonus()[2][0];
//        initialParams[7] = initialConfig.getBishopOutpostBonus()[2][1];
        tune(
                initialParams,
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.getKnightOutpostBonus()[1][0] = params[0];
                    config.getKnightOutpostBonus()[1][1] = params[1];
                    config.getKnightOutpostBonus()[2][0] = params[2];
                    config.getKnightOutpostBonus()[2][1] = params[3];
//                    config.getBishopOutpostBonus()[1][0] = params[4];
//                    config.getBishopOutpostBonus()[1][1] = params[5];
//                    config.getBishopOutpostBonus()[2][0] = params[6];
//                    config.getBishopOutpostBonus()[2][1] = params[7];
                    return config;
                }
        );
    }

    @Test
    public void tuneDrawishScaleFactor() throws IOException, ExecutionException, InterruptedException {
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        tune(
                new int[] {initialConfig.getDrawishScaleFactor()},
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.setDrawishScaleFactor(params[0]);
                    return config;
                }
        );
    }

    @Test
    public void tuneScaleFactors() throws IOException, ExecutionException, InterruptedException {
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        tune(
                new int[] {initialConfig.getMopUpScaleFactor()[0], initialConfig.getMopUpScaleFactor()[1],
                        initialConfig.getKingSafetyScaleFactor()[0], initialConfig.getKingSafetyScaleFactor()[1]},
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.getMopUpScaleFactor()[0] = params[0];
                    config.getMopUpScaleFactor()[1] = params[1];
                    config.getKingSafetyScaleFactor()[0] = params[2];
                    config.getKingSafetyScaleFactor()[1] = params[3];
                    return config;
                }
        );
    }

    @Test
    public void tuneTempoBonus() throws IOException, ExecutionException, InterruptedException {
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        tune(
                new int[] {initialConfig.getTempoBonus()},
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.setTempoBonus(params[0]);
                    return config;
                }
        );
    }

    @Test
    public void tuneVirtualKingMobility() throws IOException, ExecutionException, InterruptedException {
        List<Integer> weights = new ArrayList<>();
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        weights.addAll(Arrays.stream(initialConfig.getVirtualKingMobilityPenalty()[0]).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getVirtualKingMobilityPenalty()[1]).boxed().toList());
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.getVirtualKingMobilityPenalty()[0] = Arrays.stream(params, 0, 28).toArray();
                    config.getVirtualKingMobilityPenalty()[1] = Arrays.stream(params, 28, 56).toArray();
                    return config;
                }
        );
    }

    @Test
    public void tuneThreats() throws IOException, ExecutionException, InterruptedException {
        List<Integer> weights = new ArrayList<>();
        EngineConfig initialConfig = EngineInitializer.loadDefaultConfig();
        weights.addAll(Arrays.stream(initialConfig.getPawnAttackOnMinorThreatBonus()).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getPawnAttackOnRookThreatBonus()).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getPawnAttackOnQueenThreatBonus()).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMinorAttackOnRookThreatBonus()).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getMinorAttackOnQueenThreatBonus()).boxed().toList());
        weights.addAll(Arrays.stream(initialConfig.getRookAttackOnQueenThreatBonus()).boxed().toList());
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.setPawnAttackOnMinorThreatBonus(Arrays.stream(params, 0, 2).toArray());
                    config.setPawnAttackOnRookThreatBonus(Arrays.stream(params, 2, 4).toArray());
                    config.setPawnAttackOnQueenThreatBonus(Arrays.stream(params, 4, 6).toArray());
                    config.setMinorAttackOnRookThreatBonus(Arrays.stream(params, 6, 8).toArray());
                    config.setMinorAttackOnQueenThreatBonus(Arrays.stream(params, 8, 10).toArray());
                    config.setRookAttackOnQueenThreatBonus(Arrays.stream(params, 10, 12).toArray());
                    return config;
                }
        );
    }

    private void tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction) throws IOException, ExecutionException, InterruptedException {
        int[] bestParams = tuner.tune(initialParams, createConfigFunction);
        EngineConfig bestConfig = createConfigFunction.apply(bestParams);
        System.out.println("Best config: " + objectMapper.writeValueAsString(bestConfig));
    }

}