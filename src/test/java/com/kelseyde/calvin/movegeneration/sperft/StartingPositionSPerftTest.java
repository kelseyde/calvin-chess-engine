package com.kelseyde.calvin.movegeneration.sperft;

import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class StartingPositionSPerftTest extends SPerftTest {

    @Getter
    private final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Getter
    private final String subFolder = "starting_position";

    @Test
    public void testDepthFive() {
        sPerft(5);
    }

    @Test
    public void testDepthTen() {
        sPerft(10);
    }

}
