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

    Piece[] pieceList = BoardUtils.getStartingPieceList();

    boolean isWhiteToMove = true;

    GameState gameState = new GameState();
    Deque<GameState> gameStateHistory = new ArrayDeque<>();
    Deque<Move> moveHistory = new ArrayDeque<>();

    public Board() {
        gameState.setZobristKey(Zobrist.generateKey(this));
        gameState.setPawnKey(Zobrist.generatePawnKey(this));
        recalculatePieces();
    }

    /**
     * Updates the internal board representation with the {@link Move} just made. Toggles the piece bitboards to move the
     * piece + remove the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public void makeMove(Move move) {

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece piece = pieceList[startSquare];
        Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : pieceList[endSquare];

        int newFiftyMoveCounter = gameState.getFiftyMoveCounter();
        int newEnPassantFile = -1;
        boolean resetFiftyMoveCounter = capturedPiece != null || Piece.PAWN.equals(piece);
        newFiftyMoveCounter = resetFiftyMoveCounter ? 0 : ++newFiftyMoveCounter;
        long zobrist = gameState.getZobristKey();
        long pawnZobrist = gameState.getPawnKey();

        if (move.isPawnDoubleMove()) {
            toggleSquares(piece, isWhiteToMove, startSquare, endSquare);
            pieceList[startSquare] = null;
            pieceList[endSquare] = Piece.PAWN;
            zobrist = Zobrist.updatePiece(zobrist, startSquare, Piece.PAWN, isWhiteToMove);
            zobrist = Zobrist.updatePiece(zobrist, endSquare, Piece.PAWN, isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, startSquare, Piece.PAWN, isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, Piece.PAWN, isWhiteToMove);
            newEnPassantFile = BoardUtils.getFile(endSquare);
        }
        else if (move.isCastling()) {
            toggleKing(isWhiteToMove, startSquare, endSquare);
            pieceList[startSquare] = null;
            pieceList[endSquare] = Piece.KING;
            zobrist = Zobrist.updatePiece(zobrist, startSquare, Piece.KING, isWhiteToMove);
            zobrist = Zobrist.updatePiece(zobrist, endSquare, Piece.KING, isWhiteToMove);
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
            toggleRooks(isWhiteToMove, rookStartSquare, rookEndSquare);
            pieceList[rookStartSquare] = null;
            pieceList[rookEndSquare] = Piece.ROOK;
            zobrist = Zobrist.updatePiece(zobrist, rookStartSquare, Piece.ROOK, isWhiteToMove);
            zobrist = Zobrist.updatePiece(zobrist, rookEndSquare, Piece.ROOK, isWhiteToMove);
        }
        else if (move.isPromotion()) {
            togglePawn(isWhiteToMove, startSquare);
            toggleSquare(move.getPromotionPiece(), isWhiteToMove, endSquare);
            pieceList[startSquare] = null;
            pieceList[endSquare] = move.getPromotionPiece();
            zobrist = Zobrist.updatePiece(zobrist, startSquare, Piece.PAWN, isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, startSquare, Piece.PAWN, isWhiteToMove);
            if (capturedPiece != null) {
                toggleSquare(capturedPiece, !isWhiteToMove, endSquare);
                zobrist = Zobrist.updatePiece(zobrist, endSquare, capturedPiece, !isWhiteToMove);
                if (capturedPiece == Piece.PAWN) {
                    pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, capturedPiece, !isWhiteToMove);
                }
            }
            zobrist = Zobrist.updatePiece(zobrist, endSquare, move.getPromotionPiece(), isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, move.getPromotionPiece(), isWhiteToMove);
        }
        else if (move.isEnPassant()) {
            toggleSquares(piece, isWhiteToMove, startSquare, endSquare);
            int pawnSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
            togglePawn(!isWhiteToMove, pawnSquare);
            pieceList[startSquare] = null;
            pieceList[pawnSquare] = null;
            pieceList[endSquare] = Piece.PAWN;
            zobrist = Zobrist.updatePiece(zobrist, startSquare, Piece.PAWN, isWhiteToMove);
            zobrist = Zobrist.updatePiece(zobrist, pawnSquare, Piece.PAWN, !isWhiteToMove);
            zobrist = Zobrist.updatePiece(zobrist, endSquare, Piece.PAWN, isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, startSquare, Piece.PAWN, isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, pawnSquare, Piece.PAWN, !isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, Piece.PAWN, isWhiteToMove);
        }
        else {
            toggleSquares(piece, isWhiteToMove, startSquare, endSquare);
            if (capturedPiece != null) {
                toggleSquare(capturedPiece, !isWhiteToMove, endSquare);
                zobrist = Zobrist.updatePiece(zobrist, endSquare, capturedPiece, !isWhiteToMove);
                if (capturedPiece == Piece.PAWN) {
                    pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, capturedPiece, !isWhiteToMove);
                }
            }
            pieceList[startSquare] = null;
            pieceList[endSquare] = piece;
            zobrist = Zobrist.updatePiece(zobrist, startSquare, piece, isWhiteToMove);
            zobrist = Zobrist.updatePiece(zobrist, endSquare, piece, isWhiteToMove);
            if (piece == Piece.PAWN) {
                pawnZobrist = Zobrist.updatePiece(pawnZobrist, startSquare, piece, isWhiteToMove);
                pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, piece, isWhiteToMove);
            }
        }

        int newCastlingRights = calculateCastlingRights(startSquare, endSquare, piece);

        zobrist = Zobrist.updateCastlingRights(zobrist, gameState.getCastlingRights(), newCastlingRights);
        zobrist = Zobrist.updateEnPassantFile(zobrist, gameState.getEnPassantFile(), newEnPassantFile);
        zobrist = Zobrist.updateSideToMove(zobrist);

        GameState newGameState = new GameState(zobrist, pawnZobrist, capturedPiece, newEnPassantFile, newCastlingRights, newFiftyMoveCounter);
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
            toggleKing(isWhiteToMove, endSquare, startSquare);
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
            toggleRooks(isWhiteToMove, rookStartSquare, rookEndSquare);
            pieceList[startSquare] = Piece.KING;
            pieceList[endSquare] = null;
            pieceList[rookEndSquare] = Piece.ROOK;
            pieceList[rookStartSquare] = null;
        }
        else if (lastMove.isPromotion()) {
            toggleSquare(lastMove.getPromotionPiece(), isWhiteToMove, endSquare);
            togglePawn(isWhiteToMove, startSquare);
            if (gameState.getCapturedPiece() != null) {
                toggleSquare(gameState.getCapturedPiece(), !isWhiteToMove, endSquare);
            }
            pieceList[startSquare] = Piece.PAWN;
            pieceList[endSquare] = gameState.getCapturedPiece() != null ? gameState.getCapturedPiece() : null;
        }
        else if (lastMove.isEnPassant()) {
            togglePawns(isWhiteToMove, endSquare, startSquare);
            int captureSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
            togglePawn(!isWhiteToMove, captureSquare);
            pieceList[startSquare] = Piece.PAWN;
            pieceList[endSquare] = null;
            pieceList[captureSquare] = Piece.PAWN;
        }
        else {
            toggleSquares(piece, isWhiteToMove, endSquare, startSquare);
            if (gameState.getCapturedPiece() != null) {
                toggleSquare(gameState.getCapturedPiece(), !isWhiteToMove, endSquare);
            }
            pieceList[startSquare] = piece;
            pieceList[endSquare] = gameState.getCapturedPiece() != null ? gameState.getCapturedPiece() : null;;
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
        GameState newGameState = new GameState(newZobristKey, gameState.getPawnKey(), null, -1, gameState.getCastlingRights(), 0);
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

    public void toggleSquares(Piece type, boolean white, int startSquare, int endSquare) {
        long toggleMask = (1L << startSquare | 1L << endSquare);
        toggle(type, white, toggleMask);
    }

    public void toggleSquare(Piece type, boolean white, int square) {
        long toggleMask = 1L << square;
        toggle(type, white, toggleMask);
    }

    private void toggle(Piece type, boolean white, long toggleMask) {
        switch (type) {
            case PAWN ->    { if (white) whitePawns ^= toggleMask;    else blackPawns ^= toggleMask; }
            case KNIGHT ->  { if (white) whiteKnights ^= toggleMask;  else blackKnights ^= toggleMask; }
            case BISHOP ->  { if (white) whiteBishops ^= toggleMask;  else blackBishops ^= toggleMask; }
            case ROOK ->    { if (white) whiteRooks ^= toggleMask;    else blackRooks ^= toggleMask; }
            case QUEEN ->   { if (white) whiteQueens ^= toggleMask;   else blackQueens ^= toggleMask; }
            case KING ->    { if (white) whiteKing ^= toggleMask;     else blackKing ^= toggleMask; }
        }
    }

    public void togglePawns(boolean white, int startSquare, int endSquare) {
        if (white) whitePawns ^= (1L << startSquare | 1L << endSquare);
        else blackPawns ^= (1L << startSquare | 1L << endSquare);
    }

    public void toggleKnights(boolean white, int startSquare, int endSquare) {
        if (white) whiteKnights ^= (1L << startSquare | 1L << endSquare);
        else blackKnights ^= (1L << startSquare | 1L << endSquare);
    }

    public void toggleBishops(boolean white, int startSquare, int endSquare) {
        if (white) whiteBishops ^= (1L << startSquare | 1L << endSquare);
        else blackBishops ^= (1L << startSquare | 1L << endSquare);
    }

    public void toggleRooks(boolean white, int startSquare, int endSquare) {
        if (white) whiteRooks ^= (1L << startSquare | 1L << endSquare);
        else blackRooks ^= (1L << startSquare | 1L << endSquare);
    }

    public void toggleQueens(boolean white, int startSquare, int endSquare) {
        if (white) whiteQueens ^= (1L << startSquare | 1L << endSquare);
        else blackQueens ^= (1L << startSquare | 1L << endSquare);
    }

    public void toggleKing(boolean white, int startSquare, int endSquare) {
        if (white) whiteKing ^= (1L << startSquare | 1L << endSquare);
        else blackKing ^= (1L << startSquare | 1L << endSquare);
    }

    public void togglePawn(boolean white, int startSquare) {
        if (white) whitePawns ^= 1L << startSquare;
        else blackPawns ^= 1L << startSquare;
    }

    public void toggleKnight(boolean white, int startSquare) {
        if (white) whiteKnights ^= 1L << startSquare;
        else blackKnights ^= 1L << startSquare;
    }

    public void toggleBishop(boolean white, int startSquare) {
        if (white) whiteBishops ^= 1L << startSquare;
        else blackBishops ^= 1L << startSquare;
    }

    public void toggleRook(boolean white, int startSquare) {
        if (white) whiteRooks ^= 1L << startSquare;
        else blackRooks ^= 1L << startSquare;
    }

    public void toggleQueen(boolean white, int startSquare) {
        if (white) whiteQueens ^= 1L << startSquare;
        else blackQueens ^= 1L << startSquare;
    }

    public void toggleKing(boolean white, int startSquare) {
        if (white) whiteKing ^= 1L << startSquare;
        else blackKing ^= 1L << startSquare;
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
        return pieceList[square];
    }

    public long getPawns(boolean white) {
        return white ? whitePawns : blackPawns;
    }

    public long getKnights(boolean white) {
        return white ? whiteKnights : blackKnights;
    }

    public long getBishops(boolean white) {
        return white ? whiteBishops : blackBishops;
    }

    public long getRooks(boolean white) {
        return white ? whiteRooks : blackRooks;
    }

    public long getQueens(boolean white) {
        return white ? whiteQueens : blackQueens;
    }

    public long getKing(boolean white) {
        return white ? whiteKing : blackKing;
    }

    public long getPieces(boolean white) {
        return white ? whitePieces : blackPieces;
    }

    public int countPieces() {
        return Bitwise.countBits(occupied);
    }

    public boolean hasPiecesRemaining(boolean white) {
        if (white && Bitwise.countBits(whiteKnights) > 0 || Bitwise.countBits(whiteBishops) > 0 ||
                Bitwise.countBits(whiteRooks) > 0 || Bitwise.countBits(whiteQueens) > 0) {
            return true;
        }
        else return Bitwise.countBits(blackKnights) > 0 || Bitwise.countBits(blackBishops) > 0 ||
                Bitwise.countBits(blackRooks) > 0 || Bitwise.countBits(blackQueens) > 0;
    }

    public boolean isPawnEndgame() {
        return whitePawns != 0 && blackPawns != 0
                && whiteKnights == 0 && blackKnights == 0
                && whiteBishops == 0 && blackBishops == 0
                && whiteRooks == 0 && blackRooks == 0
                && whiteQueens == 0 && blackQueens == 0;
    }

}
