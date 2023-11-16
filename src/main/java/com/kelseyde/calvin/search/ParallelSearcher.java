package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import com.kelseyde.calvin.tuning.SearchResult;
import com.kelseyde.calvin.utils.BoardUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class ParallelSearcher implements Search {

    private final int threadCount;

    private TranspositionTable sharedTranspositionTable;

    private final List<Searcher> searchers;

    public ParallelSearcher(Board board, int threadCount) {
        this.threadCount = threadCount;
        this.searchers = new ArrayList<>();
        init(board);
    }

    @Override
    public void init(Board board) {
        this.sharedTranspositionTable = new TranspositionTable(board);
        IntStream.range(0, threadCount)
                .forEach(i -> searchers.add(new Searcher(BoardUtils.copy(board), sharedTranspositionTable)));
    }

    @Override
    public SearchResult search(Duration duration) {

        CompletableFuture<SearchResult> thread1 = CompletableFuture.supplyAsync(() -> searchers.get(0).search(duration));
        CompletableFuture<SearchResult> thread2 = CompletableFuture.supplyAsync(() -> searchers.get(1).search(duration));
        CompletableFuture<SearchResult> thread3 = CompletableFuture.supplyAsync(() -> searchers.get(2).search(duration));

        try {
            return (SearchResult) CompletableFuture.anyOf(thread1, thread2, thread3).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void clearHistory() {
        if (sharedTranspositionTable != null) {
            sharedTranspositionTable.clear();
        }
    }

    @Override
    public void logStatistics() {

    }

}
