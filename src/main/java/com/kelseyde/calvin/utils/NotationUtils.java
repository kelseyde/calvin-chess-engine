package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;

import java.util.*;

public class NotationUtils {

    /**
     * Generate a {@link Move} from algebraic notation of the start and end square (e.g. "e2", "e4" -> new Move(12, 28))
     */
    public static Move fromNotation(String startSquare, String endSquare) {
        return Move.builder()
                .startSquare(fromNotation(startSquare))
                .endSquare(fromNotation(endSquare))
                .build();
    }

    /**
     * Generate a {@link Move} from algebraic notation of the start and end square (e.g. "e2", "e4" -> new Move(12, 28))
     */
    public static Move fromNotation(String startSquare, String endSquare, PieceType type) {
        return Move.builder()
                .startSquare(fromNotation(startSquare))
                .endSquare(fromNotation(endSquare))
                .pieceType(type)
                .build();
    }

    public static String toNotation(Move move) {
        return toNotation(move.getStartSquare()) + toNotation(move.getEndSquare());
    }

    public static List<String> toNotation(Deque<Move> moveHistory) {
        List<Move> moves = new ArrayList<>(moveHistory);
        Collections.reverse(moves);
        return moves.stream()
                .map(NotationUtils::toNotation)
                .toList();
    }

    /**
     * Generate a square co-ordinate from algebraic notation (e.g. "e4" -> 28)
     */
    public static int fromNotation(String algebraic) {
        int xOffset = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').indexOf(algebraic.charAt(0));
        int yAxis = (Integer.parseInt(Character.valueOf(algebraic.charAt(1)).toString()) - 1) * 8;
        return yAxis + xOffset;
    }

    public static String toNotation(int square) {
        return getFile(square) + getRank(square);
    }

    public static String getRank(int square) {
        long bb = 1L << square;
        if ((Bits.RANK_1 & bb) != 0) return "1";
        if ((Bits.RANK_2 & bb) != 0) return "2";
        if ((Bits.RANK_3 & bb) != 0) return "3";
        if ((Bits.RANK_4 & bb) != 0) return "4";
        if ((Bits.RANK_5 & bb) != 0) return "5";
        if ((Bits.RANK_6 & bb) != 0) return "6";
        if ((Bits.RANK_7 & bb) != 0) return "7";
        if ((Bits.RANK_8 & bb) != 0) return "8";
        return "X";
    }

    public static String getFile(int square) {
        long bb = 1L << square;
        if ((Bits.FILE_A & bb) != 0) return "a";
        if ((Bits.FILE_B & bb) != 0) return "b";
        if ((Bits.FILE_C & bb) != 0) return "c";
        if ((Bits.FILE_D & bb) != 0) return "d";
        if ((Bits.FILE_E & bb) != 0) return "e";
        if ((Bits.FILE_F & bb) != 0) return "f";
        if ((Bits.FILE_G & bb) != 0) return "g";
        if ((Bits.FILE_H & bb) != 0) return "h";
        return "X";
    }

}
