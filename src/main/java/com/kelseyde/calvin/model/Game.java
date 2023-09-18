package com.kelseyde.calvin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveKey;
import com.kelseyde.calvin.model.result.*;
import com.kelseyde.calvin.service.game.DrawEvaluator;
import com.kelseyde.calvin.service.game.LegalMoveGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

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

    private Deque<Board> boardHistory = new ArrayDeque<>();
    private Deque<Move> moveHistory = new ArrayDeque<>();
    private Map<MoveKey, Move> legalMoves = new HashMap<>();

    @JsonIgnore
    private LegalMoveGenerator legalMoveService = new LegalMoveGenerator();

    @JsonIgnore
    private DrawEvaluator drawEvaluator = new DrawEvaluator();

    public Game() {
        this.board = new Board();
        this.legalMoves = legalMoveService.generateLegalMoves(board).stream()
                .collect(Collectors.toMap(Move::getKey, m -> m));
    }

    public Game(Board board) {
        this.board = board;
        this.legalMoves = legalMoveService.generateLegalMoves(board).stream()
                .collect(Collectors.toMap(Move::getKey, m -> m));
    }

    public GameResult makeMove(Move move) {

        Optional<Move> legalMove = Optional.ofNullable(legalMoves.get(move.getKey()));
        if (legalMove.isEmpty()) {
            return new InvalidMoveResult(move);
        }
        move = legalMove.get();

        boardHistory.push(board.copy());
        moveHistory.push(move);
        board.applyMove(move);
        legalMoves = legalMoveService.generateLegalMoves(board).stream()
                .collect(Collectors.toMap(Move::getKey, m -> m));

        if (isCheckmate()) {
            Colour winner = board.getTurn().oppositeColour();
            return new WinResult(winner, WinType.CHECKMATE);
        }
        Optional<DrawType> drawType = drawEvaluator.calculateDraw(this);
        if (drawType.isPresent()) {
            return new DrawResult(drawType.get());
        } else {
            Colour sideToMove = board.getTurn();
            return new NextMoveResult(sideToMove);
        }

    }

    public void unmakeMove() {
        if (!boardHistory.isEmpty()) {
            board = boardHistory.pop();
        }
        if (!moveHistory.isEmpty()) {
            moveHistory.pop();
        }
        legalMoves = legalMoveService.generateLegalMoves(board).stream()
                .collect(Collectors.toMap(Move::getKey, m -> m));
    }

    public Colour getTurn() {
        return board.getTurn();
    }

    public void setTurn(Colour turn) {
        this.board.setTurn(turn);
        this.legalMoves = legalMoveService.generateLegalMoves(board).stream()
                .collect(Collectors.toMap(Move::getKey, m -> m));
    }

    public boolean isCheckmate() {
        if (moveHistory.isEmpty()) {
            return false;
        }
        return moveHistory.peek().isCheck() && legalMoves.isEmpty();
    }

    public boolean isDraw() {
        return drawEvaluator.calculateDraw(this).isPresent();
    }

}
