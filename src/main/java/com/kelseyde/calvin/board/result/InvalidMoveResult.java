package com.kelseyde.calvin.board.result;

import com.kelseyde.calvin.board.move.Move;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidMoveResult extends GameResult {

    private final ResultType resultType = ResultType.ILLEGAL_MOVE;

    private final Move invalidMove;

}
