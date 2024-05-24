package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.generation.MoveGeneration;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import com.kelseyde.calvin.transposition.TranspositionTable;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Implementation of {@link Search} that uses a parallel search strategy called 'Lazy SMP'. The idea is to have multiple
 * threads searching the same position simultaneously, but sharing a {@link TranspositionTable}, so each thread benefits
 * from the work of the others. Each thread is simply a {@link Searcher} that runs its own iterative deepening loop.
 *
 * @see <a href="https://www.chessprogramming.org/Lazy_SMP">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParallelSearcher implements Search {

    final EngineConfig config;
    final Supplier<MoveGeneration> moveGeneratorSupplier;
    final Supplier<MoveOrdering> moveOrdererSupplier;
    final Supplier<Evaluation> evaluatorSupplier;
    ThreadManager threadManager;
    TranspositionTable transpositionTable;
    int threadCount;
    int hashSize;
    Board board;

    private List<Searcher> searchers;

    public ParallelSearcher(EngineConfig config,
                            Supplier<MoveGeneration> moveGeneratorSupplier,
                            Supplier<MoveOrdering> moveOrdererSupplier,
                            Supplier<Evaluation> evaluatorSupplier,
                            TranspositionTable transpositionTable) {
        this.config = config;
        this.hashSize = config.getDefaultHashSizeMb();
        this.threadCount = config.getDefaultThreadCount();
        this.threadManager = new ThreadManager();
        this.transpositionTable = transpositionTable;
        this.moveGeneratorSupplier = moveGeneratorSupplier;
        this.moveOrdererSupplier = moveOrdererSupplier;
        this.evaluatorSupplier = evaluatorSupplier;
        this.searchers = initSearchers();
    }

    @Override
    public SearchResult search(Duration duration) {
        try {
            setPosition(board);
            threadManager.reset();
            List<CompletableFuture<SearchResult>> threads = searchers.stream()
                    .map(searcher -> CompletableFuture.supplyAsync(() -> searcher.search(duration)))
                    .toList();
            SearchResult result = selectResult(threads).get();
            threads.forEach(thread -> thread.cancel(true));
            transpositionTable.incrementGeneration();
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
        searchers.forEach(searcher -> searcher.setPosition(BoardUtils.copy(board)));
    }

    @Override
    public void setHashSize(int hashSizeMb) {
        this.hashSize = hashSizeMb;
        this.transpositionTable = new TranspositionTable(this.hashSize);
        this.searchers = initSearchers();
    }

    @Override
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        this.searchers = initSearchers();
    }

    /**
     * Combines the {@link SearchResult} results of the different threads and selects a final result to use.
     * Simply selects the result from the thread which searched to the greatest depth.
     */
    private CompletableFuture<SearchResult> selectResult(List<CompletableFuture<SearchResult>> threads) {
        CompletableFuture<SearchResult> collector = CompletableFuture.completedFuture(new SearchResult(0, null, 0, 0, 0, 0));
        for (CompletableFuture<SearchResult> thread : threads) {
            collector = collector.thenCombine(thread, (thread1, thread2) -> thread1.depth() > thread2.depth() ? thread1 : thread2);
        }
        return collector;
    }

    private List<Searcher> initSearchers() {
        return IntStream.range(0, threadCount).mapToObj(i -> initSearcher()).toList();
    }

    private Searcher initSearcher() {
        MoveGeneration moveGenerator = this.moveGeneratorSupplier.get();
        MoveOrdering moveOrderer = this.moveOrdererSupplier.get();
        Evaluation evaluator = this.evaluatorSupplier.get();
        return new Searcher(config, threadManager, moveGenerator, moveOrderer, evaluator, transpositionTable);
    }

    @Override
    public TranspositionTable getTranspositionTable() {
        return transpositionTable;
    }

    @Override
    public void clearHistory() {
        transpositionTable.clear();
        searchers.forEach(Searcher::clearHistory);
    }

    @Override
    public void logStatistics() {

    }

}
