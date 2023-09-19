package com.kelseyde.calvin.model.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NextMoveResult extends GameResult {

    private final ResultType resultType = ResultType.NEXT_MOVE;

    private final boolean isWhiteToMove;

}
