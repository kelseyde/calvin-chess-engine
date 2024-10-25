package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Bits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Magics {

    // All the possible move 'vectors' for a sliding piece, i.e., the offsets for the directions in which a sliding
    // piece is permitted to move. Bishops will use only the diagonal vectors, rooks only the orthogonal vectors, while
    // queens will use both.
    public static final Set<Integer> DIAGONAL_MOVE_VECTORS = Set.of(-9, -7, 7, 9);
    public static final Set<Integer> ORTHOGONAL_MOVE_VECTORS = Set.of(-8, -1, 1, 8);

    // The following sets are exceptions to the initial rule, in scenarios where the sliding piece is placed on the a or h-files.
    // These exceptions prevent the piece from 'wrapping' around to the other side of the board.
    public static final Set<Integer> A_FILE_OFFSET_EXCEPTIONS = Set.of(-9, -1, 7);
    public static final Set<Integer> H_FILE_OFFSET_EXCEPTIONS = Set.of(-7, 1, 9);

    public static final long[] ROOK_MAGICS = new long[] {
            0x0080001020400080L, 0x0040001000200040L, 0x0080081000200080L, 0x0080040800100080L,
            0x0080020400080080L, 0x0080010200040080L, 0x0080008001000200L, 0x0080002040800100L,
            0x0000800020400080L, 0x0000400020005000L, 0x0000801000200080L, 0x0000800800100080L,
            0x0000800400080080L, 0x0000800200040080L, 0x0000800100020080L, 0x0000800040800100L,
            0x0000208000400080L, 0x0000404000201000L, 0x0000808010002000L, 0x0000808008001000L,
            0x0000808004000800L, 0x0000808002000400L, 0x0000010100020004L, 0x0000020000408104L,
            0x0000208080004000L, 0x0000200040005000L, 0x0000100080200080L, 0x0000080080100080L,
            0x0000040080080080L, 0x0000020080040080L, 0x0000010080800200L, 0x0000800080004100L,
            0x0000204000800080L, 0x0000200040401000L, 0x0000100080802000L, 0x0000080080801000L,
            0x0000040080800800L, 0x0000020080800400L, 0x0000020001010004L, 0x0000800040800100L,
            0x0000204000808000L, 0x0000200040008080L, 0x0000100020008080L, 0x0000080010008080L,
            0x0000040008008080L, 0x0000020004008080L, 0x0000010002008080L, 0x0000004081020004L,
            0x0000204000800080L, 0x0000200040008080L, 0x0000100020008080L, 0x0000080010008080L,
            0x0000040008008080L, 0x0000020004008080L, 0x0000800100020080L, 0x0000800041000080L,
            0x00FFFCDDFCED714AL, 0x007FFCDDFCED714AL, 0x003FFFCDFFD88096L, 0x0000040810002101L,
            0x0001000204080011L, 0x0001000204000801L, 0x0001000082000401L, 0x0001FFFAABFAD1A2L
    };

    public static final long[] BISHOP_MAGICS = new long[] {
            0x0002020202020200L, 0x0002020202020000L, 0x0004010202000000L, 0x0004040080000000L,
            0x0001104000000000L, 0x0000821040000000L, 0x0000410410400000L, 0x0000104104104000L,
            0x0000040404040400L, 0x0000020202020200L, 0x0000040102020000L, 0x0000040400800000L,
            0x0000011040000000L, 0x0000008210400000L, 0x0000004104104000L, 0x0000002082082000L,
            0x0004000808080800L, 0x0002000404040400L, 0x0001000202020200L, 0x0000800802004000L,
            0x0000800400A00000L, 0x0000200100884000L, 0x0000400082082000L, 0x0000200041041000L,
            0x0002080010101000L, 0x0001040008080800L, 0x0000208004010400L, 0x0000404004010200L,
            0x0000840000802000L, 0x0000404002011000L, 0x0000808001041000L, 0x0000404000820800L,
            0x0001041000202000L, 0x0000820800101000L, 0x0000104400080800L, 0x0000020080080080L,
            0x0000404040040100L, 0x0000808100020100L, 0x0001010100020800L, 0x0000808080010400L,
            0x0000820820004000L, 0x0000410410002000L, 0x0000082088001000L, 0x0000002011000800L,
            0x0000080100400400L, 0x0001010101000200L, 0x0002020202000400L, 0x0001010101000200L,
            0x0000410410400000L, 0x0000208208200000L, 0x0000002084100000L, 0x0000000020880000L,
            0x0000001002020000L, 0x0000040408020000L, 0x0004040404040000L, 0x0002020202020000L,
            0x0000104104104000L, 0x0000002082082000L, 0x0000000020841000L, 0x0000000000208800L,
            0x0000000010020200L, 0x0000000404080200L, 0x0000040404040400L, 0x0002020202020200L
    };

    public static final int[] ROOK_SHIFTS = new int[]{
            52, 53, 53, 53, 53, 53, 53, 52,
            53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 53, 53, 53, 53, 53
    };
    public static final int[] BISHOP_SHIFTS = new int[] {
            58, 59, 59, 59, 59, 59, 59, 58,
            59, 59, 59, 59, 59, 59, 59, 59,
            59, 59, 57, 57, 57, 57, 59, 59,
            59, 59, 57, 55, 55, 57, 59, 59,
            59, 59, 57, 55, 55, 57, 59, 59,
            59, 59, 57, 57, 57, 57, 59, 59,
            59, 59, 59, 59, 59, 59, 59, 59,
            58, 59, 59, 59, 59, 59, 59, 58
    };

    public static final long[] ROOK_MASKS = Magics.initMagicMask(true);
    public static final long[] BISHOP_MASKS = Magics.initMagicMask(false);

    public static final long[][] ROOK_ATTACKS = Magics.initMagicAttacks(true, ROOK_MAGICS, ROOK_SHIFTS);
    public static final long[][] BISHOP_ATTACKS = Magics.initMagicAttacks(false, BISHOP_MAGICS, BISHOP_SHIFTS);

    public static final MagicLookup[] ROOK_MAGIC_LOOKUP = Magics.initMagicLookups(ROOK_ATTACKS, ROOK_MASKS, ROOK_MAGICS, ROOK_SHIFTS);
    public static final MagicLookup[] BISHOP_MAGIC_LOOKUP = Magics.initMagicLookups(BISHOP_ATTACKS, BISHOP_MASKS, BISHOP_MAGICS, BISHOP_SHIFTS);

    public record MagicLookup(long[] attacks, long mask, long magic, int shift) {}

    public static long[] initMagicMask(boolean isOrthogonal) {
        long[] magicMasks = new long[Bits.Square.COUNT];
        for (int square = 0; square < Bits.Square.COUNT; square++) {
            magicMasks[square] = initMovementMask(square, isOrthogonal);
        }
        return magicMasks;
    }

    public static long[][] initMagicAttacks(boolean isOrthogonal, long[] magics, int[] shifts) {
        long[][] magicAttacks = new long[Bits.Square.COUNT][];

        for (int square = 0; square < Bits.Square.COUNT; square++) {
            magicAttacks[square] = initMagicTable(square, isOrthogonal, magics[square], shifts[square]);
        }

        return magicAttacks;
    }

    public static long[] initMagicTable(int square, boolean isOrthogonal, long magic, int shift) {
        int numBits = Bits.Square.COUNT - shift;
        int tableSize = 1 << numBits;
        long[] table = new long[tableSize];

        long movementMask = initMovementMask(square, isOrthogonal);
        long[] blockerMasks = initBlockerMasks(movementMask);

        for (long blockerMask : blockerMasks) {
            long index = (blockerMask * magic) >>> shift;
            long attacks = initAttackMask(square, blockerMask, isOrthogonal);
            table[(int) index] = attacks;
        }
        return table;
    }

    public static long[] initBlockerMasks(long movementMask) {
        List<Integer> moveSquares = new ArrayList<>();
        for (int i = 0; i < Bits.Square.COUNT; i++) {
            if ((movementMask & Bits.of(i)) != 0) {
                moveSquares.add(i);
            }
        }

        int patternsCount = 1 << moveSquares.size();
        long[] blockerBitboards = new long[patternsCount];

        for (int patternIndex = 0; patternIndex < patternsCount; patternIndex++) {
            for (int bitIndex = 0; bitIndex < moveSquares.size(); bitIndex++) {
                int bit = (patternIndex >>> bitIndex) & 1;
                blockerBitboards[patternIndex] |= (long) bit << moveSquares.get(bitIndex);
            }
        }
        return blockerBitboards;
    }

    public static long initMovementMask(int from, boolean isOrthogonal) {
        long movementMask = 0L;
        Set<Integer> vectors = isOrthogonal ? ORTHOGONAL_MOVE_VECTORS : DIAGONAL_MOVE_VECTORS;

        for (int vector : vectors) {
            int currentSquare = from;
            if (!isValidVectorOffset(currentSquare, vector)) {
                continue;
            }
            for (int distance = 1; distance < 8; distance++) {
                currentSquare = currentSquare + vector;
                if (Bits.Square.isValid(currentSquare + vector) && isValidVectorOffset(currentSquare, vector)) {
                    movementMask |= 1L << currentSquare;
                } else {
                    break;
                }
            }
        }
        return movementMask;
    }

    public static long initAttackMask(int from, long blockers, boolean isOrthogonal) {

        long attackMask = 0L;
        Set<Integer> vectors = isOrthogonal ? ORTHOGONAL_MOVE_VECTORS : DIAGONAL_MOVE_VECTORS;

        for (int vector : vectors) {
            int currentSquare = from;
            for (int distance = 1; distance < 8; distance++) {
                if (Bits.Square.isValid(currentSquare + vector) && isValidVectorOffset(currentSquare, vector)) {
                    currentSquare = currentSquare + vector;
                    attackMask |= 1L << currentSquare;
                    if ((blockers & 1L << currentSquare) != 0) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return attackMask;

    }

    public static MagicLookup[] initMagicLookups(long[][] allAttacks, long[] masks, long[] magics, int[] shifts) {
        MagicLookup[] magicLookups = new MagicLookup[Bits.Square.COUNT];
        for (int i = 0; i < Bits.Square.COUNT; i++) {
            long[] attacks = allAttacks[i];
            long mask = masks[i];
            long magic = magics[i];
            int shift = shifts[i];
            magicLookups[i] = new MagicLookup(attacks, mask, magic, shift);
        }
        return magicLookups;
    }

    private static boolean isValidVectorOffset(int square, int vectorOffset) {
        boolean isAFile = (Bits.File.A & Bits.of(square)) != 0;
        boolean isHFile = (Bits.File.H & Bits.of(square)) != 0;
        boolean isVectorAFileException = A_FILE_OFFSET_EXCEPTIONS.contains(vectorOffset);
        boolean isVectorHFileException = H_FILE_OFFSET_EXCEPTIONS.contains(vectorOffset);
        return (!isAFile || !isVectorAFileException) && (!isHFile || !isVectorHFileException);
    }

}
