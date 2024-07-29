package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.generation.MoveGenerator;

import java.util.List;

public class SAN {

    /**
     * Convert move to Standard Algebraic Notation (SAN)
     * Note: the move must not yet have been made on the board
     */
    public static String fromMove(Move move, Board board) {
        Piece pieceType = board.pieceAt(move.getStartSquare());
        Piece capturedPieceType = board.pieceAt(move.getEndSquare());

        if (move.isCastling()) {
            int delta = move.getEndSquare() - move.getStartSquare();
            return delta == 2 ? "O-O" : "O-O-O";
        }

        MoveGenerator moveGenerator = new MoveGenerator();
        String notation = "";
        if (pieceType != Piece.PAWN) {
            notation += Notation.PIECE_CODE_INDEX.get(pieceType).toUpperCase();
        }

        // Check if any ambiguity exists in notation (e.g. if e2 can be reached via Nfe2 and Nbe2)
        if (pieceType != Piece.PAWN && pieceType != Piece.KING) {
            List<Move> legalMoves = moveGenerator.generateMoves(board);

            for (Move legalMove : legalMoves) {

                if (legalMove.getStartSquare() != move.getStartSquare() && legalMove.getEndSquare() == move.getEndSquare()) {
                    if (board.pieceAt(legalMove.getStartSquare()) == pieceType) {
                        int fromFileIndex = Board.file(move.getStartSquare());
                        int alternateFromFileIndex = Board.file(legalMove.getEndSquare());
                        int fromRankIndex = Board.rank(move.getStartSquare());
                        int alternateFromRankIndex = Board.rank(legalMove.getStartSquare());

                        if (fromFileIndex != alternateFromFileIndex) {
                            notation += Notation.getFileChar(move.getStartSquare());
                            break;
                        }
                        else if (fromRankIndex != alternateFromRankIndex)
                        {
                            notation += Notation.getRankChar(move.getStartSquare());
                            break;
                        }
                    }
                }
            }
        }

        if (capturedPieceType != null) {
            // add 'x' to indicate capture
            if (pieceType == Piece.PAWN) {
                notation += Notation.getFileChar(move.getStartSquare());
            }
            notation += "x";
        }
        else {
            // Check if capturing en passant
            if (move.isEnPassant()) {
                notation += Notation.getFileChar(move.getStartSquare()) + "x";
            }
        }

        notation += Notation.getFileChar(move.getEndSquare());
        notation += Notation.getRankChar(move.getEndSquare());

        // Add promotion piece type
        if (move.isPromotion()) {
            Piece promotionPieceType = move.getPromotionPiece();
            notation += "=" + Notation.PIECE_CODE_INDEX.get(promotionPieceType).toUpperCase();
        }

        board.makeMove(move);
        if (moveGenerator.isCheck(board, board.isWhiteToMove())) {
            List<Move> legalMoves = moveGenerator.generateMoves(board);
            notation += legalMoves.isEmpty() ? "#" : "+";
        }
        board.unmakeMove();

        return notation;
    }

}
