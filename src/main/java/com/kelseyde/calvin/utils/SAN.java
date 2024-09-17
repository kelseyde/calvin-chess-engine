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
        Piece piece = board.pieceAt(move.from());
        Piece captured = board.pieceAt(move.to());

        if (move.isCastling()) {
            int delta = move.to() - move.from();
            return delta == 2 ? "O-O" : "O-O-O";
        }

        MoveGenerator moveGenerator = new MoveGenerator();
        String notation = "";
        if (piece != Piece.PAWN) {
            notation += Notation.PIECE_CODE_INDEX.get(piece).toUpperCase();
        }

        // Check if any ambiguity exists in notation (e.g. if e2 can be reached via Nfe2 and Nbe2)
        if (piece != Piece.PAWN && piece != Piece.KING) {
            List<Move> legalMoves = moveGenerator.generateMoves(board);

            for (Move legalMove : legalMoves) {

                if (legalMove.from() != move.from() && legalMove.to() == move.to()) {
                    if (board.pieceAt(legalMove.from()) == piece) {
                        int fromFileIndex = Board.file(move.from());
                        int alternateFromFileIndex = Board.file(legalMove.to());
                        int fromRankIndex = Board.rank(move.from());
                        int alternateFromRankIndex = Board.rank(legalMove.from());

                        if (fromFileIndex != alternateFromFileIndex) {
                            notation += Notation.getFileChar(move.from());
                            break;
                        }
                        else if (fromRankIndex != alternateFromRankIndex)
                        {
                            notation += Notation.getRankChar(move.from());
                            break;
                        }
                    }
                }
            }
        }

        if (captured != null) {
            // add 'x' to indicate capture
            if (piece == Piece.PAWN) {
                notation += Notation.getFileChar(move.from());
            }
            notation += "x";
        }
        else {
            // Check if capturing en passant
            if (move.isEnPassant()) {
                notation += Notation.getFileChar(move.from()) + "x";
            }
        }

        notation += Notation.getFileChar(move.to());
        notation += Notation.getRankChar(move.to());

        // Add promotion piece type
        if (move.isPromotion()) {
            Piece promotionPieceType = move.promoPiece();
            notation += "=" + Notation.PIECE_CODE_INDEX.get(promotionPieceType).toUpperCase();
        }

        board.makeMove(move);
        if (moveGenerator.isCheck(board, board.isWhite())) {
            List<Move> legalMoves = moveGenerator.generateMoves(board);
            notation += legalMoves.isEmpty() ? "#" : "+";
        }
        board.unmakeMove();

        return notation;
    }

}
