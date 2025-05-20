package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.Search;

public class PlyHistoryTable extends AbstractHistoryTable {

    private static final int BUCKET_COUNT = bucketCount();

    short[][][][] table = new short[2][BUCKET_COUNT][Piece.COUNT][Square.COUNT];

    public PlyHistoryTable(EngineConfig config) {
        super((short) config.plyHistBonusMax(),
                (short) config.plyHistBonusScale(),
                (short) config.plyHistMalusMax(),
                (short) config.plyHistMalusScale(),
                (short) config.plyHistMaxScore());
    }

    public void update(Move move, Piece piece, int depth, int ply, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int bucket = plyBucket(ply);
        short current = table[colourIndex][bucket][piece.index()][move.to()];
        short bonus = good ? bonus(depth, ply) : malus(depth, ply);
        short update = gravity(current, bonus);
        table[colourIndex][bucket][piece.index()][move.to()] = update;
    }

    public short get(Move move, Piece piece, int ply, boolean white) {
        int colourIndex = Colour.index(white);
        int bucket = plyBucket(ply);
        return table[colourIndex][bucket][piece.index()][move.to()];
    }

    public void clear() {
        table = new short[2][BUCKET_COUNT][Piece.COUNT][Square.COUNT];
    }

    private short bonus(int depth, int ply) {
        // Decrease weight as ply increases
        return (short) (bonus(depth) / Math.max(ply, 1));
    }

    private short malus(int depth, int ply) {
        // Decrease weight as ply increases
        return (short) (malus(depth) / Math.max(ply, 1));
    }

    private static int bucketCount() {
        return plyBucket(Search.MAX_DEPTH);
    }

    private static int plyBucket(int ply) {
        if (ply <= 0 || ply > Search.MAX_DEPTH) {
            return 0;
        }
        return (int) (Math.log(ply) / Math.log(2));
    }

}
