package com.kelseyde.calvin.endgame.lichess;

import java.util.List;

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
