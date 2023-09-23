package com.kelseyde.calvin.api.repository;

import com.kelseyde.calvin.board.Board;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryBoardRepository implements BoardRepository {

    private final Map<String, Board> boardMap = new HashMap<>();

    @Override
    public Optional<Board> getBoard(String boardId) {
        return Optional.ofNullable(boardMap.get(boardId));
    }

    @Override
    public void putBoard(Board board) {
        boardMap.put(board.getId(), board);
    }

}
