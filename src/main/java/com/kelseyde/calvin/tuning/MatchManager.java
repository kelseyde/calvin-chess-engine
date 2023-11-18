package com.kelseyde.calvin.tuning;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Slf4j
public class MatchManager {

    private final MatchConfig config;

    int player1Wins = 0;
    int player2Wins = 0;
    int draws = 0;

    public MatchManager(MatchConfig config) {
        this.config = config;
    }

    public MatchResult run() {

        List<CompletableFuture<MatchResult>> futures = IntStream.range(0, 9)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> new Match(config).run()))
                .toList();
        futures.forEach(f -> f.thenAccept(this::updateResults));
        futures.forEach(CompletableFuture::join);

        log.info("Match over! Results:");
        log.info("Player 1 wins: {}",  player1Wins);
        log.info("Player 2 wins: {}", player2Wins);
        log.info("Draws: {}", draws);
        return new MatchResult(player1Wins, player2Wins, draws);
    }

    private void updateResults(MatchResult result) {
        player1Wins += result.getPlayer1Wins();
        player2Wins += result.getPlayer2Wins();
        draws += result.getDraws();
        log.info("Current standings: Player 1 wins: {}, Player 2 wins: {}, Draws: {}",
                player1Wins, player2Wins, draws);
    }

}
