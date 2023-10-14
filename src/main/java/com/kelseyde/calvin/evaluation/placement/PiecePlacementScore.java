package com.kelseyde.calvin.evaluation.placement;

public record PiecePlacementScore(int pawnScore,
                                  int knightScore,
                                  int bishopScore,
                                  int rookScore,
                                  int queenScore,
                                  int kingScore) {
    public int sum() {
        return pawnScore + knightScore + bishopScore + rookScore + queenScore + kingScore;
    }
}
