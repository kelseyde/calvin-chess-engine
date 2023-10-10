package com.kelseyde.calvin.search.repetition;

import com.kelseyde.calvin.board.Board;

import java.util.HashMap;
import java.util.Map;

public class RepetitionTable {

    private final Map<Long, Integer> hashes;

    public RepetitionTable(Board board) {
        hashes = new HashMap<>();
        board.getGameStateHistory().forEach(gameState -> push(gameState.getZobristKey()));
    }

    public void push(Board board) {
        long hash = board.getGameState().getZobristKey();
        hashes.compute(hash, (key, value) -> value == null ? 1 : value++);
    }

    public void push(long hash) {
        hashes.compute(hash, (key, value) -> value == null ? 1 : value++);
    }

    public void pop(Board board) {
        long hash = board.getGameState().getZobristKey();
        hashes.computeIfPresent(hash, (key, value) -> value--);
    }

    public boolean isRepeated(Board board) {
        long hash = board.getGameState().getZobristKey();
        return hashes.getOrDefault(hash, 0) > 1;
    }

}
