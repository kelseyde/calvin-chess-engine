package com.kelseyde.calvin.tuning;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class MatchConfig {

    private Player player1;
    private Player player2;

    private int gameCount;
    private int threadCount;
    private int maxMoves;
    private int minThinkTimeMs;
    private int maxThinkTimeMs;


}
