package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.Bits;

import java.util.List;

public class NotationUtils {

    /**
     * Generate a {@link Move} from algebraic notation of the start and end square (e.g. "e2", "e4" -> new Move(12, 28))
     */
    public static Move fromNotation(String startSquare, String endSquare) {
        return new Move(fromNotation(startSquare), fromNotation(endSquare));
    }

    public static Move fromNotation(String startSquare, String endSquare, short flag) {
        return new Move(fromNotation(startSquare), fromNotation(endSquare), flag);
    }

    /**
     * Generate a {@link Move} from combined albegraic notation (e.g. "e2e4"), as used in the UCI protocol.
     * Special case promotion: "a2a1q" - values 'q' | 'b' | 'r' | 'n'
     */
    public static Move fromCombinedNotation(String notation) {
        int startSquare = fromNotation(notation.substring(0, 2));
        int endSquare = fromNotation(notation.substring(2, 4));

        short flag = Move.NO_FLAG;
        if (notation.length() == 5) {
            PieceType promotionPieceType = PieceType.fromPieceCode(notation.substring(4, 5));
            flag = Move.getPromotionFlag(promotionPieceType);
        }
        return new Move(startSquare, endSquare, flag);
    }

    public static String toNotation(Move move) {
        String notation = toNotation(move.getStartSquare()) + toNotation(move.getEndSquare());
        if (move.getPromotionPieceType() != null) {
            notation += move.getPromotionPieceType().getPieceCode();
        }
        return notation;
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
