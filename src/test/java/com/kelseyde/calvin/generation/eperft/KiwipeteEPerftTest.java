package com.kelseyde.calvin.generation.eperft;

import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Getter
@Disabled
public class KiwipeteEPerftTest extends EPerftTest {

    private final String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";

    private final String subFolder = "kiwipete";

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
