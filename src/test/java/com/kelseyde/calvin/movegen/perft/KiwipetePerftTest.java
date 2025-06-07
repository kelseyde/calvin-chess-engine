package com.kelseyde.calvin.movegen.perft;

public class KiwipetePerftTest extends PerftTest {

    private static final String FEN = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";

    private static final String SUB_FOLDER = "kiwipete";

    public void testDepthOne() {
        perft(1, 48);
    }

    public void testDepthTwo() {
        perft(2, 2039);
    }

    public void testDepthThree() {
        perft(3, 97862);
    }

    public void testDepthFour() {
        perft(4, 4085603);
    }

    public void testDepthFive() {
        perft(5, 193690690);
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
