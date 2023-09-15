package com.kelseyde.calvin.model.result;

import com.kelseyde.calvin.model.DrawType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DrawResult extends GameResult {

    private final ResultType resultType = ResultType.DRAW;

    private final DrawType drawType;


}
