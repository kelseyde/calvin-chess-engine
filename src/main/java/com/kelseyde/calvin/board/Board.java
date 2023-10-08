package com.kelseyde.calvin.board;

import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.Piece;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Also maintains the position
 * 'metadata': side to move, castling rights, en passant target square, full-move counter and half-move counter (used for
 * the 50-move rule) Equivalent to a FEN string.
 */
@Data
public class Board {

    private final String id = UUID.randomUUID().toString();

    private long whitePawns =   Bits.WHITE_PAWNS_START;
    private long whiteKnights = Bits.WHITE_KNIGHTS_START;
    private long whiteBishops = Bits.WHITE_BISHOPS_START;
    private long whiteRooks =   Bits.WHITE_ROOKS_START;
    private long whiteQueens =  Bits.WHITE_QUEENS_START;
    private long whiteKing =    Bits.WHITE_KING_START;

    private long blackPawns =   Bits.BLACK_PAWNS_START;
    private long blackKnights = Bits.BLACK_KNIGHTS_START;
    private long blackBishops = Bits.BLACK_BISHOPS_START;
    private long blackRooks =   Bits.BLACK_ROOKS_START;
    private long blackQueens =  Bits.BLACK_QUEENS_START;
    private long blackKing =    Bits.BLACK_KING_START;

    private long whitePieces;
    private long blackPieces;

    private long occupied;

    private boolean isWhiteToMove = true;

    private GameState gameState = new GameState();
    private Deque<GameState> gameStateHistory = new ArrayDeque<>();
    private Deque<Move> moveHistory = new ArrayDeque<>();

    public Board() {
        gameState.setZobristKey(ZobristKey.generateKey(this));
        recalculatePieces();
    }

