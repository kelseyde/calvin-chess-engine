package com.kelseyde.calvin.board;

import com.kelseyde.calvin.board.bitboard.BitBoardConstants;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.Piece;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.Data;

import java.util.*;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Also maintains the position
 * 'metadata': side to move, castling rights, en passant target square, full-move counter and half-move counter (used for
 * the 50-move rule) Equivalent to a FEN string.
 */
@Data
public class Board {

    private final String id = UUID.randomUUID().toString();

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

    private boolean isWhiteToMove = true;

    private GameState currentGameState = new GameState();
    private Deque<GameState> gameStateHistory = new ArrayDeque<>();
    private Deque<Move> moveHistory = new ArrayDeque<>();

    public Board() {
        currentGameState.setZobristKey(ZobristKey.generateKey(this));
        recalculatePieces();
    }

    public void makeMove(Move move) {

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        PieceType pieceType = move.getPieceType();

        long newZobristKey = currentGameState.getZobristKey();
        int newFiftyMoveCounter = currentGameState.getFiftyMoveCounter();
        int newEnPassantFile = move.getEnPassantFile();
        PieceType capturedPieceType = getCapturedPieceType(move);
        boolean resetFiftyMoveCounter = capturedPieceType != null || PieceType.PAWN.equals(move.getPieceType());
        newFiftyMoveCounter = resetFiftyMoveCounter ? 0 :  ++newFiftyMoveCounter;

        switch (move.getMoveType()) {
            case STANDARD -> {
                unsetPiece(startSquare, false);
                setPiece(endSquare, pieceType, isWhiteToMove, true);
            }
            case EN_PASSANT -> {
                unsetPiece(startSquare, false);
                setPiece(endSquare, pieceType, isWhiteToMove, true);
                int captureSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
                unsetPiece(captureSquare, true);
            }
            case PROMOTION -> {
                unsetPiece(startSquare, false);
                PieceType promotedPieceType = move.getPromotionPieceType();
                setPiece(endSquare, promotedPieceType, isWhiteToMove, true);
            }
            case KINGSIDE_CASTLE -> {
                unsetPiece(startSquare, false);
                setPiece(endSquare, pieceType, isWhiteToMove, true);
                unsetPiece(isWhiteToMove ? 7 : 63, true);
                setPiece(isWhiteToMove ? 5 : 61, PieceType.ROOK, isWhiteToMove, true);
            }
            case QUEENSIDE_CASTLE -> {
                unsetPiece(startSquare, false);
                setPiece(endSquare, pieceType, isWhiteToMove, true);
                unsetPiece(isWhiteToMove ? 0 : 56, true);
                setPiece(isWhiteToMove ? 3 : 59, PieceType.ROOK, isWhiteToMove, true);
            }
        }

        int newCastlingRights = calculateCastlingRights(move);
        PieceType newPieceType = Optional.ofNullable(move.getPromotionPieceType()).orElse(pieceType);

        newZobristKey ^= ZobristKey.PIECE_SQUARE_HASH[startSquare][isWhiteToMove ? 0 : 1][pieceType.getIndex()];
        newZobristKey ^= ZobristKey.PIECE_SQUARE_HASH[endSquare][isWhiteToMove ? 0 : 1][newPieceType.getIndex()];
        newZobristKey ^= ZobristKey.CASTLING_RIGHTS[currentGameState.getCastlingRights()];
        newZobristKey ^= ZobristKey.CASTLING_RIGHTS[calculateCastlingRights(move)];
        newZobristKey ^= ZobristKey.EN_PASSANT_FILE[currentGameState.getEnPassantFile() + 1];
        newZobristKey ^= ZobristKey.EN_PASSANT_FILE[move.getEnPassantFile() + 1];
        newZobristKey ^= ZobristKey.BLACK_TO_MOVE;

        GameState newGameState = new GameState(newZobristKey, capturedPieceType, newEnPassantFile, newCastlingRights, newFiftyMoveCounter);
        gameStateHistory.push(currentGameState);
        currentGameState = newGameState;

        moveHistory.push(move);

        isWhiteToMove = !isWhiteToMove;
        recalculatePieces();
    }

