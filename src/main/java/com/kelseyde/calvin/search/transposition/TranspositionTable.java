package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TranspositionTable implements TT {

    private final Board board;

    private ConcurrentHashMap<Long, Transposition> entries;

    private int tries;
    private int hits;

    public TranspositionTable(Board board) {
        this.board = board;
        entries = new ConcurrentHashMap<>();
        tries = 0;
        hits = 0;
    }

    public Transposition get() {
        long zobristKey = board.getGameState().getZobristKey();
        Transposition entry = entries.get(zobristKey);
        tries++;
        if (entry != null) hits++;
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, int depth, Move move, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        Transposition entry = new Transposition(zobristKey, type, move, depth, value);
        entries.put(zobristKey, entry);
    }

    public void clear() {
        printStatistics();
        entries = new ConcurrentHashMap<>();
    }

    public int size() {
        return entries.size();
    }

    public void printStatistics() {
        float hitRate = ((float) hits / (float) tries) * 100;
        System.out.printf("Old: tries: %s, hits: %s (%s)%n", tries, hits, hitRate);
//        System.out.printf("Old size: %s%n", entries.values().stream()
//                .filter(Objects::nonNull)
//                .count());
    }

}
