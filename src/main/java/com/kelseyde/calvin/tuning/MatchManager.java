package com.kelseyde.calvin.tuning;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class MatchManager {

    private final MatchConfig config;

    int player1Wins = 0;
    int player2Wins = 0;
    int draws = 0;

    public MatchManager(MatchConfig config) {
        this.config = config;
    }

    public MatchResult run() {

        MatchConfig configPerMatch = config.toBuilder()
                .gameCount(config.getGameCount() / config.getThreadCount())
                .build();
        List<CompletableFuture<MatchResult>> futures = IntStream.range(0, config.getThreadCount())
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> new Match(configPerMatch).run()))
                .toList();
        futures.forEach(f -> f.thenAccept(this::updateResults));
        futures.forEach(CompletableFuture::join);

        System.out.println("Match over! Results:");
        System.out.printf("Player 1 wins: %s%n",  player1Wins);
        System.out.printf("Player 2 wins: %s%n", player2Wins);
        System.out.printf("Draws: %s%n", draws);
        return new MatchResult(player1Wins, player2Wins, draws);
    }

    private void updateResults(MatchResult result) {
        player1Wins += result.getPlayer1Wins();
        player2Wins += result.getPlayer2Wins();
        draws += result.getDraws();
        System.out.printf("Current standings: Player 1 wins: %s, Player 2 wins: %s, Draws: %s%n",
                player1Wins, player2Wins, draws);
    }

}
