package com.kelseyde.calvin.api.request;

import com.kelseyde.calvin.movegeneration.result.GameResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayResponse {

    String gameId;
    GameResult result;
    MoveResponse move;

}
