package com.kelseyde.calvin.model;

import lombok.Data;

@Data
public class BitBoardBoard {

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

    public BitBoardBoard() {
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

        whitePieces = whitePawns.or(whiteKnights).or(whiteBishops).or(whiteRooks).or(whiteQueens).or(whiteKing);
        blackPieces = blackPawns.or(blackKnights).or(blackBishops).or(blackRooks).or(blackQueens).or(blackKing);
        occupied = whitePieces.or(blackPieces);

        enPassantTarget = new BitBoard();

        System.out.println(Long.toBinaryString(occupied.getBoard()));

    }


}
