package com.kelseyde.calvin.model;

import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Also maintains the position
 * 'metadata': side to move, castling rights, en passant target square, full-move counter and half-move counter (used for
 * the 50-move rule) Equivalent to a FEN string.
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

    public void applyMove(Move move) {
        Piece piece = getPieceAt(move.getStartSquare()).orElseThrow();
        unsetPiece(move.getStartSquare());
        setPiece(move.getEndSquare(), piece);

        switch (move.getMoveType()) {
            case EN_PASSANT -> {
                unsetPiece(move.getEnPassantCapturedSquare());
            }
            case PROMOTION -> {
                Piece promotedPiece = new Piece(piece.getColour(), move.getPromotionPieceType());
                setPiece(move.getEndSquare(), promotedPiece);
            }
            case CASTLE -> {
                Piece rook = getPieceAt(move.getRookStartSquare()).orElseThrow();
                unsetPiece(move.getRookStartSquare());
                setPiece(move.getRookEndSquare(), rook);
            }
        }

        enPassantTargetSquare = move.getEnPassantTargetSquare();
        if (move.isNegatesKingsideCastling()) {
            castlingRights.get(turn).setKingSide(false);
        }
        if (move.isNegatesQueensideCastling()) {
            castlingRights.get(turn).setQueenSide(false);
        }
        if (Colour.BLACK.equals(turn)) {
            ++fullMoveCounter;
        }
        boolean resetHalfMoveClock = move.isCapture() || PieceType.PAWN.equals(move.getPieceType());
        if (resetHalfMoveClock) {
            halfMoveCounter = 0;
        } else {
            ++halfMoveCounter;
        }

        turn = turn.oppositeColour();
    }

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
