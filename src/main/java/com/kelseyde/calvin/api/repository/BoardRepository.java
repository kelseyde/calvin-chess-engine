package com.kelseyde.calvin.api.repository;

import com.kelseyde.calvin.board.Board;

import java.util.Optional;

public interface BoardRepository {

    Optional<Board> getBoard(String boardId);

    void putBoard(Board board);

}
