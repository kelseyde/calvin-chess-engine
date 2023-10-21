package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;

@Slf4j
public class TranspositionTable {

    private final Board board;

    private LinkedHashMap<Long, TranspositionNode> entries;

    public TranspositionTable(Board board) {
        this.board = board;
        entries = new LinkedHashMap<>();
    }

    public TranspositionNode get() {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionNode entry = entries.get(zobristKey);
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, int depth, Move move, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionNode entry = new TranspositionNode(zobristKey, type, move, depth, value);
        entries.put(zobristKey, entry);
    }

    public void clear() {
        entries = new LinkedHashMap<>();
    }

    public int size() {
        return entries.size();
    }

}
