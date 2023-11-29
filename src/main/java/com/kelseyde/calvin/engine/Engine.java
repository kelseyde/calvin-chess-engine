package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.search.ParallelSearcher;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.utils.notation.FEN;
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
    Board board;
    Search search;
    MoveGenerator moveGenerator;
    CompletableFuture<Move> think;

    public Engine(EngineConfig config) {
        this.search = new ParallelSearcher(config, board);
        this.moveGenerator = new MoveGenerator();
    }

    public void newGame() {
        if (search != null) {
            search.clearHistory();
        }
    }

    public void setPosition(String fen, List<Move> moves) {
        if (board == null) {
            board = FEN.toBoard(fen);
            search.init(board);
            moveGenerator = new MoveGenerator();
            moves.forEach(this::applyMove);
        } else {
            if (!moves.isEmpty()) {
                Move lastMove = moves.get(moves.size() - 1);
                board.makeMove(lastMove);
            }
        }
    }

    public void applyMove(Move move) {
        board.makeMove(move);
        search.setPosition(board);
    }

    public void think(int thinkTimeMs, Consumer<Move> onThinkComplete) {
        stopThinking();
        think = CompletableFuture.supplyAsync(() -> think(thinkTimeMs));
        think.thenAccept((move -> {
            applyMove(move);
            onThinkComplete.accept(move);
        }));
    }

    public Move think(int thinkTimeMs) {
        return search.search(Duration.ofMillis(thinkTimeMs)).move();
    }

    public int chooseThinkTime(int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs) {

        int timeRemainingMs = board.isWhiteToMove() ? timeWhiteMs : timeBlackMs;
        int incrementMs = board.isWhiteToMove() ? incrementWhiteMs : incrementBlackMs;

        // A game lasts on average 40 moves, so start with a simple fraction of the remaining time.
        double thinkTimeMs = timeRemainingMs / 40.0;

        if (thinkTimeMs > incrementMs * 2) {
            thinkTimeMs += incrementMs * 0.8;
        }

        double minThinkTimeMs = Math.min(50, timeRemainingMs * 0.25);

        thinkTimeMs = Math.max(thinkTimeMs, minThinkTimeMs);
        return (int) thinkTimeMs;

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
        if (isThinking()) {
            stopThinking();
        }
        board = null;
    }

}
