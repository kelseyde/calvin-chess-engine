package com.kelseyde.calvin.tuning.texel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.engine.EngineInitializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

@Disabled
public class TexelTunerTest {

    private final TexelTuner tuner = new TexelTuner("quiet_positions.txt");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void tunePieceValuesAndPSTs() throws IOException {
        tune(
                new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 91, 127, 54, 88, 61, 119, 27, -18, -13, 0, 19, 24, 58, 63, 18, -27, -13, 10, 5, 14, 24, 19, 24, -20, -34, -9, -5, 9, 10, 13, 3, -32, -29, -11, -3, -9, 10, -4, 33, -12, -32, -1, -21, -16, -8, 31, 38, -15, 0, 0, 0, 0, 0, 0, 0, 0, -174, -89, -41, -42, 61, -96, -22, -100, -70, -34, 79, 29, 30, 69, 14, -10, -40, 53, 37, 72, 91, 136, 80, 44, -2, 24, 19, 60, 40, 69, 15, 29, -6, 11, 23, 20, 35, 26, 28, -9, -21, -6, 15, 17, 26, 17, 32, -15, -22, -46, -5, 4, 6, 20, -7, -12, -98, -19, -51, -26, -24, -21, -20, -30, -22, -3, -75, -44, -32, -35, 6, -1, -26, 9, -21, -13, 23, 66, 11, -40, -23, 30, 50, 33, 42, 50, 44, 5, -11, -2, 12, 43, 32, 30, 0, -9, -9, 6, 6, 23, 37, 5, 7, -3, 7, 22, 8, 8, 11, 20, 15, 10, -3, 22, 9, -7, 0, 21, 40, 8, -26, -10, -7, -21, -20, -15, -36, -18, 39, 49, 35, 51, 61, 16, 38, 50, 25, 25, 51, 62, 73, 74, 33, 51, 2, 19, 26, 35, 24, 52, 68, 23, -24, -11, 4, 26, 17, 28, -1, -13, -31, -25, -19, -8, 2, -5, 13, -24, -45, -24, -23, -17, -4, 0, -5, -26, -37, -17, -27, -16, -6, 4, 1, -70, -12, -12, -6, 10, 9, 10, -44, -19, -21, -1, 36, 19, 61, 51, 38, 52, -17, -32, -12, -6, -23, 64, 35, 61, -20, -19, 14, 15, 36, 63, 54, 64, -34, -20, -13, -9, 6, 20, -7, 8, -16, -26, -8, -10, 5, 3, 10, -4, -21, 1, -11, -2, -2, 4, 21, -2, -28, -1, 8, 5, 11, 16, 4, 2, -8, -19, -9, 3, -18, -32, -38, -43, -63, 24, 17, -13, -49, -34, 5, 8, 29, -1, -19, -7, -7, 3, -37, -36, -16, 24, 3, -16, -20, 6, 22, -27, -24, -27, -19, -28, -37, -25, -14, -43, -52, -2, -28, -46, -45, -49, -40, -58, -19, -13, -29, -53, -49, -31, -15, -34, 8, 0, -15, -57, -36, -23, 10, 1, -22, 29, 13, -47, 9, -31, 31, 7, 0, 0, 0, 0, 0, 0, 0, 0, 171, 166, 151, 127, 140, 125, 158, 180, 87, 93, 78, 60, 49, 46, 75, 77, 32, 17, 6, -2, -9, -3, 14, 10, 16, 6, -4, -14, -6, -1, 0, 0, 4, 0, 1, 1, 3, 2, -8, -7, 16, 1, 15, 9, 20, 7, -5, -4, 0, 0, 0, 0, 0, 0, 0, 0, -56, -45, -20, -21, -38, -27, -63, -97, -24, -15, -22, -9, -6, -24, -31, -49, -24, -27, 3, 2, -8, -16, -26, -40, -10, 10, 15, 29, 17, 11, 8, -18, -11, -6, 15, 25, 23, 10, -3, -15, -20, -4, -6, 14, 9, -5, -17, -29, -35, -13, -17, -2, -2, -17, -16, -37, -29, -51, -20, -8, -25, -11, -50, -63, -7, -28, -4, -7, -14, -9, -17, -17, -15, -11, 0, -15, -10, -20, -11, -14, 1, -15, 0, -8, -3, 7, -3, 7, -3, 2, 5, 2, 7, 3, -4, -5, -13, -4, 6, 12, 0, 3, -10, -16, -12, 0, 5, 9, 13, -4, -14, -20, -14, -19, -14, 0, 5, -10, -16, -27, -16, -10, -23, -5, -6, -23, -12, -14, 20, 13, 13, 8, 13, 19, 15, 12, 14, 20, 12, 8, -10, 10, 15, 10, 14, 14, 7, 3, 11, 4, 2, 4, 11, 10, 12, 1, 2, 8, 6, 9, 10, 7, 9, 4, 2, 1, -1, -4, 3, 2, -3, 2, -7, -10, -8, -9, 1, 1, 7, 1, -6, -6, -4, 4, -2, 9, 3, -8, -8, -6, 11, -13, -2, 29, 29, 34, 30, 26, 10, 27, -10, 20, 25, 43, 65, 32, 37, -2, -27, 3, 16, 42, 54, 42, 26, 9, -4, 25, 25, 52, 64, 47, 57, 43, -15, 35, 19, 47, 38, 34, 39, 20, -23, -34, 22, 6, 16, 14, 17, 5, -15, -16, -33, -9, -9, -30, -29, -30, -32, -21, -22, -44, -5, -37, -27, -34, -73, -42, -25, -25, -18, 8, -3, -24, -19, 10, 7, 10, 10, 38, 23, 4, 3, 10, 16, 12, 20, 38, 37, 13, -15, 15, 17, 20, 26, 31, 23, 0, -25, -11, 14, 25, 29, 30, 9, -10, -26, -6, 14, 28, 30, 23, 10, -8, -29, -4, 7, 20, 21, 11, 2, -20, -50, -34, -14, -4, -28, -12, -24, -50, 82, 344, 365, 484, 1032, 0, 87, 280, 290, 519, 936, 0 },
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
    public void tuneMobilityWeights() throws IOException {
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
    public void tuneBishopAndRookWeights() throws IOException {
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
    public void tuneKingSafetyWeights() throws IOException {
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
    public void tunePieceValues() throws IOException {
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

    private void tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction) throws IOException {
        EngineConfig initialConfig = createConfigFunction.apply(initialParams);
        System.out.println("Initial config: " + objectMapper.writeValueAsString(initialConfig));
        int[] bestParams = tuner.tune(initialParams, createConfigFunction);
        EngineConfig bestConfig = createConfigFunction.apply(bestParams);
        System.out.println("Best config: " + objectMapper.writeValueAsString(bestConfig));
    }

}