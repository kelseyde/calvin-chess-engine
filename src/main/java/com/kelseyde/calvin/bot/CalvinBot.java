package com.kelseyde.calvin.bot;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.iterative.IterativeDeepeningSearch;
import com.kelseyde.calvin.utils.fen.FEN;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Data
@Component
@Slf4j
public class CalvinBot implements Bot {

    private Search search;

    private Board board;

    private CompletableFuture<Move> think;

    private boolean gameInProgress;

    private boolean maxThinkTimeEnabled;
    private int maxThinkTimeMs = 2500;

    @Override
    public void newGame() {
        if (gameInProgress) {
        }
        gameInProgress = true;
        if (search != null) {
            search.clearHistory();
        }
    }

    @Override
    public void setPosition(String fen, List<Move> moves) {
        if (board == null) {
            board = FEN.fromFEN(fen);
            search = new IterativeDeepeningSearch(board);
            moves.forEach(board::makeMove);
        } else {
            if (!moves.isEmpty()) {
                Move lastMove = moves.get(moves.size() - 1);
                board.makeMove(lastMove);
            }
        }
    }

    @Override
    public void think(int thinkTimeMs, Consumer<Move> onThinkComplete) {
        think = CompletableFuture.supplyAsync(() -> search.search(Duration.ofMillis(thinkTimeMs)).move());
        think.thenAccept(onThinkComplete);
    }

    @Override
    public int chooseThinkTime(int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs) {

        int timeRemainingMs = board.isWhiteToMove() ? timeWhiteMs : timeBlackMs;
        int incrementMs = board.isWhiteToMove() ? incrementWhiteMs : incrementBlackMs;

        // A game lasts on average 40 moves, so start with a simple fraction of the remaining time.
        double thinkTimeMs = timeRemainingMs / 40.0;

        if (maxThinkTimeEnabled) {
            thinkTimeMs = Math.min(thinkTimeMs, maxThinkTimeMs);
        }

        if (thinkTimeMs > incrementMs * 2) {
            thinkTimeMs += incrementMs * 0.8;
        }

        double minThinkTimeMs = Math.min(50, timeRemainingMs * 0.25);

        thinkTimeMs =  Math.max(thinkTimeMs, minThinkTimeMs);
        log.trace("Selecting think time {}", Duration.ofMillis((int) thinkTimeMs));
        return (int) thinkTimeMs;

    }

    @Override
    public boolean isThinking() {
        return think != null && !think.isDone();
    }

    @Override
    public void stopThinking() {
        if (isThinking()) {
            think.cancel(true);
        }
    }

    @Override
    public void gameOver() {
        if (isThinking()) {
            stopThinking();
        }
        board = null;
        search = null;
    }

}
