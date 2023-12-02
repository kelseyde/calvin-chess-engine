package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The engine is responsible for actually playing a game of chess. It manages the game state, updates the board, and 'thinks'
 * (executes a search) to find the best move. It can also select how long to think for in the case of a time-controlled
 * game, or it can 'ponder' (think indefinitely when it is the opponent's turn).
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Engine {

    EngineConfig config;
    MoveGeneration moveGenerator;
    Search searcher;
    CompletableFuture<Move> think;
    Board board;

    public Engine(EngineConfig config, MoveGeneration moveGenerator, Search searcher) {
        this.config = config;
        this.moveGenerator = moveGenerator;
        this.searcher = searcher;
    }

    public void newGame() {
        searcher.clearHistory();
    }

    public void setPosition(String fen, List<Move> moves) {
        board = FEN.toBoard(fen);
        for (Move move : moves) {
            Move legalMove = getLegalMove(move);
            board.makeMove(legalMove);
        }
        searcher.setPosition(board);
    }

    public void setHashSize(int hashSizeMb) {
        // TODO
    }

    public void setThreadCount(int threadCount) {
        // TODO
    }

    public void think(int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs, Consumer<Move> onThinkComplete) {
        int thinkTimeMs = TimeManager.chooseThinkTime(board.isWhiteToMove(), timeWhiteMs, timeBlackMs, incrementWhiteMs, incrementBlackMs);
        think(thinkTimeMs, onThinkComplete);
    }

    public void think(int thinkTimeMs, Consumer<Move> onThinkComplete) {
        stopThinking();
        think = CompletableFuture.supplyAsync(() -> think(thinkTimeMs));
        think.thenAccept(onThinkComplete);
    }

    public Move think(int thinkTimeMs) {
        Duration thinkTime = Duration.ofMillis(thinkTimeMs);
        return searcher.search(thinkTime).move();
    }

    public boolean isThinking() {
        return think != null && !think.isDone();
    }

    public void stopThinking() {
        if (isThinking()) {
            think.cancel(true);
        }
    }

    public void gameOver() {
        stopThinking();
        board = null;
    }

    private Move getLegalMove(Move move) {
        return moveGenerator.generateMoves(board).stream()
                .filter(move::matches)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Illegal move " + Notation.toNotation(move)));
    }

}
