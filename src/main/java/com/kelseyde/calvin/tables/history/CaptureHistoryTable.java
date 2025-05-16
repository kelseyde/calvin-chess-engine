package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class CaptureHistoryTable extends AbstractHistoryTable {

    short[][][][] table = new short[2][Piece.COUNT][Square.COUNT][Piece.COUNT];

    public CaptureHistoryTable(EngineConfig config) {
        super((short) config.captHistBonusMax(),
                (short) config.captHistBonusScale(),
                (short) config.captHistMalusMax(),
                (short) config.captHistMalusScale(),
                (short) config.captHistMoveCountMult(),
                (short) config.captHistMaxScore());
    }

    public void update(Piece piece, int to, Piece captured, int depth, int moveCount, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int capturedIndex = captured.index();
        short current = table[colourIndex][pieceIndex][to][capturedIndex];
        short bonus = good ? bonus(depth, moveCount) : malus(depth, moveCount);
        short update = gravity(current, bonus);
        table[colourIndex][pieceIndex][to][capturedIndex] = update;
    }

    public int get(Piece piece, int to, Piece captured, boolean white) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int capturedIndex = captured.index();
        return table[colourIndex][pieceIndex][to][capturedIndex];
    }

    public void clear() {
        table = new short[2][Piece.COUNT][Square.COUNT][Piece.COUNT];
    }

}
