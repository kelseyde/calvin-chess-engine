package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.FEN;

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
public class Board {

    private long[] bitboards;
    private Piece[] pieces;
    private GameState state;
    private Deque<GameState> stateHistory;
    private Deque<Move> moves;
    private boolean white;

    public Board() {
        bitboards = new long[9];
        pieces = new Piece[64];
        state = new GameState();
        stateHistory = new ArrayDeque<>();
        moves = new ArrayDeque<>();
        white = true;
        state.setKey(Zobrist.generateKey(this));
        state.setPawnKey(Zobrist.generatePawnKey(this));
    }

    /**
     * Updates the internal board representation with the {@link Move} just made. Toggles the piece bitboards to move the
     * piece + remove the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public boolean makeMove(Move move) {

        final int from = move.from();
        final int to = move.to();
        final Piece piece = pieces[from];
        if (piece == null) return false;
        final Piece captured = move.isEnPassant() ? Piece.PAWN : pieces[to];
        stateHistory.push(state.copy());

        if (move.isPawnDoubleMove())  makePawnDoubleMove(from, to);
        else if (move.isCastling())   makeCastleMove(from, to);
        else if (move.isPromotion())  makePromotionMove(from, to, move.promoPiece(), captured);
        else if (move.isEnPassant())  makeEnPassantMove(from, to);
        else                          makeStandardMove(from, to, piece, captured);

        updateGameState(from, to, piece, captured, move);
        moves.push(move);
        white = !white;
        return true;

    }

    /**
     * Reverts the internal board representation to the state before the last move. Toggles the piece bitboards to move the
     * piece + reinstate the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public void unmakeMove() {

        white = !white;
        final Move move = moves.pop();
        final int from = move.from();
        final int to = move.to();
        final Piece piece = pieceAt(to);

        if (move.isCastling())        unmakeCastlingMove(from, to);
        else if (move.isPromotion())  unmakePromotionMove(from, to, move.promoPiece());
        else if (move.isEnPassant())  unmakeEnPassantMove(from, to);
        else                          unmakeStandardMove(from, to, piece);

        state = stateHistory.pop();

    }

    private void makePawnDoubleMove(int from, int to) {
        toggleSquares(Piece.PAWN, white, from, to);
        pieces[from] = null;
        pieces[to] = Piece.PAWN;
        state.key = Zobrist.updatePiece(state.key, from, to, Piece.PAWN, white);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, to, Piece.PAWN, white);
    }

    private void makeCastleMove(int from, int to) {
        toggleSquares(Piece.KING, white, from, to);
        pieces[from] = null;
        pieces[to] = Piece.KING;
        state.key = Zobrist.updatePiece(state.key, from, to, Piece.KING, white);
        final boolean kingside = Board.file(to) == 6;
        final int rookFrom, rookTo;
        if (kingside) {
            rookFrom = white ? 7 : 63;
            rookTo = white ? 5 : 61;
        } else {
            rookFrom = white ? 0 : 56;
            rookTo = white ? 3 : 59;
        }
        toggleSquares(Piece.ROOK, white, rookFrom, rookTo);
        pieces[rookFrom] = null;
        pieces[rookTo] = Piece.ROOK;
        state.key = Zobrist.updatePiece(state.key, rookFrom, rookTo, Piece.ROOK, white);
    }

    private void makeEnPassantMove(int from, int to) {
        toggleSquares(Piece.PAWN, white, from, to);
        final int pawnSquare = white ? to - 8 : to + 8;
        toggleSquare(Piece.PAWN, !white, pawnSquare);
        pieces[from] = null;
        pieces[pawnSquare] = null;
        pieces[to] = Piece.PAWN;
        state.key = Zobrist.updatePiece(state.key, from, Piece.PAWN, white);
        state.key = Zobrist.updatePiece(state.key, pawnSquare, Piece.PAWN, !white);
        state.key = Zobrist.updatePiece(state.key, to, Piece.PAWN, white);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, Piece.PAWN, white);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, pawnSquare, Piece.PAWN, !white);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, to, Piece.PAWN, white);
    }

    private void makePromotionMove(int from, int to, Piece promoted, Piece captured) {
        toggleSquare(Piece.PAWN, white, from);
        toggleSquare(promoted, white, to);
        pieces[from] = null;
        pieces[to] = promoted;
        state.key = Zobrist.updatePiece(state.key, from, Piece.PAWN, white);
        state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, Piece.PAWN, white);
        if (captured != null) {
            toggleSquare(captured, !white, to);
            state.key = Zobrist.updatePiece(state.key, to, captured, !white);
            if (captured == Piece.PAWN) {
                state.pawnKey = Zobrist.updatePiece(state.pawnKey, to, captured, !white);
            }
        }
        state.key = Zobrist.updatePiece(state.key, to, promoted, white);
    }

    private void makeStandardMove(int from, int to, Piece piece, Piece captured) {
        toggleSquares(piece, white, from, to);
        if (captured != null) {
            toggleSquare(captured, !white, to);
            state.key = Zobrist.updatePiece(state.key, to, captured, !white);
            if (captured == Piece.PAWN) {
                state.pawnKey = Zobrist.updatePiece(state.pawnKey, to, captured, !white);
            }
        }
        pieces[from] = null;
        pieces[to] = piece;
        state.key = Zobrist.updatePiece(state.key, from, to, piece, white);
        if (piece == Piece.PAWN) {
            state.pawnKey = Zobrist.updatePiece(state.pawnKey, from, to, piece, white);
        }
    }

    private void updateGameState(int from, int to, Piece piece, Piece captured, Move move) {
        state.captured = captured;
        final boolean resetClock = captured != null || Piece.PAWN.equals(piece);
        state.halfMoveClock = resetClock ? 0 : ++state.halfMoveClock;

        final int castleRights = updateCastleRights(from, to, piece);
        state.key = Zobrist.updateCastlingRights(state.key, state.rights, castleRights);
        state.rights = castleRights;

        final int enPassantFile = move.isPawnDoubleMove() ? Board.file(to) : -1;
        state.key = Zobrist.updateEnPassantFile(state.key, state.enPassantFile, enPassantFile);
        state.enPassantFile = enPassantFile;

        state.key = Zobrist.updateSideToMove(state.key);
    }

    private void unmakeCastlingMove(int from, int to) {
        toggleSquares(Piece.KING, white, to, from);
        final boolean kingside = Board.file(to) == 6;
        final int rookFrom, rookTo;
        if (kingside) {
            rookFrom = white ? 5 : 61;
            rookTo = white ? 7 : 63;
        } else {
            rookFrom = white ? 3 : 59;
            rookTo = white ? 0 : 56;
        }
        toggleSquares(Piece.ROOK, white, rookFrom, rookTo);
        pieces[from] = Piece.KING;
        pieces[to] = null;
        pieces[rookTo] = Piece.ROOK;
        pieces[rookFrom] = null;
    }

    private void unmakePromotionMove(int from, int to, Piece promotionPiece) {
        toggleSquare(promotionPiece, white, to);
        toggleSquare(Piece.PAWN, white, from);
        if (state.getCaptured() != null) {
            toggleSquare(state.getCaptured(), !white, to);
        }
        pieces[from] = Piece.PAWN;
        pieces[to] = state.getCaptured() != null ? state.getCaptured() : null;
    }

    private void unmakeEnPassantMove(int from, int to) {
        toggleSquares(Piece.PAWN, white, to, from);
        final int captureSquare = white ? to - 8 : to + 8;
        toggleSquare(Piece.PAWN, !white, captureSquare);
        pieces[from] = Piece.PAWN;
        pieces[to] = null;
        pieces[captureSquare] = Piece.PAWN;
    }

    private void unmakeStandardMove(int from, int to, Piece piece) {
        toggleSquares(piece, white, to, from);
        if (state.getCaptured() != null) {
            toggleSquare(state.getCaptured(), !white, to);
        }
        pieces[from] = piece;
        pieces[to] = state.getCaptured() != null ? state.getCaptured() : null;
    }

    /**
     * Make a 'null' move, meaning the side to move passes their turn and gives the opponent a double-move. Used exclusively
     * during null-move pruning during search.
     */
    public void makeNullMove() {
        white = !white;
        final long key = Zobrist.updateKeyAfterNullMove(state.getKey(), state.getEnPassantFile());
        GameState newState = new GameState(key, state.getPawnKey(), null, -1, state.getRights(), 0);
        stateHistory.push(state);
        state = newState;
    }

