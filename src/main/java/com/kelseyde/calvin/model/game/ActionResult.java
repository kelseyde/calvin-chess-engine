package com.kelseyde.calvin.model.game;

import com.kelseyde.calvin.model.Colour;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionResult {

    @Builder.Default
    boolean isValidMove = true;

    // The relevant fields if the game is ongoing.
    Colour sideToPlay;

    // The relevant fields if the game ended in victory for one side.
    boolean isWin;
    Colour winningColour;
    WinType winType;

    // The relevant fields if the game ended in a draw.
    boolean isDraw;
    DrawType drawType;

}
