package com.kelseyde.calvin.movegen.perft;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MadnessTest extends PerftTest{

    @Override
    protected String getFen() {
        return "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
    }

    @Override
    protected String getSubFolder() {
        return "madness";
    }

    @Test
    public void depth1() {
        perft(1, 6);
    }

    @Test
    public void depth2() {
        perft(2, 264);
    }

    @Test
    public void depth3() {
        perft(3, 9467);
    }

    @Test
    public void depth4() {
        perft(4, 422333);
    }

    @Test
    public void depth5() {
        perft(5, 15833292);
    }

    @Test
    public void depth6() {
        perft(6, 706045033);
    }

}
