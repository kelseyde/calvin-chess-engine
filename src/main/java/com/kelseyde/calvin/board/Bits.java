package com.kelseyde.calvin.board;

import java.util.List;
import java.util.Map;

public class Bits {

    public static int next(final long board) {
        return Long.numberOfTrailingZeros(board);
    }

    public static long pop(long board) {
        return board & (board - 1);
    }

    public static int count(long board) {
        return Long.bitCount(board);
    }

    public static long of(int sq) {
        return 1L << sq;
    }

    public static boolean contains(long bb, int sq) {
        return (bb & of(sq)) != 0;
    }

    public static long north(long board) {
        return board << 8;
    }

    public static long south(long board) {
        return board >>> 8;
    }

    public static long east(long board) {
        return (board << 1) & ~File.A;
    }

    public static long west(long board) {
        return (board >>> 1) & ~File.H;
    }

    public static long northEast(long board) {
        return (board << 9) & ~File.A;
    }

    public static long southEast(long board) {
        return (board >>> 7) & ~File.A;
    }

    public static long northWest(long board) {
        return (board << 7) & ~File.H;
    }

    public static long southWest(long board) {
        return (board >>> 9) & ~File.H;
    }

    public static void print(long bb) {

        for (int rank = 7; rank >= 0; --rank) {
            System.out.print(" +---+---+---+---+---+---+---+---+\n");

            for (int file = 0; file < 8; ++file) {
                boolean piece = (bb & (Bits.of(Square.of(rank, file)))) != 0;
                System.out.print(" | " + (piece ? '1' : ' '));
            }

            System.out.print(" | "  + (rank + 1) + "\n");
        }

        System.out.print(" +---+---+---+---+---+---+---+---+\n");
        System.out.print("   a   b   c   d   e   f   g   h\n\n");

    }

    public static class Square {

        public static final int COUNT = 64;
        public static final long ALL = ~0L;
        public static final long NONE = 0L;

        public static int of(int rank, int file) {
            return (rank << 3) + file;
        }

        public static boolean isValid(int sq) {
            return sq >= 0 && sq < Square.COUNT;
        }

        public static String toNotation(int sq) {
            return File.toNotation(sq) + Rank.toRankNotation(sq);
        }

        public static int fromNotation(String algebraic) {
            int xOffset = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').indexOf(algebraic.charAt(0));
            int yAxis = (Integer.parseInt(Character.valueOf(algebraic.charAt(1)).toString()) - 1) * 8;
            return yAxis + xOffset;
        }

    }

    public static class File {

        public static final long A = 0b0000000100000001000000010000000100000001000000010000000100000001L;
        public static final long B = 0b0000001000000010000000100000001000000010000000100000001000000010L;
        public static final long C = 0b0000010000000100000001000000010000000100000001000000010000000100L;
        public static final long D = 0b0000100000001000000010000000100000001000000010000000100000001000L;
        public static final long E = 0b0001000000010000000100000001000000010000000100000001000000010000L;
        public static final long F = 0b0010000000100000001000000010000000100000001000000010000000100000L;
        public static final long G = 0b0100000001000000010000000100000001000000010000000100000001000000L;
        public static final long H = 0b1000000010000000100000001000000010000000100000001000000010000000L;

        public static final Map<Integer, String> FILE_CHAR_MAP = Map.of(
                0, "a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h"
        );

        public static int of(int sq) {
            return sq & 7;
        }

        public static boolean kingside(int sq) {
            return of(sq) >= 4;
        }

        public static long toBitboard(int file) {
            return 0x0101010101010101L << file;
        }

        public static String toNotation(int sq) {
            return FILE_CHAR_MAP.get(of(sq));
        }

        public static int fromNotation(char file) {
            return FILE_CHAR_MAP.entrySet().stream()
                    .filter(entry -> entry.getValue().equalsIgnoreCase(Character.toString(file)))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();
        }

    }

    public static class Rank {

        public static final long FIRST  = 0b0000000000000000000000000000000000000000000000000000000011111111L;
        public static final long SECOND = 0b0000000000000000000000000000000000000000000000001111111100000000L;
        public static final long THIRD  = 0b0000000000000000000000000000000000000000111111110000000000000000L;
        public static final long FOURTH = 0b0000000000000000000000000000000011111111000000000000000000000000L;
        public static final long FIFTH  = 0b0000000000000000000000001111111100000000000000000000000000000000L;
        public static final long SIXTH  = 0b0000000000000000111111110000000000000000000000000000000000000000L;
        public static final long SEVENTH = 0b0000000011111111000000000000000000000000000000000000000000000000L;
        public static final long EIGHTH  = 0b1111111100000000000000000000000000000000000000000000000000000000L;

        public static final Map<Integer, String> RANK_CHAR_MAP = Map.of(
                0, "1", 1, "2", 2, "3", 3, "4", 4, "5", 5, "6", 6, "7", 7, "8"
        );

        public static int of(int sq) {
            return sq >>> 3;
        }

        public static String toRankNotation(int sq) {
            return RANK_CHAR_MAP.get(of(sq));
        }

    }

    public static class Ray {

        /**
         * Calculates the ray (bitboard) between two squares on the chessboard.
         */
        public static long between(int from, int to) {
            if (!Square.isValid(from) || !Square.isValid(to) || (from == to)) {
                return 0L;
            }
            int offset = direction(from, to);
            if (offset == 0) return 0L;
            long ray = 0L;
            int sq = from + offset;
            while (Square.isValid(sq) && sq != to) {
                ray |= Bits.of(sq);
                sq += offset;
            }
            return ray;
        }

        /**
         * Determines the direction offset between two squares on the chessboard.
         */
        private static int direction(int from, int to) {
            int startRank = Rank.of(from);
            int endRank = Rank.of(to);
            int startFile = File.of(from);
            int endFile = File.of(to);
            if (startRank == endRank) {
                return from > to ? -1 : 1;
            }
            else if (startFile == endFile) {
                return from > to ? -8 : 8;
            }
            else if (Math.abs(startRank - endRank) == Math.abs(startFile - endFile)) {
                return from > to ? (from - to) % 9 == 0 ? -9 : -7 : (to - from) % 9 == 0 ? 9 : 7;
            }
            else if (startRank + startFile == endRank + endFile) {
                return from > to ? -9 : 9;
            }
            return 0;
        }

    }

}
