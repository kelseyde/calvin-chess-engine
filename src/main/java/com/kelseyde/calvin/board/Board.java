package com.kelseyde.calvin.board;

import com.kelseyde.calvin.board.bitboard.BitBoardConstants;
import com.kelseyde.calvin.board.bitboard.BitBoardUtil;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.Piece;
import com.kelseyde.calvin.board.piece.PieceType;
import lombok.Data;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Also maintains the position
 * 'metadata': side to move, castling rights, en passant target square, full-move counter and half-move counter (used for
 * the 50-move rule) Equivalent to a FEN string.
 */
@Data
public class Board {

    private long whitePawns = BitBoardConstants.WHITE_PAWNS_START;
    private long whiteKnights = BitBoardConstants.WHITE_KNIGHTS_START;
    private long whiteBishops = BitBoardConstants.WHITE_BISHOPS_START;
    private long whiteRooks = BitBoardConstants.WHITE_ROOKS_START;
    private long whiteQueens = BitBoardConstants.WHITE_QUEENS_START;
    private long whiteKing = BitBoardConstants.WHITE_KING_START;

    private long blackPawns = BitBoardConstants.BLACK_PAWNS_START;
    private long blackKnights = BitBoardConstants.BLACK_KNIGHTS_START;
    private long blackBishops = BitBoardConstants.BLACK_BISHOPS_START;
    private long blackRooks = BitBoardConstants.BLACK_ROOKS_START;
    private long blackQueens = BitBoardConstants.BLACK_QUEENS_START;
    private long blackKing = BitBoardConstants.BLACK_KING_START;

    private long whitePieces;
    private long blackPieces;

    private long occupied;

    private long whiteAttacks;
    private long blackAttacks;

    private long enPassantTarget = 0L;

    private boolean isWhiteToMove = true;

    private boolean whiteKingsideCastlingAllowed = true;
    private boolean whiteQueensideCastlingAllowed = true;
    private boolean blackKingsideCastlingAllowed = true;
    private boolean blackQueensideCastlingAllowed = true;

    private int halfMoveCounter = 0;

    private int fullMoveCounter = 1;

    public Board() {
        recalculatePieces();
    }

    public void applyMove(Move move) {

        PieceType pieceType = move.getPieceType();
        String pieceCode = Piece.getPieceCode(isWhiteToMove, pieceType);

        // TODO work out a nice placement, this needs to go above for isCapture
        long opponentPieces = isWhiteToMove ? blackPieces : whitePieces;
        boolean isCapture = (1L << move.getEndSquare() & opponentPieces) != 0;
        boolean resetHalfMoveClock = isCapture || PieceType.PAWN.equals(move.getPieceType());
        if (resetHalfMoveClock) {
            halfMoveCounter = 0;
        } else {
            ++halfMoveCounter;
        }

        // TODO make nice
        unsetPiece(move.getStartSquare(), false);
        setPiece(move.getEndSquare(), pieceCode, true);

        switch (move.getMoveType()) {
            case EN_PASSANT -> {
                int enPassantCapturedSquare = BitBoardUtil.scanForward(move.getEnPassantCapture());
                unsetPiece(enPassantCapturedSquare, true);
            }
            case PROMOTION -> {
                String promotedPiece = Piece.getPieceCode(isWhiteToMove, move.getPromotionPieceType());
                setPiece(move.getEndSquare(), promotedPiece, true);
            }
            case KINGSIDE_CASTLE -> {
                unsetPiece(isWhiteToMove ? 7 : 63, true);
                setPiece(isWhiteToMove ? 5 : 61, Piece.getPieceCode(isWhiteToMove, PieceType.ROOK), true);
            }
            case QUEENSIDE_CASTLE -> {
                unsetPiece(isWhiteToMove ? 0 : 56, true);
                setPiece(isWhiteToMove ? 3 : 59, Piece.getPieceCode(isWhiteToMove, PieceType.ROOK), true);
            }
        }

        enPassantTarget = move.getEnPassantTarget();

        updateCastlingRights(move);

        if (!isWhiteToMove) {
            ++fullMoveCounter;
        }

        isWhiteToMove = !isWhiteToMove;
        recalculatePieces();
    }

