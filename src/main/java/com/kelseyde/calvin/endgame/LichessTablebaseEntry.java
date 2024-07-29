package com.kelseyde.calvin.endgame;

import java.util.List;

/**
 * The response from the Lichess tablebase API containing the tablebase entry for the provided FEN.
 */
public record LichessTablebaseEntry(int dtz,
                                    int precise_dtz,
                                    int dtm,
                                    boolean checkmate,
                                    boolean stalemate,
                                    boolean insufficient_material,
                                    boolean variant_win,
                                    boolean variant_loss,
                                    String category,
                                    List<LichessTablebaseMove> moves) {

}
