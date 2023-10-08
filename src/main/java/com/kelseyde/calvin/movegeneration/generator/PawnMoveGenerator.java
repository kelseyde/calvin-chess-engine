package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.utils.BoardUtils;
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
        long opponents = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        long occupied = board.getOccupied();
        long enPassantFile = BitboardUtils.getFileBitboard(board.getGameState().getEnPassantFile());

        long singleAdvances = isWhite ?
                BitboardUtils.shiftNorth(pawns) &~ occupied &~ Bits.RANK_8 :
                BitboardUtils.shiftSouth(pawns) &~ occupied &~ Bits.RANK_1;

        long singleAdvancesCopy = singleAdvances;
        while (singleAdvancesCopy != 0) {
            int endSquare = BitboardUtils.scanForward(singleAdvancesCopy);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.add(move(startSquare, endSquare).build());
            singleAdvancesCopy = BitboardUtils.popLSB(singleAdvancesCopy);
        }

        long doubleAdvances = isWhite ?
                BitboardUtils.shiftNorth(singleAdvances) &~ occupied & Bits.RANK_4 :
                BitboardUtils.shiftSouth(singleAdvances) &~ occupied & Bits.RANK_5;
        while (doubleAdvances != 0) {
            int endSquare = BitboardUtils.scanForward(doubleAdvances);
            int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
            moves.add(move(startSquare, endSquare)
                    .enPassantFile(BoardUtils.getFile(endSquare))
                    .build());
            doubleAdvances = BitboardUtils.popLSB(doubleAdvances);
        }

        long leftCaptures = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_1;
        while (leftCaptures != 0) {
            int endSquare = BitboardUtils.scanForward(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).build());
            leftCaptures = BitboardUtils.popLSB(leftCaptures);
        }

        long rightCaptures = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_8:
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_1;
        while (rightCaptures != 0) {
            int endSquare = BitboardUtils.scanForward(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).build());
            rightCaptures = BitboardUtils.popLSB(rightCaptures);
        }

        long enPassantLeftCaptures = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_H :
                BitboardUtils.shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_H;
        while (enPassantLeftCaptures != 0) {
            int endSquare = BitboardUtils.scanForward(enPassantLeftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT).build());
            enPassantLeftCaptures = BitboardUtils.popLSB(enPassantLeftCaptures);
        }

        long enPassantRightCaptures = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_6 :
                BitboardUtils.shiftSouthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_3;
        while (enPassantRightCaptures != 0) {
            int endSquare = BitboardUtils.scanForward(enPassantRightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT).build());
            enPassantRightCaptures = BitboardUtils.popLSB(enPassantRightCaptures);
        }

        long advancePromotions = isWhite ?
                BitboardUtils.shiftNorth(pawns) &~ occupied & Bits.RANK_8 :
                BitboardUtils.shiftSouth(pawns) &~ occupied & Bits.RANK_1;
        while (advancePromotions != 0) {
            int endSquare = BitboardUtils.scanForward(advancePromotions);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            advancePromotions = BitboardUtils.popLSB(advancePromotions);
        }

        long captureLeftPromotions = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_1;
        while (captureLeftPromotions != 0) {
            int endSquare = BitboardUtils.scanForward(captureLeftPromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureLeftPromotions = BitboardUtils.popLSB(captureLeftPromotions);
        }

        long captureRightPromotions = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_8 :
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_1;
        while (captureRightPromotions != 0) {
            int endSquare = BitboardUtils.scanForward(captureRightPromotions);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureRightPromotions = BitboardUtils.popLSB(captureRightPromotions);
        }

        return moves;

    }

    @Override
    public long generateAttackMask(Board board, boolean isWhite) {
        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long rightAttackMask = isWhite ? BitboardUtils.shiftNorthEast(pawns) : BitboardUtils.shiftSouthEast(pawns);
        long leftAttackMask = isWhite ? BitboardUtils.shiftNorthWest(pawns) : BitboardUtils.shiftSouthWest(pawns) ;
        return leftAttackMask | rightAttackMask;
    }

    @Override
    public long generateAttackMaskFromSquare(Board board, int square, boolean isWhite) {
        long attackMask = 0L;
        long squareBB = 1L << square;
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        long leftCapture = isWhite ?
                BitboardUtils.shiftNorthWest(squareBB) &~ friendlies &~ Bits.FILE_H :
                BitboardUtils.shiftSouthWest(squareBB) &~ friendlies &~ Bits.FILE_H;
        attackMask |= leftCapture;

        long rightCapture = isWhite ?
                BitboardUtils.shiftNorthEast(squareBB) &~ friendlies &~ Bits.FILE_A :
                BitboardUtils.shiftSouthEast(squareBB) &~ friendlies &~ Bits.FILE_A;
        attackMask |= rightCapture;

        return attackMask;
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
