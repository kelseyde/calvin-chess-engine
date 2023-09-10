package com.kelseyde.calvin.model;

import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.LegalMoveService;
import lombok.Data;

import java.util.Map;
import java.util.Stack;

@Data
public class Game {

    private Board board = Board.startingPosition();

    private Stack<Move> moveHistory = new Stack<>();

    private Colour turn = Colour.WHITE;

    private Map<Colour, CastlingRights> castlingRights = Map.of(
            Colour.WHITE, new CastlingRights(),
            Colour.BLACK, new CastlingRights()
    );

    private int enPassantTargetSquare = -1;

    private int halfMoveClock = 0;

    private int fullMoveCounter = 1;

    private LegalMoveService legalMoveService = new LegalMoveService();

    public void makeMove(Move move) {

        if (!legalMoveService.isLegalMove(this, move)) {
            throw new IllegalArgumentException(String.format("Move is invalid! %s", move));
        }

        Piece piece = board.pieceAt(move.getStartSquare()).orElseThrow();

        board.unsetPiece(move.getStartSquare());
        board.setPiece(move.getEndSquare(), piece);

        switch (move.getType()) {
            case EN_PASSANT -> {
                board.unsetPiece(move.getEnPassantConfig().getEnPassantTargetSquare());
            }
            case PROMOTION -> {
                Piece promotedPiece = new Piece(piece.getColour(), move.getPromotionConfig().getPromotionPieceType());
                board.setPiece(move.getEndSquare(), promotedPiece);
            }
            case CASTLE -> {
                Piece rook = board.pieceAt(move.getCastlingConfig().getRookStartSquare()).orElseThrow();
                board.unsetPiece(move.getCastlingConfig().getRookStartSquare());
                board.setPiece(move.getCastlingConfig().getRookEndSquare(), rook);
                castlingRights.get(piece.getColour()).setKingSide(false);
                castlingRights.get(piece.getColour()).setQueenSide(false);
            }
        }

        if (Colour.BLACK.equals(turn)) {
            fullMoveCounter++;
        }
        turn = Colour.BLACK.equals(turn) ? Colour.WHITE : Colour.BLACK;
        enPassantTargetSquare = move.getEnPassantConfig().getEnPassantTargetSquare();
        moveHistory.push(move);

    }

    public static Game fromPosition(Board board) {
        Game game = new Game();
        game.setBoard(board);
        return game;
    }

}
