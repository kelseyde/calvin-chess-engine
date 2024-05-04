package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.opening.OpeningBook;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
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
    OpeningBook book;
    MoveGeneration moveGenerator;
    Search searcher;
    CompletableFuture<SearchResult> think;
    Board board;

    public Engine(EngineConfig config, OpeningBook book, MoveGeneration moveGenerator, Search searcher) {
        this.config = config;
        this.book = book;
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
        this.searcher.setHashSize(hashSizeMb);
    }

    public void setThreadCount(int threadCount) {
        this.searcher.setThreadCount(threadCount);
    }

    public void setOwnBookEnabled(boolean ownBookEnabled) {
        this.config.setOwnBookEnabled(ownBookEnabled);
    }

    public void setPonderEnabled(boolean ponderEnabled) {
        this.config.setPonderEnabled(ponderEnabled);
    }

    public void setPondering(boolean pondering) {
        this.config.setPondering(pondering);
    }

    public void think(int thinkTimeMs, Consumer<SearchResult> onThinkComplete) {
        if (hasBookMove()) {
            Move bookMove = getLegalMove(book.getBookMove(board.getGameState().getZobristKey()));
            onThinkComplete.accept(new SearchResult(0, bookMove, null, 0));
            return;
        }
        stopThinking();
        think = CompletableFuture.supplyAsync(() -> think(thinkTimeMs));
        think.thenAccept(onThinkComplete);
    }

    public SearchResult think(int thinkTimeMs) {
        Duration thinkTime = Duration.ofMillis(thinkTimeMs);
        return searcher.search(thinkTime);
    }

    public boolean isThinking() {
        return think != null && !think.isDone();
    }

    public void stopThinking() {
        if (isThinking()) {
            think.cancel(true);
        }
    }

    public int chooseThinkTime(int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs) {

        boolean isWhite = board.isWhiteToMove();
        int timeRemainingMs = isWhite ? timeWhiteMs : timeBlackMs;
        int incrementMs = isWhite ? incrementWhiteMs : incrementBlackMs;

        // Add a small overhead to avoid timing out
        int overheadMs = 50;

        int minSearchTimeMs = 50;

        // A game lasts on average 45 moves
        int movesDivisor = config.getDefaultMovesToGo();

        timeRemainingMs -= overheadMs;
        timeRemainingMs = Math.clamp(timeRemainingMs, minSearchTimeMs, Integer.MAX_VALUE);

        int thinkTimeHardLimitMs = (int) (timeRemainingMs * config.getHardTimeBoundMultiplier());

        float softLimitBase = ((float) timeRemainingMs / movesDivisor) + (incrementMs * config.getSoftTimeBaseIncrementMultiplier());
        int thinkTimeSoftLimitMs = Math.min(thinkTimeHardLimitMs, (int)(softLimitBase * config.getSoftTimeBoundMultiplier()));

        return thinkTimeSoftLimitMs;
    }

    public void gameOver() {
        stopThinking();
        board = null;
    }

    private boolean hasBookMove() {
        long key = board.getGameState().getZobristKey();
        int moveCount = board.getMoveHistory().size();
        return config.isOwnBookEnabled() && moveCount < config.getMaxBookMoves() && book.hasBookMove(key);
    }

    private Move getLegalMove(Move move) {
        return moveGenerator.generateMoves(board).stream()
                .filter(move::matches)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Illegal move " + Notation.toNotation(move)));
    }

}
