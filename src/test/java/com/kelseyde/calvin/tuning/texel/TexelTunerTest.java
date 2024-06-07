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
        weights.add(initialConfig.getPawnAttackOnMinorThreatBonus());
        weights.add(initialConfig.getPawnAttackOnRookThreatBonus());
        weights.add(initialConfig.getPawnAttackOnQueenThreatBonus());
        weights.add(initialConfig.getMinorAttackOnRookThreatBonus());
        weights.add(initialConfig.getMinorAttackOnQueenThreatBonus());
        weights.add(initialConfig.getRookAttackOnQueenThreatBonus());
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    int length = 1041;
                    if (params.length != length) {
                        throw new IllegalArgumentException("Expected " + length + " parameters, got " + params.length);
                    }
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
                    config.getVirtualKingMobilityPenalty()[0] = Arrays.stream(params, 979, 1007).toArray();
                    config.getVirtualKingMobilityPenalty()[1] = Arrays.stream(params, 1007, 1035).toArray();
                    config.setPawnAttackOnMinorThreatBonus(params[1035]);
                    config.setPawnAttackOnRookThreatBonus(params[1036]);
                    config.setPawnAttackOnQueenThreatBonus(params[1037]);
                    config.setMinorAttackOnRookThreatBonus(params[1038]);
                    config.setMinorAttackOnQueenThreatBonus(params[1039]);
                    config.setRookAttackOnQueenThreatBonus(params[1040]);
                    return config;
                }
        );
    }

    @Test
    public void convertParamsToJson() throws JsonProcessingException {
        int[] params = new int[] {0, 0, 0, 0, 0, 0, 0, 0, -76, -82, -102, -73, -29, -62, -76, -183, -8, 5, 43, 19, 53, 117, 33, 4, -6, 4, 11, 28, 42, 46, 27, 4, -29, -12, 6, 33, 30, 44, 18, -25, -19, -15, 13, 16, 32, 28, 41, 14, -23, 5, -6, 9, 16, 54, 64, 6, 0, 0, 0, 0, 0, 0, 0, 0, -267, -60, -34, -36, 37, -121, -145, -135, -68, -38, 76, 17, 102, 62, 2, 5, 4, 31, 29, 77, 103, 151, 76, 3, 41, 39, 30, 73, 37, 58, 17, 61, 19, 12, 37, 28, 44, 44, 25, 4, -1, 15, 40, 39, 52, 34, 46, 10, 9, -8, 17, 22, 28, 38, 31, 32, -21, 11, -27, 7, 3, 20, 8, -39, -23, -55, -23, -74, -113, -19, -14, -81, -19, -7, -15, -6, -11, 58, -17, 12, -6, 17, 49, 5, 69, 65, 68, 42, -22, 9, 19, 24, 31, -16, 21, -2, 7, 13, 19, 37, 39, 20, 17, -5, 44, 42, 36, 17, 37, 37, 34, 41, -2, 50, 29, 18, 22, 54, 72, 28, 33, 4, 25, 4, 3, 12, 21, 29, 36, 51, 36, 56, 94, 151, 106, 29, -13, -37, 3, 46, 5, 69, 90, 77, -5, -17, -24, 1, 13, 43, 90, 40, -40, -15, -8, 6, -21, 2, 28, 34, -28, -37, -48, -32, -26, -14, 12, -4, -30, -29, -17, -4, -4, 2, 21, 8, -19, -21, -30, -12, -6, 13, 34, -29, -3, -6, -2, 3, 6, 12, -12, 17, -34, -6, 33, 57, 97, 26, -3, -36, -40, -56, -62, -93, -117, 10, -60, 98, -35, -27, -23, -30, -18, -14, 41, -10, -31, -16, -26, -46, -47, -59, -67, -27, -15, -18, -12, -28, -6, -20, -5, -33, -19, 10, -7, 0, -3, 0, 7, -16, -4, 7, 9, 11, 20, 34, 46, 26, 0, -14, -1, 18, -6, -14, -21, 33, 151, 98, 159, 141, 149, 146, 70, 4, 3, 172, 143, 107, 112, 161, 21, -64, 66, 223, 123, 133, 62, 154, 124, -51, 11, 63, 100, 71, 37, 18, 30, -83, 42, 84, 55, -18, -16, -77, -30, -138, 13, 45, -51, -51, -61, -48, -16, -83, 12, -12, -24, -49, -37, -35, 17, -16, -86, 35, 21, -33, 3, -18, 43, 8, 0, 0, 0, 0, 0, 0, 0, 0, 33, 29, 8, -33, -50, -30, 24, 25, 71, 53, 31, -6, -18, -9, 30, 36, 46, 35, 18, -7, -4, 2, 21, 18, 32, 24, 7, -1, 2, -2, 8, 9, 21, 18, 9, 5, 10, 11, 2, 0, 30, 16, 26, 14, 16, 11, -4, 1, 0, 0, 0, 0, 0, 0, 0, 0, -15, -50, -20, -14, -31, -27, -26, -104, -17, 1, -25, -5, -37, -29, -20, -41, -28, -16, 28, 19, -6, -5, -23, -14, -9, 19, 30, 40, 37, 31, 19, -17, -6, 6, 30, 38, 35, 18, -3, 2, -15, 1, 2, 24, 21, 6, -14, -29, -29, -9, -13, 4, -1, -11, -16, -29, -63, -31, -9, 1, -12, -2, -32, -38, 0, -11, -14, 3, 10, -5, -7, 13, -1, 2, 3, -11, -2, -14, 1, -25, 13, 6, 0, 16, -4, 12, -3, 1, 19, 15, 11, 23, 14, 12, -1, 2, -4, 0, 13, 8, 12, 11, -3, -3, -14, 4, 7, 16, 12, 6, -5, -12, 5, -9, -11, 5, 11, -3, -3, -16, -22, -1, -14, 8, 7, 9, -19, -14, 24, 15, 20, 8, -3, -20, -8, 9, 25, 45, 31, 13, 18, 7, 2, -5, 20, 22, 20, 15, 11, 5, -5, -1, 22, 14, 20, 12, 15, 16, 4, 0, 15, 19, 28, 25, 22, 10, 0, -5, 7, 12, 12, 7, 6, 2, -12, -13, 6, 9, 20, 14, 8, -2, -8, 0, 15, 11, 13, 10, 4, 8, 0, -13, 32, 26, 10, 10, -20, 39, 4, 68, 54, 49, 82, 139, 126, 110, 105, -52, 9, 30, 63, 62, 110, 54, 11, 33, 28, 46, 43, 89, 100, 99, 85, 42, 31, 46, 37, 77, 49, 32, 47, 41, 3, -14, 49, 24, 50, 39, 52, 39, 27, 17, 16, 36, 22, -30, -40, -6, 12, 8, -1, -43, 30, 1, -19, -48, -112, -50, -59, -42, -42, -37, -34, -61, -45, -13, -7, -2, 2, 12, 25, -1, -44, -11, 25, 30, 44, 43, 28, 4, -47, 1, 33, 68, 72, 58, 26, 4, -55, -6, 39, 79, 81, 65, 23, 5, -46, -1, 49, 62, 63, 53, 17, 2, -38, 5, 25, 39, 36, 32, 7, -16, -39, -33, -13, -5, -22, -9, -38, -70, 110, 437, 451, 659, 1359, 0, 82, 276, 300, 573, 1120, 0, -23, -10, 5, 10, 26, 29, 32, 30, 34, -34, -19, 3, 4, 9, 14, 23, 29, 33, 33, 44, 50, 48, 67, -114, -31, 10, 5, 12, 1, 5, 13, 18, 15, 19, 23, 11, 25, 34, -13, -50, -146, -10, -9, -12, 5, -9, 1, 3, 2, 2, 6, 7, 9, 8, 3, 1, 6, 3, -2, -5, 1, 9, 34, 51, 123, 333, 45, 44, 43, 35, 38, 51, 46, 50, 41, -24, -1, -16, 8, 18, 34, 39, 43, 52, 53, 51, 46, 55, 38, -60, -53, -10, 6, -5, 10, 12, 12, 17, 20, 24, 29, 44, 38, 33, -13, -233, -47, -195, -60, -118, -104, -38, -65, -47, -28, -14, -7, -5, 6, 7, 28, 35, 41, 40, 50, 55, 49, 37, 10, 19, -10, -147, 54, 48, -7, 12, 13, 0, 124, -10, -7, -28, -21, -2, 0, 242, 140, 62, 33, 9, 5, 0, -5, -14, -41, -19, -36, -60, -67, -95, -3, -15, -15, -39, -40, -88, -21, 4045, -110, 7, -15, -30, -55, -74, -48, -88, -78, -75, -2, -13, -20, -29, -35, -56, -56, -152, -80, 21, 0, -1, 5, 19, 18, 0, -46, 69, 15, 24, 14, 14, 0, 0, -436, -682, 67, 44, 47, 9, 31, 21, 15, -1, 3, 5, -9, -12, -2, -7, -11, -30, -10, -2, -3, -12, -11, -64, -49, -7, 0, 0, 178, 33, -62, 0, -17, -5, -5, -1, 15, 7, 14, 15, 23, 16, 11, 12, 10, 4, -4, -16, -26, -33, -51, -52, -80, 25, -6, 6, 19, 15, 15};
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
        config.getVirtualKingMobilityPenalty()[0] = Arrays.stream(params, 979, 1007).toArray();
        config.getVirtualKingMobilityPenalty()[1] = Arrays.stream(params, 1007, 1035).toArray();
        config.setPawnAttackOnMinorThreatBonus(params[1035]);
        config.setPawnAttackOnRookThreatBonus(params[1036]);
        config.setPawnAttackOnQueenThreatBonus(params[1037]);
        config.setMinorAttackOnRookThreatBonus(params[1038]);
        config.setMinorAttackOnQueenThreatBonus(params[1039]);
        config.setRookAttackOnQueenThreatBonus(params[1040]);
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
        weights.add(initialConfig.getPawnAttackOnMinorThreatBonus());
        weights.add(initialConfig.getPawnAttackOnRookThreatBonus());
        weights.add(initialConfig.getPawnAttackOnQueenThreatBonus());
        weights.add(initialConfig.getMinorAttackOnRookThreatBonus());
        weights.add(initialConfig.getMinorAttackOnQueenThreatBonus());
        weights.add(initialConfig.getRookAttackOnQueenThreatBonus());
        tune(
                weights.stream().mapToInt(i -> i).toArray(),
                (params) -> {
                    EngineConfig config = EngineInitializer.loadDefaultConfig();
                    config.setPawnAttackOnMinorThreatBonus(params[0]);
                    config.setPawnAttackOnRookThreatBonus(params[1]);
                    config.setPawnAttackOnQueenThreatBonus(params[2]);
                    config.setMinorAttackOnRookThreatBonus(params[3]);
                    config.setMinorAttackOnQueenThreatBonus(params[4]);
                    config.setRookAttackOnQueenThreatBonus(params[5]);
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