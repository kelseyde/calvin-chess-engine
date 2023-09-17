package com.kelseyde.calvin.service.game.generator.bitboard;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveType;

import java.util.HashSet;
import java.util.Set;

public class PawnMoveGenerator {

    public Set<Move> generateMoves(Board board) {

        Colour turn = board.getTurn();

        Set<Move> moves = new HashSet<>();

        BitBoard pawns = turn.isWhite() ? board.getWhitePawns() : board.getBlackPawns();
        BitBoard opponentPieces = turn.isWhite() ? board.getBlackPieces() : board.getWhitePieces();
        BitBoard occupied = board.getOccupied();
        BitBoard enPassantTarget = board.getEnPassantTarget();
        BitBoard copy;

        BitBoard singleAdvances = turn.isWhite() ?
                pawns.shiftNorth().not(occupied).not(BitBoards.RANK_8) :
                pawns.shiftSouth().not(occupied).not(BitBoards.RANK_1);
        copy = singleAdvances.copy();
        while (copy.isNotZero()) {
            int endSquare = copy.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            moves.add(move(startSquare, endSquare).build());
            copy.popLSB();
        }

        BitBoard doubleAdvances = turn.isWhite() ?
                singleAdvances.shiftNorth().not(occupied).and(BitBoards.RANK_4) :
                singleAdvances.shiftSouth().not(occupied).and(BitBoards.RANK_5);
        while (doubleAdvances.isNotZero()) {
            int endSquare = doubleAdvances.scanForward();
            int enPassantTargetSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            int startSquare = turn.isWhite() ? endSquare - 16 : endSquare + 16;
            moves.add(move(startSquare, endSquare)
                    .enPassantTarget(new BitBoard(1L << enPassantTargetSquare))
                    .build());
            doubleAdvances.popLSB();
        }

        BitBoard leftCaptures = turn.isWhite() ?
                pawns.shiftNorthWest().and(opponentPieces).not(BitBoards.FILE_H).not(BitBoards.RANK_8) :
                pawns.shiftSouthWest().and(opponentPieces).not(BitBoards.FILE_H).not(BitBoards.RANK_1);
        while (leftCaptures.isNotZero()) {
            int endSquare = leftCaptures.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).isCapture(true).build());
            leftCaptures.popLSB();
        }

        BitBoard rightCaptures = turn.isWhite() ?
                pawns.shiftNorthEast().and(opponentPieces).not(BitBoards.FILE_A).not(BitBoards.RANK_8) :
                pawns.shiftSouthEast().and(opponentPieces).not(BitBoards.FILE_A).not(BitBoards.RANK_1);
        while (rightCaptures.isNotZero()) {
            int endSquare = rightCaptures.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).isCapture(true).build());
            rightCaptures.popLSB();
        }

        BitBoard enPassantLeftCaptures = turn.isWhite() ?
                pawns.shiftNorthWest().and(enPassantTarget).not(BitBoards.FILE_H) :
                pawns.shiftSouthWest().and(enPassantTarget).not(BitBoards.FILE_H);
        while (enPassantLeftCaptures.isNotZero()) {
            int endSquare = enPassantLeftCaptures.scanForward();
            int enPassantCaptureSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT)
                    .isCapture(true)
                    .enPassantCapture(new BitBoard(1L << enPassantCaptureSquare))
                    .build());
            enPassantLeftCaptures.popLSB();
        }

        BitBoard enPassantRightCaptures = turn.isWhite() ?
                pawns.shiftNorthEast().and(enPassantTarget).not(BitBoards.FILE_A) :
                pawns.shiftSouthEast().and(enPassantTarget).not(BitBoards.FILE_A);
        while (enPassantRightCaptures.isNotZero()) {
            int endSquare = enPassantRightCaptures.scanForward();
            int enPassantCaptureSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT)
                    .isCapture(true)
                    .enPassantCapture(new BitBoard(1L << enPassantCaptureSquare))
                    .build());
            enPassantRightCaptures.popLSB();
        }

        BitBoard advancePromotions = turn.isWhite() ?
                pawns.shiftNorth().not(occupied).and(BitBoards.RANK_8) :
                pawns.shiftSouth().not(occupied).and(BitBoards.RANK_1);
        while (advancePromotions.isNotZero()) {
            int endSquare = advancePromotions.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            moves.addAll(getPromotionMoves(startSquare, endSquare, false));
            advancePromotions.popLSB();
        }

        BitBoard captureLeftPromotions = turn.isWhite() ?
                pawns.shiftNorthWest().and(opponentPieces).not(BitBoards.FILE_H).and(BitBoards.RANK_8) :
                pawns.shiftSouthWest().and(opponentPieces).not(BitBoards.FILE_H).and(BitBoards.RANK_1);
        while (captureLeftPromotions.isNotZero()) {
            int endSquare = captureLeftPromotions.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.addAll(getPromotionMoves(startSquare, endSquare, true));
            captureLeftPromotions.popLSB();
        }

        BitBoard captureRightPromotions = turn.isWhite() ?
                pawns.shiftNorthEast().and(opponentPieces).not(BitBoards.FILE_A).and(BitBoards.RANK_8) :
                pawns.shiftSouthEast().and(opponentPieces).not(BitBoards.FILE_A).and(BitBoards.RANK_1);
        while (captureRightPromotions.isNotZero()) {
            int endSquare = captureRightPromotions.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.addAll(getPromotionMoves(startSquare, endSquare, true));
            captureRightPromotions.popLSB();
        }

        return moves;

    }

    private Set<Move> getPromotionMoves(int startSquare, int endSquare, boolean isCapture) {
        return Set.of(
                move(startSquare, endSquare)
                        .moveType(MoveType.PROMOTION)
                        .promotionPieceType(PieceType.QUEEN)
                        .isCapture(isCapture)
                        .build(),
                move(startSquare, endSquare)
                        .moveType(MoveType.PROMOTION)
                        .promotionPieceType(PieceType.ROOK)
                        .isCapture(isCapture)
                        .build(),
                move(startSquare, endSquare)
                        .moveType(MoveType.PROMOTION)
                        .promotionPieceType(PieceType.BISHOP)
                        .isCapture(isCapture)
                        .build(),
                move(startSquare, endSquare)
                        .moveType(MoveType.PROMOTION)
                        .promotionPieceType(PieceType.KNIGHT)
                        .isCapture(isCapture)
                        .build());
    }

    private Move.MoveBuilder move(int startSquare, int endSquare) {
        return Move.builder()
                .pieceType(PieceType.PAWN)
                .startSquare(startSquare)
                .endSquare(endSquare);
    }

}
