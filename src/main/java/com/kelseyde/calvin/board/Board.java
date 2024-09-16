package com.kelseyde.calvin.board;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayDeque;
import java.util.Arrays;
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

    long pawns =        Bits.WHITE_PAWNS_START | Bits.BLACK_PAWNS_START;
    long knights =      Bits.WHITE_KNIGHTS_START | Bits.BLACK_KNIGHTS_START;
    long bishops =      Bits.WHITE_BISHOPS_START | Bits.BLACK_BISHOPS_START;
    long rooks =        Bits.WHITE_ROOKS_START | Bits.BLACK_ROOKS_START;
    long queens =       Bits.WHITE_QUEENS_START | Bits.BLACK_QUEENS_START;
    long kings =        Bits.WHITE_KING_START | Bits.BLACK_KING_START;

    long whitePieces =  Bits.WHITE_PIECES_START;
    long blackPieces =  Bits.BLACK_PIECES_START;
    long occupied =     Bits.PIECES_START;

    Piece[] pieceList = Bits.getStartingPieceList();

    boolean whiteToMove = true;

    GameState state = new GameState();
    Deque<GameState> stateHistory = new ArrayDeque<>();
    Deque<Move> moves = new ArrayDeque<>();

    public Board() {
        state.setKey(Zobrist.generateKey(this));
        state.setPawnKey(Zobrist.generatePawnKey(this));
        state.setNonPawnKeys(Zobrist.generateNonPawnKeys(this));
    }

    /**
     * Updates the internal board representation with the {@link Move} just made. Toggles the piece bitboards to move the
     * piece + remove the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public boolean makeMove(Move move) {

        int from = move.getFrom();
        int to = move.getTo();
        Piece piece = pieceList[from];
        if (piece == null) return false;
        Piece captured = move.isEnPassant() ? Piece.PAWN : pieceList[to];
        stateHistory.push(state.copy());

        if (move.isPawnDoubleMove())  makePawnDoubleMove(from, to);
        else if (move.isCastling())   makeCastleMove(from, to);
        else if (move.isPromotion())  makePromotionMove(from, to, move.getPromotionPiece(), captured);
        else if (move.isEnPassant())  makeEnPassantMove(from, to);
        else                          makeStandardMove(from, to, piece, captured);

        updateGameState(from, to, piece, captured, move);
        moves.push(move);
        whiteToMove = !whiteToMove;
        return true;

    }

    /**
     * Reverts the internal board representation to the state before the last move. Toggles the piece bitboards to move the
     * piece + reinstate the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public void unmakeMove() {

        whiteToMove = !whiteToMove;
        Move move = moves.pop();
        int from = move.getFrom();
        int to = move.getTo();
        Piece piece = pieceAt(to);

        if (move.isCastling())        unmakeCastlingMove(from, to);
        else if (move.isPromotion())  unmakePromotionMove(from, to, move.getPromotionPiece());
        else if (move.isEnPassant())  unmakeEnPassantMove(from, to);
        else                          unmakeStandardMove(from, to, piece);

        state = stateHistory.pop();

    }

    private void makePawnDoubleMove(int from, int to) {
        toggleSquares(Piece.PAWN, whiteToMove, from, to);
        pieceList[from] = null;
        pieceList[to] = Piece.PAWN;
        state.key = Zobrist.updatePiece(state.key, from, to, Piece.PAWN, whiteToMove);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, to, Piece.PAWN, whiteToMove);
    }

    private void makeCastleMove(int from, int to) {
        toggleSquares(Piece.KING, whiteToMove, from, to);
        pieceList[from] = null;
        pieceList[to] = Piece.KING;
        int colourIndex = Colour.index(whiteToMove);
        state.key = Zobrist.updatePiece(state.key, from, to, Piece.KING, whiteToMove);
        state.nonPawnKeys[colourIndex] = Zobrist.updatePiece(state.nonPawnKeys[colourIndex], from, to, Piece.KING, whiteToMove);
        boolean isKingside = Board.file(to) == 6;
        int rookFrom, rookTo;
        if (isKingside) {
            rookFrom = whiteToMove ? 7 : 63;
            rookTo = whiteToMove ? 5 : 61;
        } else {
            rookFrom = whiteToMove ? 0 : 56;
            rookTo = whiteToMove ? 3 : 59;
        }
        toggleSquares(Piece.ROOK, whiteToMove, rookFrom, rookTo);
        pieceList[rookFrom] = null;
        pieceList[rookTo] = Piece.ROOK;
        state.key = Zobrist.updatePiece(state.key, rookFrom, rookTo, Piece.ROOK, whiteToMove);
        state.nonPawnKeys[colourIndex] = Zobrist.updatePiece(state.nonPawnKeys[colourIndex], rookFrom, rookTo, Piece.ROOK, whiteToMove);
    }

    private void makeEnPassantMove(int from, int to) {
        toggleSquares(Piece.PAWN, whiteToMove, from, to);
        int pawnSquare = whiteToMove ? to - 8 : to + 8;
        toggleSquare(Piece.PAWN, !whiteToMove, pawnSquare);
        pieceList[from] = null;
        pieceList[pawnSquare] = null;
        pieceList[to] = Piece.PAWN;
        state.key = Zobrist.updatePiece(state.key, from, Piece.PAWN, whiteToMove);
        state.key = Zobrist.updatePiece(state.key, pawnSquare, Piece.PAWN, !whiteToMove);
        state.key = Zobrist.updatePiece(state.key, to, Piece.PAWN, whiteToMove);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, Piece.PAWN, whiteToMove);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, pawnSquare, Piece.PAWN, !whiteToMove);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, to, Piece.PAWN, whiteToMove);
    }

    private void makePromotionMove(int from, int to, Piece promoted, Piece captured) {
        toggleSquare(Piece.PAWN, whiteToMove, from);
        toggleSquare(promoted, whiteToMove, to);
        pieceList[from] = null;
        pieceList[to] = promoted;
        state.key = Zobrist.updatePiece(state.key, from, Piece.PAWN, whiteToMove);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, Piece.PAWN, whiteToMove);
        if (captured != null) {
            toggleSquare(captured, !whiteToMove, to);
            state.key = Zobrist.updatePiece(state.key, to, captured, !whiteToMove);
            int colourIndex = Colour.index(!whiteToMove);
            state.nonPawnKeys[colourIndex] = Zobrist.updatePiece(state.nonPawnKeys[colourIndex], to, captured, !whiteToMove);
        }
        state.key = Zobrist.updatePiece(state.key, to, promoted, whiteToMove);
        int colourIndex = Colour.index(whiteToMove);
        state.nonPawnKeys[colourIndex] = Zobrist.updatePiece(state.nonPawnKeys[colourIndex], to, promoted, whiteToMove);
    }

    private void makeStandardMove(int from, int to, Piece piece, Piece captured) {
        toggleSquares(piece, whiteToMove, from, to);
        if (captured != null) {
            toggleSquare(captured, !whiteToMove, to);
            state.key = Zobrist.updatePiece(state.key, to, captured, !whiteToMove);
            if (captured == Piece.PAWN) {
                state.pawnKey = Zobrist.updatePiece(state.pawnKey, to, captured, !whiteToMove);
            } else {
                int colourIndex = Colour.index(!whiteToMove);
                state.nonPawnKeys[colourIndex] = Zobrist.updatePiece(state.nonPawnKeys[colourIndex], to, captured, !whiteToMove);
            }
        }
        pieceList[from] = null;
        pieceList[to] = piece;
        state.key = Zobrist.updatePiece(state.key, from, to, piece, whiteToMove);
        if (piece == Piece.PAWN) {
            state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, to, piece, whiteToMove);
        } else {
            int colourIndex = Colour.index(whiteToMove);
            state.nonPawnKeys[colourIndex] = Zobrist.updatePiece(state.nonPawnKeys[colourIndex], from, to, piece, whiteToMove);
        }
    }

    private void updateGameState(int from, int to, Piece piece, Piece captured, Move move) {
        state.capturedPiece = captured;
        boolean resetClock = captured != null || Piece.PAWN.equals(piece);
        state.halfMoveClock = resetClock ? 0 : ++state.halfMoveClock;

        int castlingRights = calculateCastlingRights(from, to, piece);
        state.key = Zobrist.updateCastlingRights(state.key, state.castlingRights, castlingRights);
        state.castlingRights = castlingRights;

        int enPassantFile = move.isPawnDoubleMove() ? Board.file(to) : -1;
        state.key = Zobrist.updateEnPassantFile(state.key, state.enPassantFile, enPassantFile);
        state.enPassantFile = enPassantFile;

        state.key = Zobrist.updateSideToMove(state.key);
    }

    private void unmakeCastlingMove(int from, int to) {
        toggleSquares(Piece.KING, whiteToMove, to, from);
        boolean isKingside = Board.file(to) == 6;
        int rookFrom, rookTo;
        if (isKingside) {
            rookFrom = whiteToMove ? 5 : 61;
            rookTo = whiteToMove ? 7 : 63;
        } else {
            rookFrom = whiteToMove ? 3 : 59;
            rookTo = whiteToMove ? 0 : 56;
        }
        toggleSquares(Piece.ROOK, whiteToMove, rookFrom, rookTo);
        pieceList[from] = Piece.KING;
        pieceList[to] = null;
        pieceList[rookTo] = Piece.ROOK;
        pieceList[rookFrom] = null;
    }

    private void unmakePromotionMove(int from, int to, Piece promoPiece) {
        toggleSquare(promoPiece, whiteToMove, to);
        toggleSquare(Piece.PAWN, whiteToMove, from);
        if (state.getCapturedPiece() != null) {
            toggleSquare(state.getCapturedPiece(), !whiteToMove, to);
        }
        pieceList[from] = Piece.PAWN;
        pieceList[to] = state.getCapturedPiece() != null ? state.getCapturedPiece() : null;
    }

    private void unmakeEnPassantMove(int from, int to) {
        toggleSquares(Piece.PAWN, whiteToMove, to, from);
        int captureSquare = whiteToMove ? to - 8 : to + 8;
        toggleSquare(Piece.PAWN, !whiteToMove, captureSquare);
        pieceList[from] = Piece.PAWN;
        pieceList[to] = null;
        pieceList[captureSquare] = Piece.PAWN;
    }

    private void unmakeStandardMove(int from, int to, Piece piece) {
        toggleSquares(piece, whiteToMove, to, from);
        if (state.getCapturedPiece() != null) {
            toggleSquare(state.getCapturedPiece(), !whiteToMove, to);
        }
        pieceList[from] = piece;
        pieceList[to] = state.getCapturedPiece() != null ? state.getCapturedPiece() : null;
    }

    /**
     * Make a 'null' move, meaning the side to move passes their turn and gives the opponent a double-move. Used exclusively
     * during null-move pruning during search.
     */
    public void makeNullMove() {
        whiteToMove = !whiteToMove;
        long newZobristKey = Zobrist.updateKeyAfterNullMove(state.getKey(), state.getEnPassantFile());
        long[] nonPawnKeysCopy = new long[]{state.getNonPawnKeys()[0], state.getNonPawnKeys()[1]};
        GameState newGameState = new GameState(newZobristKey, state.getPawnKey(), nonPawnKeysCopy, null, -1, state.getCastlingRights(), 0);
        stateHistory.push(state);
        state = newGameState;
    }

    /**
     * Unmake the 'null' move used during null-move pruning to try and prove a beta cut-off.
     */
    public void unmakeNullMove() {
        whiteToMove = !whiteToMove;
        state = stateHistory.pop();
    }

    public void toggleSquares(Piece type, boolean white, int from, int to) {
        long toggleMask = (1L << from | 1L << to);
        toggle(type, white, toggleMask);
    }

    public void toggleSquare(Piece type, boolean white, int square) {
        long toggleMask = 1L << square;
        toggle(type, white, toggleMask);
    }

    private void toggle(Piece type, boolean white, long toggleMask) {
        switch (type) {
            case PAWN ->    pawns ^= toggleMask;
            case KNIGHT ->  knights ^= toggleMask;
            case BISHOP ->  bishops ^= toggleMask;
            case ROOK ->    rooks ^= toggleMask;
            case QUEEN ->   queens ^= toggleMask;
            case KING ->    kings ^= toggleMask;
        }
        if (white) {
            whitePieces ^= toggleMask;
        } else {
            blackPieces ^= toggleMask;
        }
        occupied ^= toggleMask;
    }

    public void removeKing(boolean white) {
        long toggleMask = white ? (kings & whitePieces) : (kings & blackPieces);
        kings ^= toggleMask;
        if (white) {
            whitePieces ^= toggleMask;
        } else {
            blackPieces ^= toggleMask;
        }
        occupied ^= toggleMask;
    }

    public void addKing(int kingSquare, boolean white) {
        long toggleMask = 1L << kingSquare;
        kings |= toggleMask;
        if (white) {
            whitePieces |= toggleMask;
        } else {
            blackPieces |= toggleMask;
        }
        occupied |= toggleMask;
    }

    private int calculateCastlingRights(int from, int to, Piece piece) {
        int newRights = state.getCastlingRights();
        if (newRights == 0b0000) {
            // Both sides already lost castling rights, so nothing to calculate.
            return newRights;
        }
        // Any move by the king removes castling rights.
        if (Piece.KING.equals(piece)) {
            newRights &= whiteToMove ? Bits.CLEAR_WHITE_CASTLING_MASK : Bits.CLEAR_BLACK_CASTLING_MASK;
        }
        // Any move starting from/ending at a rook square removes castling rights for that corner.
        // Note: all of these cases need to be checked, to cover the scenario where a rook in starting position captures
        // another rook in starting position; in that case, both sides lose castling rights!
        if (from == 7 || to == 7) {
            newRights &= Bits.CLEAR_WHITE_KINGSIDE_MASK;
        }
        if (from == 63 || to == 63) {
            newRights &= Bits.CLEAR_BLACK_KINGSIDE_MASK;
        }
        if (from == 0 || to == 0) {
            newRights &= Bits.CLEAR_WHITE_QUEENSIDE_MASK;
        }
        if (from == 56 || to == 56) {
            newRights &= Bits.CLEAR_BLACK_QUEENSIDE_MASK;
        }
        return newRights;
    }

    public Piece pieceAt(int square) {
        return pieceList[square];
    }

    public long getPawns(boolean white) {
        long side = white ? whitePieces : blackPieces;
        return pawns & side;
    }

    public long getKnights(boolean white) {
        long side = white ? whitePieces : blackPieces;
        return knights & side;
    }

    public long getBishops(boolean white) {
        long side = white ? whitePieces : blackPieces;
        return bishops & side;
    }

    public long getRooks(boolean white) {
        long side = white ? whitePieces : blackPieces;
        return rooks & side;
    }

    public long getQueens(boolean white) {
        long side = white ? whitePieces : blackPieces;
        return queens & side;
    }

    public long getKing(boolean white) {
        long side = white ? whitePieces : blackPieces;
        return kings & side;
    }

    public long getPieces(boolean white) {
        return white ? whitePieces : blackPieces;
    }

    public boolean isCapture(Move move) {
        return move.isEnPassant() || pieceAt(move.getTo()) != null;
    }

    public boolean isQuiet(Move move) {
        return !move.isPromotion() && !isCapture(move);
    }

    public long key() {
        return state.getKey();
    }

    public long pawnKey() {
        return state.getPawnKey();
    }

    public long nonPawnKey(boolean white) {
        return state.getNonPawnKeys()[Colour.index(white)];
    }

    public int countPieces() {
        return Bitwise.countBits(occupied);
    }

    public boolean hasPiecesRemaining(boolean white) {
        return white ?
                (getKnights(true) != 0 || getBishops(true) != 0 || getRooks(true) != 0 || getQueens(true) != 0) :
                (getKnights(false) != 0 || getBishops(false) != 0 || getRooks(false) != 0 || getQueens(false) != 0);
    }

    public static int file(int sq) {
        return sq & 0b000111;
    }

    public static int rank(int sq) {
        return sq >>> 3;
    }

    public static int squareIndex(int rank, int file) {
        return 8 * rank + file;
    }

    public static boolean isValidIndex(int square) {
        return square >= 0 && square < 64;
    }

    public Board copy() {
        Board newBoard = new Board();
        newBoard.setPawns(this.getPawns());
        newBoard.setKnights(this.getKnights());
        newBoard.setBishops(this.getBishops());
        newBoard.setRooks(this.getRooks());
        newBoard.setQueens(this.getQueens());
        newBoard.setKings(this.getKings());
        newBoard.setWhitePieces(this.getWhitePieces());
        newBoard.setBlackPieces(this.getBlackPieces());
        newBoard.setOccupied(this.getOccupied());
        newBoard.setWhiteToMove(this.isWhiteToMove());
        newBoard.setState(this.getState().copy());
        Deque<GameState> gameStateHistory = new ArrayDeque<>();
        this.getStateHistory().forEach(gameState -> gameStateHistory.add(gameState.copy()));
        newBoard.setStateHistory(gameStateHistory);
        Deque<Move> moveHistory = new ArrayDeque<>();
        this.getMoves().forEach(move -> moveHistory.add(new Move(move.value())));
        newBoard.setMoves(moveHistory);
        newBoard.setPieceList(Arrays.copyOf(this.getPieceList(), this.getPieceList().length));
        return newBoard;
    }

}
