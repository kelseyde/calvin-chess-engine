package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    int[][][][] table = new int[2][Piece.COUNT][Square.COUNT][4];

    public QuietHistoryTable(EngineConfig config) {
        super(config.quietHistBonusMax.value,
                config.quietHistBonusScale.value,
                config.quietHistMalusMax.value,
                config.quietHistMalusScale.value,
                config.quietHistMaxScore.value);
    }

    public void update(Move move, Piece piece, long threats, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int from = move.from();
        int to = move.to();
        int threatIndex = threatIndex(from, to, threats);
        int current = table[colourIndex][pieceIndex][to][threatIndex];
        int bonus = bonus(depth);
        if (!good) bonus = -bonus;
        int update = gravity(current, bonus);
        table[colourIndex][pieceIndex][to][threatIndex] = update;
    }

    public int get(Move historyMove, Piece piece, long threats, boolean white) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int from = historyMove.from();
        int to = historyMove.to();
        int threatIndex = threatIndex(from, to, threats);
        return table[colourIndex][pieceIndex][to][threatIndex];
    }

    public void ageScores(boolean white) {
        int colourIndex = Colour.index(white);
        for (int pieceIndex = 0; pieceIndex < Piece.COUNT; pieceIndex++) {
            for (int to = 0; to < Square.COUNT; to++) {
                table[colourIndex][pieceIndex][to][0] /= 2;
                table[colourIndex][pieceIndex][to][1] /= 2;
                table[colourIndex][pieceIndex][to][2] /= 2;
                table[colourIndex][pieceIndex][to][3] /= 2;
            }
        }
    }

    public void clear() {
        table = new int[2][Piece.COUNT][Square.COUNT][4];
    }

}
