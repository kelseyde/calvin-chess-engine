package com.kelseyde.calvin.model;

import com.kelseyde.calvin.utils.BoardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Does not know anything
 * about the game state (move number, whose turn, en-passant captures etc.) - it only stores the current position of the
 * pieces.
 */
@Data
@AllArgsConstructor
public class Board {

    private static final Character[] EMPTY_BOARD = new Character[]{
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'};

    private static final Character[] STARTING_POSITION = new Character[]{
            'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R',
            'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p',
            'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'};

    private Piece[] squares;

    public void setPiece(int square, Piece piece) {
        squares[square] = piece;
    }

    public void unsetPiece(int square) {
        squares[square] = null;
    }

    public Optional<Piece> pieceAt(int i) {
        return Optional.ofNullable(squares[i]);
    }

    public boolean pieceIs(int i, Colour colour, PieceType type) {
        return pieceAt(i)
                .filter(piece -> piece.getColour().equals(colour) && piece.getType().equals(type))
                .isPresent();
    }

    public boolean isSquareEmpty(int i) {
        return pieceAt(i).isEmpty();
    }

    public void clear() {
        this.squares = new Piece[64];
    }

    public static Board empty() {
        return BoardUtils.fromCharArray(EMPTY_BOARD);
    }

    public static Board startingPosition() {
        return BoardUtils.fromCharArray(STARTING_POSITION);
    }

}
