package com.kelseyde.calvin.board;

import com.kelseyde.calvin.board.Bits.Castling;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.utils.notation.FEN;

import java.util.Arrays;

/**
 * Represents the current state of the chess board, including the positions of the pieces, the side to move, en passant
 * rights, fifty-move counter, and the move counter. Includes functions to 'make' and 'unmake' moves on the board, which
 * are fundamental to both the search and evaluation algorithms. Uses bitboards to represent the pieces and 'toggling'
 * functions to set and unset pieces.
 *
 * @see <a href="https://www.chessprogramming.org/Board_Representation">Chess Programming Wiki</a>
 */
public class Board {

    private long pawns;
    private long knights;
    private long bishops;
    private long rooks;
    private long queens;
    private long kings;
    private long whitePieces;
    private long blackPieces;

    private Piece[] pieces;
    private BoardState state;
    private BoardState[] states;
    private Move[] moves;
    private boolean white;
    private int ply;

    public Board() {
        this.pawns       = 0L;
        this.knights     = 0L;
        this.bishops     = 0L;
        this.rooks       = 0L;
        this.queens      = 0L;
        this.kings       = 0L;
        this.whitePieces = 0L;
        this.blackPieces = 0L;
        this.pieces      = new Piece[Square.COUNT];
        this.moves       = new Move[Search.MAX_DEPTH];
        this.states      = new BoardState[Search.MAX_DEPTH];
        this.state       = new BoardState();
        this.white       = true;
        this.ply         = 0;
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
        states[ply] = state.copy();

        if (move.isPawnDoubleMove())  makePawnDoubleMove(from, to);
        else if (move.isCastling())   makeCastleMove(from, to);
        else if (move.isPromotion())  makePromotionMove(from, to, move.promoPiece(), captured);
        else if (move.isEnPassant())  makeEnPassantMove(from, to);
        else                          makeStandardMove(from, to, piece, captured);

        updateState(from, to, piece, captured, move);
        moves[ply++] = move;
        white = !white;
        return true;

    }

    /**
     * Reverts the internal board representation to the state before the last move. Toggles the piece bitboards to move the
     * piece + reinstate the captured piece, plus special rules for pawn double-moves, castling, promotion and en passant.
     */
    public void unmakeMove() {

        white = !white;
        final Move move = moves[--ply];
        final int from = move.from();
        final int to = move.to();
        final Piece piece = pieceAt(to);

        if (move.isCastling())        unmakeCastlingMove(from, to);
        else if (move.isPromotion())  unmakePromotionMove(from, to, move.promoPiece());
        else if (move.isEnPassant())  unmakeEnPassantMove(from, to);
        else                          unmakeStandardMove(from, to, piece);

        state = states[ply];

    }

    private void makePawnDoubleMove(int from, int to) {
        // Handle moving pawn
        toggleSquares(from, to, Piece.PAWN, white);
        updateMailbox(from, to, Piece.PAWN);
        updateKeys(from, to, Piece.PAWN, white);
    }

    private void makeCastleMove(int from, int to) {
        // Handle castling king
        toggleSquares(from, to, Piece.KING, white);
        updateMailbox(from, to, Piece.KING);
        updateKeys(from, to, Piece.KING, white);
        // Handle castling rook
        final boolean kingside = File.of(to) == 6;
        final int rookFrom = Castling.rookFrom(kingside, white);
        final int rookTo = Castling.rookTo(kingside, white);
        toggleSquares(rookFrom, rookTo, Piece.ROOK, white);
        updateMailbox(rookFrom, rookTo, Piece.ROOK);
        updateKeys(rookFrom, rookTo, Piece.ROOK, white);
    }

    private void makeEnPassantMove(int from, int to) {
        // Handle capturing pawn
        toggleSquares(from, to, Piece.PAWN, white);
        updateMailbox(from, to, Piece.PAWN);
        updateKeys(from, to, Piece.PAWN, white);
        // Handle captured pawn
        final int pawnSquare = white ? to - 8 : to + 8;
        toggleSquare(pawnSquare, Piece.PAWN, !white);
        updateMailbox(pawnSquare, null);
        updateKeys(pawnSquare, Piece.PAWN, !white);
    }

