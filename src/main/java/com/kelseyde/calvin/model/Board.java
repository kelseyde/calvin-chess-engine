package com.kelseyde.calvin.model;

import com.kelseyde.calvin.utils.BoardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public static Board emptyBoard() {
        return BoardUtils.fromCharArray(EMPTY_BOARD);
    }

    public static Board startingPosition() {
        return BoardUtils.fromCharArray(STARTING_POSITION);
    }

    private Piece[] squares;

    public void setPiece(int square, Piece piece) {
        squares[square] = piece;
    }

    public void unsetPiece(int square) {
        squares[square] = null;
    }

    public Optional<Piece> pieceAt(int square) {
        return Optional.ofNullable(squares[square]);
    }

    public boolean pieceIs(int square, Colour colour, PieceType type) {
        return pieceAt(square)
                .filter(piece -> piece.getColour().equals(colour) && piece.getType().equals(type))
                .isPresent();
    }

    public Set<Integer> getPiecePositions(Colour colour) {
        return IntStream.range(0, 64)
                .filter(square -> pieceAt(square).filter(piece -> colour.equals(piece.getColour())).isPresent())
                .boxed()
                .collect(Collectors.toSet());
    }

    public Integer getKingSquare(Colour colour) {
        return Arrays.asList(squares).indexOf(new Piece(colour, PieceType.KING));
    }

    public boolean isSquareEmpty(int square) {
        return pieceAt(square).isEmpty();
    }

    public Board copy() {
        Piece[] squaresCopy = new Piece[64];
        IntStream.range(0, 64)
                .forEach(square -> squaresCopy[square] = pieceAt(square)
                        .map(Piece::copy)
                        .orElse(null));
        return new Board(squaresCopy);
    }

    public void clear() {
        this.squares = new Piece[64];
    }

}
