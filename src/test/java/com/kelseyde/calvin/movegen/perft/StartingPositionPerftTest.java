package com.kelseyde.calvin.movegen.perft;

public class StartingPositionPerftTest extends PerftTest {

    private static final String FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final String SUB_FOLDER = "starting_position";

    @Override
    protected String getFen() {
        return FEN;
    }

    @Override
    protected String getSubFolder() {
        return SUB_FOLDER;
    }

    public void testDepthOne() {
        perft(1, 20);
    }

    public void testDepthTwo() {
        perft(2, 400);
    }

    public void testDepthThree() {
        perft(3, 8902);
    }

    public void testDepthFour() {
        perft(4, 197281);
    }

    public void testDepthFive() {
        perft(5, 4865609);
    }

    public void testDepthSix() {
        perft(6, 119060324);
    }

    public void testDepthSeven() {
        perft(7, 3195901860L);
    }

}
