package com.kelseyde.calvin.service.game.generator.bitboard;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveType;

import java.util.HashSet;
import java.util.Set;

public class PawnMoveGenerator {

    public Set<Move> generateMoves(Board board) {

        Set<Move> moves = new HashSet<>();

        BitBoard whitePawns = board.getWhitePawns();
        BitBoard blackPieces = board.getBlackPieces();
        BitBoard occupied = board.getOccupied();
        BitBoard enPassantTarget = board.getEnPassantTarget();
        BitBoard copy;

        BitBoard singleAdvances = whitePawns.shiftNorth().not(occupied).not(BitBoards.RANK_8);
        copy = singleAdvances.copy();
        while (copy.greaterThanZero()) {
            int endSquare = copy.scanForward();
            int startSquare = endSquare - 8;
            moves.add(move(startSquare, endSquare).build());
            copy.popLSB();
        }

        BitBoard doubleAdvances = singleAdvances.shiftNorth().not(occupied).and(BitBoards.RANK_4);
        while (doubleAdvances.greaterThanZero()) {
            int endSquare = doubleAdvances.scanForward();
            int startSquare = endSquare - 16;
            moves.add(move(startSquare, endSquare).build());
            doubleAdvances.popLSB();
        }

        BitBoard leftCaptures = whitePawns.shiftNorthWest().and(blackPieces).not(BitBoards.FILE_H);
        while (leftCaptures.greaterThanZero()) {
            int endSquare = leftCaptures.scanForward();
            int startSquare = endSquare - 7;
            moves.add(move(startSquare, endSquare).build());
            leftCaptures.popLSB();
        }

        BitBoard rightCaptures = whitePawns.shiftNorthEast().and(blackPieces).not(BitBoards.FILE_A);
        while (rightCaptures.greaterThanZero()) {
            int endSquare = rightCaptures.scanForward();
            int startSquare = endSquare - 9;
            moves.add(move(startSquare, endSquare).build());
            rightCaptures.popLSB();
        }

        BitBoard enPassantLeftCaptures = whitePawns.shiftNorthWest().and(enPassantTarget).not(BitBoards.FILE_H);
        while (enPassantLeftCaptures.greaterThanZero()) {
            int endSquare = enPassantLeftCaptures.scanForward();
            int startSquare = endSquare - 7;
            moves.add(move(startSquare, endSquare).moveType(MoveType.EN_PASSANT).build());
            enPassantLeftCaptures.popLSB();
        }

        BitBoard enPassantRightCaptures = whitePawns.shiftNorthEast().and(enPassantTarget).not(BitBoards.FILE_A);
        while (enPassantRightCaptures.greaterThanZero()) {
            int endSquare = enPassantRightCaptures.scanForward();
            int startSquare = endSquare - 9;
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
