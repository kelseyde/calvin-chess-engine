package com.kelseyde.calvin.model.game;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.CastlingRights;
import com.kelseyde.calvin.model.Colour;
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

        Move legalMove = legalMoveService.isLegalMove(this, move)
                .orElseThrow(() -> new IllegalArgumentException("Illegal move! " + move));

        board.applyMove(legalMove);

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
        turn = turn.oppositeColour();
        moveHistory.push(legalMove);

    }

    public static Game fromPosition(Board board) {
        Game game = new Game();
        game.setBoard(board);
        return game;
    }

}
