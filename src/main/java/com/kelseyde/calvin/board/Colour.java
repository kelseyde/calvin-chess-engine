package com.kelseyde.calvin.board;

public class Colour {

    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final int STRIDE = 6;

    public static int index(boolean white) {
        return white ? WHITE : BLACK;
    }

}
