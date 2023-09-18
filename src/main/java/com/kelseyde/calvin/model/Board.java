package com.kelseyde.calvin.model;

import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Data;

import java.util.Map;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Also maintains the position
 * 'metadata': side to move, castling rights, en passant target square, full-move counter and half-move counter (used for
 * the 50-move rule) Equivalent to a FEN string.
 */
@Data
public class Board {

    private long whitePawns;
    private long whiteKnights;
    private long whiteBishops;
    private long whiteRooks;
    private long whiteQueens;
    private long whiteKing;

    private long blackPawns;
    private long blackKnights;
    private long blackBishops;
    private long blackRooks;
    private long blackQueens;
    private long blackKing;

    private long whitePieces;
    private long blackPieces;

    private long occupied;

    private long whiteAttacks;
    private long blackAttacks;

    private long enPassantTarget;

    private Colour turn = Colour.WHITE;

    private Map<Colour, CastlingRights> castlingRights = BoardUtils.getDefaultCastlingRights();

    private int halfMoveCounter = 0;

    private int fullMoveCounter = 1;

    public Board() {
        whitePawns = BitBoards.WHITE_PAWNS_START;
        whiteKnights = BitBoards.WHITE_KNIGHTS_START;
        whiteBishops = BitBoards.WHITE_BISHOPS_START;
        whiteRooks = BitBoards.WHITE_ROOKS_START;
        whiteQueens = BitBoards.WHITE_QUEENS_START;
        whiteKing = BitBoards.WHITE_KING_START;

        blackPawns = BitBoards.BLACK_PAWNS_START;
        blackKnights = BitBoards.BLACK_KNIGHTS_START;
        blackBishops = BitBoards.BLACK_BISHOPS_START;
        blackRooks = BitBoards.BLACK_ROOKS_START;
        blackQueens = BitBoards.BLACK_QUEENS_START;
        blackKing = BitBoards.BLACK_KING_START;

        enPassantTarget = 0L;

        recalculatePieces();
    }

    // TODO move outside board?
    public static Board emptyBoard() {
        Board board = new Board();
        board.whitePawns = 0L;
        board.whiteKnights = 0L;
        board.whiteBishops = 0L;
        board.whiteRooks = 0L;
        board.whiteQueens = 0L;
        board.whiteKing = 0L;

        board.blackPawns = 0L;
        board.blackKnights = 0L;
        board.blackBishops = 0L;
        board.blackRooks = 0L;
        board.blackQueens = 0L;
        board.blackKing = 0L;

        board.enPassantTarget = 0L;

        board.getCastlingRights().get(Colour.WHITE).setKingSide(false);
        board.getCastlingRights().get(Colour.WHITE).setQueenSide(false);
        board.getCastlingRights().get(Colour.BLACK).setKingSide(false);
        board.getCastlingRights().get(Colour.BLACK).setQueenSide(false);

        board.recalculatePieces();

        return board;
    }

    public void applyMove(Move move) {

        PieceType pieceType = move.getPieceType();
        String pieceCode = Piece.getPieceCode(turn, pieceType);

        // TODO work out a nice placement, this needs to go above for isCapture
        long opponentPieces = turn.isWhite() ? blackPieces : whitePieces;
        boolean isCapture = (1L << move.getEndSquare() & opponentPieces) != 0;
        boolean resetHalfMoveClock = isCapture || PieceType.PAWN.equals(move.getPieceType());
        if (resetHalfMoveClock) {
            halfMoveCounter = 0;
        } else {
            ++halfMoveCounter;
        }

        // TODO make nice
        occupied &= ~(1L << move.getStartSquare());

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
        setPiece(move.getEndSquare(), pieceCode);

        switch (move.getMoveType()) {
            case EN_PASSANT -> {
                unsetPiece(BitBoard.scanForward(move.getEnPassantCapture()));
            }
            case PROMOTION -> {
                String promotedPiece = Piece.getPieceCode(turn, move.getPromotionPieceType());
                setPiece(move.getEndSquare(), promotedPiece);
            }
            case KINGSIDE_CASTLE -> {
                unsetPiece(turn.isWhite() ? 7 : 63);
                setPiece(turn.isWhite() ? 5 : 61, Piece.getPieceCode(turn, PieceType.ROOK));
            }
            case QUEENSIDE_CASTLE -> {
                unsetPiece(turn.isWhite() ? 0 : 56);
                setPiece(turn.isWhite() ? 3 : 59, Piece.getPieceCode(turn, PieceType.ROOK));
            }
        }

        enPassantTarget = move.getEnPassantTarget();

        calculateCastlingRights(move);

        if (Colour.BLACK.equals(turn)) {
            ++fullMoveCounter;
        }


        turn = turn.oppositeColour();
        recalculatePieces();
    }

    public void setPiece(int square, String pieceCode) {
        // TODO make nice
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
        recalculatePieces();
    }

    public void unsetPiece(int square) {
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

        recalculatePieces();
    }

    public Board copy() {
        Board board = new Board();
        board.setTurn(turn);
        board.setCastlingRights(Map.of(
                Colour.WHITE, castlingRights.get(Colour.WHITE).copy(),
                Colour.BLACK, castlingRights.get(Colour.BLACK).copy()
        ));
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

    private void recalculatePieces() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        occupied = whitePieces | blackPieces;
    }

    private void calculateCastlingRights(Move move) {
        if (PieceType.KING.equals(move.getPieceType())) {
            castlingRights.get(turn).setKingSide(false);
            castlingRights.get(turn).setQueenSide(false);
        }
        if (PieceType.ROOK.equals(move.getPieceType())) {
            long rooks = turn.isWhite() ? whiteRooks : blackRooks;
            long kingsideRookStart = turn.isWhite() ? 1L << 7 : 1L << 63;
            long queensideRookStart = turn.isWhite() ? 1L : 1L << 56;
            if ((rooks & kingsideRookStart) != kingsideRookStart) {
                castlingRights.get(turn).setKingSide(false);
            }
            if ((rooks & queensideRookStart) != queensideRookStart) {
                castlingRights.get(turn).setQueenSide(false);
            }
        }
    }

}
