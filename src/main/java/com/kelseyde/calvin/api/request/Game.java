package com.kelseyde.calvin.api.request;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.search.DepthSearch;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Game {

    private String id;

    private Board board;

    private DepthSearch engine;

}
