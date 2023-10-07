package com.kelseyde.calvin.evaluation.material;

public record Material(int pawns,
                       int knights,
                       int bishops,
                       int rooks,
                       int queens,
                       float phase,
                       int eval) {
}
