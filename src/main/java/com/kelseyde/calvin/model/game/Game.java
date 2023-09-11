package com.kelseyde.calvin.model.game;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.DrawService;
import com.kelseyde.calvin.service.MoveService;
import lombok.Data;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

/**
 * Represents a full game of chess, capturing the current and previous board state, as well as all other metadata required
 * to play the game (castling rights, en passant square, etc). Essentially a statemachine that carries the game from the
 * first move to the end result.
 */
@Data
public class Game {

    private Board board;
    private Colour turn;
    private Stack<Board> boardHistory;
    private Stack<Move> moveHistory;
    private Map<Colour, CastlingRights> castlingRights;
    private int enPassantTargetSquare;
    private int halfMoveClock;
    private int fullMoveCounter;

    private MoveService moveService;
    private DrawService drawService;
    private Set<Move> legalMoves;

    public Game() {
        this.board = Board.startingPosition();
        this.turn = Colour.WHITE;
        this.boardHistory = new Stack<>();
        this.moveHistory = new Stack<>();
        this.castlingRights = Map.of(Colour.WHITE, new CastlingRights(), Colour.BLACK, new CastlingRights());
        this.enPassantTargetSquare = -1;
        this.halfMoveClock = 0;
        this.fullMoveCounter = 1;

        this.moveService = new MoveService();
        this.drawService = new DrawService();
        this.legalMoves = moveService.generateLegalMoves(this);
    }

    public ActionResult executeAction(GameAction action) {
        return switch (action.getActionType()) {
            case MOVE -> handleMove(action.getMove());
            case RESIGN -> handleResignation(action.getPlayer());
        };
    }

    public ActionResult handleMove(Move move) {

        Move legalMove = legalMoves.stream()
                .filter(move::moveMatches)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Illegal move! " + move));

        applyMove(legalMove);

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
        moveHistory.push(legalMove);
        turn = turn.oppositeColour();
        legalMoves = moveService.generateLegalMoves(this);

        if (legalMove.isCheck() && legalMoves.isEmpty()) {
            return ActionResult.builder()
                    .isWin(true)
                    .winningColour(turn.oppositeColour())
                    .winType(WinType.CHECKMATE)
                    .build();
        } else {
            Optional<DrawType> drawType = drawService.calculateDraw(this);
            if (drawType.isPresent()) {
                return ActionResult.builder()
                        .isDraw(true)
                        .drawType(drawType.get())
                        .build();
            } else {
                return ActionResult.builder()
                        .sideToPlay(turn)
                        .build();
            }
        }


    }

    public void applyMove(Move move) {
        boardHistory.push(board.copy());
        Piece piece = board.pieceAt(move.getStartSquare()).orElseThrow();
        board.unsetPiece(move.getStartSquare());
        board.setPiece(move.getEndSquare(), piece);

        switch (move.getType()) {
            case EN_PASSANT -> {
                board.unsetPiece(move.getEnPassantConfig().getEnPassantCapturedSquare());
            }
            case PROMOTION -> {
                Piece promotedPiece = new Piece(piece.getColour(), move.getPromotionConfig().getPromotionPieceType());
                board.setPiece(move.getEndSquare(), promotedPiece);
            }
            case CASTLE -> {
                Piece rook = board.pieceAt(move.getCastlingConfig().getRookStartSquare()).orElseThrow();
                board.unsetPiece(move.getCastlingConfig().getRookStartSquare());
                board.setPiece(move.getCastlingConfig().getRookEndSquare(), rook);
            }
        }
    }

    public void unapplyLastMove() {
        board = boardHistory.pop();
    }

    private ActionResult handleResignation(Player player) {
        return ActionResult.builder()
                .isWin(true)
                .winningColour(player.getColour().oppositeColour())
                .winType(WinType.RESIGNATION)
                .build();
    }

    public void setTurn(Colour turn) {
        this.turn = turn;
        this.legalMoves = moveService.generateLegalMoves(this);
    }

    public static Game fromPosition(Board board) {
        Game game = new Game();
        game.setBoard(board);
        return game;
    }

}
