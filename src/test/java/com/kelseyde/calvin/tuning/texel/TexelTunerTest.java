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
        weights.add(initialConfig.getDrawishScaleFactor());
        weights.add(initialConfig.getTempoBonus());
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    if (params.length != 919) throw new IllegalArgumentException();
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
                    config.setDrawishScaleFactor(params[917]);
                    config.setTempoBonus(params[917]);
                    return config;
                }
        );
    }

    @Test
    public void parse() throws JsonProcessingException {

        int[] params = new int[] {0, 0, 0, 0, 0, 0, 0, 0, -94, -75, -122, -86, -47, -64, -88, -207, -44, -38, 3, -41, 11, 111, 6, -13, -9, 11, 14, 22, 42, 59, 26, 1, -25, -10, 6, 30, 30, 44, 21, -8, -22, -6, 11, 11, 36, 28, 49, 9, -25, 8, -2, 18, 26, 60, 65, 1, 0, 0, 0, 0, 0, 0, 0, 0, -252, -78, -52, -77, 63, -111, -87, -93, -45, -8, 117, 25, 126, 103, 34, 52, 13, 21, 25, 73, 125, 139, 93, 44, 29, 34, 26, 73, 37, 60, 12, 68, 18, 9, 25, 27, 45, 37, 43, 6, -7, -3, 16, 20, 29, 16, 38, 4, 1, -10, 4, 6, 16, 26, 29, 20, -37, -9, -37, 1, -4, 4, -12, -32, -19, -62, -33, -96, -112, -31, -33, -33, -12, 17, -13, 11, 19, 84, 8, 59, -18, 1, 64, 6, 74, 60, 67, 54, -25, 1, 24, 43, 38, -1, 12, -1, 16, 6, 18, 44, 51, 6, 19, 8, 24, 41, 26, 12, 24, 28, 30, 33, 1, 36, 20, 2, 6, 45, 56, 24, 27, 13, 8, -11, -11, -3, 14, 19, 50, 60, 65, 80, 108, 182, 157, 50, -2, -19, 22, 72, 22, 104, 120, 79, 7, -2, 1, 30, 57, 83, 83, 63, -37, -10, 4, 25, -2, 24, 44, 46, -20, -34, -42, -25, -10, 0, 17, -9, -32, -23, -17, -9, 0, 7, 39, 4, -22, -17, -29, -17, -2, 4, 30, -51, -5, -9, -3, -1, 7, 8, -25, 4, -15, -4, 31, 92, 70, 96, 17, 15, -16, -38, -47, -80, -54, 91, 27, 116, -22, -21, 12, 23, 47, 95, 111, 89, -24, -17, -13, -13, -7, -9, -17, 18, -21, -26, -15, -26, 12, 3, 15, -7, -17, 5, -15, -8, -13, 3, 16, -1, -7, -1, 7, 4, 9, 23, 28, 28, -7, -15, -9, 1, -18, -30, -55, 23, -63, 95, 142, 98, 148, 149, 65, 85, 29, -1, 123, -7, 62, 6, -37, -36, 53, 177, 56, 94, -2, 84, 101, -39, -33, 25, 43, -7, -40, -24, -14, -42, -52, -2, -27, -62, -45, -104, -53, -118, 20, 17, -83, -87, -95, -79, -15, -49, 43, -13, -36, -76, -54, -52, 4, -11, -63, 38, 25, -39, 3, -19, 43, 12, 0, 0, 0, 0, 0, 0, 0, 0, 75, 64, 43, -5, -26, -1, 67, 70, 88, 70, 43, -1, -8, 4, 51, 55, 41, 26, 13, -16, -5, 1, 21, 17, 26, 22, 8, -7, 2, 4, 9, 7, 16, 9, 11, 0, 10, 15, -1, 0, 24, 7, 25, 9, 15, 14, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, -41, -68, -30, -21, -56, -52, -63, -124, -34, -13, -44, -17, -55, -43, -39, -68, -44, -23, 8, 2, -34, -14, -43, -39, -20, 5, 8, 21, 20, 13, 7, -25, -18, -9, 12, 14, 19, -2, -24, -14, -25, -13, -13, 6, 6, -7, -30, -37, -33, -29, -23, -9, -14, -25, -23, -47, -64, -52, -18, -13, -25, -14, -50, -39, -21, -38, -27, -16, -13, -20, -17, -13, -18, -13, -9, -26, -13, -30, -10, -52, 1, -10, -14, 5, -18, 1, -14, -20, 1, 5, 1, 7, 4, -4, -12, -17, -20, -12, 4, -9, 1, 7, -21, -23, -12, 1, -3, 9, 13, -2, -17, -19, -13, -24, -20, 0, 5, -17, -14, -30, -28, -14, -23, -7, -3, -9, -38, -26, 23, 17, 17, 7, -2, -21, -13, 12, 27, 44, 32, 7, 20, 11, 3, 2, 18, 20, 19, 7, 1, 2, 5, -5, 22, 12, 20, 8, 12, 12, -1, -7, 12, 13, 21, 20, 12, 6, 1, -7, 3, 0, 3, 3, 2, -7, -20, -19, 5, 3, 18, 9, 2, 5, -11, 3, 9, 7, 5, 8, -2, 13, 10, -20, -1, 27, 32, 11, 16, 20, 10, 54, 22, 28, 69, 134, 101, 98, 84, -2, -17, 21, 38, 27, 103, 35, 26, 6, -5, 46, 27, 63, 95, 106, 84, 51, 22, 51, 38, 82, 38, 34, 39, 50, -23, -19, 46, 26, 51, 32, 44, 21, 19, 20, -3, 16, 14, -25, -28, -23, 7, -6, -19, -44, 3, -6, -3, -53, -73, -37, -62, -37, -48, -25, -37, -56, -42, 8, -18, 7, 1, 38, 25, 1, -34, -13, 8, 4, 25, 35, 35, 14, -34, -5, 12, 28, 33, 42, 25, 9, -42, -7, 21, 36, 41, 49, 20, 6, -41, -6, 29, 41, 45, 41, 12, -7, -37, -3, 16, 32, 25, 30, 9, -14, -37, -33, -18, -18, -25, -13, -37, -50, 92, 393, 400, 544, 1119, 0, 78, 254, 280, 535, 1072, 0, -65, -30, -19, -11, 0, 9, 19, 24, 34, -53, -38, -18, -15, -5, 4, 12, 17, 20, 31, 35, 69, 16, 92, -109, -39, -2, -7, 0, -4, -3, 6, 7, 11, 17, 12, 16, 20, 8, -13, -180, -60, -28, -27, -33, -21, -21, -13, -9, -6, -3, 5, 6, 7, 8, 2, 3, 2, 3, 23, -2, 30, 75, 80, 130, 190, 650, -76, -36, -4, -3, 1, 10, 8, 11, -9, -68, -45, -43, -26, -11, 0, 8, 1, 14, 2, 5, -17, 21, -19, -79, -57, -19, -5, -10, -3, 3, -1, 2, 7, 8, 15, 15, 15, -1, -13, -244, -265, -248, -137, -107, -88, -64, -52, -39, -31, -19, -24, -13, -7, -1, 14, 21, 27, 27, 20, 35, 5, -33, -8, -33, -113, -309, 50, 48, 9, 16, 24, 33, 10};
        if (params.length != 919) throw new IllegalArgumentException();
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
        config.setDrawishScaleFactor(params[917]);
        config.setTempoBonus(params[917]);
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
        tune(
                new int[] { -18, -14, -8, -4, 0, 4, 8, 12, 16, -26, -21, -16, -12, -8, -4, 0, 4, 8, 12, 16, 16, 16, 16, -14, -12, -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10, 12, 12, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12, 12, -18, -14, -8, -4, 0, 4, 8, 12, 16, -26, -21, -16, -12, -8, -4, 0, 4, 8, 12, 16, 16, 16, 16, -14, -12, -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10, 12, 12, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12, 12 },
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
        int[] initialParams = new int[8];
        initialParams[0] = initialConfig.getKnightOutpostBonus()[1][0];
        initialParams[1] = initialConfig.getKnightOutpostBonus()[1][1];
        initialParams[2] = initialConfig.getKnightOutpostBonus()[2][0];
        initialParams[3] = initialConfig.getKnightOutpostBonus()[2][1];
        initialParams[4] = initialConfig.getBishopOutpostBonus()[1][0];
        initialParams[5] = initialConfig.getBishopOutpostBonus()[1][1];
        initialParams[6] = initialConfig.getBishopOutpostBonus()[2][0];
        initialParams[7] = initialConfig.getBishopOutpostBonus()[2][1];
        tune(
                initialParams,
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.getKnightOutpostBonus()[1][0] = params[0];
                    config.getKnightOutpostBonus()[1][1] = params[1];
                    config.getKnightOutpostBonus()[2][0] = params[2];
                    config.getKnightOutpostBonus()[2][1] = params[3];
                    config.getBishopOutpostBonus()[1][0] = params[4];
                    config.getBishopOutpostBonus()[1][1] = params[5];
                    config.getBishopOutpostBonus()[2][0] = params[6];
                    config.getBishopOutpostBonus()[2][1] = params[7];
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

    private void tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction) throws IOException, ExecutionException, InterruptedException {
        int[] bestParams = tuner.tune(initialParams, createConfigFunction);
        EngineConfig bestConfig = createConfigFunction.apply(bestParams);
        System.out.println("Best config: " + objectMapper.writeValueAsString(bestConfig));
    }

}