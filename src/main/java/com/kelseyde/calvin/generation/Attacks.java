package com.kelseyde.calvin.generation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Attacks {

    // All the possible move 'vectors' for a sliding piece, i.e., the offsets for the directions in which a sliding
    // piece is permitted to move. Bishops will use only the diagonal vectors, rooks only the orthogonal vectors, while
    // queens will use both.
    public static final Set<Integer> DIAGONAL_MOVE_VECTORS = Set.of(-9, -7, 7, 9);
    public static final Set<Integer> ORTHOGONAL_MOVE_VECTORS = Set.of(-8, -1, 1, 8);

    // The following sets are exceptions to the initial rule, in scenarios where the sliding piece is placed on the a or h-files.
    // These exceptions prevent the piece from 'wrapping' around to the other side of the board.
    public static final Set<Integer> A_FILE_OFFSET_EXCEPTIONS = Set.of(-9, -1, 7);
    public static final Set<Integer> H_FILE_OFFSET_EXCEPTIONS = Set.of(-7, 1, 9);

    public static final long[] KNIGHT_ATTACKS = new long[] {
            0x0000000000020400L, 0x0000000000050800L, 0x00000000000a1100L, 0x0000000000142200L,
            0x0000000000284400L, 0x0000000000508800L, 0x0000000000a01000L, 0x0000000000402000L,
            0x0000000002040004L, 0x0000000005080008L, 0x000000000a110011L, 0x0000000014220022L,
            0x0000000028440044L, 0x0000000050880088L, 0x00000000a0100010L, 0x0000000040200020L,
            0x0000000204000402L, 0x0000000508000805L, 0x0000000a1100110aL, 0x0000001422002214L,
            0x0000002844004428L, 0x0000005088008850L, 0x000000a0100010a0L, 0x0000004020002040L,
            0x0000020400040200L, 0x0000050800080500L, 0x00000a1100110a00L, 0x0000142200221400L,
            0x0000284400442800L, 0x0000508800885000L, 0x0000a0100010a000L, 0x0000402000204000L,
            0x0002040004020000L, 0x0005080008050000L, 0x000a1100110a0000L, 0x0014220022140000L,
            0x0028440044280000L, 0x0050880088500000L, 0x00a0100010a00000L, 0x0040200020400000L,
            0x0204000402000000L, 0x0508000805000000L, 0x0a1100110a000000L, 0x1422002214000000L,
            0x2844004428000000L, 0x5088008850000000L, 0xa0100010a0000000L, 0x4020002040000000L,
            0x0400040200000000L, 0x0800080500000000L, 0x1100110a00000000L, 0x2200221400000000L,
            0x4400442800000000L, 0x8800885000000000L, 0x100010a000000000L, 0x2000204000000000L,
            0x0004020000000000L, 0x0008050000000000L, 0x00110a0000000000L, 0x0022140000000000L,
            0x0044280000000000L, 0x0088500000000000L, 0x0010a00000000000L, 0x0020400000000000L
    };

    public static final long[] KING_ATTACKS = new long[] {
            0x0000000000000302L, 0x0000000000000705L, 0x0000000000000e0aL, 0x0000000000001c14L,
            0x0000000000003828L, 0x0000000000007050L, 0x000000000000e0a0L, 0x000000000000c040L,
            0x0000000000030203L, 0x0000000000070507L, 0x00000000000e0a0eL, 0x00000000001c141cL,
            0x0000000000382838L, 0x0000000000705070L, 0x0000000000e0a0e0L, 0x0000000000c040c0L,
            0x0000000003020300L, 0x0000000007050700L, 0x000000000e0a0e00L, 0x000000001c141c00L,
            0x0000000038283800L, 0x0000000070507000L, 0x00000000e0a0e000L, 0x00000000c040c000L,
            0x0000000302030000L, 0x0000000705070000L, 0x0000000e0a0e0000L, 0x0000001c141c0000L,
            0x0000003828380000L, 0x0000007050700000L, 0x000000e0a0e00000L, 0x000000c040c00000L,
            0x0000030203000000L, 0x0000070507000000L, 0x00000e0a0e000000L, 0x00001c141c000000L,
            0x0000382838000000L, 0x0000705070000000L, 0x0000e0a0e0000000L, 0x0000c040c0000000L,
            0x0003020300000000L, 0x0007050700000000L, 0x000e0a0e00000000L, 0x001c141c00000000L,
            0x0038283800000000L, 0x0070507000000000L, 0x00e0a0e000000000L, 0x00c040c000000000L,
            0x0302030000000000L, 0x0705070000000000L, 0x0e0a0e0000000000L, 0x1c141c0000000000L,
            0x3828380000000000L, 0x7050700000000000L, 0xe0a0e00000000000L, 0xc040c00000000000L,
            0x0203000000000000L, 0x0507000000000000L, 0x0a0e000000000000L, 0x141c000000000000L,
            0x2838000000000000L, 0x5070000000000000L, 0xa0e0000000000000L, 0x40c0000000000000L
    };

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

    public static final long[] ROOK_MASKS = initMagicMask(true);
    public static final long[] BISHOP_MASKS = initMagicMask(false);

    public static final long[][] ROOK_ATTACKS = initMagicAttacks(true, ROOK_MAGICS, ROOK_SHIFTS);
    public static final long[][] BISHOP_ATTACKS = initMagicAttacks(false, BISHOP_MAGICS, BISHOP_SHIFTS);

    public static final MagicLookup[] ROOK_MAGIC_LOOKUP = initMagicLookups(ROOK_ATTACKS, ROOK_MASKS, ROOK_MAGICS, ROOK_SHIFTS);
    public static final MagicLookup[] BISHOP_MAGIC_LOOKUP = initMagicLookups(BISHOP_ATTACKS, BISHOP_MASKS, BISHOP_MAGICS, BISHOP_SHIFTS);

    public static long pawnAttacks(long pawns, boolean white) {
        return white ?
                (Bitwise.shiftNorthWest(pawns) &~ Bits.FILE_H) | (Bitwise.shiftNorthEast(pawns) &~ Bits.FILE_A) :
                (Bitwise.shiftSouthWest(pawns) &~ Bits.FILE_H) | (Bitwise.shiftSouthEast(pawns) &~ Bits.FILE_A);
    }

    public static long kingAttacks(int square) {
        return KING_ATTACKS[square];
    }

    public static long knightAttacks(int square) {
        return KNIGHT_ATTACKS[square];
    }

    public static long rookAttacks(int square, long blockers) {
        return sliderAttacks(square, blockers, ROOK_MAGIC_LOOKUP);
    }

    public static long bishopAttacks(int square, long blockers) {
        return sliderAttacks(square, blockers, BISHOP_MAGIC_LOOKUP);
    }

    public static long sliderAttacks(int sq, long occ, MagicLookup[] lookups) {
        MagicLookup lookup = lookups[sq];
        occ      &= lookup.mask;
        occ      *= lookup.magic;
        occ    >>>= lookup.shift;
        return lookup.attacks[(int) occ];
    }

    public static long[] initMagicMask(boolean isOrthogonal) {
        long[] magicMasks = new long[64];
        for (int square = 0; square < 64; square++) {
            magicMasks[square] = initMovementMask(square, isOrthogonal);
        }
        return magicMasks;
    }

    public static long[][] initMagicAttacks(boolean isOrthogonal, long[] magics, int[] shifts) {
        long[][] magicAttacks = new long[64][];

        for (int square = 0; square < 64; square++) {
            magicAttacks[square] = initMagicTable(square, isOrthogonal, magics[square], shifts[square]);
        }

        return magicAttacks;
    }

    public static long[] initMagicTable(int square, boolean isOrthogonal, long magic, int shift) {
        int numBits = 64 - shift;
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
        for (int i = 0; i < 64; i++) {
            if ((movementMask & 1L << i) != 0) {
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

    public static long initMovementMask(int startSquare, boolean isOrthogonal) {
        long movementMask = 0L;
        Set<Integer> vectors = isOrthogonal ? ORTHOGONAL_MOVE_VECTORS : DIAGONAL_MOVE_VECTORS;

        for (int vector : vectors) {
            int currentSquare = startSquare;
            if (!isValidVectorOffset(currentSquare, vector)) {
                continue;
            }
            for (int distance = 1; distance < 8; distance++) {
                currentSquare = currentSquare + vector;
                if (Board.isValidIndex(currentSquare + vector) && isValidVectorOffset(currentSquare, vector)) {
                    movementMask |= 1L << currentSquare;
                } else {
                    break;
                }
            }
        }
        return movementMask;
    }

    public static long initAttackMask(int startSquare, long blockers, boolean isOrthogonal) {

        long attackMask = 0L;
        Set<Integer> vectors = isOrthogonal ? ORTHOGONAL_MOVE_VECTORS : DIAGONAL_MOVE_VECTORS;

        for (int vector : vectors) {
            int currentSquare = startSquare;
            for (int distance = 1; distance < 8; distance++) {
                if (Board.isValidIndex(currentSquare + vector) && isValidVectorOffset(currentSquare, vector)) {
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

    public record MagicLookup(long[] attacks, long mask, long magic, int shift) {}

    public static MagicLookup[] initMagicLookups(long[][] allAttacks, long[] masks, long[] magics, int[] shifts) {
        MagicLookup[] magicLookups = new MagicLookup[64];
        for (int i = 0; i < 64; i++) {
            long[] attacks = allAttacks[i];
            long mask = masks[i];
            long magic = magics[i];
            int shift = shifts[i];
            magicLookups[i] = new MagicLookup(attacks, mask, magic, shift);
        }
        return magicLookups;
    }

    private static boolean isValidVectorOffset(int square, int vectorOffset) {
        boolean isAFile = (Bits.FILE_A & 1L << square) != 0;
        boolean isHFile = (Bits.FILE_H & 1L << square) != 0;
        boolean isVectorAFileException = A_FILE_OFFSET_EXCEPTIONS.contains(vectorOffset);
        boolean isVectorHFileException = H_FILE_OFFSET_EXCEPTIONS.contains(vectorOffset);
        return (!isAFile || !isVectorAFileException) && (!isHFile || !isVectorHFileException);
    }


}
