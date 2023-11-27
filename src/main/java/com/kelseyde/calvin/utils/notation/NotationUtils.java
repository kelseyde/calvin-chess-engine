package com.kelseyde.calvin.utils.notation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.BoardUtils;

import java.util.*;

public class NotationUtils {

    private static final Map<Piece, String> PIECE_CODE_INDEX = Map.of(
            Piece.PAWN, "p",
            Piece.KNIGHT, "n",
            Piece.BISHOP, "b",
            Piece.ROOK, "r",
            Piece.QUEEN, "q",
            Piece.KING, "k"
    );

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
            String pieceCode = notation.substring(4, 5);
            Piece promotionPieceType = PIECE_CODE_INDEX.entrySet().stream()
                    .filter(entry -> entry.getValue().equalsIgnoreCase(pieceCode))
                    .findAny().orElseThrow().getKey();
            flag = Move.getPromotionFlag(promotionPieceType);
        }
        return new Move(startSquare, endSquare, flag);
    }

    public static String toNotation(Move move) {
        if (move == null) {
            return "-";
        }
        String notation = toNotation(move.getStartSquare()) + toNotation(move.getEndSquare());
        if (move.getPromotionPieceType() != null) {
            notation += PIECE_CODE_INDEX.get(move.getPromotionPieceType());
        }
        return notation;
    }

    public static String toNotation(Deque<Move> moveHistory) {
        List<Move> moves = new ArrayList<>(moveHistory);
        Collections.reverse(moves);
        return moves.stream()
                .map(NotationUtils::toNotation)
                .toList()
                .toString();
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

    /**
     * Convert move to Standard Algebraic Notation (SAN)
     * Note: the move must not yet have been made on the board
     */
    public static String toStandardAlgebraicNotation(Move move, Board board) {
        Piece pieceType = board.pieceAt(move.getStartSquare());
        Piece capturedPieceType = board.pieceAt(move.getEndSquare());

        if (move.isCastling()) {
            int delta = move.getEndSquare() - move.getStartSquare();
            return delta == 2 ? "O-O" : "O-O-O";
        }

        MoveGenerator moveGenerator = new MoveGenerator();
        String notation = "";
        if (pieceType != Piece.PAWN) {
            notation += PIECE_CODE_INDEX.get(pieceType).toUpperCase();
        }

        // Check if any ambiguity exists in notation (e.g. if e2 can be reached via Nfe2 and Nbe2)
        if (pieceType != Piece.PAWN && pieceType != Piece.KING) {
            List<Move> legalMoves = moveGenerator.generateMoves(board);

            for (Move legalMove : legalMoves) {

                if (legalMove.getStartSquare() != move.getStartSquare() && legalMove.getEndSquare() == move.getEndSquare()) {
                    if (board.pieceAt(legalMove.getStartSquare()) == pieceType) {
                        int fromFileIndex = BoardUtils.getFile(move.getStartSquare());
                        int alternateFromFileIndex = BoardUtils.getFile(legalMove.getEndSquare());
                        int fromRankIndex = BoardUtils.getRank(move.getStartSquare());
                        int alternateFromRankIndex = BoardUtils.getRank(legalMove.getStartSquare());

                        if (fromFileIndex != alternateFromFileIndex) {
                            notation += getFile(fromFileIndex);
                            break;
                        }
                        else if (fromRankIndex != alternateFromRankIndex)
                        {
                            notation += getRank(fromRankIndex);
                            break;
                        }
                    }
                }
            }
        }

        if (capturedPieceType != null) {
            // add 'x' to indicate capture
            if (pieceType == Piece.PAWN) {
                notation += getFile(move.getStartSquare());
            }
            notation += "x";
        }
        else {
            // Check if capturing en passant
            if (move.isEnPassant()) {
                notation += getFile(move.getStartSquare()) + "x";
            }
        }

        notation += getFile(move.getEndSquare());
        notation += getRank(move.getEndSquare());

        // Add promotion piece type
        if (move.isPromotion()) {
            Piece promotionPieceType = move.getPromotionPieceType();
            notation += "=" + PIECE_CODE_INDEX.get(promotionPieceType).toUpperCase();
        }

        board.makeMove(move);
        if (moveGenerator.isCheck(board, board.isWhiteToMove())) {
            List<Move> legalMoves = moveGenerator.generateMoves(board);
            notation += legalMoves.isEmpty() ? "#" : "+";
        }
        board.unmakeMove();

        return notation;
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
