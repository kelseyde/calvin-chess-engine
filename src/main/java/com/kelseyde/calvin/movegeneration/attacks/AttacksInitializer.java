package com.kelseyde.calvin.movegeneration.attacks;

import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.utils.BoardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Using a 'perfect hashing' algorithm to pre-calculate a lookup table for sliding piece attacks.
 *
 * @see <a href="https://www.chessprogramming.org/Magic_Bitboards">Chess Programming Wiki</a>
 */
public class AttacksInitializer {

    public static long[] generateWhitePawnAttacks() {
        // TODO
        return null;
    }

    public static long[] generateBlackPawnAttacks() {
        // ODO
        return null;
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
        Set<Integer> vectors = isOrthogonal ? BoardUtils.ORTHOGONAL_MOVE_VECTORS : BoardUtils.DIAGONAL_MOVE_VECTORS;

        for (int vector : vectors) {
            int currentSquare = startSquare;
            if (!isValidVectorOffset(currentSquare, vector)) {
                continue;
            }
            for (int distance = 1; distance < 8; distance++) {
                currentSquare = currentSquare + vector;
                if (BoardUtils.isValidIndex(currentSquare + vector) && isValidVectorOffset(currentSquare, vector)) {
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
        Set<Integer> vectors = isOrthogonal ? BoardUtils.ORTHOGONAL_MOVE_VECTORS : BoardUtils.DIAGONAL_MOVE_VECTORS;

        for (int vector : vectors) {
            int currentSquare = startSquare;
            for (int distance = 1; distance < 8; distance++) {
                if (BoardUtils.isValidIndex(currentSquare + vector) && isValidVectorOffset(currentSquare, vector)) {
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

    private static boolean isValidVectorOffset(int square, int vectorOffset) {
        boolean isAFile = (Bits.FILE_A & 1L << square) != 0;
        boolean isHFile = (Bits.FILE_H & 1L << square) != 0;
        boolean isVectorAFileException = BoardUtils.A_FILE_OFFSET_EXCEPTIONS.contains(vectorOffset);
        boolean isVectorHFileException = BoardUtils.H_FILE_OFFSET_EXCEPTIONS.contains(vectorOffset);

        return (!isAFile || !isVectorAFileException) && (!isHFile || !isVectorHFileException);
    }

}
