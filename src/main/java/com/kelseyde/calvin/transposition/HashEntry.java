package com.kelseyde.calvin.transposition;

import com.kelseyde.calvin.board.Move;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Individual entry in the transposition table containing the 64-bit zobrist key, and a 64-bit encoding of the score,
 * move, flag and depth:
 * - score: 32 bits     (-1000000-1000000, capturing negative -> positive checkmate score)
 * - move: 16 bits      (0-5 = start square, 6-11 = end square, 12-15 = special move flag, see {@link Move})
 * - flag: 4 bits       (0-2, capturing three possible flag values + 1 bit padding)
 * - depth: 12 bits     (0-265, max depth = 256 = 8 bits + 4 bit padding)
 */
@Data
@AllArgsConstructor
public class HashEntry {
    private long key;
    private byte age;
    private long value;

    private static final long CLEAR_SCORE_MASK = 0xffffffffL;

    public int getScore() {
        long score = value >>> 32;
        return (int) score;
    }

    public Move getMove() {
        long move = (value >> 16) & 0xffff;
        return move > 0 ? new Move((short) move) : null;
    }

    public HashFlag getFlag() {
        long flag = (value >>> 12) & 0xf;
        return HashFlag.valueOf((int) flag);
    }

    public int getDepth() {
        return (int) value & 0xfff;
    }

    public void incrementAge() {
        if (this.age < Byte.MAX_VALUE) {
            this.age++;
        }
    }

    public static HashEntry of(long zobristKey, int score, Move move, HashFlag flag, int depth) {
        long moveValue = move != null ? move.value() : 0;
        long flagValue = HashFlag.value(flag);
        long value = (long) score << 32 | moveValue << 16 | flagValue << 12 | depth;
        byte age = 0;
        return new HashEntry(zobristKey, age, value);
    }

    public static HashEntry withScore(HashEntry entry, int score) {
        long value = (entry.getValue() & CLEAR_SCORE_MASK) | (long) score << 32;
        return new HashEntry(entry.getKey(), entry.getAge(), value);
    }

}
