package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;

public record SearchResult(int eval, Move move, Move ponderMove, int depth) { }
