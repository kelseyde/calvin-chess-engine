package com.kelseyde.calvin.board;


/**
 * Stores the metadata for a given chess position - that is, the castling rights, en passant rights, the fifty-move counter
 * (the number of half-moves since the last capture or pawn move), and the last captured piece.
 * The game state history is stored by the {@link Board} to easily 'unmake' moves during search + evaluation.
 */
public class BoardState {

    public long key;
    public long pawnKey;
    public long[] nonPawnKeys;
    public long majorKey;
    public int enPassantFile;
    public int rights;
    public int halfMoveClock;
    public Piece captured;

    public BoardState() {
        this.key = 0L;
        this.pawnKey = 0L;
        this.nonPawnKeys = new long[2];
        this.majorKey = 0L;
        this.captured = null;
        this.enPassantFile = -1;
        this.rights = Castling.startpos();
        this.halfMoveClock = 0;
    }

    public BoardState(long key, long pawnKey, long[] nonPawnKeys, long majorKey, Piece captured, int enPassantFile, int rights, int halfMoveClock) {
        this.key = key;
        this.pawnKey = pawnKey;
        this.nonPawnKeys = nonPawnKeys;
        this.majorKey = majorKey;
        this.captured = captured;
        this.enPassantFile = enPassantFile;
        this.rights = rights;
        this.halfMoveClock = halfMoveClock;
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

    public void setNonPawnKeys(long[] nonPawnKeys) {
        this.nonPawnKeys = nonPawnKeys;
    }

    public void setMajorKey(long majorKey) {
        this.majorKey = majorKey;
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

    public BoardState copy() {
        long[] nonPawnKeysCopy = new long[]{nonPawnKeys[0], nonPawnKeys[1]};
        return new BoardState(key, pawnKey, nonPawnKeysCopy, majorKey, captured, enPassantFile, rights, halfMoveClock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardState boardState = (BoardState) o;
        return key == boardState.key
                && pawnKey == boardState.pawnKey
                && majorKey == boardState.majorKey
                && enPassantFile == boardState.enPassantFile
                && rights == boardState.rights
                && halfMoveClock == boardState.halfMoveClock
                && captured == boardState.captured;
    }

}
