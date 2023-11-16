package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TranspositionTable implements TT {

    private final Board board;

    private ConcurrentHashMap<Long, Transposition> entries;

    public TranspositionTable(Board board) {
        this.board = board;
        entries = new ConcurrentHashMap<>();
    }

    public Transposition get() {
        long zobristKey = board.getGameState().getZobristKey();
        Transposition entry = entries.get(zobristKey);
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, int depth, Move move, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        Transposition entry = new Transposition(zobristKey, type, move, depth, value);
        entries.put(zobristKey, entry);
    }

    public void clear() {
        entries = new ConcurrentHashMap<>();
    }

    public int size() {
        return entries.size();
    }

}
