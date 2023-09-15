package com.kelseyde.calvin.model;

import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
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
@Builder
public class Board {

    private Piece[] squares;

    @Builder.Default
    private Colour turn = Colour.WHITE;

    @Builder.Default
    private Map<Colour, CastlingRights> castlingRights = BoardUtils.getDefaultCastlingRights();

    @Builder.Default
    private int enPassantTargetSquare = -1;

    @Builder.Default
    private int halfMoveCounter = 0;

    @Builder.Default
    private int fullMoveCounter = 1;

    public Optional<Piece> getPieceAt(int square) {
        return Optional.ofNullable(squares[square]);
    }

    public void setPiece(int square, Piece piece) {
        squares[square] = piece;
    }

    public void unsetPiece(int square) {
        squares[square] = null;
    }

    public Set<Integer> getPiecePositions(Colour colour) {
        return IntStream.range(0, 64)
                .filter(square -> getPieceAt(square).filter(piece -> colour.equals(piece.getColour())).isPresent())
                .boxed()
                .collect(Collectors.toSet());
    }

    public Map<Integer, Piece> getPieces(Colour colour) {
        return getPiecePositions(colour).stream()
                .collect(Collectors.toMap(square -> square, square -> getPieceAt(square).orElseThrow()));
    }

    public void incrementHalfMoveCounter() {
        ++halfMoveCounter;
    }

    public void resetHalfMoveCounter() {
        halfMoveCounter = 0;
    }

    public void incrementMoveCounter() {
        ++fullMoveCounter;
    }

    public Board copy() {
        Piece[] squaresCopy = new Piece[64];
        IntStream.range(0, 64)
                .forEach(square -> squaresCopy[square] = getPieceAt(square).map(Piece::copy).orElse(null));
        return Board.builder()
                .squares(squaresCopy)
                .turn(turn)
                .castlingRights(Map.of(
                        Colour.WHITE, castlingRights.get(Colour.WHITE).copy(),
                        Colour.BLACK, castlingRights.get(Colour.BLACK).copy()
                ))
                .enPassantTargetSquare(enPassantTargetSquare)
                .halfMoveCounter(halfMoveCounter)
                .fullMoveCounter(fullMoveCounter)
                .build();
    }

}
