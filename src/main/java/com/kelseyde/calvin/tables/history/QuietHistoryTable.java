package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    int[][][][] table = new int[2][Piece.COUNT][Square.COUNT][2];

    public QuietHistoryTable(EngineConfig config) {
        super(config.quietHistBonusMax.value,
                config.quietHistBonusScale.value,
                config.quietHistMalusMax.value,
                config.quietHistMalusScale.value,
                config.quietHistMaxScore.value);
    }

    public void update(Move move, Piece piece, int depth, long threats, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int threatIndex = Bits.contains(threats, move.to()) ? 1 : 0;
        int current = table[colourIndex][piece.index()][move.to()][threatIndex];
        int bonus = good ? bonus(depth) : malus(depth);
        int update = gravity(current, bonus);
        table[colourIndex][piece.index()][move.to()][threatIndex] = update;
    }

    public int get(Move move, Piece piece, long threats, boolean white) {
        int colourIndex = Colour.index(white);
        int threatIndex = Bits.contains(threats, move.to()) ? 1 : 0;
        return table[colourIndex][piece.index()][move.to()][threatIndex];
    }

    public void ageScores(boolean white) {
        int colourIndex = Colour.index(white);
        for (int from = 0; from < Piece.COUNT; from++) {
            for (int to = 0; to < Square.COUNT; to++) {
                for (int i = 0; i < 2; i++) {
                    table[colourIndex][from][to][i] /= 2;
                }
            }
        }
    }

    public void clear() {
        table = new int[2][Piece.COUNT][Square.COUNT][2];
    }

}
