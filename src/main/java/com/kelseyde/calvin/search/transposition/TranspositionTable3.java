package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

/*

value: 64 bits

score: 32 bits
move: 16 bits (short)
flag: 4 bits (3 bits + 1 bit padding)
depth: 12 bits (max depth = 265 = 8 bits + 4 bit padding)
== 59


    private static final int SCORE = 45; //48
    private static final int MOVE = 11; //14
    private static final int FLAG = 9; //12
    DEPTH //8

// SCORE,MOVE,FLAG,DEPTH

 */
public class TranspositionTable3 implements TT {

    private static final int TABLE_SIZE_MB = 64;
    private static final int BUCKET_SIZE = 4;

    private static final int FLAG = 9; //12
    private static final int MOVE = 11; //14
    private static final int SCORE = 45; //48

    private long[] keys;

    private long[] values;

    private final Board board;

    public TranspositionTable3(Board board) {
        this.board = board;
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        long maxEntries = tableSizeBytes / 16; //two longs per entry and one long has 8 bytes: 2 * 8 = 16
        keys = new long[(int) maxEntries];
        values = new long[(int) maxEntries];
    }

    @Override
    public final Transposition get() {

        long key = board.getGameState().getZobristKey();
        long value = 0;
        int index = createIndex(key);
        for (int i = 0; i < BUCKET_SIZE; i++) {
            long storedKey = keys[index + i];
            long hashValue = values[index + i];
            if ((storedKey ^ hashValue) == key) {
                value = hashValue;
            }
        }

        if (value != 0) {
            int depth = getDepth(value);
            int flagValue = getFlag(value);
            NodeType flag = NodeType.valueOf(flagValue);
            int moveValue = getMove(value);
            Move move = moveValue > 0 ? new Move((short) moveValue) : null;
            int score = getScore(value);
            return new Transposition(key, flag, move, depth, score);
        }
        return null;

    }

    @Override
    public final void put(NodeType type, int depth, Move move, int score) {

        long key = board.getGameState().getZobristKey();
        int flag = NodeType.value(type);
        short moveValue = move != null ? move.getValue() : 0;
        long entry = createValue(score, moveValue, flag, depth);
        int entryStart = createIndex(key);

        int replacedMinDepth = Integer.MAX_VALUE;
        int replacedDepth = -1;

        for (int i = entryStart; i < entryStart + BUCKET_SIZE; i++) {

            long storedKey = keys[i];

            if (storedKey == 0) {
                replacedMinDepth = 0;
                replacedDepth = i;
                break;
            }

            long storedValue = values[i];
            int storedDepth = getDepth(storedValue);

            if ((storedKey ^ storedValue) == key) {

                // Minimize writes in the shared static arrays, because in multi-threaded case it impacts performance.
                if (entry == storedValue) {
                    return;
                }

                if (depth >= storedDepth) {
                    replacedMinDepth = storedDepth;
                    replacedDepth = i;
                    break;

                } else {
                    return;
                }
            }

            // keep the lowest depth and its index
            if (storedDepth < replacedMinDepth) {
                replacedMinDepth = storedDepth;
                replacedDepth = i;
            }
        }

        keys[replacedDepth] = key ^ entry;
        values[replacedDepth] = entry;

    }

    @Override
    public void clear() {
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        long maxEntries = tableSizeBytes / 16; //two longs per entry and one long has 8 bytes: 2 * 8 = 16
        keys = new long[(int) maxEntries];
        values = new long[(int) maxEntries];
    }

    /**
     * Gets the index to the BUCKET of 4 entries
     */
    private int createIndex(final long key) {
        long index = (int) (key ^ (key >>> 32));
        if (index < 0) index = -index;
        index = index % (keys.length - 3);
        index = 4 * (index / 4);
        return (int) index;
    }

    // SCORE,MOVE,FLAG,DEPTH
    private static long createValue(long score, long move, long flag, long depth) {
        return score << SCORE | move << MOVE | flag << FLAG | depth;
    }

    private static int getScore(final long value) {
        return (int) (value >> SCORE);
    }

    private static int getDepth(final long value) {
        return (int) (value & 0xff);
    }

    private static int getFlag(final long value) {
        return (int) (value >>> FLAG & 3); //...00000011 - last 2 right bits after the shift
    }

    //21 bits - 0x3fffff, 24 bits - 0x3ffffff
    private static int getMove(final long value) {
        return (int) (value >>> MOVE & 0x3fffff); //1111111111111111111111 binary or 4194303 decimal
    }

}
