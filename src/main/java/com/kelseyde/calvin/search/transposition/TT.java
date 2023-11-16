package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Move;

public interface TT {

    Transposition get();

    void put(NodeType type, int depth, Move move, int score);

    void clear();

}
