package com.kelseyde.calvin.tuning;

import lombok.Builder;
import lombok.Data;

import java.util.function.Supplier;

@Data
@Builder(toBuilder = true)
public class MatchConfig {

    private Supplier<Player> player1;
    private Supplier<Player> player2;

    private int gameCount;
    private int threadCount;
    private int maxMoves;
    private int minThinkTimeMs;
    private int maxThinkTimeMs;


}
