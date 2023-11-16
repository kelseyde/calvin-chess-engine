package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Move;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Individual entry in the hash table containing the 64-bit zobrist key, and a 64-bit encoding of the score, move,
 * flag and depth:
 * - score: 32 bits     (-1000000-1000000, capturing negative -> positive checkmate score)
 * - move: 16 bits      (0-5 = start square, 6-11 = end square, 12-15 = special move flag, see {@link Move})
 * - flag: 4 bits       (0-2, capturing three possible flag values + 1 bit padding)
 * - depth: 12 bits     (0-265, max depth = 256 = 8 bits + 4 bit padding)
 * SCORE_MASK =   0b1111111111111111111111111111111100000000000000000000000000000000L;
 * MOVE_MASK =    0b0000000000000000000000000000000011111111111111110000000000000000L;
 * FLAG_MASK =    0b0000000000000000000000000000000000000000000000001111000000000000L;
 * DEPTH_MASK =   0b0000000000000000000000000000000000000000000000000000111111111111L;
 */
public record TranspositionEntry(long key, long value) {

    private static final long CLEAR_SCORE_MASK = 0xffffffffL;

    public int getScore() {
        long score = value >>> 32;
        return (int) score;
    }

    public Move getMove() {
        long move = (value >> 16) & 0xffff;
        return move > 0 ? new Move((short) move) : null;
    }

    public NodeType getFlag() {
        long flag = (value >>> 12) & 0xf;
        return NodeType.valueOf((int) flag);
    }

    public int getDepth() {
        return (int) value & 0xfff;
    }

    public static TranspositionEntry of(long zobristKey, int score, Move move, NodeType flag, int depth) {
        long moveValue = move != null ? move.getValue() : 0;
        long flagValue = NodeType.value(flag);
        long value = (long) score << 32 | moveValue << 16 | flagValue << 12 | depth;
        return new TranspositionEntry(zobristKey, value);
    }

    public static TranspositionEntry withScore(TranspositionEntry entry, int score) {
        long value = (entry.value() & CLEAR_SCORE_MASK) | (long) score << 32;
        return new TranspositionEntry(entry.key(), value);
    }

}
