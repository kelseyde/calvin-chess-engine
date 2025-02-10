package com.kelseyde.calvin.movegen.perft;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class EnPassantFunhouseTest extends PerftTest {

    private static final String FEN = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ";

    private static final String SUB_FOLDER = "en_passant_funhouse";

    @Test
    public void testDepthOne() {
        perft(1, 14);
    }

    @Test
    public void testDepthTwo() {
        perft(2, 191);
    }

    @Test
    public void testDepthThree() {
        perft(3, 2812);
    }

    @Test
    public void testDepthFour() {
        perft(4, 43238);
    }

    @Test
    public void testDepthFive() {
        perft(5, 674624);
    }

    @Test
    public void testDepthSix() {
        perft(6, 11030083);
    }

    @Override
    protected String getFen() {
        return FEN;
    }

    @Override
    protected String getSubFolder() {
        return SUB_FOLDER;
    }

}
