package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.bot.EngineConfiguration;
import com.kelseyde.calvin.search.Searcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MatchTest {

    @Test
    public void testMatch() {
        EngineConfiguration config = EngineConfiguration.builder().build();

        MatchConfig matchConfig = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(config, new Searcher(config))))
                .player2(() -> new Player("player2", new CalvinBot(config, new Searcher2(config))))
                .gameCount(50)
                .maxMoves(100)
                .minThinkTimeMs(35)
                .maxThinkTimeMs(75)
                .threadCount(1)
                .build();

        MatchManager match = new MatchManager(matchConfig);

        match.run();

    }

    @Test
    public void testSlowMatch() {
        EngineConfiguration config = EngineConfiguration.builder().build();

        MatchConfig matchConfig = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(config, new Searcher(config))))
                .player2(() -> new Player("player2", new CalvinBot(config, new Searcher2(config))))
                .gameCount(16)
                .maxMoves(100)
                .minThinkTimeMs(250)
                .maxThinkTimeMs(550)
                .threadCount(1)
                .build();

        MatchManager match = new MatchManager(matchConfig);

        match.run();

    }

    @Test
    public void testMatchTunedPSTs() {

        EngineConfiguration oldConfig = EngineConfiguration.builder().build();
        EngineConfiguration newConfig = EngineConfiguration.builder()
                .pawnMgTable(new int[] {
                        0, 0, 0, 0, 0, 0, 0, 0,
                        -24, -17, -9, -10, 0, 18, 22, -14,
                        -22, -19, -2, 10, 15, 20, 17, 4,
                        -19, -12, 3, 15, 21, 24, 1, -2,
                        -17, -9, 2, 18, 23, 22, 0, -3,
                        -21, -13, -2, 5, 12, 15, 12, 0,
                        -24, -14, -13, -9, 2, 13, 13, -20,
                        0, 0, 0, 0, 0, 0, 0, 0,
                })
                .pawnEgTable(new int[] {
                        0, 0, 0, 0, 0, 0, 0, 0,
                        10, 9, 3, -11, 3, 1, -3, -7,
                        8, 9, -1, -11, -6, -6, -1, -7,
                        20, 14, 1, -16, -14, -10, 6, 0,
                        18, 14, -2, -13, -12, -7, 4, -2,
                        10, 8, -3, -9, -2, -3, -1, -8,
                        12, 9, 7, -13, 6, 4, -1, -4,
                        0, 0, 0, 0, 0, 0, 0, 0,
                })
                .knightMgTable(new int[]{
                        -111, -27, -42, -31, -16, -19, -20, -71,
                        -36, -19, -2, 13, 14, 17, -9, -14,
                        -24, 2, 18, 42, 47, 33, 29, -1,
                        -6, 22, 33, 47, 47, 47, 34, 15,
                        -3, 17, 36, 37, 45, 46, 34, 13,
                        -22, 4, 17, 36, 45, 27, 22, -1,
                        -33, -9, 4, 11, 12, 12, -6, -15,
                        -123, -30, -41, -22, -14, -13, -26, -69,
                })
                .knightEgTable(new int[]{
                        -57, -41, -10, -8, -7, -21, -35, -69,
                        -15, -2, 13, 7, 7, 6, -10, -15,
                        -6, 11, 25, 27, 27, 13, 5, -12,
                        6, 15, 37, 37, 39, 34, 16, -4,
                        6, 21, 35, 38, 39, 29, 21, -1,
                        -9, 15, 19, 33, 24, 14, 4, -9,
                        -24, -1, 4, 9, 4, 3, -12, -16,
                        -61, -40, -7, -10, -7, -17, -33, -63,
                })
                .bishopMgTable(new int[] {
                        -14, 10, -7, -19, -12, -16, -14, 5,
                        5, 4, 5, -13, 2, -2, 23, -5,
                        -4, 5, -2, 1, -5, 10, 7, 23,
                        -2, -6, -6, 14, 12, -13, 3, 1,
                        -9, 0, -10, 12, 3, -8, -3, 7,
                        4, 4, 5, -5, 3, 4, 8, 20,
                        6, 13, 9, -4, -3, -1, 17, 1,
                        11, 10, 3, -27, -13, -18, -4, -9,
                })
                .bishopEgTable(new int[]{
                        -8, 14, -16, 4, -4, 3, -3, -28,
                        -3, -4, -3, 4, 1, -8, -2, -11,
                        11, 12, 6, 4, 10, 5, 2, 4,
                        10, 8, 7, 0, -1, 7, 3, 1,
                        7, 7, 5, 4, -7, 2, 3, 2,
                        10, 3, 0, 2, 5, 0, 1, 4,
                        -12, -9, -14, 2, 1, 0, 0, -10,
                        -8, -10, -13, 4, 6, 4, -1, -16,
                })
                .rookMgTable(new int[] {
                        -5, -11, -11, -7, 5, -4, 2, -5,
                        -22, -9, -10, -10, -1, 0, 14, -3,
                        -22, -12, -13, -7, 5, 11, 42, 22,
                        -20, -13, -9, -6, 0, 5, 31, 20,
                        -14, -10, -5, 1, 0, 9, 23, 18,
                        -19, -10, -7, 0, 6, 17, 41, 26,
                        -21, -19, -6, -6, -1, 0, 18, -1,
                        -4, -6, -5, 2, 11, -1, 7, 6,
                })
                .rookEgTable(new int[]{
                        5, 7, 12, 3, -4, 4, 5, -1,
                        10, 15, 15, 8, -1, 2, -2, 2,
                        6, 4, 6, 2, -7, -8, -18, -19,
                        8, 4, 8, 5, -4, -1, -11, -17,
                        7, 5, 9, 1, -2, -7, -12, -14,
                        7, 10, 1, -5, -9, -11, -17, -13,
                        14, 17, 12, 4, -3, 0, -4, 4,
                        0, 0, 7, -1, -9, -1, -3, -9,
                })
                .queenMgTable(new int[] {
                        -13, -15, -16, -2, -9, -27, 1, -2,
                        4, -5, 6, 0, 3, 5, 15, 38,
                        -2, -2, -3, -3, -6, 8, 28, 42,
                        -4, -12, -9, -4, -5, 0, 13, 22,
                        -6, -8, -11, -11, -5, 1, 7, 17,
                        -1, -1, -8, -6, -1, 4, 18, 28,
                        -7, -15, 3, 8, 5, 0, 9, 29,
                        -10, -20, -10, 1, -6, -35, -14, 11,
                })
                .queenEgTable(new int[]{
                        -18, -10, 0, -8, -4, -3, -28, 1,
                        -19, -14, -19, -2, -1, -13, -32, -4,
                        -13, -4, -1, -2, 14, 14, -10, 6,
                        -11, 5, -5, 7, 17, 24, 29, 22,
                        0, 0, 6, 16, 14, 18, 18, 33,
                        -12, -8, 8, 6, 10, 17, 12, 15,
                        -11, -6, -15, -14, -5, -3, -27, 3,
                        -7, -4, -1, -8, 3, 21, 13, -6,
                })
                .kingMgTable(new int[] {
                        19, 27, 4, -74, -15, -64, 14, 32,
                        -16, -22, -39, -70, -79, -59, -21, 2,
                        -80, -74, -107, -110, -114, -123, -86, -91,
                        -116, -115, -135, -165, -160, -149, -150, -173,
                        -81, -97, -126, -154, -158, -128, -150, -157,
                        -71, -45, -101, -110, -94, -103, -77, -81,
                        56, -17, -37, -62, -63, -49, -8, 10,
                        28, 45, 11, -64, -4, -54, 27, 43,
                })
                .kingEgTable(new int[] {
                        -58, -32, -11, 10, -20, 5, -22, -63,
                        -8, 19, 29, 40, 46, 34, 17, -8,
                        10, 40, 56, 65, 67, 61, 43, 25,
                        19, 52, 72, 87, 83, 75, 64, 44,
                        9, 45, 69, 83, 86, 73, 66, 41,
                        8, 35, 55, 65, 62, 56, 43, 22,
                        -29, 13, 28, 37, 39, 31, 13, -13,
                        -66, -39, -14, 5, -17, 2, -26, -67,
                })
                .build();

        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(oldConfig, new Searcher(oldConfig))))
                .player2(() -> new Player("player2", new CalvinBot(newConfig, new Searcher2(newConfig))))
                .gameCount(50)
                .maxMoves(100)
                .minThinkTimeMs(35)
                .maxThinkTimeMs(75)
                .threadCount(1)
                .build();

        MatchManager match = new MatchManager(config);

        match.run();

    }

    @Test
    public void testTuneEverything() {

        EngineConfiguration oldConfig = EngineConfiguration.builder().build();

        int[] pawnMgTable = decrement(oldConfig.getPawnMgTable());
        int[] pawnEgTable = decrement(oldConfig.getPawnEgTable());
        int[] knightMgTable = decrement(oldConfig.getKnightMgTable());
        int[] knightEgTable = decrement(oldConfig.getKnightEgTable());
        int[] bishopMgTable = decrement(oldConfig.getBishopMgTable());
        int[] bishopEgTable = decrement(oldConfig.getBishopEgTable());
        int[] rookMgTable = decrement(oldConfig.getRookMgTable());
        int[] rookEgTable = decrement(oldConfig.getRookEgTable());
        int[] queenMgTable = decrement(oldConfig.getQueenMgTable());
        int[] queenEgTable = decrement(oldConfig.getQueenEgTable());
        int[] kingMgTable = decrement(oldConfig.getKingMgTable());
        int[] kingEgTable = decrement(oldConfig.getKingEgTable());

        EngineConfiguration newConfig = EngineConfiguration.builder()
                .pawnMgTable(pawnMgTable)
                .pawnEgTable(pawnEgTable)
                .knightMgTable(knightMgTable)
                .knightEgTable(knightEgTable)
                .bishopMgTable(bishopMgTable)
                .bishopEgTable(bishopEgTable)
                .rookMgTable(rookMgTable)
                .rookEgTable(rookEgTable)
                .queenMgTable(queenMgTable)
                .queenEgTable(queenEgTable)
                .kingMgTable(kingMgTable)
                .kingEgTable(kingEgTable)
                .build();

        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(oldConfig, new Searcher(oldConfig))))
                .player2(() -> new Player("player2", new CalvinBot(newConfig, new Searcher2(newConfig))))
                .gameCount(50)
                .maxMoves(100)
                .minThinkTimeMs(35)
                .maxThinkTimeMs(75)
                .threadCount(1)
                .build();

        MatchManager match = new MatchManager(config);

        match.run();

    }

    private int[] decrement(int[] params) {
        int[] newParams = new int[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = params[i] - 1;
        }
        return newParams;
    }

}