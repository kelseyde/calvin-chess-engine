package com.kelseyde.calvin.generation.eperft;

import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Getter
@Disabled
public class StartingPositionEPerftTest extends EPerftTest {

    private final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

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
