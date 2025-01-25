package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.*;
import com.kelseyde.calvin.movegen.Attacks;

public class Cuckoo {

    private static long[] keyDiffs;
    private static Move[] moves;

    static {

        keyDiffs = new long[8192];
        moves = new Move[8192];
        int count = 0;

        final Piece[] pieces = { Piece.KNIGHT, Piece.BISHOP, Piece.ROOK, Piece.QUEEN, Piece.KING };
        final int[] colours = { Colour.WHITE, Colour.BLACK };

        for (Piece piece : pieces) {

            for (int colour : colours) {

                for (int from = 0; from < 63; from++) {

                    for (int to = from + 1; to < 64; to++) {

                        long attacks = switch (piece) {
                            case KNIGHT -> Attacks.knightAttacks(from);
                            case BISHOP -> Attacks.bishopAttacks(from, 0L);
                            case ROOK ->   Attacks.rookAttacks(from, 0L);
                            case QUEEN ->  Attacks.queenAttacks(from, 0L);
                            case KING ->   Attacks.kingAttacks(from);
                            default -> 0L;
                        };

                        if (!Bits.contains(attacks, to)) {
                            continue;
                        }

                        Move move = new Move(from, to);

                        long keyDiff = Key.moveKey(move, piece, colour == 0);

                        int slot = (int) h1(keyDiff);

                        while (true) {

                            long keyDiffTemp = keyDiffs[slot];
                            keyDiffs[slot] = keyDiff;
                            keyDiff = keyDiffTemp;

                            Move moveTemp = moves[slot];
                            moves[slot] = move;
                            move = moveTemp;

                            if (move == null) {
                                break;
                            }

                            slot = slot == h1(keyDiff) ? (int) h2(keyDiff) : (int) h1(keyDiff);

                        }

                        count++;

                    }

                }

            }

        }

        System.out.println(count);
        assert count == 3668;

    }

    private static long h1(long keyDiff) {
        return keyDiff % 8192;
    }

    private static long h2(long keyDiff) {
        return (keyDiff >> 16) % 8192;
    }

}
