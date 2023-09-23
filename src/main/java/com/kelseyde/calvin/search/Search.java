package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;

public interface Search {

    SearchResult search(Board board, int depth);

}
