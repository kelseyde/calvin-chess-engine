package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.BoardUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Represents the current state of the chess board, including the positions of the pieces, the side to move, en passant
 * rights, fifty-move counter, and the move counter. Includes functions to 'make' and 'unmake' moves on the board, which
 * are fundamental to both the search and evaluation algorithms. Uses bitboards to represent the pieces and 'toggling'
 * functions to set and unset pieces.
 *
 * @see <a href="https://www.chessprogramming.org/Board_Representation">Chess Programming Wiki</a>
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Board {

    long whitePawns =   Bits.WHITE_PAWNS_START;
    long whiteKnights = Bits.WHITE_KNIGHTS_START;
    long whiteBishops = Bits.WHITE_BISHOPS_START;
    long whiteRooks =   Bits.WHITE_ROOKS_START;
    long whiteQueens =  Bits.WHITE_QUEENS_START;
    long whiteKing =    Bits.WHITE_KING_START;
    long blackPawns =   Bits.BLACK_PAWNS_START;
    long blackKnights = Bits.BLACK_KNIGHTS_START;
    long blackBishops = Bits.BLACK_BISHOPS_START;
    long blackRooks =   Bits.BLACK_ROOKS_START;
    long blackQueens =  Bits.BLACK_QUEENS_START;
    long blackKing =    Bits.BLACK_KING_START;

    long whitePieces;
    long blackPieces;
    long occupied;

    boolean isWhiteToMove = true;

    GameState gameState = new GameState();
    Deque<GameState> gameStateHistory = new ArrayDeque<>();
    Deque<Move> moveHistory = new ArrayDeque<>();

    public Board() {
        gameState.setZobristKey(Zobrist.generateKey(this));
        recalculatePieces();
    }

    /**
     * Updates the internal board representation with the {@link Move} just made. Toggles the piece bitboards to move the
     * piece + remove the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public void makeMove(Move move) {

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece pieceType = pieceAt(startSquare);
        Piece capturedPieceType = move.isEnPassant() ? Piece.PAWN : pieceAt(endSquare);

        int newFiftyMoveCounter = gameState.getFiftyMoveCounter();
        int newEnPassantFile = -1;
        boolean resetFiftyMoveCounter = capturedPieceType != null || Piece.PAWN.equals(pieceType);
        newFiftyMoveCounter = resetFiftyMoveCounter ? 0 : ++newFiftyMoveCounter;

        if (move.isPawnDoubleMove()) {
            toggleSquares(pieceType, isWhiteToMove, startSquare, endSquare);
            newEnPassantFile = BoardUtils.getFile(endSquare);
        }
        else if (move.isCastling()) {
            toggleSquares(Piece.KING, isWhiteToMove, startSquare, endSquare);
            boolean isKingside = BoardUtils.getFile(endSquare) == 6;
            int rookStartSquare;
            int rookEndSquare;
            if (isKingside) {
                rookStartSquare = isWhiteToMove ? 7 : 63;
                rookEndSquare = isWhiteToMove ? 5 : 61;
            } else {
                rookStartSquare = isWhiteToMove ? 0 : 56;
                rookEndSquare = isWhiteToMove ? 3 : 59;
            }
            toggleSquares(Piece.ROOK, isWhiteToMove, rookStartSquare, rookEndSquare);
        }
        else if (move.isPromotion()) {
            toggleSquare(Piece.PAWN, isWhiteToMove, startSquare);
            toggleSquare(move.getPromotionPieceType(), isWhiteToMove, endSquare);
            if (capturedPieceType != null) {
                toggleSquare(capturedPieceType, !isWhiteToMove, endSquare);
            }
        }
        else if (move.isEnPassant()) {
            toggleSquares(pieceType, isWhiteToMove, startSquare, endSquare);
            int pawnSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
            toggleSquare(Piece.PAWN, !isWhiteToMove, pawnSquare);
        }
        else {
            toggleSquares(pieceType, isWhiteToMove, startSquare, endSquare);
            if (capturedPieceType != null) {
                toggleSquare(capturedPieceType, !isWhiteToMove, endSquare);
            }
        }

        int newCastlingRights = calculateCastlingRights(startSquare, endSquare, pieceType);
        Piece newPieceType = move.getPromotionPieceType() == null ? pieceType : move.getPromotionPieceType();

        long newZobristKey =
                Zobrist.updateKey(gameState.getZobristKey(), isWhiteToMove, startSquare, endSquare, pieceType, newPieceType, capturedPieceType,
                        gameState.getCastlingRights(), newCastlingRights, gameState.getEnPassantFile(), newEnPassantFile);

        GameState newGameState = new GameState(newZobristKey, capturedPieceType, newEnPassantFile, newCastlingRights, newFiftyMoveCounter);
        gameStateHistory.push(gameState);
        gameState = newGameState;

        moveHistory.push(move);

        isWhiteToMove = !isWhiteToMove;
        recalculatePieces();
    }

    /**
     * Reverts the internal board representation to the state before the last move. Toggles the piece bitboards to move the
     * piece + reinstate the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public void unmakeMove() {

        isWhiteToMove = !isWhiteToMove;
        Move lastMove = moveHistory.pop();
        int startSquare = lastMove.getStartSquare();
        int endSquare = lastMove.getEndSquare();
        Piece piece = pieceAt(endSquare);

        if (lastMove.isCastling()) {
            toggleSquares(Piece.KING, isWhiteToMove, endSquare, startSquare);
            boolean isKingside = BoardUtils.getFile(endSquare) == 6;
            int rookStartSquare;
            int rookEndSquare;
            if (isKingside) {
                rookStartSquare = isWhiteToMove ? 5 : 61;
                rookEndSquare = isWhiteToMove ? 7 : 63;
            } else {
                rookStartSquare = isWhiteToMove ? 3 : 59;
                rookEndSquare = isWhiteToMove ? 0 : 56;
            }
            toggleSquares(Piece.ROOK, isWhiteToMove, rookStartSquare, rookEndSquare);
        }
        else if (lastMove.isPromotion()) {
            toggleSquare(lastMove.getPromotionPieceType(), isWhiteToMove, endSquare);
            toggleSquare(Piece.PAWN, isWhiteToMove, startSquare);
            if (gameState.getCapturedPiece() != null) {
                toggleSquare(gameState.getCapturedPiece(), !isWhiteToMove, endSquare);
            }
        }
        else if (lastMove.isEnPassant()) {
            toggleSquares(Piece.PAWN, isWhiteToMove, endSquare, startSquare);
            int captureSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
            toggleSquare(Piece.PAWN, !isWhiteToMove, captureSquare);
        }
        else {
            toggleSquares(piece, isWhiteToMove, endSquare, startSquare);
            if (gameState.getCapturedPiece() != null) {
                toggleSquare(gameState.getCapturedPiece(), !isWhiteToMove, endSquare);
            }
        }

        gameState = gameStateHistory.pop();
        recalculatePieces();

    }

    /**
     * Make a 'null' move, meaning the side to move passes their turn and gives the opponent a double-move. Used exclusively
     * during null-move pruning during search.
     */
    public void makeNullMove() {
        isWhiteToMove = !isWhiteToMove;
        long newZobristKey = Zobrist.updateKeyAfterNullMove(gameState.getZobristKey(), gameState.getEnPassantFile());
        GameState newGameState = new GameState(newZobristKey, null, -1, gameState.getCastlingRights(), 0);
        gameStateHistory.push(gameState);
        gameState = newGameState;
    }

    /**
     * Unmake the 'null' move used during null-move pruning to try and prove a beta cut-off.
     */
    public void unmakeNullMove() {
        isWhiteToMove = !isWhiteToMove;
        gameState = gameStateHistory.pop();
    }

    public void toggleSquares(Piece type, boolean isWhite, int startSquare, int endSquare) {
        long toggleMask = (1L << startSquare | 1L << endSquare);
        toggle(type, isWhite, toggleMask);
    }

    public void toggleSquare(Piece type, boolean isWhite, int square) {
        long toggleMask = 1L << square;
        toggle(type, isWhite, toggleMask);
    }

    private void toggle(Piece type, boolean isWhite, long toggleMask) {
        switch (type) {
            case PAWN ->    { if (isWhite) whitePawns ^= toggleMask;    else blackPawns ^= toggleMask; }
            case KNIGHT ->  { if (isWhite) whiteKnights ^= toggleMask;  else blackKnights ^= toggleMask; }
            case BISHOP ->  { if (isWhite) whiteBishops ^= toggleMask;  else blackBishops ^= toggleMask; }
            case ROOK ->    { if (isWhite) whiteRooks ^= toggleMask;    else blackRooks ^= toggleMask; }
            case QUEEN ->   { if (isWhite) whiteQueens ^= toggleMask;   else blackQueens ^= toggleMask; }
            case KING ->    { if (isWhite) whiteKing ^= toggleMask;     else blackKing ^= toggleMask; }
        }
    }

    public void recalculatePieces() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        occupied = whitePieces | blackPieces;
    }

    private int calculateCastlingRights(int startSquare, int endSquare, Piece pieceType) {
        int newCastlingRights = gameState.getCastlingRights();
        if (newCastlingRights == 0b0000) {
            // Both sides already lost castling rights, so nothing to calculate.
            return newCastlingRights;
        }
        // Any move by the king removes castling rights.
        if (Piece.KING.equals(pieceType)) {
            newCastlingRights &= isWhiteToMove ? Bits.CLEAR_WHITE_CASTLING_MASK : Bits.CLEAR_BLACK_CASTLING_MASK;
        }
        // Any move starting from/ending at a rook square removes castling rights for that corner.
        // Note: all of these cases need to be checked, to cover the scenario where a rook in starting position captures
        // another rook in starting position; in that case, both sides lose castling rights!
        if (startSquare == 7 || endSquare == 7) {
            newCastlingRights &= Bits.CLEAR_WHITE_KINGSIDE_MASK;
        }
        if (startSquare == 63 || endSquare == 63) {
            newCastlingRights &= Bits.CLEAR_BLACK_KINGSIDE_MASK;
        }
        if (startSquare == 0 || endSquare == 0) {
            newCastlingRights &= Bits.CLEAR_WHITE_QUEENSIDE_MASK;
        }
        if (startSquare == 56 || endSquare == 56) {
            newCastlingRights &= Bits.CLEAR_BLACK_QUEENSIDE_MASK;
        }
        return newCastlingRights;
    }

    public Piece pieceAt(int square) {
        long squareMask = 1L << square;
        if ((squareMask & (whitePawns | blackPawns)) != 0)          return Piece.PAWN;
        else if ((squareMask & (whiteKnights | blackKnights)) != 0) return Piece.KNIGHT;
        else if ((squareMask & (whiteBishops | blackBishops)) != 0) return Piece.BISHOP;
        else if ((squareMask & (whiteRooks | blackRooks)) != 0)     return Piece.ROOK;
        else if ((squareMask & (whiteQueens | blackQueens)) != 0)   return Piece.QUEEN;
        else if ((squareMask & (whiteKing | blackKing)) != 0)       return Piece.KING;
        else return null;
    }

    public long getPawns(boolean isWhite) {
        return isWhite ? whitePawns : blackPawns;
    }

    public long getKnights(boolean isWhite) {
        return isWhite ? whiteKnights : blackKnights;
    }

    public long getBishops(boolean isWhite) {
        return isWhite ? whiteBishops : blackBishops;
    }

    public long getRooks(boolean isWhite) {
        return isWhite ? whiteRooks : blackRooks;
    }

    public long getQueens(boolean isWhite) {
        return isWhite ? whiteQueens : blackQueens;
    }

    public long getKing(boolean isWhite) {
        return isWhite ? whiteKing : blackKing;
    }

    public long getPieces(boolean isWhite) {
        return isWhite ? whitePieces : blackPieces;
    }

    public boolean hasPiecesRemaining(boolean isWhite) {
        if (isWhite && Bitwise.countBits(whiteKnights) > 0 || Bitwise.countBits(whiteBishops) > 0 ||
                Bitwise.countBits(whiteRooks) > 0 || Bitwise.countBits(whiteQueens) > 0) {
            return true;
        }
        else return Bitwise.countBits(blackKnights) > 0 || Bitwise.countBits(blackBishops) > 0 ||
                Bitwise.countBits(blackRooks) > 0 || Bitwise.countBits(blackQueens) > 0;
    }

}
