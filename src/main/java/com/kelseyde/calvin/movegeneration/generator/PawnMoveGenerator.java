package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardConstants;
import com.kelseyde.calvin.board.bitboard.BitBoardUtil;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class PawnMoveGenerator implements PseudoLegalMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.PAWN;

    public Set<Move> generatePseudoLegalMoves(Board board) {

        boolean isWhite = board.isWhiteToMove();
        Set<Move> moves = new HashSet<>();

        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long opponentPieces = isWhite? board.getBlackPieces() : board.getWhitePieces();
        long occupied = board.getOccupied();
        long enPassantTarget = board.getEnPassantTarget();
        long copy;

        long singleAdvances = isWhite ?
                BitBoardUtil.shiftNorth(pawns) &~ occupied &~ BitBoardConstants.RANK_8 :
                BitBoardUtil.shiftSouth(pawns) &~ occupied &~ BitBoardConstants.RANK_1;

        copy = singleAdvances;
        while (copy != 0) {
            int endSquare = BitBoardUtil.scanForward(copy);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.add(move(startSquare, endSquare).build());
            copy = BitBoardUtil.popLSB(copy);
        }

        long doubleAdvances = isWhite ?
                BitBoardUtil.shiftNorth(singleAdvances) &~ occupied & BitBoardConstants.RANK_4 :
                BitBoardUtil.shiftSouth(singleAdvances) &~ occupied & BitBoardConstants.RANK_5;
        while (doubleAdvances != 0) {
            int endSquare = BitBoardUtil.scanForward(doubleAdvances);
            int enPassantTargetSquare = isWhite ? endSquare - 8 : endSquare + 8;
            int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
            moves.add(move(startSquare, endSquare)
                    .enPassantTarget(1L << enPassantTargetSquare)
                    .build());
            doubleAdvances = BitBoardUtil.popLSB(doubleAdvances);
        }

        long leftCaptures = isWhite ?
                BitBoardUtil.shiftNorthWest(pawns) & opponentPieces &~ BitBoardConstants.FILE_H &~ BitBoardConstants.RANK_8 :
                BitBoardUtil.shiftSouthWest(pawns) & opponentPieces &~ BitBoardConstants.FILE_H &~ BitBoardConstants.RANK_1;
        while (leftCaptures != 0) {
            int endSquare = BitBoardUtil.scanForward(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).build());
            leftCaptures = BitBoardUtil.popLSB(leftCaptures);
        }

        long rightCaptures = isWhite ?
                BitBoardUtil.shiftNorthEast(pawns) & opponentPieces &~ BitBoardConstants.FILE_A &~ BitBoardConstants.RANK_8 :
                BitBoardUtil.shiftSouthEast(pawns) & opponentPieces &~ BitBoardConstants.FILE_A &~ BitBoardConstants.RANK_1;
        while (rightCaptures != 0) {
            int endSquare = BitBoardUtil.scanForward(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).build());
            rightCaptures = BitBoardUtil.popLSB(rightCaptures);
        }

        long enPassantLeftCaptures = isWhite ?
                BitBoardUtil.shiftNorthWest(pawns) & enPassantTarget &~ BitBoardConstants.FILE_H :
                BitBoardUtil.shiftSouthWest(pawns) & enPassantTarget &~ BitBoardConstants.FILE_H;
        while (enPassantLeftCaptures != 0) {
            int endSquare = BitBoardUtil.scanForward(enPassantLeftCaptures);
            int enPassantCaptureSquare = isWhite ? endSquare - 8 : endSquare + 8;
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT)
                    .enPassantCapture(1L << enPassantCaptureSquare)
                    .build());
            enPassantLeftCaptures = BitBoardUtil.popLSB(enPassantLeftCaptures);
        }

        long enPassantRightCaptures = isWhite ?
                BitBoardUtil.shiftNorthEast(pawns) & enPassantTarget &~ BitBoardConstants.FILE_A :
                BitBoardUtil.shiftSouthEast(pawns) & enPassantTarget &~ BitBoardConstants.FILE_A;
        while (enPassantRightCaptures != 0) {
            int endSquare = BitBoardUtil.scanForward(enPassantRightCaptures);
            int enPassantCaptureSquare = isWhite ? endSquare - 8 : endSquare + 8;
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT)
                    .enPassantCapture(1L << enPassantCaptureSquare)
                    .build());
            enPassantRightCaptures = BitBoardUtil.popLSB(enPassantRightCaptures);
        }

        long advancePromotions = isWhite ?
                BitBoardUtil.shiftNorth(pawns) &~ occupied & BitBoardConstants.RANK_8 :
                BitBoardUtil.shiftSouth(pawns) &~ occupied & BitBoardConstants.RANK_1;
        while (advancePromotions != 0) {
            int endSquare = BitBoardUtil.scanForward(advancePromotions);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            advancePromotions = BitBoardUtil.popLSB(advancePromotions);
        }

        long captureLeftPromotions = isWhite ?
                BitBoardUtil.shiftNorthWest(pawns) & opponentPieces &~ BitBoardConstants.FILE_H & BitBoardConstants.RANK_8 :
                BitBoardUtil.shiftSouthWest(pawns) & opponentPieces &~ BitBoardConstants.FILE_H & BitBoardConstants.RANK_1;
        while (captureLeftPromotions != 0) {
            int endSquare = BitBoardUtil.scanForward(captureLeftPromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureLeftPromotions = BitBoardUtil.popLSB(captureLeftPromotions);
        }

        long captureRightPromotions = isWhite ?
                BitBoardUtil.shiftNorthEast(pawns) & opponentPieces &~ BitBoardConstants.FILE_A & BitBoardConstants.RANK_8 :
                BitBoardUtil.shiftSouthEast(pawns) & opponentPieces &~ BitBoardConstants.FILE_A & BitBoardConstants.RANK_1;
        while (captureRightPromotions != 0) {
            int endSquare = BitBoardUtil.scanForward(captureRightPromotions);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureRightPromotions = BitBoardUtil.popLSB(captureRightPromotions);
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
