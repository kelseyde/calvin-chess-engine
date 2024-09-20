package com.kelseyde.calvin.movegen.sperft;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class StartingPositionSPerftTest extends SPerftTest {

    private final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final String subFolder = "starting_position";

    @Test
    public void testDepthFive() {
        sPerft(5);
    }

    @Test
    public void testDepthTen() {
        sPerft(10);
    }

    @Test
    public void testDepthFourteen() {
        sPerft(14);
    }

    @Test
    public void testDepthFifteen() {
        sPerft(15);
    }

    @Test
    public void testDepthSixteen() {
        sPerft(16);
    }

    @Test
    public void testDepthSeventeen() {
        sPerft(17);
    }

    @Test
    public void testDepthTwenty() {
        sPerft(20);
    }

    @Override
    protected String getFen() {
        return fen;
    }

    @Override
    protected String getSubFolder() {
        return subFolder;
    }
}
