package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.*;
import com.kelseyde.calvin.movegen.Attacks;

public class Cuckoo {

    private static final long[] keys;
    private static final Move[] moves;

    public static int h1(long h) {
        return ((int) (h >> 32)) & 0x1FFF;
    }

    public static int h2(long h) {
        return ((int) (h >> 48)) & 0x1FFF;
    }

    static {

        keys = new long[8192];
        moves = new Move[8192];
        int count = 0;

        final Piece[] pieces = { Piece.KNIGHT, Piece.BISHOP, Piece.ROOK, Piece.QUEEN, Piece.KING };
        final int[] colours = { Colour.WHITE, Colour.BLACK };

        for (Piece piece : pieces) {
            for (int colour : colours) {
                for (int from = 0; from < 64; from++) {
                    for (int to = from + 1; to < 64; to++) {

                        final boolean white = colour == Colour.WHITE;
                        final long attacks = Attacks.attacks(from, piece, white, 0L);

                        if (!Bits.contains(attacks, to))
                            continue;

                        Move move = new Move(from, to);
                        long keyDiff = Key.moveKey(move, piece, white);
                        int slot = h1(keyDiff);

                        while (true) {

                            long keyDiffTemp = keys[slot];
                            keys[slot] = keyDiff;
                            keyDiff = keyDiffTemp;

                            Move moveTemp = moves[slot];
                            moves[slot] = move;
                            move = moveTemp;

                            if (move == null)
                                break;

                            slot = slot == h1(keyDiff) ? h2(keyDiff) : h1(keyDiff);

                        }
                        count++;
                    }
                }
            }
        }

        if (count != 3668)
            throw new IllegalStateException("Failed to initialise cuckoo tables");

    }

    public static long[] keys() {
        return keys;
    }

    public static Move[] moves() {
        return moves;
    }

}
