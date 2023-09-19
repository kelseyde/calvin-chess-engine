package com.kelseyde.calvin.board.result;

import com.kelseyde.calvin.board.Board;
import lombok.Builder;
import lombok.Data;

/**
 * All the metadata constituting the current 'position' as defined by the 50-move rule. Used for draw calculation.
 */
@Data
@Builder
public class PositionMetadata {

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

    private boolean whiteKingsideCastlingAllowed;
    private boolean whiteQueensideCastlingAllowed;
    private boolean blackKingsideCastlingAllowed;
    private boolean blackQueensideCastlingAllowed;

    private boolean whiteToMove;

    public static PositionMetadata fromBoard(Board board) {
        return PositionMetadata.builder()
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
                .whiteKingsideCastlingAllowed(board.isWhiteKingsideCastlingAllowed())
                .whiteQueensideCastlingAllowed(board.isWhiteQueensideCastlingAllowed())
                .blackKingsideCastlingAllowed(board.isBlackKingsideCastlingAllowed())
                .blackQueensideCastlingAllowed(board.isBlackQueensideCastlingAllowed())
                .enPassantTarget(board.getEnPassantTarget())
                .whiteToMove(board.isWhiteToMove())
                .build();

    }

}
