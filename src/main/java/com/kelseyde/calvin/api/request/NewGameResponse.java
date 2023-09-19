package com.kelseyde.calvin.api.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NewGameResponse {

    private final String gameId;

}