    /**
     * Unmake the 'null' move used during null-move pruning to try and prove a beta cut-off.
     */
    public void unmakeNullMove() {
        white = !white;
        state = stateHistory.pop();
    }

    public void toggleSquares(Piece type, boolean white, int from, int to) {
        final long toggleMask = (1L << from | 1L << to);
        toggle(type, white, toggleMask);
    }

    public void toggleSquare(Piece type, boolean white, int square) {
        final long toggleMask = 1L << square;
        toggle(type, white, toggleMask);
    }

    private void toggle(Piece type, boolean white, long toggleMask) {
        bitboards[type.index()] ^= toggleMask;
        bitboards[white ? Piece.WHITE_PIECES : Piece.BLACK_PIECES] ^= toggleMask;
        bitboards[Piece.OCCUPIED] ^= toggleMask;
    }

    public void removeKing(boolean white) {
        final long toggleMask = getKing(white);
        bitboards[Piece.KING.index()] ^= toggleMask;
        bitboards[white ? Piece.WHITE_PIECES : Piece.BLACK_PIECES] ^= toggleMask;
        bitboards[Piece.OCCUPIED] ^= toggleMask;
    }

    public void addKing(int kingSquare, boolean white) {
        final long toggleMask = 1L << kingSquare;
        bitboards[Piece.KING.index()] |= toggleMask;
        bitboards[white ? Piece.WHITE_PIECES : Piece.BLACK_PIECES] |= toggleMask;
        bitboards[Piece.OCCUPIED] |= toggleMask;
    }

