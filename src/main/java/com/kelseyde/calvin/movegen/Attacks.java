package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Rank;
import com.kelseyde.calvin.movegen.Magics.MagicLookup;

public class Attacks {

    public static long king(int square) {
        return KING_ATTACKS[square];
    }

    public static long knight(int square) {
        return KNIGHT_ATTACKS[square];
    }

    public static long rook(int square, long blockers) {
        return slider(square, blockers, Magics.ROOK_MAGIC_LOOKUP);
    }

    public static long bishop(int square, long blockers) {
        return slider(square, blockers, Magics.BISHOP_MAGIC_LOOKUP);
    }

    public static long slider(int sq, long occ, MagicLookup[] lookups) {
        MagicLookup lookup = lookups[sq];
        occ      &= lookup.mask();
        occ      *= lookup.magic();
        occ    >>>= lookup.shift();
        return lookup.attacks()[(int) occ];
    }

    public static long pawn(long pawns, boolean white) {
        return white ?
                (Bits.northWest(pawns) &~ File.H) | (Bits.northEast(pawns) &~ File.A) :
                (Bits.southWest(pawns) &~ File.H) | (Bits.southEast(pawns) &~ File.A);
    }

    public static long pawnPushes(long pawns, long occupied, boolean white) {
        return white ?
                Bits.north(pawns) & ~occupied & ~Rank.EIGHTH :
                Bits.south(pawns) & ~occupied & ~Rank.FIRST;
    }

    public static long pawnDoublePushes(long pawns, long occupied, boolean white) {
        return white ?
                Bits.north(pawnPushes(pawns, occupied, true)) & ~occupied & Rank.FOURTH :
                Bits.south(pawnPushes(pawns, occupied, false)) & ~occupied & Rank.FIFTH;
    }

    public static long pawnPushPromos(long pawns, long occupied, boolean white) {
        return white ?
                Bits.north(pawns) & ~occupied & Rank.EIGHTH :
                Bits.south(pawns) & ~occupied & Rank.FIRST;
    }

    public static long pawnLeftCaptures(long pawns, long opponents, boolean white) {
        return white ?
                Bits.northWest(pawns) & opponents & ~File.H & ~Rank.EIGHTH :
                Bits.southWest(pawns) & opponents & ~File.H & ~Rank.FIRST;
    }

    public static long pawnRightCaptures(long pawns, long opponents, boolean white) {
        return white ?
                Bits.northEast(pawns) & opponents & ~File.A & ~Rank.EIGHTH :
                Bits.southEast(pawns) & opponents & ~File.A & ~Rank.FIRST;
    }

    public static long pawnLeftEnPassants(long pawns, long enPassantFile, boolean white) {
        return white ?
                Bits.northWest(pawns) & enPassantFile & Rank.SIXTH & ~File.H :
                Bits.southWest(pawns) & enPassantFile & Rank.THIRD & ~File.H;
    }

    public static long pawnRightEnPassants(long pawns, long enPassantFile, boolean white) {
        return white ?
                Bits.northEast(pawns) & enPassantFile & Rank.SIXTH & ~File.A :
                Bits.southEast(pawns) & enPassantFile & Rank.THIRD & ~File.A;
    }

    public static long pawnLeftCapturePromos(long pawns, long opponents, boolean white) {
        return white ?
                Bits.northWest(pawns) & opponents & ~File.H & Rank.EIGHTH :
                Bits.southWest(pawns) & opponents & ~File.H & Rank.FIRST;
    }

    public static long pawnRightCapturePromos(long pawns, long opponents, boolean white) {
        return white ?
                Bits.northEast(pawns) & opponents & ~File.A & Rank.EIGHTH :
                Bits.southEast(pawns) & opponents & ~File.A & Rank.FIRST;
    }

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


}
