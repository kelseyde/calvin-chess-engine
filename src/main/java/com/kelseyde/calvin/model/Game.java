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

        Move legalMove = legalMoveService.getLegalMove(this, move)
                .orElseThrow(() -> new IllegalArgumentException("Illegal move! " + move));

        Piece piece = board.pieceAt(legalMove.getStartSquare()).orElseThrow();

        board.unsetPiece(legalMove.getStartSquare());
        board.setPiece(legalMove.getEndSquare(), piece);

        switch (legalMove.getType()) {
            case EN_PASSANT -> {
                board.unsetPiece(legalMove.getEnPassantConfig().getEnPassantTargetSquare());
            }
            case PROMOTION -> {
                Piece promotedPiece = new Piece(piece.getColour(), legalMove.getPromotionConfig().getPromotionPieceType());
                board.setPiece(legalMove.getEndSquare(), promotedPiece);
            }
            case CASTLE -> {
                Piece rook = board.pieceAt(legalMove.getCastlingConfig().getRookStartSquare()).orElseThrow();
                board.unsetPiece(legalMove.getCastlingConfig().getRookStartSquare());
                board.setPiece(legalMove.getCastlingConfig().getRookEndSquare(), rook);
                castlingRights.get(piece.getColour()).setKingSide(false);
                castlingRights.get(piece.getColour()).setQueenSide(false);
            }
        }

        enPassantTargetSquare = legalMove.getEnPassantConfig().getEnPassantTargetSquare();
        if (legalMove.getCastlingConfig().isNegatesKingsideCastling()) {
            castlingRights.get(turn).setKingSide(false);
        }
        if (legalMove.getCastlingConfig().isNegatesQueensideCastling()) {
            castlingRights.get(turn).setQueenSide(false);
        }

        if (Colour.BLACK.equals(turn)) {
            fullMoveCounter++;
        }
        turn = Colour.BLACK.equals(turn) ? Colour.WHITE : Colour.BLACK;
        moveHistory.push(legalMove);

    }

    public static Game fromPosition(Board board) {
        Game game = new Game();
        game.setBoard(board);
        return game;
    }

}
