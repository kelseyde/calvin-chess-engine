package com.kelseyde.calvin.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kelseyde.calvin.exception.InvalidMoveException;
import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.game.DrawService;
import com.kelseyde.calvin.service.game.LegalMoveService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Represents a full game of chess, capturing the current and previous board state, as well as all other metadata required
 * to play the game (castling rights, en passant square, etc). Essentially a statemachine that carries the game from the
 * first move to the end result.
 */
@Data
@Slf4j
public class Game {

    private String id = UUID.randomUUID().toString();
    private Board board;
    private Colour turn;
    private Map<Colour, CastlingRights> castlingRights;
    private int enPassantTargetSquare;
    private int halfMoveClock;
    private int fullMoveCounter;
    private Stack<BoardMetadata> boardHistory;
    private Stack<Move> moveHistory;
    private Set<Move> legalMoves;

    @JsonIgnore
    private LegalMoveService moveService;
    @JsonIgnore
    private DrawService drawService;

    public Game() {
        this.board = Board.startingPosition();
        this.turn = Colour.WHITE;
        this.boardHistory = new Stack<>();
        this.moveHistory = new Stack<>();
        this.castlingRights = Map.of(Colour.WHITE, new CastlingRights(), Colour.BLACK, new CastlingRights());
        this.enPassantTargetSquare = -1;
        this.halfMoveClock = 0;
        this.fullMoveCounter = 1;

        this.moveService = new LegalMoveService();
        this.drawService = new DrawService();
        this.legalMoves = moveService.generateLegalMoves(this);
    }

    public Game(Board board) {
        this.board = board;
        this.turn = Colour.WHITE;
        this.boardHistory = new Stack<>();
        this.moveHistory = new Stack<>();
        this.castlingRights = Map.of(Colour.WHITE, new CastlingRights(), Colour.BLACK, new CastlingRights());
        this.enPassantTargetSquare = -1;
        this.halfMoveClock = 0;
        this.fullMoveCounter = 1;

        this.moveService = new LegalMoveService();
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

        log.info("Move: {}", move);
        log.info("Legal moves: {}", legalMoves);
        Optional<Move> legalMoveOpt = legalMoves.stream()
                .filter(move::moveMatches)
                .findFirst();

        if (legalMoveOpt.isEmpty()) {
            return ActionResult.builder()
                    .isValidMove(false)
                    .build();
        }
        Move legalMove = legalMoveOpt.get();

        applyMove(legalMove);

        enPassantTargetSquare = legalMove.getEnPassantTargetSquare();
        if (legalMove.isNegatesKingsideCastling()) {
            castlingRights.get(turn).setKingSide(false);
        }
        if (legalMove.isNegatesQueensideCastling()) {
            castlingRights.get(turn).setQueenSide(false);
        }

        if (Colour.BLACK.equals(turn)) {
            fullMoveCounter++;
        }
        boolean resetFiftyMoveCounter = legalMove.isCapture() || PieceType.PAWN.equals(legalMove.getPieceType());
        halfMoveClock = resetFiftyMoveCounter ? 0 : ++halfMoveClock;
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
        boardHistory.push(BoardMetadata.fromGame(this));
        Piece piece = board.pieceAt(move.getStartSquare()).orElseThrow();
        board.unsetPiece(move.getStartSquare());
        board.setPiece(move.getEndSquare(), piece);

        switch (move.getMoveType()) {
            case EN_PASSANT -> {
                board.unsetPiece(move.getEnPassantCapturedSquare());
            }
            case PROMOTION -> {
                Piece promotedPiece = new Piece(piece.getColour(), move.getPromotionPieceType());
                board.setPiece(move.getEndSquare(), promotedPiece);
            }
            case CASTLE -> {
                Piece rook = board.pieceAt(move.getRookStartSquare()).orElseThrow();
                board.unsetPiece(move.getRookStartSquare());
                board.setPiece(move.getRookEndSquare(), rook);
            }
        }
    }

    public void unapplyLastMove() {
        board = boardHistory.pop().getBoard();
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

    public void setBoard(Board board) {
        this.board = board;
        this.legalMoves = moveService.generateLegalMoves(this);
    }

    public static Game fromPosition(Board board) {
        return new Game(board);
    }

}
