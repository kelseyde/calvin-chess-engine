package com.kelseyde.calvin.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RepetitionMetadata {

    private long whitePawns;
    private long whiteKnights;
    private long whiteBishops;
    private long whiteRooks;
    private long whiteQueens;
    private long whiteKing;

    private long blackPawns;
    private long blackKnights;
    private long blackBishops;
    private long blackRooks;
    private long blackQueens;
    private long blackKing;

    private long enPassantTarget;
    private Map<Colour, CastlingRights> castlingRights;
    private Colour turn;

    public static RepetitionMetadata fromBoard(Board board) {
        return RepetitionMetadata.builder()
                .whitePawns(board.getWhitePawns())
                .whiteKnights(board.getWhiteKnights())
                .whiteBishops(board.getWhiteBishops())
                .whiteRooks(board.getWhiteRooks())
                .whiteQueens(board.getWhiteQueens())
                .whiteKing(board.getWhiteKing())
                .blackPawns(board.getBlackPawns())
                .blackKnights(board.getBlackKnights())
                .blackBishops(board.getBlackBishops())
                .blackRooks(board.getBlackRooks())
                .blackQueens(board.getBlackQueens())
                .blackKing(board.getBlackKing())
                .castlingRights(board.getCastlingRights())
                .enPassantTarget(board.getEnPassantTarget())
                .turn(board.getTurn())
                .build();

    }

}
