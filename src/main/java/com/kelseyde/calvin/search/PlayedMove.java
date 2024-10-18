package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.tables.tt.HashFlag;

public class PlayedMove {

    public Move move;
    public Piece piece;
    public Piece captured;
    public int score;
    public boolean quiet;
    public boolean capture;

    public PlayedMove(Move move, Piece piece, Piece captured) {
        this.move = move;
        this.piece = piece;
        this.captured = captured;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public boolean isCapture() {
        return capture;
    }

}