    public void unmakeMove() {

        isWhiteToMove = !isWhiteToMove;
        Move lastMove = moveHistory.pop();
        int startSquare = lastMove.getStartSquare();
        int endSquare = lastMove.getEndSquare();
        PieceType pieceType = lastMove.getPieceType();
        if (pieceType == null) {
            throw new NoSuchElementException("piece type is null! " + NotationUtils.toNotation(lastMove));
        }

        switch (lastMove.getMoveType()) {
            case STANDARD, PROMOTION -> {
                unsetPiece(endSquare, true);
                setPiece(startSquare, pieceType, isWhiteToMove, true);
            }
            case EN_PASSANT -> {
                unsetPiece(endSquare, false);
                setPiece(startSquare, pieceType, isWhiteToMove, true);
                int captureSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
                setPiece(captureSquare, PieceType.PAWN, !isWhiteToMove, true);
            }
            case KINGSIDE_CASTLE -> {
                unsetPiece(endSquare, false);
                setPiece(startSquare, pieceType, isWhiteToMove, true);
                unsetPiece(isWhiteToMove ? 5 : 61, true);
                setPiece(isWhiteToMove ? 7 : 63, PieceType.ROOK, isWhiteToMove, true);
            }
            case QUEENSIDE_CASTLE -> {
                unsetPiece(endSquare, false);
                setPiece(startSquare, pieceType, isWhiteToMove, true);
                unsetPiece(isWhiteToMove ? 3 : 59, true);
                setPiece(isWhiteToMove ? 0 : 56, PieceType.ROOK, isWhiteToMove, true);
            }
        }
        if (currentGameState.getCapturedPiece() != null) {
            setPiece(endSquare, currentGameState.getCapturedPiece(), !isWhiteToMove, false);
        }

        currentGameState = gameStateHistory.pop();
        recalculatePieces();

    }

    public void setPiece(int square, PieceType pieceType, boolean isWhite, boolean recalculate) {
        String pieceCode = Piece.getPieceCode(isWhite, pieceType);
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

        // TODO needed? occupied modified above
        if (recalculate) {
            recalculatePieces();
        }
    }

    public void recalculatePieces() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        occupied = whitePieces | blackPieces;
    }

    private int calculateCastlingRights(Move move) {
        int newCastlingRights = currentGameState.getCastlingRights();
        // Any move by the king removes castling rights.
        if (PieceType.KING.equals(move.getPieceType())) {
            newCastlingRights &= isWhiteToMove ? GameState.CLEAR_WHITE_CASTLING_MASK : GameState.CLEAR_BLACK_CASTLING_MASK;
        }
        // Any move starting from/ending at a rook square removes castling rights for that corner.
        else if (move.getStartSquare() == 7 || move.getEndSquare() == 7) {
            newCastlingRights &= GameState.CLEAR_WHITE_KINGSIDE_MASK;
        }
        else if (move.getStartSquare() == 63 || move.getEndSquare() == 63) {
            newCastlingRights &= GameState.CLEAR_BLACK_KINGSIDE_MASK;
        }
        else if (move.getStartSquare() == 0 || move.getEndSquare() == 0) {
            newCastlingRights &= GameState.CLEAR_WHITE_QUEENSIDE_MASK;
        }
        else if (move.getStartSquare() == 56 || move.getEndSquare() == 56) {
            newCastlingRights &= GameState.CLEAR_BLACK_QUEENSIDE_MASK;
        }
        return newCastlingRights;
    }

    public PieceType getCapturedPieceType(Move move) {
        long squareMask = 1L << move.getEndSquare();

        long pawnMask = isWhiteToMove ? blackPawns : whitePawns;
        if ((squareMask & pawnMask) != 0) {
            return PieceType.PAWN;
        }
        long knightMask = isWhiteToMove ? blackKnights : whiteKnights;
        if ((squareMask & knightMask) != 0) {
            return PieceType.KNIGHT;
        }
        long bishopMask = isWhiteToMove ? blackBishops : whiteBishops;
        if ((squareMask & bishopMask) != 0) {
            return PieceType.BISHOP;
        }
        long rookMask = isWhiteToMove ? blackRooks : whiteRooks;
        if ((squareMask & rookMask) != 0) {
            return PieceType.ROOK;
        }
        long queenMask = isWhiteToMove ? blackQueens : whiteQueens;
        if ((squareMask & queenMask) != 0) {
            return PieceType.QUEEN;
        }
        return null;
    }

}
