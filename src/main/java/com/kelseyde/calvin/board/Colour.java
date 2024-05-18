package com.kelseyde.calvin.board;

public enum Colour {

    WHITE(0, 0),
    BLACK(1, 6),
    ALL(2, 0);

    final int index;
    final int shift;

    Colour(int index, int shift) {
        this.index = index;
        this.shift = shift;
    }

    public int getIndex() {
        return index;
    }

    public int getShift() {
        return shift;
    }

    public static Colour of(boolean isWhite) {
        return isWhite ? WHITE : BLACK;
    }

    public static int shift(boolean isWhite) {
        return isWhite ? 0 : 6;
    }

}
