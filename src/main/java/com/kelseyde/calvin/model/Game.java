package com.kelseyde.calvin.model;

import com.kelseyde.calvin.service.LegalMoveService;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Game {

    private Board board = Board.startingPosition();

    private List<Move> moveHistory = new ArrayList<>();

    private Colour turn = Colour.WHITE;

    private CastlingRights castlingRights = new CastlingRights();

    private int enPassantTargetSquare = -1;

    private int halfMoveClock = 0;

    private int fullMoveCounter = 1;

    private LegalMoveService legalMoveService = new LegalMoveService();

    public void makeMove(Move move) {

        if (legalMoveService.isLegalMove(this, move)) {

            Piece piece = board.pieceAt(move.getStartSquare()).orElseThrow();
            board.unsetPiece(move.getStartSquare());
            board.setPiece(move.getEndSquare(), piece);

            if (Colour.BLACK.equals(turn)) {
                fullMoveCounter++;
            }
            turn = Colour.BLACK.equals(turn) ? Colour.WHITE : Colour.BLACK;
            enPassantTargetSquare = move.getEnPassantTargetSquare();

        }

    }

    public static Game fromPosition(Board board) {
        Game game = new Game();
        game.setBoard(board);
        return game;
    }

}
