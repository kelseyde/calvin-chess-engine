package com.kelseyde.calvin.model;

import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents the current board state, as a 64x one-dimensional array of {@link Piece} pieces. Also maintains the position
 * 'metadata': side to move, castling rights, en passant target square, full-move counter and half-move counter (used for
 * the 50-move rule) Equivalent to a FEN string.
 */
@Data
public class Board {

    private Piece[] squares;

    private BitBoard whitePawns;
    private BitBoard whiteKnights;
    private BitBoard whiteBishops;
    private BitBoard whiteRooks;
    private BitBoard whiteQueens;
    private BitBoard whiteKing;

    private BitBoard blackPawns;
    private BitBoard blackKnights;
    private BitBoard blackBishops;
    private BitBoard blackRooks;
    private BitBoard blackQueens;
    private BitBoard blackKing;

    private BitBoard whitePieces;
    private BitBoard blackPieces;

    private BitBoard occupied;

    private BitBoard enPassantTarget;

    public Board() {
        whitePawns = BitBoards.WHITE_PAWNS_START;
        whiteKnights = BitBoards.WHITE_KNIGHTS_START;
        whiteBishops = BitBoards.WHITE_BISHOPS_START;
        whiteRooks = BitBoards.WHITE_ROOKS_START;
        whiteQueens = BitBoards.WHITE_QUEENS_START;
        whiteKing = BitBoards.WHITE_KING_START;

        blackPawns = BitBoards.BLACK_PAWNS_START;
        blackKnights = BitBoards.BLACK_KNIGHTS_START;
        blackBishops = BitBoards.BLACK_BISHOPS_START;
        blackRooks = BitBoards.BLACK_ROOKS_START;
        blackQueens = BitBoards.BLACK_QUEENS_START;
        blackKing = BitBoards.BLACK_KING_START;

        enPassantTarget = new BitBoard();

        recalculatePieces();
    }

    public static Board emptyBoard() {
        Board board = new Board();
        board.whitePawns = new BitBoard();
        board.whiteKnights = new BitBoard();
        board.whiteBishops = new BitBoard();
        board.whiteRooks = new BitBoard();
        board.whiteQueens = new BitBoard();
        board.whiteKing = new BitBoard();

        board.blackPawns = new BitBoard();
        board.blackKnights = new BitBoard();
        board.blackBishops = new BitBoard();
        board.blackRooks = new BitBoard();
        board.blackQueens = new BitBoard();
        board.blackKing = new BitBoard();

        board.enPassantTarget = new BitBoard();

        board.recalculatePieces();

        return board;
    }

    private Colour turn = Colour.WHITE;

    private Map<Colour, CastlingRights> castlingRights = BoardUtils.getDefaultCastlingRights();

    private int enPassantTargetSquare = -1;

    private int halfMoveCounter = 0;

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
        switch (piece.toPieceCode()) {
            case "wP" -> whitePawns.setBit(square);
            case "wN" -> whiteKnights.setBit(square);
            case "wB" -> whiteBishops.setBit(square);
            case "wR" -> whiteRooks.setBit(square);
            case "wQ" -> whiteQueens.setBit(square);
            case "wK" -> whiteKing.setBit(square);
            case "bP" -> blackPawns.setBit(square);
            case "bN" -> blackKnights.setBit(square);
            case "bB" -> blackBishops.setBit(square);
            case "bR" -> blackRooks.setBit(square);
            case "bQ" -> blackQueens.setBit(square);
            case "bK" -> blackKing.setBit(square);
        }
        recalculatePieces();
    }

    public void unsetPiece(int square) {
        occupied.unsetBit(square);

        whitePawns = whitePawns.and(occupied);
        whiteKnights = whiteKnights.and(occupied);
        whiteBishops = whiteBishops.and(occupied);
        whiteRooks = whiteRooks.and(occupied);
        whiteQueens = whiteQueens.and(occupied);
        whiteKing = whiteKing.and(occupied);

        blackPawns = blackPawns.and(occupied);
        blackKnights = blackKnights.and(occupied);
        blackBishops = blackBishops.and(occupied);
        blackRooks = blackRooks.and(occupied);
        blackQueens = blackQueens.and(occupied);
        blackKing = blackKing.and(occupied);

        recalculatePieces();
    }

    public Set<Integer> getPiecePositions(Colour colour) {
        return IntStream.range(0, 64)
                .filter(square -> getPieceAt(square).filter(piece -> colour.equals(piece.getColour())).isPresent())
                .boxed()
                .collect(Collectors.toSet());
    }

    public List<Piece> getPieces(Colour colour) {
        return getPiecePositions(colour).stream()
                .map(square -> getPieceAt(square).orElseThrow())
                .toList();
    }

    public Board copy() {
        return null;
        // todo fix
//        Piece[] squaresCopy = new Piece[64];
//        IntStream.range(0, 64)
//                .forEach(square -> squaresCopy[square] = getPieceAt(square).map(Piece::copy).orElse(null));
//        return Board.builder()
//                .squares(squaresCopy)
//                .turn(turn)
//                .castlingRights(Map.of(
//                        Colour.WHITE, castlingRights.get(Colour.WHITE).copy(),
//                        Colour.BLACK, castlingRights.get(Colour.BLACK).copy()
//                ))
//                .enPassantTargetSquare(enPassantTargetSquare)
//                .halfMoveCounter(halfMoveCounter)
//                .fullMoveCounter(fullMoveCounter)
//                .build();
    }

    private void recalculatePieces() {
        whitePieces = whitePawns.or(whiteKnights).or(whiteBishops).or(whiteRooks).or(whiteQueens).or(whiteKing);
        blackPieces = blackPawns.or(blackKnights).or(blackBishops).or(blackRooks).or(blackQueens).or(blackKing);
        occupied = whitePieces.or(blackPieces);
    }

}