    private int updateCastleRights(int from, int to, Piece pieceType) {
        int newRights = state.getRights();
        if (newRights == 0b0000) {
            // Both sides already lost castling rights, so nothing to calculate.
            return newRights;
        }
        // Any move by the king removes castling rights.
        if (Piece.KING.equals(pieceType)) {
            newRights &= white ? Bits.CLEAR_WHITE_CASTLING_MASK : Bits.CLEAR_BLACK_CASTLING_MASK;
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
        return pieces[square];
    }

    public long getPawns(boolean white) {
        return bitboards[Piece.PAWN.index()] & getPieces(white);
    }

    public long getKnights(boolean white) {
        return bitboards[Piece.KNIGHT.index()] & getPieces(white);
    }

    public long getBishops(boolean white) {
        return bitboards[Piece.BISHOP.index()] & getPieces(white);
    }

    public long getRooks(boolean white) {
        return bitboards[Piece.ROOK.index()] & getPieces(white);
    }

    public long getQueens(boolean white) {
        return bitboards[Piece.QUEEN.index()] & getPieces(white);
    }

    public long getKing(boolean white) {
        return bitboards[Piece.KING.index()] & getPieces(white);
    }

    public long getPieces(boolean white) {
        return bitboards[white ? Piece.WHITE_PIECES : Piece.BLACK_PIECES];
    }

    public void setBitboards(long[] bitboards) {
        this.bitboards = bitboards;
    }

    public void setPieces(Piece[] pieces) {
        this.pieces = pieces;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setStateHistory(Deque<GameState> stateHistory) {
        this.stateHistory = stateHistory;
    }

    public void setMoves(Deque<Move> moves) {
        this.moves = moves;
    }

    public long getPawns() {
        return bitboards[Piece.PAWN.index()];
    }

    public long getKnights() {
        return bitboards[Piece.KNIGHT.index()];
    }

    public long getBishops() {
        return bitboards[Piece.BISHOP.index()];
    }

    public long getRooks() {
        return bitboards[Piece.ROOK.index()];
    }

    public long getQueens() {
        return bitboards[Piece.QUEEN.index()];
    }

    public long getKings() {
        return bitboards[Piece.KING.index()];
    }

    public long getWhitePieces() {
        return bitboards[Piece.WHITE_PIECES];
    }

    public long getBlackPieces() {
        return bitboards[Piece.BLACK_PIECES];
    }

    public long getOccupied() {
        return bitboards[Piece.OCCUPIED];
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public boolean isWhite() {
        return white;
    }

    public GameState getState() {
        return state;
    }

    public Deque<GameState> getStateHistory() {
        return stateHistory;
    }

    public Deque<Move> getMoves() {
        return moves;
    }

    public long key() {
        return state.getKey();
    }

    public long pawnKey() {
        return state.getPawnKey();
    }

    public int countPieces() {
        return Bitwise.countBits(getOccupied());
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

    public static Board from(String fen) {
        return FEN.toBoard(fen);
    }

    public Board copy() {
        final Board newBoard = new Board();
        newBoard.setBitboards(Arrays.copyOf(bitboards, bitboards.length));
        newBoard.setWhite(this.isWhite());
        newBoard.setState(this.getState().copy());
        Deque<GameState> gameStateHistory = new ArrayDeque<>();
        this.getStateHistory().forEach(gameState -> gameStateHistory.add(gameState.copy()));
        newBoard.setStateHistory(gameStateHistory);
        Deque<Move> moveHistory = new ArrayDeque<>();
        this.getMoves().forEach(move -> moveHistory.add(new Move(move.value())));
        newBoard.setMoves(moveHistory);
        newBoard.setPieces(Arrays.copyOf(this.getPieces(), this.getPieces().length));
        return newBoard;
    }

}
