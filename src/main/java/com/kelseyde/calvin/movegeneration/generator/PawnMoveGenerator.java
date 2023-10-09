package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveGenerator implements PseudoLegalMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.PAWN;

    public List<Move> generatePseudoLegalMoves(Board board) {

        boolean isWhite = board.isWhiteToMove();
        List<Move> moves = new ArrayList<>();

        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long opponents = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        long occupied = board.getOccupied();
        long enPassantFile = BitboardUtils.getFileBitboard(board.getGameState().getEnPassantFile());

        long singleAdvances = isWhite ?
                BitboardUtils.shiftNorth(pawns) &~ occupied &~ Bits.RANK_8 :
                BitboardUtils.shiftSouth(pawns) &~ occupied &~ Bits.RANK_1;

        long singleAdvancesCopy = singleAdvances;
        while (singleAdvancesCopy != 0) {
            int endSquare = BitboardUtils.getLSB(singleAdvancesCopy);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.add(new Move(startSquare, endSquare));
            singleAdvancesCopy = BitboardUtils.popLSB(singleAdvancesCopy);
        }

        long doubleAdvances = isWhite ?
                BitboardUtils.shiftNorth(singleAdvances) &~ occupied & Bits.RANK_4 :
                BitboardUtils.shiftSouth(singleAdvances) &~ occupied & Bits.RANK_5;
        while (doubleAdvances != 0) {
            int endSquare = BitboardUtils.getLSB(doubleAdvances);
            int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
            moves.add(new Move(startSquare, endSquare, Move.PAWN_DOUBLE_MOVE_FLAG));
            doubleAdvances = BitboardUtils.popLSB(doubleAdvances);
        }

        long leftCaptures = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_1;
        while (leftCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(new Move(startSquare, endSquare));
            leftCaptures = BitboardUtils.popLSB(leftCaptures);
        }

        long rightCaptures = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_8:
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_1;
        while (rightCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(new Move(startSquare, endSquare));
            rightCaptures = BitboardUtils.popLSB(rightCaptures);
        }

        long enPassantLeftCaptures = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_H :
                BitboardUtils.shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_H;
        while (enPassantLeftCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(enPassantLeftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.add(new Move(startSquare, endSquare, Move.EN_PASSANT_FLAG));
            enPassantLeftCaptures = BitboardUtils.popLSB(enPassantLeftCaptures);
        }

        long enPassantRightCaptures = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_6 :
                BitboardUtils.shiftSouthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_3;
        while (enPassantRightCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(enPassantRightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            moves.add(new Move(startSquare, endSquare, Move.EN_PASSANT_FLAG));
            enPassantRightCaptures = BitboardUtils.popLSB(enPassantRightCaptures);
        }

        long advancePromotions = isWhite ?
                BitboardUtils.shiftNorth(pawns) &~ occupied & Bits.RANK_8 :
                BitboardUtils.shiftSouth(pawns) &~ occupied & Bits.RANK_1;
        while (advancePromotions != 0) {
            int endSquare = BitboardUtils.getLSB(advancePromotions);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            advancePromotions = BitboardUtils.popLSB(advancePromotions);
        }

        long captureLeftPromotions = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_1;
        while (captureLeftPromotions != 0) {
            int endSquare = BitboardUtils.getLSB(captureLeftPromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            moves.addAll(getPromotionMoves(startSquare, endSquare));
            captureLeftPromotions = BitboardUtils.popLSB(captureLeftPromotions);
        }

        long captureRightPromotions = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_8 :
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_1;
        while (captureRightPromotions != 0) {
            int endSquare = BitboardUtils.getLSB(captureRightPromotions);
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

    private List<Move> getPromotionMoves(int startSquare, int endSquare) {
        return List.of(
                new Move(startSquare, endSquare, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(startSquare, endSquare, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(startSquare, endSquare, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(startSquare, endSquare, Move.PROMOTE_TO_KNIGHT_FLAG));
    }

}
