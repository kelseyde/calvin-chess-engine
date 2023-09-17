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
        while (copy.greaterThanZero()) {
            int endSquare = copy.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 8 : endSquare + 8;
            moves.add(move(startSquare, endSquare).build());
            copy.popLSB();
        }

        BitBoard doubleAdvances = turn.isWhite() ?
                singleAdvances.shiftNorth().not(occupied).and(BitBoards.RANK_4) :
                singleAdvances.shiftSouth().not(occupied).and(BitBoards.RANK_5);
        while (doubleAdvances.greaterThanZero()) {
            int endSquare = doubleAdvances.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 16 : endSquare + 16;
            moves.add(move(startSquare, endSquare).build());
            doubleAdvances.popLSB();
        }

        BitBoard leftCaptures = turn.isWhite() ?
                pawns.shiftNorthWest().and(opponentPieces).not(BitBoards.FILE_H) :
                pawns.shiftSouthWest().and(opponentPieces).not(BitBoards.FILE_H);
        while (leftCaptures.greaterThanZero()) {
            int endSquare = leftCaptures.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).build());
            leftCaptures.popLSB();
        }

        BitBoard rightCaptures = turn.isWhite() ?
                pawns.shiftNorthEast().and(opponentPieces).not(BitBoards.FILE_A) :
                pawns.shiftSouthEast().and(opponentPieces).not(BitBoards.FILE_A);
        while (rightCaptures.greaterThanZero()) {
            int endSquare = rightCaptures.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).build());
            rightCaptures.popLSB();
        }

        BitBoard enPassantLeftCaptures = turn.isWhite() ?
                pawns.shiftNorthWest().and(enPassantTarget).not(BitBoards.FILE_H) :
                pawns.shiftSouthWest().and(enPassantTarget).not(BitBoards.FILE_H);
        while (enPassantLeftCaptures.greaterThanZero()) {
            int endSquare = enPassantLeftCaptures.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 7 : endSquare + 9;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT).build());
            enPassantLeftCaptures.popLSB();
        }

        BitBoard enPassantRightCaptures = turn.isWhite() ?
                pawns.shiftNorthEast().and(enPassantTarget).not(BitBoards.FILE_A) :
                pawns.shiftSouthEast().and(enPassantTarget).not(BitBoards.FILE_A);
        while (enPassantRightCaptures.greaterThanZero()) {
            int endSquare = enPassantRightCaptures.scanForward();
            int startSquare = turn.isWhite() ? endSquare - 9 : endSquare + 7;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT).build());
            enPassantRightCaptures.popLSB();
        }

        return moves;

    }

    private Move.MoveBuilder move(int startSquare, int endSquare) {
        return Move.builder()
                .pieceType(PieceType.PAWN)
                .startSquare(startSquare)
                .endSquare(endSquare);
    }

}
