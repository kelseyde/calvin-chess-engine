package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveType;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class PawnMoveGenerator implements PseudoLegalMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.PAWN;

    public Set<Move> generatePseudoLegalMoves(Board board) {

        Colour turn = board.getTurn();

        Set<Move> moves = new HashSet<>();

        long pawns = turn.isWhite() ? board.getWhitePawns() : board.getBlackPawns();
        long opponentPieces = turn.isWhite() ? board.getBlackPieces() : board.getWhitePieces();
        long occupied = board.getOccupied();
        long enPassantTarget = board.getEnPassantTarget();
        long copy;

        long singleAdvances = turn.isWhite() ?
                BitBoard.shiftNorth(pawns) &~ occupied &~ BitBoards.RANK_8 :
                BitBoard.shiftSouth(pawns) &~ occupied &~ BitBoards.RANK_1;

        copy = singleAdvances;
        while (copy != 0) {
            int endSquare = BitBoard.scanForward(copy);
            int startSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            moves.add(move(startSquare, endSquare).build());
            copy &= (copy - 1);
        }

        long doubleAdvances = turn.isWhite() ?
                BitBoard.shiftNorth(singleAdvances) &~ occupied & BitBoards.RANK_4 :
                BitBoard.shiftSouth(singleAdvances) &~ occupied & BitBoards.RANK_5;
        while (doubleAdvances != 0) {
            int endSquare = BitBoard.scanForward(doubleAdvances);
            int enPassantTargetSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            int startSquare = turn.isWhite() ? endSquare - 16 : endSquare + 16;
            moves.add(move(startSquare, endSquare)
                    .enPassantTarget(1L << enPassantTargetSquare)
                    .build());
            doubleAdvances &= (doubleAdvances - 1);
        }

        long leftCaptures = turn.isWhite() ?
                BitBoard.shiftNorthWest(pawns) & opponentPieces &~ BitBoards.FILE_H &~ BitBoards.RANK_8 :
                BitBoard.shiftSouthWest(pawns) & opponentPieces &~ BitBoards.FILE_H &~ BitBoards.RANK_1;
        while (leftCaptures != 0) {
            int endSquare = BitBoard.scanForward(leftCaptures);
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).build());
            leftCaptures  &= (leftCaptures - 1);
        }

        long rightCaptures = turn.isWhite() ?
                BitBoard.shiftNorthEast(pawns) & opponentPieces &~ BitBoards.FILE_A &~ BitBoards.RANK_8 :
                BitBoard.shiftSouthEast(pawns) & opponentPieces &~ BitBoards.FILE_A &~ BitBoards.RANK_1;
        while (rightCaptures != 0) {
            int endSquare = BitBoard.scanForward(rightCaptures);
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).build());
            rightCaptures &= (rightCaptures - 1);
        }

        long enPassantLeftCaptures = turn.isWhite() ?
                BitBoard.shiftNorthWest(pawns) & enPassantTarget &~ BitBoards.FILE_H :
                BitBoard.shiftSouthWest(pawns) & enPassantTarget &~ BitBoards.FILE_H;
        while (enPassantLeftCaptures != 0) {
            int endSquare = BitBoard.scanForward(enPassantLeftCaptures);
            int enPassantCaptureSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT)
                    .enPassantCapture(1L << enPassantCaptureSquare)
                    .build());
            enPassantLeftCaptures &= (enPassantLeftCaptures - 1);
        }

        long enPassantRightCaptures = turn.isWhite() ?
                BitBoard.shiftNorthEast(pawns) & enPassantTarget &~ BitBoards.FILE_A :
                BitBoard.shiftSouthEast(pawns) & enPassantTarget &~ BitBoards.FILE_A;
        while (enPassantRightCaptures != 0) {
            int endSquare = BitBoard.scanForward(enPassantRightCaptures);
            int enPassantCaptureSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT)
                    .enPassantCapture(1L << enPassantCaptureSquare)
                    .build());
            enPassantRightCaptures &= (enPassantRightCaptures - 1);
        }

        long advancePromotions = turn.isWhite() ?
                BitBoard.shiftNorth(pawns) &~ occupied & BitBoards.RANK_8 :
                BitBoard.shiftSouth(pawns) &~ occupied & BitBoards.RANK_1;
        while (advancePromotions != 0) {
            int endSquare = BitBoard.scanForward(advancePromotions);
            int startSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            advancePromotions &= (advancePromotions - 1);
        }

        long captureLeftPromotions = turn.isWhite() ?
                BitBoard.shiftNorthWest(pawns) & opponentPieces &~ BitBoards.FILE_H & BitBoards.RANK_8 :
                BitBoard.shiftSouthWest(pawns) & opponentPieces &~ BitBoards.FILE_H & BitBoards.RANK_1;
        while (captureLeftPromotions != 0) {
            int endSquare = BitBoard.scanForward(captureLeftPromotions);
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureLeftPromotions &= (captureLeftPromotions - 1);
        }

        long captureRightPromotions = turn.isWhite() ?
                BitBoard.shiftNorthEast(pawns) & opponentPieces &~ BitBoards.FILE_A & BitBoards.RANK_8 :
                BitBoard.shiftSouthEast(pawns) & opponentPieces &~ BitBoards.FILE_A & BitBoards.RANK_1;
        while (captureRightPromotions != 0) {
            int endSquare = BitBoard.scanForward(captureRightPromotions);
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureRightPromotions &= (captureRightPromotions - 1);
        }

        return moves;

    }

    private Set<Move> getPromotionMoves(int startSquare, int endSquare) {
        return Set.of(
                move(startSquare, endSquare).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.QUEEN).build(),
                move(startSquare, endSquare).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.ROOK).build(),
                move(startSquare, endSquare).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.BISHOP).build(),
                move(startSquare, endSquare).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.KNIGHT).build());
    }

    private Move.MoveBuilder move(int startSquare, int endSquare) {
        return Move.builder()
                .pieceType(PieceType.PAWN)
                .startSquare(startSquare)
                .endSquare(endSquare);
    }

}
