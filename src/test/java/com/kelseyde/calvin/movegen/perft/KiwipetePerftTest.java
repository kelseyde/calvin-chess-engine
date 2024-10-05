package com.kelseyde.calvin.movegen.perft;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class KiwipetePerftTest extends PerftTest {

    private final String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";

    private final String subFolder = "kiwipete";

    @Test
    public void testDepthOne() {
        perft(1, 48);
    }

    @Test
    public void testDepthTwo() {
        perft(2, 2039);
    }

    @Test
    public void testDepthThree() {
        perft(3, 97862);
    }

    @Test
    public void testDepthFour() {
        perft(4, 4085603);
    }

    @Test
    public void testDepthFive() {
        perft(5, 193690690);
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
