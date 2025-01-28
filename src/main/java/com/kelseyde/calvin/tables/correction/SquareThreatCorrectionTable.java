package com.kelseyde.calvin.tables.correction;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;

public class SquareThreatCorrectionTable extends CorrectionHistoryTable {

    private short[][][] entries;

    public SquareThreatCorrectionTable() {
        this.entries = new short[2][Square.COUNT][2];
    }

    public void update(boolean white, long ourThreats, int depth, int score, int staticEval) {
        for (int sq = 0; sq < Square.COUNT; sq++) {
            boolean threatened = Bits.contains(ourThreats, sq);
            int oldValue = get(white, sq, threatened);
            int correction = correction(oldValue, staticEval, score, depth);
            put(white, sq, threatened, correction);
        }
    }

    public int getAll(boolean white, long ourThreats) {
        int total = 0;
        for (int sq = 0; sq < Square.COUNT; sq++) {
            boolean threatened = Bits.contains(ourThreats, sq);
            total += get(white, sq, threatened);
        }
        return total / 64;
    }

    public int get(boolean white, int square, boolean threatened) {
        int colourIndex = Colour.index(white);
        int threatIndex = threatened ? 1 : 0;
        return entries[colourIndex][square][threatIndex];
    }

    private void put(boolean white, int square, boolean threatened, int value) {
        int colourIndex = Colour.index(white);
        int threatenedIndex = threatened ? 1 : 0;
        entries[colourIndex][square][threatenedIndex] = (short) value;
    }

    @Override
    public void clear() {
        this.entries = new short[2][Square.COUNT][2];
    }

}