    private void makePromotionMove(int from, int to, Piece promoted, Piece captured) {
        // Remove promoting pawn
        toggleSquare(from, Piece.PAWN, white);
        updateKeys(from, Piece.PAWN, white);
        // Add promoted piece
        toggleSquare(to, promoted, white);
        updateMailbox(from, to, promoted);
        updateKeys(to, promoted, white);
        if (captured != null) {
            // Handle captured piece
            toggleSquare(to, captured, !white);
            updateKeys(to, captured, !white);
        }
    }

    private void makeStandardMove(int from, int to, Piece piece, Piece captured) {
        // Handle moving piece
        toggleSquares(from, to, piece, white);
        updateKeys(from, to, piece, white);
        updateMailbox(from, to, piece);
        if (captured != null) {
            // Remove captured piece
            toggleSquare(to, captured, !white);
            updateKeys(to, captured, !white);
        }
    }

    private void updateState(int from, int to, Piece piece, Piece captured, Move move) {
        state.captured = captured;
        final boolean resetClock = captured != null || Piece.PAWN.equals(piece);
        state.halfMoveClock = resetClock ? 0 : ++state.halfMoveClock;

        final int castleRights = Castling.updateRights(state.rights, from, to, piece, white);
        state.key ^= Key.rights(state.rights, castleRights);
        state.rights = castleRights;

        final int enPassantFile = move.isPawnDoubleMove() ? File.of(to) : -1;
        state.key ^= Key.enPassant(state.enPassantFile, enPassantFile);
        state.enPassantFile = enPassantFile;

        state.key ^= Key.sideToMove();
    }

    private void unmakeCastlingMove(int from, int to) {
        // Put back king
        toggleSquares(to, from, Piece.KING, white);
        updateMailbox(to, from, Piece.KING);
        // Put back rook
        final boolean kingside = File.of(to) == 6;
        final int rookTo = Castling.rookTo(kingside, white);
        final int rookFrom = Castling.rookFrom(kingside, white);
        toggleSquares(rookTo, rookFrom, Piece.ROOK, white);
        updateMailbox(rookTo, rookFrom, Piece.ROOK);
    }

    private void unmakePromotionMove(int from, int to, Piece promotionPiece) {
        // Remove promoted piece
        toggleSquare(to, promotionPiece, white);
        // Put back promoting pawn
        updateMailbox(from, Piece.PAWN);
        toggleSquare(from, Piece.PAWN, white);
        // Put back captured piece
        if (state.getCaptured() != null) {
            toggleSquare(to, state.getCaptured(), !white);
        }
        // If no piece was captured, this correctly nullifies the promo square
        updateMailbox(to, state.getCaptured());
    }

    private void unmakeEnPassantMove(int from, int to) {
        // Put back capturing pawn
        toggleSquares(to, from, Piece.PAWN, white);
        updateMailbox(to, from, Piece.PAWN);
        // Add back captured pawn
        final int captureSquare = white ? to - 8 : to + 8;
        toggleSquare(captureSquare, Piece.PAWN, !white);
        updateMailbox(captureSquare, Piece.PAWN);
    }

    private void unmakeStandardMove(int from, int to, Piece piece) {
        // Put back moving piece
        toggleSquares(to, from, piece, white);
        updateMailbox(to, from, piece);
        if (state.getCaptured() != null) {
            // Add back captured piece
            toggleSquare(to, state.getCaptured(), !white);
            updateMailbox(to, state.getCaptured());
        }
    }

    /**
     * Make a 'null' move, meaning the side to move passes their turn and gives the opponent a double-move. Used exclusively
     * during null-move pruning during search.
     */
    public void makeNullMove() {
        white = !white;
        final long key = state.key ^ Key.nullMove(state.enPassantFile);
        final long[] nonPawnKeys = new long[] {state.nonPawnKeys[0], state.nonPawnKeys[1]};
        final BoardState newState = new BoardState(key, state.pawnKey, nonPawnKeys, null, -1, state.getRights(), 0);
        states[ply++] = state;
        state = newState;
    }