    public void makeMove(Move move) {

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        PieceType pieceType = move.getPieceType();
        PieceType capturedPieceType = pieceAt(move.getEndSquare());

        long newZobristKey = gameState.getZobristKey();
        int newFiftyMoveCounter = gameState.getFiftyMoveCounter();
        int newEnPassantFile = move.getEnPassantFile();
        boolean resetFiftyMoveCounter = capturedPieceType != null|| PieceType.PAWN.equals(move.getPieceType());
        newFiftyMoveCounter = resetFiftyMoveCounter ? 0 :  ++newFiftyMoveCounter;

        switch (move.getMoveType()) {
            case STANDARD -> {
                toggleSquares(pieceType, isWhiteToMove, startSquare, endSquare);
                if (capturedPieceType != null) {
                    toggleSquare(capturedPieceType, !isWhiteToMove, endSquare);
                }
            }
            case EN_PASSANT -> {
                toggleSquares(pieceType, isWhiteToMove, startSquare, endSquare);
                int pawnSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
                toggleSquare(PieceType.PAWN, !isWhiteToMove, pawnSquare);
            }
            case PROMOTION -> {
                toggleSquare(PieceType.PAWN, isWhiteToMove, startSquare);
                toggleSquare(move.getPromotionPieceType(), isWhiteToMove, endSquare);
                if (capturedPieceType != null) {
                    toggleSquare(capturedPieceType, !isWhiteToMove, endSquare);
                }
            }
            case KINGSIDE_CASTLE -> {
                toggleSquares(PieceType.KING, isWhiteToMove, startSquare, endSquare);
                int rookStartSquare = isWhiteToMove ? 7 : 63;
                int rookEndSquare = isWhiteToMove ? 5 : 61;
                toggleSquares(PieceType.ROOK, isWhiteToMove, rookStartSquare, rookEndSquare);
            }
            case QUEENSIDE_CASTLE -> {
                toggleSquares(PieceType.KING, isWhiteToMove, startSquare, endSquare);
                int rookStartSquare = isWhiteToMove ? 0 : 56;
                int rookEndSquare = isWhiteToMove ? 3 : 59;
                toggleSquares(PieceType.ROOK, isWhiteToMove, rookStartSquare, rookEndSquare);
            }
        }

        int newCastlingRights = calculateCastlingRights(move);
        PieceType newPieceType = move.getPromotionPieceType() == null ? pieceType : move.getPromotionPieceType();

        newZobristKey ^= ZobristKey.PIECE_SQUARE_HASH[startSquare][isWhiteToMove ? 0 : 1][pieceType.getIndex()];
        newZobristKey ^= ZobristKey.PIECE_SQUARE_HASH[endSquare][isWhiteToMove ? 0 : 1][newPieceType.getIndex()];
        newZobristKey ^= ZobristKey.CASTLING_RIGHTS[gameState.getCastlingRights()];
        newZobristKey ^= ZobristKey.CASTLING_RIGHTS[calculateCastlingRights(move)];
        newZobristKey ^= ZobristKey.EN_PASSANT_FILE[gameState.getEnPassantFile() + 1];
        newZobristKey ^= ZobristKey.EN_PASSANT_FILE[move.getEnPassantFile() + 1];
        newZobristKey ^= ZobristKey.BLACK_TO_MOVE;

        GameState newGameState = new GameState(newZobristKey, capturedPieceType, newEnPassantFile, newCastlingRights, newFiftyMoveCounter);
        gameStateHistory.push(gameState);
        gameState = newGameState;

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
            case STANDARD -> {
                toggleSquares(pieceType, isWhiteToMove, endSquare, startSquare);
                if (gameState.getCapturedPiece() != null) {
                    toggleSquare(gameState.getCapturedPiece(), !isWhiteToMove, endSquare);
                }
            }
            case EN_PASSANT -> {
                toggleSquares(PieceType.PAWN, isWhiteToMove, endSquare, startSquare);
                int captureSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
                toggleSquare(PieceType.PAWN, !isWhiteToMove, captureSquare);
            }
            case PROMOTION -> {
                toggleSquare(lastMove.getPromotionPieceType(), isWhiteToMove, endSquare);
                toggleSquare(PieceType.PAWN, isWhiteToMove, startSquare);
                if (gameState.getCapturedPiece() != null) {
                    toggleSquare(gameState.getCapturedPiece(), !isWhiteToMove, endSquare);
                }
            }
            case KINGSIDE_CASTLE -> {
                toggleSquares(PieceType.KING, isWhiteToMove, endSquare, startSquare);
                int rookStartSquare = isWhiteToMove ? 5 : 61;
                int rookEndSquare = isWhiteToMove ? 7 : 63;
                toggleSquares(PieceType.ROOK, isWhiteToMove, rookStartSquare, rookEndSquare);
            }
            case QUEENSIDE_CASTLE -> {
                toggleSquares(PieceType.KING, isWhiteToMove, endSquare, startSquare);
                int rookStartSquare = isWhiteToMove ? 3 : 59;
                int rookEndSquare = isWhiteToMove ? 0 : 56;
                toggleSquares(PieceType.ROOK, isWhiteToMove, rookStartSquare, rookEndSquare);
            }
        }

