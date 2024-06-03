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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * The ParallelSearcher class is an implementation of the {@link Search} interface that uses a parallel search strategy
 * called 'Lazy SMP'. The idea is to have multiple threads searching the same position simultaneously, but sharing a
 * {@link TranspositionTable}, so each thread benefits from the work of the others. Each thread is simply a Searcher
 * that runs its own iterative deepening loop.
 * </p>
 * Lazy SMP (Symmetric MultiProcessing) is a simple yet effective technique for parallelizing search trees. It involves
 * running multiple instances of the search algorithm on the same position, with each instance running on a separate CPU
 * core. The instances share a transposition table, which allows them to benefit from each other's work. The "lazy" part
 * of the name comes from the fact that there is no explicit work division or synchronization between the threads - they
 * each do their own thing, but can use the results of the others' work when they come across the same positions.
 *
 * @see <a href="https://www.chessprogramming.org/Lazy_SMP">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParallelSearcher implements Search {

    final EngineConfig config;
    final Supplier<MoveGeneration> moveGeneratorSupplier;
    final Supplier<MoveOrdering> moveOrdererSupplier;
    final Supplier<Evaluation> evaluatorSupplier;
    final ThreadManager threadManager;
    TranspositionTable transpositionTable;
    int threadCount;
    int hashSize;
    Board board;
    private List<Searcher> searchers;
    private final ExecutorService executor;

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
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public SearchResult search(Duration duration) {
        try {
            setPosition(board);
            threadManager.reset();
            List<Thread> threads = searchers.stream()
                    .map(searcher -> Thread.ofVirtual().start(() -> searcher.search(duration)))
                    .toList();

            for (Thread thread : threads) {
                thread.join();
            }

            SearchResult result = selectResult(searchers);
            transpositionTable.incrementGeneration();
            return result;
        } catch (InterruptedException e) {
            System.out.println("info error " + e);
            return SearchResult.empty();
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
    private SearchResult selectResult(List<Searcher> searchers) {
        return searchers.stream()
                .map(Searcher::getResult)
                .max(Comparator.comparingInt(SearchResult::depth))
                .orElseThrow();
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
        System.out.println("info threads " + searchers.stream().map(Searcher::toString).toList());
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

}
