package com.kelseyde.calvin.board.move;

public enum MoveType {

    STANDARD,
    EN_PASSANT,
    KINGSIDE_CASTLE,
    QUEENSIDE_CASTLE,
    PROMOTION;

    public boolean isCastling() {
        return this.equals(KINGSIDE_CASTLE) || this.equals(QUEENSIDE_CASTLE);
    }

    public boolean isEnPassant() {
        return this.equals(EN_PASSANT);
    }

    public boolean isPromotion() {
        return this.equals(PROMOTION);
    }

}
