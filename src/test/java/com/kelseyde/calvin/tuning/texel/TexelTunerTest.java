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

    private final TexelTuner tuner = new TexelTuner("quiet_positions_extended.epd");

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
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    if (params.length != 917) throw new IllegalArgumentException();
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
                    return config;
                }
        );
    }

    @Test
    public void convertParamsToJson() throws JsonProcessingException {
        int[] params = new int[] {0, 0, 0, 0, 0, 0, 0, 0, -109, -136, -132, -127, -115, -181, -156, -205, -42, -48, -20, -47, -10, 35, -3, -12, -26, -12, -10, 2, 18, 36, 16, -5, -35, -30, -5, 6, 5, 32, -1, -23, -31, -27, -5, -7, 8, 21, 21, -3, -34, -8, -21, -10, -4, 32, 40, -11, 0, 0, 0, 0, 0, 0, 0, 0, -216, -268, -250, -241, -139, -298, -265, -205, -82, -70, -2, -199, -147, -77, -128, -92, -70, -32, -83, -4, -23, -12, -14, -74, -41, 0, 0, -15, -4, 16, -18, -2, -17, -40, 1, -12, -13, 9, -25, -25, -26, -11, 14, 0, 13, 15, 11, -27, -56, -52, -21, -4, 0, -2, -10, -19, -98, -21, -75, -51, -39, -31, -19, -90, -100, -249, -280, -264, -209, -275, -272, -73, -53, -19, -50, -107, -117, -46, -82, -104, -121, -42, -68, -73, -70, -163, -86, -80, -45, -23, -23, -18, -2, -65, -10, -56, -17, -33, -6, -3, -3, 0, -22, -15, 3, 9, 6, 1, 10, 13, 8, -5, -6, 23, 2, -4, 2, 21, 40, 1, -16, -40, 5, -52, -36, 1, -20, -34, -128, -88, -165, -142, -106, -136, -69, -115, -53, -59, -35, -4, -24, -71, -99, -76, -71, -73, -76, -47, -56, -65, -90, -68, -86, -59, -48, -48, -43, -28, -26, -14, -35, -70, -58, -56, -44, -26, -44, -4, -51, -34, -39, -34, -15, -14, 6, -11, -41, -43, -44, -38, -28, 0, 20, -53, -3, -18, -12, -10, -2, 17, -12, 21, -98, -232, -299, -239, -181, -117, -96, -39, -44, -28, -89, -121, -132, -77, -52, -38, -70, -63, -92, -90, -99, -193, -86, -63, -55, -24, -43, -73, -58, -98, -60, -42, -8, -50, -7, -22, -13, -27, -28, -24, -20, 15, -7, 2, 9, 6, 17, -10, -11, 15, 17, 14, 21, 31, 39, 30, -6, -18, -1, 28, -4, -27, -30, 33, 99, 107, 103, 142, 88, 86, 79, 2, -23, 107, 126, 35, 90, 66, 38, -72, 59, 135, 92, 116, 34, 135, 85, -10, 11, 66, 76, 16, -6, 6, 3, -112, -1, 69, 18, -18, -34, -41, -31, -138, -17, 43, -16, -32, -19, -14, 14, -48, -1, 11, -5, -39, -22, -9, 37, -17, -67, 41, 24, -48, -8, -35, 41, -9, 0, 0, 0, 0, 0, 0, 0, 0, 75, 67, 54, 23, 18, 22, 55, 79, 41, 33, 3, -29, -39, -13, 12, 15, 17, 11, 0, -27, -14, -9, 5, 1, 11, 7, -4, -13, -7, -6, -4, -2, 2, -3, -4, -10, -1, -2, -16, -9, 12, -8, 7, -2, 4, 0, -16, -3, 0, 0, 0, 0, 0, 0, 0, 0, -44, -4, -3, 1, -24, -18, -24, -64, -40, -11, -14, 34, 5, -34, -32, -34, -28, -12, 32, 4, -10, -20, -36, -42, -25, -3, 17, 23, 12, 6, 1, -29, -36, 8, 16, 20, 21, 8, -3, -30, -43, -9, -8, 12, 9, -12, -20, -45, -53, -24, -13, -11, -12, -9, -21, -39, -48, -68, -29, -15, -27, -30, -68, -51, -28, 18, 16, 8, -3, 10, 10, -40, -21, -14, -5, -10, 0, -33, -21, -24, 4, -5, 7, 8, -7, 28, -15, -14, -11, -9, -2, 3, 0, -4, -26, -24, -27, -8, -1, 10, -3, -7, -25, -29, -30, -11, -5, 0, -1, -9, -13, -24, -28, -33, -24, -14, -15, -15, -24, -39, -29, -13, -38, -7, -5, -27, -25, -32, 37, 25, 41, 23, 8, 15, 2, 23, 23, 28, 17, 0, -11, -3, 15, 12, 18, 15, 16, -2, -20, -11, -4, -10, 15, 6, 3, -3, -10, -21, -26, -21, -15, -1, -1, -3, -19, -21, -23, -33, -16, -24, -14, -19, -27, -29, -42, -44, -25, -25, -13, -16, -22, -31, -49, -29, -10, -11, -6, -4, -15, -25, -23, -44, 21, 156, 192, 151, 128, 72, 42, 9, 27, 8, 95, 123, 141, 95, 47, 11, 15, 42, 84, 78, 87, 166, 48, 14, 9, 13, 33, 90, 82, 65, 20, -11, -19, 44, 20, 60, 39, 36, 35, -6, -23, -30, 22, 10, 22, 20, -14, -16, -23, -39, -31, -1, -13, -40, -46, -67, -41, -32, -44, -82, -35, -28, -19, -100, -95, -37, -47, -48, -40, -26, -25, -58, -26, 0, -1, 4, 8, 17, 28, -2, -23, 13, 23, 21, 32, 34, 36, 1, -32, 15, 27, 49, 52, 46, 36, 12, -36, 0, 33, 55, 60, 50, 24, 9, -33, 1, 34, 49, 49, 39, 14, -7, -34, 2, 16, 30, 31, 25, 1, -17, -49, -43, -21, -9, -19, -9, -41, -75, 57, 307, 313, 387, 915, 0, 65, 243, 265, 495, 848, 0, -35, -14, -6, -10, -5, -9, -7, -11, -5, -40, -29, -16, -11, -7, -8, -3, 1, 0, 0, 4, 10, 7, 3, -77, -1, 19, 13, 19, 5, 5, 11, 11, 3, 5, 6, -12, -4, -17, -4108, -23, -45, 1, -4, -5, 7, -1, 4, 3, 6, 3, 5, 5, 4, 2, -1, -4, -2, -5, -2, -8, -4, -6, 4, 26, 160, 181, -93, 15, 11, 14, 20, 16, 16, 22, 18, -50, -19, -18, 0, 12, 21, 27, 24, 31, 23, 23, 14, 35, 11, -107, -60, -11, 6, -2, 7, 16, 5, 12, 17, 16, 15, 23, 21, 9, -1036, -340, -250, -262, -128, -153, -108, -69, -62, -57, -45, -23, -24, -13, -8, -3, 4, 6, 4, 2, -4, 6, -4, -6, -31, -34, -140, -134, 27, 70, -7, 26, 34};
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

    private void tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction) throws IOException, ExecutionException, InterruptedException {
        int[] bestParams = tuner.tune(initialParams, createConfigFunction);
        EngineConfig bestConfig = createConfigFunction.apply(bestParams);
        System.out.println("Best config: " + objectMapper.writeValueAsString(bestConfig));
    }

}