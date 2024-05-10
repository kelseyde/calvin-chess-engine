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
                new int[] { 0, 0, 0, 0, 0, 0, 0, 0,
                        -94, -97, -115, -89, -52, -38, -39, -169,
                        -44, -44, 0, -44, 11, 135, 31, 5,
                        -12, 5, 12, 17, 40, 62, 31, 0,
                        -33, -13, -1, 21, 23, 37, 12, -24,
                        -26,-16,4,-2,24,16,43, 4,
                        -27, -1, -12, -10, 8, 51, 57, 1,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        -270, -89, -56, -77, 63, -112, -87,
                        -119, -75, -25, 100, 25, 113, 93, 14,
                        25, -5, 21, 34, 73, 114, 139,
                        84, 19, 24, 25, 24, 68, 34,
                        58, 12, 45, 0, 9, 23, 22,
                        37, 37, 25, -10, -17, -3, 19,
                        19, 29, 16, 34, -14, -17, -24,
                        1, 5, 9, 19, 7, 7, -55,
                        -17, -50, -21, -24, -10, -20, -69,
                        -19, -55, -33, -84, -111, -34, -33, -29,
                        -26, 10, -15, -5, 9, 67, 2, 50,
                        -25, 9, 53, 6, 73, 62, 63, 45,
                        -30, 1, 22, 38, 36, 4, 10, -1,
                        -1, 9, 16, 42, 42, 10, 14, -14,
                        24, 32, 25, 15, 23, 29, 21, 27,
                        -10, 36, 16, 3, 8, 40, 53, 16,
                        12, -7, 1, -14, -22, -10, -5, 4,
                        50, 60, 65, 80, 108, 182, 157, 50,
                        -2, -15, 22, 61, 24, 100, 120, 66,
                        7, -3, 4, 30, 47, 82, 83, 63,
                        -37, -10, 7, 29, 0, 24, 44, 40,
                        -28, -35, -46, -25, -9, -2, 17, -9,
                        -34, -24, -17, -9, -1, 7, 32, -4,
                        -31, -17, -29, -18, -9, 4, 26, -59,
                        -9, -9, -3, -1, 8, 8, -29, -3,
                        -15, -5, 31, 60, 70, 96, 17, 5, -16,
                        -38, -44, -73, -54, 91, 24, 116, -27,
                        -24, 16, 29, 41, 86, 111, 72, -33,
                        -21, -13, -10, -7, -3, -17, 15, -26,
                        -26, -16, -20, 9, 3, 13, -8, -21,
                        3, -11, -6, -9, 5, 11, -9, -24,
                        -2, 3, 4, 9, 20, 23, 3, -11,
                        -19, -9, 5, -18, -35, -61, -5,
                        -63, 95, 142, 98, 148, 83, 65, 87,
                        29, -1, 123, -7, 61, 6, -37, -36,
                        53, 177, 3, 93, -2, 61, 86, -38,
                        -33, 25, 43, -7, -40, -23, -14, -42,
                        -52, -2, -27, -62, -45, -103, -53, -118,
                        20, 17, -83, -77, -95, -79, -15, -49,
                        43, -13, -36, -51, -34, -52, 4, -5,
                        -63, 39, 27, -23, 7, -25, 41, 12,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        69, 60, 33, -12, -28, -12, 50, 64,
                        84, 69, 41, -2, -12, -5, 44, 46,
                        38, 26, 9, -16, -7, -4, 19, 14,
                        26, 19, 5, -8, 0, 1, 7, 7,
                        14, 10, 8, 2, 8, 14, -1, 0,
                        23, 7, 25, 14, 15, 14, -4, -1,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        -41, -68, -30, -21, -56, -52, -63, -124,
                        -34, -13, -44, -19, -55, -43, -39, -67,
                        -41, -23, 1, 1, -34, -25, -38, -39,
                        -20, 5, 8, 21, 19, 11, 7, -25,
                        -18, -9, 12, 14, 19, -2, -18, -14,
                        -25, -13, -17, 9, 6, -7, -26, -40,
                        -32, -29, -26, -9, -13, -19, -23, -47,
                        -66, -52, -17, -13, -25, -14, -50, -40,
                        -21, -38, -28, -9, -13, -15, -18, -18,
                        -18, -14, -9, -20, -9, -28, -10, -53,
                        1, -13, 1, 2, -18, 1, -15, -20,
                        2, 5, 1, 3, 2, -11, -3, -18,
                        -20, -11, 5, -9, 1, 2, -17, -26,
                        -12, 1, -3, 9, 13, -3, -17, -24,
                        -13, -29, -20, 1, 5, -12, -15, -30,
                        -34, -18, -22, -11, -6, -22, -31, -26,
                },
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