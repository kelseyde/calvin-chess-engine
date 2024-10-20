package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class CaptureHistoryTable extends AbstractHistoryTable {

    int[][][][][] table = new int[2][Piece.COUNT][Square.COUNT][Piece.COUNT][4];

    public CaptureHistoryTable(EngineConfig config) {
        super(config.captHistBonusMax.value,
                config.captHistBonusScale.value,
                config.captHistMalusMax.value,
                config.captHistMalusScale.value,
                config.captHistMaxScore.value);
    }

    public void update(Move move, Piece piece, Piece captured, long threats, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int capturedIndex = captured.index();
        int from = move.from();
        int to = move.to();
        int threatIndex = threatIndex(from, to, threats);
        int current = table[colourIndex][pieceIndex][to][capturedIndex][threatIndex];
        int bonus = good ? bonus(depth) : malus(depth);
        int update = gravity(current, bonus);
        table[colourIndex][pieceIndex][to][capturedIndex][threatIndex] = update;
    }

    public int get(Move move, Piece piece, Piece captured, long threats, boolean white) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int capturedIndex = captured.index();
        int from = move.from();
        int to = move.to();
        int threatIndex = threatIndex(from, to, threats);
        return table[colourIndex][pieceIndex][to][capturedIndex][threatIndex];
    }

    public void clear() {
        table = new int[2][Piece.COUNT][Square.COUNT][Piece.COUNT][4];
    }

}
