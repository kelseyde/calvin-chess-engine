package com.kelseyde.calvin.generation.perft;

import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Getter
@Disabled
public class StartingPositionPerftTest extends PerftTest {

    private final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final String subFolder = "starting_position";

    @Test
    public void testDepthOne() {
        perft(1, 20);
    }

    @Test
    public void testDepthTwo() {
        perft(2, 400);
    }

    @Test
    public void testDepthThree() {
        perft(3, 8902);
    }

    @Test
    public void testDepthFour() {
        perft(4, 197281);
    }

    @Test
    public void testDepthFive() {
        perft(5, 4865609);
    }

    @Test
    public void testDepthSix() {
        perft(6, 119060324);
    }

    @Test
    public void testDepthSeven() {
        perft(7, 3195901860L);
    }

}
