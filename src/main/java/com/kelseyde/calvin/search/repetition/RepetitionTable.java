package com.kelseyde.calvin.search.repetition;

import com.kelseyde.calvin.board.Board;

import java.util.HashMap;
import java.util.Map;

public class RepetitionTable {

    private Map<Long, Integer> hashes;

    public RepetitionTable(Board board) {
        init(board);
    }

    public void init(Board board) {
        hashes = new HashMap<>();
        push(board.getGameState().getZobristKey());
        board.getGameStateHistory().forEach(gameState -> push(gameState.getZobristKey()));
    }

    public void push(long hash) {
        hashes.compute(hash, (key, value) -> value == null ? 1 : ++value);
    }

    public void pop(long hash) {
        hashes.computeIfPresent(hash, (key, value) -> --value);
    }

    public boolean isThreefoldRepetition(long hash) {
        return hashes.getOrDefault(hash, 0) == 3;
    }

}
