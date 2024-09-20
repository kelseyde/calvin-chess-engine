package com.kelseyde.calvin.board;


/**
 * Stores the metadata for a given chess position - that is, the castling rights, en passant rights, the fifty-move counter
 * (the number of half-moves since the last capture or pawn move), and the last captured piece.
 * The game state history is stored by the {@link Board} to easily 'unmake' moves during search + evaluation.
 */
public class GameState {

    long key = 0L;
    long pawnKey = 0L;
    Piece captured;
    int enPassantFile = -1;
    int rights = Bits.INITIAL_CASTLING_RIGHTS;
    int halfMoveClock = 0;

    public GameState() {
    }

    public GameState(long key, long pawnKey, Piece captured, int enPassantFile, int rights, int halfMoveClock) {
        this.key = key;
        this.pawnKey = pawnKey;
        this.captured = captured;
        this.enPassantFile = enPassantFile;
        this.rights = rights;
        this.halfMoveClock = halfMoveClock;
    }

    public boolean isKingsideCastlingAllowed(boolean white) {
        long kingsideMask = white ? 0b0001 : 0b0100;
        return (rights & kingsideMask) == kingsideMask;
    }

    public boolean isQueensideCastlingAllowed(boolean white) {
        long queensideMask = white ? 0b0010 : 0b1000;
        return (rights & queensideMask) == queensideMask;
    }

    public long getKey() {
        return key;
    }

    public long getPawnKey() {
        return pawnKey;
    }

    public Piece getCaptured() {
        return captured;
    }

    public int getEnPassantFile() {
        return enPassantFile;
    }

    public int getRights() {
        return rights;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public void setPawnKey(long pawnKey) {
        this.pawnKey = pawnKey;
    }

    public void setCaptured(Piece captured) {
        this.captured = captured;
    }

    public void setEnPassantFile(int enPassantFile) {
        this.enPassantFile = enPassantFile;
    }

    public void setRights(int rights) {
        this.rights = rights;
    }

    public void setHalfMoveClock(int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;
    }

    public GameState copy() {
        return new GameState(key, pawnKey, captured, enPassantFile, rights, halfMoveClock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return key == gameState.key
                && pawnKey == gameState.pawnKey
                && enPassantFile == gameState.enPassantFile
                && rights == gameState.rights
                && halfMoveClock == gameState.halfMoveClock
                && captured == gameState.captured;
    }

}
