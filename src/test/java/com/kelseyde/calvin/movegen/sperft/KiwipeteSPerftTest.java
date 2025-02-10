package com.kelseyde.calvin.movegen.sperft;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class KiwipeteSPerftTest extends SPerftTest {

    private static final String FEN = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";

    private static final String SUB_FOLDER = "kiwipete";

    @Test
    public void testDepthFive() {
        sPerft(5);
    }

    @Test
    public void testDepthTen() {
        sPerft(10);
    }

    @Test
    public void testDepthFifteen() {
        sPerft(15);
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