    /**
     * Unmake the 'null' move used during null-move pruning to try and prove a beta cut-off.
     */
    public void unmakeNullMove() {
        white = !white;
        state = states[--ply];
    }

    public void toggleSquares(int from, int to, Piece piece, boolean white) {
        final long toggleMask = Bits.of(from) | Bits.of(to);
        toggle(toggleMask, piece, white);
    }

    public void toggleSquare(int square, Piece piece, boolean white) {
        final long toggleMask = Bits.of(square);
        toggle(toggleMask, piece, white);
    }

    private void toggle(long mask, Piece type, boolean white) {
        switch (type) {
            case PAWN ->    pawns ^= mask;
            case KNIGHT ->  knights ^= mask;
            case BISHOP ->  bishops ^= mask;
            case ROOK ->    rooks ^= mask;
            case QUEEN ->   queens ^= mask;
            case KING ->    kings ^= mask;
        }
        if (white) {
            whitePieces ^= mask;
        } else {
            blackPieces ^= mask;
        }
    }

    private void updateKeys(int from, int to, Piece piece, boolean white) {
        final long hash = Key.piece(from, to, piece, white);
        state.key ^= hash;
        if (piece == Piece.PAWN) {
            state.pawnKey ^= hash;
        } else {
            final int colourIndex = Colour.index(white);
            state.nonPawnKeys[colourIndex] ^= hash;
        }
    }

    private void updateKeys(int square, Piece piece, boolean white) {
        final long hash = Key.piece(square, piece, white);
        state.key ^= hash;
        if (piece == Piece.PAWN) {
            state.pawnKey ^= hash;
        } else {
            final int colourIndex = Colour.index(white);
            state.nonPawnKeys[colourIndex] ^= hash;
        }
    }

    private void updateMailbox(int from, int to, Piece piece) {
        pieces[from] = null;
        pieces[to] = piece;
    }

    private void updateMailbox(int square, Piece piece) {
        pieces[square] = piece;
    }

    public void removeKing(boolean white) {
        final long toggleMask = white ? (kings & whitePieces) : (kings & blackPieces);
        kings ^= toggleMask;
        if (white) {
            whitePieces ^= toggleMask;
        } else {
            blackPieces ^= toggleMask;
        }
    }

    public void addKing(int kingSquare, boolean white) {
        final long toggleMask = Bits.of(kingSquare);
        kings |= toggleMask;
        if (white) {
            whitePieces |= toggleMask;
        } else {
            blackPieces |= toggleMask;
        }
    }

    public Piece pieceAt(int square) {
        return pieces[square];
    }

    public boolean isCapture(Move move) {
        return move.isEnPassant() || pieceAt(move.to()) != null;
    }

    public boolean isQuiet(Move move) {
        return !move.isPromotion() && !isCapture(move);
    }

    public long pawns(boolean white) {
        final long side = white ? whitePieces : blackPieces;
        return pawns & side;
    }

    public long knights(boolean white) {
        final long side = white ? whitePieces : blackPieces;
        return knights & side;
    }

    public long bishops(boolean white) {
        final long side = white ? whitePieces : blackPieces;
        return bishops & side;
    }

    public long rooks(boolean white) {
        final long side = white ? whitePieces : blackPieces;
        return rooks & side;
    }

    public long queens(boolean white) {
        final long side = white ? whitePieces : blackPieces;
        return queens & side;
    }

    public long king(boolean white) {
        final long side = white ? whitePieces : blackPieces;
        return kings & side;
    }

    public long pieces(boolean white) {
        return white ? whitePieces : blackPieces;
    }

    public long pawns() {
        return pawns;
    }

    public long knights() {
        return knights;
    }

    public long bishops() {
        return bishops;
    }

    public long rooks() {
        return rooks;
    }

    public long queens() {
        return queens;
    }

