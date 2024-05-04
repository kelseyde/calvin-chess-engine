package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.opening.OpeningBook;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.transposition.HashEntry;
import com.kelseyde.calvin.transposition.TranspositionTable;
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

    // TODO ponder indefinitely
    private static final int PONDER_TIME_MS = 3600000;

    EngineConfig config;
    OpeningBook book;
    MoveGeneration moveGenerator;
    Search searcher;
    CompletableFuture<Move> think;
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

    public void think(int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs, Consumer<Move> onThinkComplete) {
        if (hasBookMove()) {
            onThinkComplete.accept(getLegalMove(book.getBookMove(board.getGameState().getZobristKey())));
            return;
        }
        int thinkTimeMs = TimeManager.chooseThinkTime(board.isWhiteToMove(), timeWhiteMs, timeBlackMs, incrementWhiteMs, incrementBlackMs);
        think(thinkTimeMs, onThinkComplete);
    }

    public void think(int thinkTimeMs, Consumer<Move> onThinkComplete) {
        if (hasBookMove()) {
            onThinkComplete.accept(getLegalMove(book.getBookMove(board.getGameState().getZobristKey())));
            return;
        }
        stopThinking();
        think = CompletableFuture.supplyAsync(() -> think(thinkTimeMs));
        think.thenAccept(onThinkComplete);
        ponder();
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

    private void ponder() {
        TranspositionTable transpositionTable = searcher.getTranspositionTable();
        long zobristKey = board.getGameState().getZobristKey();
        HashEntry entry = transpositionTable.get(zobristKey, 0);
        if (entry.getMove() != null) {
            System.out.println("pondering...");
            board.makeMove(entry.getMove());
            think = CompletableFuture.supplyAsync(() -> think(PONDER_TIME_MS));
            think.thenAccept(move -> System.out.println("finished pondering"));
        }
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
