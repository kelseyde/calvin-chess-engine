package com.kelseyde.calvin.evaluation.eperft;

import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class StartingPositionEvalTest extends EPerftTest {

    @Getter
    private final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Getter
    private final String subFolder = "starting_position";

    @Test
    public void testDepthOne() {
        ePerft(1);
    }

    @Test
    public void testDepthTwo() {
        ePerft(2);
    }

    @Test
    public void testDepthThree() {
        ePerft(3);
    }

    @Test
    public void testDepthFour() {
        ePerft(4);
    }

    @Test
    public void testDepthFive() {
        ePerft(5);
    }

    @Test
    public void testDepthSix() {
        ePerft(6);
    }

}
