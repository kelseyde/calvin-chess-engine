package com.kelseyde.calvin.api.http.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NewGameResponse {

    private final String gameId;

    private MoveResponse move;

}
