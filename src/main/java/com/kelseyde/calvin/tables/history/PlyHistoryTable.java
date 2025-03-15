package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class PlyHistoryTable extends AbstractHistoryTable {

    private static final int PLY_WINDOW_SIZE = 8;
    private static final int PLY_WINDOW_COUNT = 64;

    short[][][] table = new short[64][Square.COUNT][Square.COUNT];

    public PlyHistoryTable(EngineConfig config) {
        super((short) config.plyHistBonusMax(),
                (short) config.plyHistBonusScale(),
                (short) config.plyHistMalusMax(),
                (short) config.plyHistMalusScale(),
                (short) config.plyHistMaxScore());
    }

    public void update(int ply, int from, int to, int depth, boolean good) {
        short current = table[ply][from][to];
        short bonus = good ? bonus(depth) : malus(depth);
        short update = gravity(current, bonus);
        int plyIndex = plyIndex(ply);
        table[plyIndex][from][to] = update;
    }

    public short get(int ply, int from, int to) {
        int plyIndex = plyIndex(ply);
        return table[plyIndex][from][to];
    }

    public void clear() {
        table = new short[32][Piece.COUNT][Square.COUNT];
    }

    private int plyIndex(int ply) {
        return Math.min(ply / PLY_WINDOW_SIZE, PLY_WINDOW_COUNT - 1);
    }

}
