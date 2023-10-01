package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
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
        long enPassantFile = BitBoardUtils.getFileBitboard(board.getGameState().getEnPassantFile());

        long singleAdvances = isWhite ?
                BitBoardUtils.shiftNorth(pawns) &~ occupied &~ Bits.RANK_8 :
                BitBoardUtils.shiftSouth(pawns) &~ occupied &~ Bits.RANK_1;

        long singleAdvancesCopy = singleAdvances;
        while (singleAdvancesCopy != 0) {
            int endSquare = BitBoardUtils.scanForward(singleAdvancesCopy);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.add(move(startSquare, endSquare).build());
            singleAdvancesCopy = BitBoardUtils.popLSB(singleAdvancesCopy);
        }

        long doubleAdvances = isWhite ?
                BitBoardUtils.shiftNorth(singleAdvances) &~ occupied & Bits.RANK_4 :
                BitBoardUtils.shiftSouth(singleAdvances) &~ occupied & Bits.RANK_5;
        while (doubleAdvances != 0) {
            int endSquare = BitBoardUtils.scanForward(doubleAdvances);
            int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
            moves.add(move(startSquare, endSquare)
                    .enPassantFile(BoardUtils.getFile(endSquare))
                    .build());
            doubleAdvances = BitBoardUtils.popLSB(doubleAdvances);
        }

        long leftCaptures = isWhite ?
                BitBoardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_8 :
                BitBoardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_1;
        while (leftCaptures != 0) {
            int endSquare = BitBoardUtils.scanForward(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).build());
            leftCaptures = BitBoardUtils.popLSB(leftCaptures);
        }

        long rightCaptures = isWhite ?
                BitBoardUtils.shiftNorthEast(pawns) & opponents &~ Bits.RANK_8 :
                BitBoardUtils.shiftSouthEast(pawns) & opponents &~ Bits.RANK_1;
        while (rightCaptures != 0) {
            int endSquare = BitBoardUtils.scanForward(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).build());
            rightCaptures = BitBoardUtils.popLSB(rightCaptures);
        }

        long enPassantLeftCaptures = isWhite ?
                BitBoardUtils.shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_H :
                BitBoardUtils.shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_H;
        while (enPassantLeftCaptures != 0) {
            int endSquare = BitBoardUtils.scanForward(enPassantLeftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT).build());
            enPassantLeftCaptures = BitBoardUtils.popLSB(enPassantLeftCaptures);
        }

        long enPassantRightCaptures = isWhite ?
                BitBoardUtils.shiftNorthEast(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_A :
                BitBoardUtils.shiftSouthEast(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_A;
        while (enPassantRightCaptures != 0) {
            int endSquare = BitBoardUtils.scanForward(enPassantRightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT).build());
            enPassantRightCaptures = BitBoardUtils.popLSB(enPassantRightCaptures);
        }

        long advancePromotions = isWhite ?
                BitBoardUtils.shiftNorth(pawns) &~ occupied & Bits.RANK_8 :
                BitBoardUtils.shiftSouth(pawns) &~ occupied & Bits.RANK_1;
        while (advancePromotions != 0) {
            int endSquare = BitBoardUtils.scanForward(advancePromotions);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            advancePromotions = BitBoardUtils.popLSB(advancePromotions);
        }

        long captureLeftPromotions = isWhite ?
                BitBoardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_8 :
                BitBoardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_1;
        while (captureLeftPromotions != 0) {
            int endSquare = BitBoardUtils.scanForward(captureLeftPromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureLeftPromotions = BitBoardUtils.popLSB(captureLeftPromotions);
        }

        long captureRightPromotions = isWhite ?
                BitBoardUtils.shiftNorthEast(pawns) & opponents & Bits.RANK_8 :
                BitBoardUtils.shiftSouthEast(pawns) & opponents & Bits.RANK_1;
        while (captureRightPromotions != 0) {
            int endSquare = BitBoardUtils.scanForward(captureRightPromotions);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureRightPromotions = BitBoardUtils.popLSB(captureRightPromotions);
        }

        return moves;

    }

    @Override
    public long generateAttackMask(Board board, boolean isWhite) {
        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long rightAttackMask = isWhite ? BitBoardUtils.shiftNorthEast(pawns) : BitBoardUtils.shiftSouthEast(pawns);
        long leftAttackMask = isWhite ? BitBoardUtils.shiftNorthWest(pawns) : BitBoardUtils.shiftSouthWest(pawns) ;
        return leftAttackMask | rightAttackMask;
    }

    @Override
    public long generateAttackMaskFromSquare(Board board, int square, boolean isWhite) {
        long attackMask = 0L;
        long squareBB = 1L << square;
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        long leftCapture = isWhite ?
                BitBoardUtils.shiftNorthWest(squareBB) &~ friendlies &~ Bits.FILE_H :
                BitBoardUtils.shiftSouthWest(squareBB) &~ friendlies &~ Bits.FILE_H;
        attackMask |= leftCapture;

        long rightCapture = isWhite ?
                BitBoardUtils.shiftNorthEast(squareBB) &~ friendlies &~ Bits.FILE_A :
                BitBoardUtils.shiftSouthEast(squareBB) &~ friendlies &~ Bits.FILE_A;
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