    public void setPiece(int square, String pieceCode, boolean recalculate) {
        unsetPiece(square, false);
        switch (pieceCode) {
            case "wP" -> whitePawns |= (1L << square);
            case "wN" -> whiteKnights |= (1L << square);
            case "wB" -> whiteBishops |= (1L << square);
            case "wR" -> whiteRooks |= (1L << square);
            case "wQ" -> whiteQueens |= (1L << square);
            case "wK" -> whiteKing |= (1L << square);
            case "bP" -> blackPawns |= (1L << square);
            case "bN" -> blackKnights |= (1L << square);
            case "bB" -> blackBishops |= (1L << square);
            case "bR" -> blackRooks |= (1L << square);
            case "bQ" -> blackQueens |= (1L << square);
            case "bK" -> blackKing |= (1L << square);
        }
        if (recalculate) {
            recalculatePieces();
        }
    }

    public void unsetPiece(int square, boolean recalculate) {
        occupied &= ~(1L << square);

        whitePawns = whitePawns & occupied;
        whiteKnights = whiteKnights & occupied;
        whiteBishops = whiteBishops & occupied;
        whiteRooks = whiteRooks & occupied;
        whiteQueens = whiteQueens & occupied;
        whiteKing = whiteKing & occupied;

        blackPawns = blackPawns & occupied;
        blackKnights = blackKnights & occupied;
        blackBishops = blackBishops & occupied;
        blackRooks = blackRooks & occupied;
        blackQueens = blackQueens & occupied;
        blackKing = blackKing & occupied;

        if (recalculate) {
            recalculatePieces();
        }
    }

    public Board copy() {
        Board board = new Board();
        board.setWhiteToMove(isWhiteToMove);
        board.setWhiteKingsideCastlingAllowed(whiteKingsideCastlingAllowed);
        board.setWhiteQueensideCastlingAllowed(whiteQueensideCastlingAllowed);
        board.setBlackKingsideCastlingAllowed(blackKingsideCastlingAllowed);
        board.setBlackQueensideCastlingAllowed(blackQueensideCastlingAllowed);
        board.setEnPassantTarget(enPassantTarget);
        board.setHalfMoveCounter(halfMoveCounter);
        board.setFullMoveCounter(fullMoveCounter);
        board.setWhitePawns(whitePawns);
        board.setWhiteKnights(whiteKnights);
        board.setWhiteBishops(whiteBishops);
        board.setWhiteRooks(whiteRooks);
        board.setWhiteQueens(whiteQueens);
        board.setWhiteKing(whiteKing);
        board.setBlackPawns(blackPawns);
        board.setBlackKnights(blackKnights);
        board.setBlackBishops(blackBishops);
        board.setBlackRooks(blackRooks);
        board.setBlackQueens(blackQueens);
        board.setBlackKing(blackKing);
        board.recalculatePieces();
        return board;
    }

    public void recalculatePieces() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        occupied = whitePieces | blackPieces;
    }

    private void updateCastlingRights(Move move) {
        if (PieceType.KING.equals(move.getPieceType())) {
            if (isWhiteToMove) {
                whiteKingsideCastlingAllowed = false;
                whiteQueensideCastlingAllowed = false;
            } else {
                blackKingsideCastlingAllowed = false;
                blackQueensideCastlingAllowed = false;
            }
        }
        if (PieceType.ROOK.equals(move.getPieceType())) {
            long rooks = isWhiteToMove ? whiteRooks : blackRooks;
            long kingsideRookStart = isWhiteToMove ? 1L << 7 : 1L << 63;
            long queensideRookStart = isWhiteToMove ? 1L : 1L << 56;
            if ((rooks & kingsideRookStart) != kingsideRookStart) {
                if (isWhiteToMove) {
                    whiteKingsideCastlingAllowed = false;
                } else {
                    blackKingsideCastlingAllowed = false;
                }
            }
            if ((rooks & queensideRookStart) != queensideRookStart) {
                if (isWhiteToMove) {
                    whiteQueensideCastlingAllowed = false;
                } else {
                    blackQueensideCastlingAllowed = false;
                }
            }
        }
    }

}