        gameState = gameStateHistory.pop();
        recalculatePieces();

    }

    public void toggleSquares(PieceType type, boolean isWhite, int startSquare, int endSquare) {
        switch (type) {
            case PAWN -> {
                if (isWhite) {
                    whitePawns ^= (1L << startSquare | 1L << endSquare);
                } else {
                    blackPawns ^= (1L << startSquare | 1L << endSquare);
                }
            }
            case KNIGHT -> {
                if (isWhite) {
                    whiteKnights ^= (1L << startSquare | 1L << endSquare);
                } else {
                    blackKnights ^= (1L << startSquare | 1L << endSquare);
                }
            }
            case BISHOP -> {
                if (isWhite) {
                    whiteBishops ^= (1L << startSquare | 1L << endSquare);
                } else {
                    blackBishops ^= (1L << startSquare | 1L << endSquare);
                }
            }
            case ROOK -> {
                if (isWhite) {
                    whiteRooks ^= (1L << startSquare | 1L << endSquare);
                } else {
                    blackRooks ^= (1L << startSquare | 1L << endSquare);
                }
            }
            case QUEEN -> {
                if (isWhite) {
                    whiteQueens ^= (1L << startSquare | 1L << endSquare);
                } else {
                    blackQueens ^= (1L << startSquare | 1L << endSquare);
                }
            }
            case KING -> {
                if (isWhite) {
                    whiteKing ^= (1L << startSquare | 1L << endSquare);
                } else {
                    blackKing ^= (1L << startSquare | 1L << endSquare);
                }
            }
        };
    }

    public void toggleSquare(PieceType type, boolean isWhite, int square) {
        switch (type) {
            case PAWN -> {
                if (isWhite) {
                    whitePawns ^= 1L << square;
                } else {
                    blackPawns ^= 1L << square;
                }
            }
            case KNIGHT -> {
                if (isWhite) {
                    whiteKnights ^= 1L << square;
                } else {
                    blackKnights ^= 1L << square;
                }
            }
            case BISHOP -> {
                if (isWhite) {
                    whiteBishops ^= 1L << square;
                } else {
                    blackBishops ^= 1L << square;
                }
            }
            case ROOK -> {
                if (isWhite) {
                    whiteRooks ^= 1L << square;
                } else {
                    blackRooks ^= 1L << square;
                }
            }
            case QUEEN -> {
                if (isWhite) {
                    whiteQueens ^= 1L << square;
                } else {
                    blackQueens ^= 1L << square;
                }
            }
            case KING -> {
                if (isWhite) {
                    whiteKing ^= 1L << square;
                } else {
                    blackKing ^= 1L << square;
                }
            }
        };
    }

    public void recalculatePieces() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        occupied = whitePieces | blackPieces;
    }

    private int calculateCastlingRights(Move move) {
        int newCastlingRights = gameState.getCastlingRights();
        // Any move by the king removes castling rights.
        if (PieceType.KING.equals(move.getPieceType())) {
            newCastlingRights &= isWhiteToMove ? GameState.CLEAR_WHITE_CASTLING_MASK : GameState.CLEAR_BLACK_CASTLING_MASK;
        }
        // Any move starting from/ending at a rook square removes castling rights for that corner.
        // Note: all of these cases need to be checked, to cover the scenario where a rook in starting position captures
        // another rook in starting position; in that case, both sides lose castling rights!
        if (move.getStartSquare() == 7 || move.getEndSquare() == 7) {
            newCastlingRights &= GameState.CLEAR_WHITE_KINGSIDE_MASK;
        }
        if (move.getStartSquare() == 63 || move.getEndSquare() == 63) {
            newCastlingRights &= GameState.CLEAR_BLACK_KINGSIDE_MASK;
        }
        if (move.getStartSquare() == 0 || move.getEndSquare() == 0) {
            newCastlingRights &= GameState.CLEAR_WHITE_QUEENSIDE_MASK;
        }
        if (move.getStartSquare() == 56 || move.getEndSquare() == 56) {
            newCastlingRights &= GameState.CLEAR_BLACK_QUEENSIDE_MASK;
        }
        return newCastlingRights;
    }

    public PieceType pieceAt(int square) {
        long squareMask = 1L << square;
        if ((squareMask & (whitePawns | blackPawns)) != 0) {
            return PieceType.PAWN;
        }
        if ((squareMask & (whiteKnights | blackKnights)) != 0) {
            return PieceType.KNIGHT;
        }
        if ((squareMask & (whiteBishops | blackBishops)) != 0) {
            return PieceType.BISHOP;
        }
        if ((squareMask & (whiteRooks | blackRooks)) != 0) {
            return PieceType.ROOK;
        }
        if ((squareMask & (whiteQueens | blackQueens)) != 0) {
            return PieceType.QUEEN;
        }
        if ((squareMask & (whiteKing | blackKing)) != 0) {
            return PieceType.KING;
        }
        return null;
    }

}