    public long kings() {
        return kings;
    }

    public long whitePieces() {
        return whitePieces;
    }

    public long blackPieces() {
        return blackPieces;
    }

    public long occupied() {
        return whitePieces | blackPieces;
    }

    public Piece[] pieces() {
        return pieces;
    }

    public boolean isWhite() {
        return white;
    }

    public BoardState state() {
        return state;
    }

    public BoardState[] states() {
        return states;
    }

    public Move[] moves() {
        return moves;
    }

    public int ply() {
        return ply;
    }

    public long key() {
        return state.getKey();
    }

    public long pawnKey() {
        return state.getPawnKey();
    }

    public long[] nonPawnKeys() {
        return state.nonPawnKeys;
    }

    public void setPawns(long pawns) {
        this.pawns = pawns;
    }

    public void setKnights(long knights) {
        this.knights = knights;
    }

    public void setBishops(long bishops) {
        this.bishops = bishops;
    }

    public void setRooks(long rooks) {
        this.rooks = rooks;
    }

    public void setQueens(long queens) {
        this.queens = queens;
    }

    public void setKings(long kings) {
        this.kings = kings;
    }

    public void setWhitePieces(long whitePieces) {
        this.whitePieces = whitePieces;
    }

    public void setBlackPieces(long blackPieces) {
        this.blackPieces = blackPieces;
    }

    public void setPieces(Piece[] pieces) {
        this.pieces = pieces;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public void setState(BoardState state) {
        this.state = state;
    }

    public void setStates(BoardState[] states) {
        this.states = states;
    }

    public void setMoves(Move[] moves) {
        this.moves = moves;
    }

    public boolean hasPiecesRemaining(boolean white) {
        return white ?
                (knights(true) != 0 || bishops(true) != 0 || rooks(true) != 0 || queens(true) != 0) :
                (knights(false) != 0 || bishops(false) != 0 || rooks(false) != 0 || queens(false) != 0);
    }

    public static Board from(String fen) {
        return FEN.toBoard(fen);
    }

    public Board copy() {
        final Board newBoard = new Board();
        newBoard.setPawns(this.pawns());
        newBoard.setKnights(this.knights());
        newBoard.setBishops(this.bishops());
        newBoard.setRooks(this.rooks());
        newBoard.setQueens(this.queens());
        newBoard.setKings(this.kings());
        newBoard.setWhitePieces(this.whitePieces());
        newBoard.setBlackPieces(this.blackPieces());
        newBoard.setWhite(this.isWhite());
        newBoard.setState(this.state().copy());
        BoardState[] newStates = new BoardState[this.states().length];
        for (int i = 0; i < this.states().length; i++) {
            if (this.states()[i] == null) {
                break;
            }
            newStates[i] = this.states()[i].copy();
        }
        newBoard.setStates(newStates);
        Move[] newMoves = new Move[this.moves().length];
        for (int i = 0; i < this.moves().length; i++) {
            if (this.moves()[i] == null) {
                break;
            }
            newMoves[i] = new Move(this.moves()[i].value());
        }
        newBoard.setMoves(newMoves);
        newBoard.setPieces(Arrays.copyOf(this.pieces(), this.pieces().length));
        return newBoard;
    }

    public void print() {

        for (int rank = 7; rank >= 0; --rank) {
            System.out.print(" +---+---+---+---+---+---+---+---+\n");

            for (int file = 0; file < 8; ++file) {
                int sq = Square.of(rank, file);
                Piece piece = pieceAt(sq);
                if (piece == null) {
                    System.out.print(" |  ");
                    continue;
                }
                boolean white = (whitePieces & Bits.of(sq)) != 0;
                System.out.print(" | " + (white ? piece.code().toUpperCase() : piece.code()));
            }

            System.out.print(" | " + (rank + 1) + "\n");
        }

        System.out.print(" +---+---+---+---+---+---+---+---+\n");
        System.out.print("   a   b   c   d   e   f   g   h\n\n");

        System.out.print((white ? "White" : "Black") + " to move\n");

    }

}
