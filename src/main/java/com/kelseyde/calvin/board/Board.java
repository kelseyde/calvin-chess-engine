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

    long[] pieceBitboards = new long[] {
            Bits.WHITE_PAWNS_START, Bits.WHITE_KNIGHTS_START, Bits.WHITE_BISHOPS_START, Bits.WHITE_ROOKS_START, Bits.WHITE_QUEENS_START, Bits.WHITE_KING_START,
            Bits.BLACK_PAWNS_START, Bits.BLACK_KNIGHTS_START, Bits.BLACK_BISHOPS_START, Bits.BLACK_ROOKS_START, Bits.BLACK_QUEENS_START, Bits.BLACK_KING_START,
    };

    long [] occupancyBitboards = new long[] {
            Bits.WHITE_PIECES_START, Bits.BLACK_PIECES_START, Bits.ALL_PIECES_START
    };

    Piece[] pieceList = BoardUtils.getStartingPieceList();

    boolean isWhiteToMove = true;

    GameState gameState = new GameState();
    Deque<GameState> gameStateHistory = new ArrayDeque<>();
    Deque<Move> moveHistory = new ArrayDeque<>();

    public Board() {
        gameState.setZobristKey(Zobrist.generateKey(this));
        gameState.setPawnKey(Zobrist.generatePawnKey(this));
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
            toggleSquares(Piece.KING, isWhiteToMove, startSquare, endSquare);
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
            toggleSquares(Piece.ROOK, isWhiteToMove, rookStartSquare, rookEndSquare);
            pieceList[rookStartSquare] = null;
            pieceList[rookEndSquare] = Piece.ROOK;
            zobrist = Zobrist.updatePiece(zobrist, rookStartSquare, Piece.ROOK, isWhiteToMove);
            zobrist = Zobrist.updatePiece(zobrist, rookEndSquare, Piece.ROOK, isWhiteToMove);
        }
        else if (move.isPromotion()) {
            toggleSquare(Piece.PAWN, isWhiteToMove, startSquare);
            toggleSquare(move.getPromotionPieceType(), isWhiteToMove, endSquare);
            pieceList[startSquare] = null;
            pieceList[endSquare] = move.getPromotionPieceType();
            zobrist = Zobrist.updatePiece(zobrist, startSquare, Piece.PAWN, isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, startSquare, Piece.PAWN, isWhiteToMove);
            if (capturedPiece != null) {
                toggleSquare(capturedPiece, !isWhiteToMove, endSquare);
                zobrist = Zobrist.updatePiece(zobrist, endSquare, capturedPiece, !isWhiteToMove);
                if (capturedPiece == Piece.PAWN) {
                    pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, capturedPiece, !isWhiteToMove);
                }
            }
            zobrist = Zobrist.updatePiece(zobrist, endSquare, move.getPromotionPieceType(), isWhiteToMove);
            pawnZobrist = Zobrist.updatePiece(pawnZobrist, endSquare, move.getPromotionPieceType(), isWhiteToMove);
        }
        else if (move.isEnPassant()) {
            toggleSquares(piece, isWhiteToMove, startSquare, endSquare);
            int pawnSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
            toggleSquare(Piece.PAWN, !isWhiteToMove, pawnSquare);
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
            pieceList[startSquare] = Piece.KING;
            pieceList[endSquare] = null;
            pieceList[rookEndSquare] = Piece.ROOK;
            pieceList[rookStartSquare] = null;
        }
        else if (lastMove.isPromotion()) {
            toggleSquare(lastMove.getPromotionPieceType(), isWhiteToMove, endSquare);
            toggleSquare(Piece.PAWN, isWhiteToMove, startSquare);
            if (gameState.getCapturedPiece() != null) {
                toggleSquare(gameState.getCapturedPiece(), !isWhiteToMove, endSquare);
            }
            pieceList[startSquare] = Piece.PAWN;
            pieceList[endSquare] = gameState.getCapturedPiece() != null ? gameState.getCapturedPiece() : null;
        }
        else if (lastMove.isEnPassant()) {
            toggleSquares(Piece.PAWN, isWhiteToMove, endSquare, startSquare);
            int captureSquare = isWhiteToMove ? endSquare - 8 : endSquare + 8;
            toggleSquare(Piece.PAWN, !isWhiteToMove, captureSquare);
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

    public void toggleSquares(Piece type, boolean isWhite, int startSquare, int endSquare) {
        long toggleMask = (1L << startSquare | 1L << endSquare);
        toggle(type, isWhite, toggleMask);
    }

    public void toggleSquare(Piece type, boolean isWhite, int square) {
        long toggleMask = 1L << square;
        toggle(type, isWhite, toggleMask);
    }

    private void toggle(Piece type, boolean isWhite, long toggleMask) {
        pieceBitboards[type.getIndex() + Colour.shift(isWhite)] ^= toggleMask;
        occupancyBitboards[Colour.of(isWhite).getIndex()] ^= toggleMask;
        occupancyBitboards[Colour.ALL.getIndex()] ^= toggleMask;
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

    public long getWhitePawns() {
        return pieceBitboards[Piece.PAWN.getIndex() + Colour.WHITE.getShift()];
    }

    public long getBlackPawns() {
        return pieceBitboards[Piece.PAWN.getIndex() + Colour.BLACK.getShift()];
    }

    public long getWhiteKnights() {
        return pieceBitboards[Piece.KNIGHT.getIndex() + Colour.WHITE.getShift()];
    }

    public long getBlackKnights() {
        return pieceBitboards[Piece.KNIGHT.getIndex() + Colour.BLACK.getShift()];
    }

    public long getWhiteBishops() {
        return pieceBitboards[Piece.BISHOP.getIndex() + Colour.WHITE.getShift()];
    }

    public long getBlackBishops() {
        return pieceBitboards[Piece.BISHOP.getIndex() + Colour.BLACK.getShift()];
    }

    public long getWhiteRooks() {
        return pieceBitboards[Piece.ROOK.getIndex() + Colour.WHITE.getShift()];
    }

    public long getBlackRooks() {
        return pieceBitboards[Piece.ROOK.getIndex() + Colour.BLACK.getShift()];
    }

    public long getWhiteQueens() {
        return pieceBitboards[Piece.QUEEN.getIndex() + Colour.WHITE.getShift()];
    }

    public long getBlackQueens() {
        return pieceBitboards[Piece.QUEEN.getIndex() + Colour.BLACK.getShift()];
    }

    public long getWhiteKing() {
        return pieceBitboards[Piece.KING.getIndex() + Colour.WHITE.getShift()];
    }

    public long getBlackKing() {
        return pieceBitboards[Piece.KING.getIndex() + Colour.BLACK.getShift()];
    }

    public void setWhitePawns(long bitboard) {
        pieceBitboards[Piece.PAWN.getIndex() + Colour.WHITE.getShift()] = bitboard;
    }

    public void setBlackPawns(long bitboard) {
        pieceBitboards[Piece.PAWN.getIndex() + Colour.BLACK.getShift()] = bitboard;
    }

    public void setWhiteKnights(long bitboard) {
        pieceBitboards[Piece.KNIGHT.getIndex() + Colour.WHITE.getShift()] = bitboard;
    }

    public void setBlackKnights(long bitboard) {
        pieceBitboards[Piece.KNIGHT.getIndex() + Colour.BLACK.getShift()] = bitboard;
    }

    public void setWhiteBishops(long bitboard) {
        pieceBitboards[Piece.BISHOP.getIndex() + Colour.WHITE.getShift()] = bitboard;
    }

    public void setBlackBishops(long bitboard) {
        pieceBitboards[Piece.BISHOP.getIndex() + Colour.BLACK.getShift()] = bitboard;
    }

    public void setWhiteRooks(long bitboard) {
        pieceBitboards[Piece.ROOK.getIndex() + Colour.WHITE.getShift()] = bitboard;
    }

    public void setBlackRooks(long bitboard) {
        pieceBitboards[Piece.ROOK.getIndex() + Colour.BLACK.getShift()] = bitboard;
    }

    public void setWhiteQueens(long bitboard) {
        pieceBitboards[Piece.QUEEN.getIndex() + Colour.WHITE.getShift()] = bitboard;
    }

    public void setBlackQueens(long bitboard) {
        pieceBitboards[Piece.QUEEN.getIndex() + Colour.BLACK.getShift()] = bitboard;
    }

    public void setWhiteKing(long bitboard) {
        pieceBitboards[Piece.KING.getIndex() + Colour.WHITE.getShift()] = bitboard;
    }

    public void setBlackKing(long bitboard) {
        pieceBitboards[Piece.KING.getIndex() + Colour.BLACK.getShift()] = bitboard;
    }

    public long getPawns(boolean isWhite) {
        return pieceBitboards[Piece.PAWN.getIndex() + Colour.shift(isWhite)];
    }

    public long getKnights(boolean isWhite) {
        return pieceBitboards[Piece.KNIGHT.getIndex() + Colour.shift(isWhite)];
    }

    public long getBishops(boolean isWhite) {
        return pieceBitboards[Piece.BISHOP.getIndex() + Colour.shift(isWhite)];
    }

    public long getRooks(boolean isWhite) {
        return pieceBitboards[Piece.ROOK.getIndex() + Colour.shift(isWhite)];
    }

    public long getQueens(boolean isWhite) {
        return pieceBitboards[Piece.QUEEN.getIndex() + Colour.shift(isWhite)];
    }

    public long getKing(boolean isWhite) {
        return pieceBitboards[Piece.KING.getIndex() + Colour.shift(isWhite)];
    }

    public long getWhitePieces() {
        return occupancyBitboards[Colour.WHITE.getIndex()];
    }

    public long getBlackPieces() {
        return occupancyBitboards[Colour.BLACK.getIndex()];
    }

    public long getOccupied() {
        return occupancyBitboards[Colour.ALL.getIndex()];
    }

    public void setWhitePieces(long bitboard) {
        occupancyBitboards[Colour.WHITE.getIndex()] = bitboard;
    }

    public void setBlackPieces(long bitboard) {
        occupancyBitboards[Colour.BLACK.getIndex()] = bitboard;
    }

    public void setOccupied(long bitboard) {
        occupancyBitboards[Colour.ALL.getIndex()] = bitboard;
    }

    public long getPieces(boolean isWhite) {
        return occupancyBitboards[isWhite ? 0 : 1];
    }

    public boolean hasPiecesRemaining(boolean isWhite) {
        int colourShift = Colour.shift(isWhite);
        return Bitwise.countBits(pieceBitboards[Piece.KNIGHT.getIndex() + colourShift]) > 0
                || Bitwise.countBits(pieceBitboards[Piece.BISHOP.getIndex() + colourShift]) > 0
                || Bitwise.countBits(pieceBitboards[Piece.ROOK.getIndex() + colourShift]) > 0
                || Bitwise.countBits(pieceBitboards[Piece.QUEEN.getIndex() + colourShift]) > 0;
    }

}
