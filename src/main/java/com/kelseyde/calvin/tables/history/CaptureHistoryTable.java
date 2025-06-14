package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class CaptureHistoryTable extends AbstractHistoryTable {

    short[][][][] table = new short[2][Piece.COUNT][Square.COUNT][Piece.COUNT];

    public CaptureHistoryTable(EngineConfig config) {
        super(new HistoryBonus(
                    config.captHistBonusBase(),
                    config.captHistBonusScale(),
                    config.captHistBonusMoveMult(),
                    config.captHistBonusMax()),
              new HistoryBonus(
                    config.captHistMalusBase(),
                    config.captHistMalusScale(),
                    config.captHistMalusMoveMult(),
                    config.captHistMalusMax()),
                (short) config.captHistMaxScore());
    }

    public void add(Piece piece, int to, Piece captured, boolean white, int bonus) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int capturedIndex = captured.index();
        short current = table[colourIndex][pieceIndex][to][capturedIndex];
        short update = gravity(current, (short) bonus);
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
