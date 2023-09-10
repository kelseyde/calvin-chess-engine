package com.kelseyde.calvin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Does not know anything
 * about the game state (move number, whose turn, en-passant captures etc.) - it only stores the current position of the
 * pieces.
 */
@Data
@AllArgsConstructor
public class Board {

    private static final Piece[] STARTING_POSITION = Stream.of(
            'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R',
            'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
            'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p',
            'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r')
            .map(Piece::fromChar)
            .toArray(Piece[]::new);

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

    public void clear() {
        this.squares = new Piece[64];
    }

    public static Board empty() {
        return new Board(new Piece[64]);
    }

    public static Board startingPosition() {
        return new Board(STARTING_POSITION);
    }

}
