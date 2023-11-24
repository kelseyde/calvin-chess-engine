package com.kelseyde.calvin.tuning.copy;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.ParallelSearcher;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Data
@Slf4j
public class CalvinBot2 implements Bot {

    private static final int DEFAULT_THREAD_COUNT = 6;

    private static final int PONDER_DURATION_MS = 60 * 60 * 1000; // 1 hour

    @Getter
    private Board board;

    private MoveGenerator moveGenerator;

    private Search search;

    private CompletableFuture<Move> think;

    public CalvinBot2() {
        this.search = new ParallelSearcher(new Board(), DEFAULT_THREAD_COUNT);
    }

    public CalvinBot2(Search search) {
        this.search = search;
    }

    @Override
    public void newGame() {
        if (search != null) {
            search.clearHistory();
        }
    }

    @Override
    public void setPosition(String fen, List<Move> moves) {
        if (board == null) {
            board = FEN.toBoard(fen);
            search.init(board);
            moveGenerator = new MoveGenerator();
            moves.stream()
                    .map(this::getLegalMove)
                    .forEach(this::applyMove);
        } else {
            if (!moves.isEmpty()) {
                Move lastMove = moves.get(moves.size() - 1);
                board.makeMove(getLegalMove(lastMove));
            }
        }
    }

    @Override
    public void applyMove(Move move) {
        board.makeMove(move);
        search.setPosition(board);
    }

    @Override
    public void think(int thinkTimeMs, Consumer<Move> onThinkComplete) {
        stopThinking();
        think = CompletableFuture.supplyAsync(() -> think(thinkTimeMs));
        think.thenAccept((move -> {
            applyMove(move);
            onThinkComplete.accept(move);
        }));
    }

    @Override
    public Move think(int thinkTimeMs) {
        return search.search(Duration.ofMillis(thinkTimeMs)).move();
//        startPondering(); TODO
    }

    @Override
    public void startPondering() {
        think(PONDER_DURATION_MS, (m) -> stopThinking());
    }

    @Override
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
//        search.logStatistics();
        board = null;
    }

    // TODO can be removed?
    private Move getLegalMove(Move move) {
        return moveGenerator.generateMoves(board, false).stream()
                .filter(move::matches)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Illegal move " + NotationUtils.toNotation(move)));
    }

}
