package com.kelseyde.calvin.tuning.texel;

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

    private final TexelTuner tuner = new TexelTuner("quiet_positions.txt");

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

    private void tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction) throws IOException, ExecutionException, InterruptedException {
        EngineConfig initialConfig = createConfigFunction.apply(initialParams);
        System.out.println("Initial config: " + objectMapper.writeValueAsString(initialConfig));
        int[] bestParams = tuner.tune(initialParams, createConfigFunction);
        EngineConfig bestConfig = createConfigFunction.apply(bestParams);
        System.out.println("Best config: " + objectMapper.writeValueAsString(bestConfig));
    }

}