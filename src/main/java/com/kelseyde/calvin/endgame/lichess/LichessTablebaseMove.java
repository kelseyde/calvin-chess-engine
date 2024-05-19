package com.kelseyde.calvin.endgame.lichess;

/**
 * Stores information for a single move returned from the Lichess tablebase API.
 */
public record LichessTablebaseMove(String uci,
                                   String san,
                                   int dtz,
                                   int precise_dtz,
                                   int dtm,
                                   boolean zeroing,
                                   boolean checkmate,
                                   boolean stalemate,
                                   boolean variant_win,
                                   boolean variant_loss,
                                   boolean insufficient_material,
                                   String category) {
}
