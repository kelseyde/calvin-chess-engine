package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.bitboard.BitBoardConstants;
import com.kelseyde.calvin.board.move.Move;

import java.util.List;

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

    public static String toNotation(Move move) {
        return toNotation(move.getStartSquare()) + toNotation(move.getEndSquare());
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
        if ((BitBoardConstants.RANK_1 & bb) != 0) return "1";
        if ((BitBoardConstants.RANK_2 & bb) != 0) return "2";
        if ((BitBoardConstants.RANK_3 & bb) != 0) return "3";
        if ((BitBoardConstants.RANK_4 & bb) != 0) return "4";
        if ((BitBoardConstants.RANK_5 & bb) != 0) return "5";
        if ((BitBoardConstants.RANK_6 & bb) != 0) return "6";
        if ((BitBoardConstants.RANK_7 & bb) != 0) return "7";
        if ((BitBoardConstants.RANK_8 & bb) != 0) return "8";
        return "X";
    }

    public static String getFile(int square) {
        long bb = 1L << square;
        if ((BitBoardConstants.FILE_A & bb) != 0) return "a";
        if ((BitBoardConstants.FILE_B & bb) != 0) return "b";
        if ((BitBoardConstants.FILE_C & bb) != 0) return "c";
        if ((BitBoardConstants.FILE_D & bb) != 0) return "d";
        if ((BitBoardConstants.FILE_E & bb) != 0) return "e";
        if ((BitBoardConstants.FILE_F & bb) != 0) return "f";
        if ((BitBoardConstants.FILE_G & bb) != 0) return "g";
        if ((BitBoardConstants.FILE_H & bb) != 0) return "h";
        return "X";
    }

}
