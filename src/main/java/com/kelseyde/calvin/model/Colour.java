package com.kelseyde.calvin.model;

public enum Colour {

    WHITE,
    BLACK;

    public boolean isSameColour(Colour colour) {
        return this.equals(colour);
    }

    public boolean isOppositeColour(Colour colour) {
        return !this.equals(colour);
    }

    public Colour oppositeColour() {
        return this.equals(WHITE) ? BLACK : WHITE;
    }

}
