package com.kelseyde.calvin.transposition.pawn;

public record PawnHashEntry(long key,
                            PawnScore whiteScore,
                            PawnScore blackScore) {

    public record PawnScore(int pawnPlacementMgScore,
                                   int pawnPlacementEgScore,
                                   int pawnStructureMgScore,
                                   int pawnStructureEgScore) {

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PawnScore otherScore)) {
                return false;
            }
            return this.pawnPlacementMgScore == otherScore.pawnPlacementMgScore
                    && this.pawnPlacementEgScore == otherScore.pawnPlacementEgScore
                    && this.pawnStructureMgScore == otherScore.pawnStructureMgScore
                    && this.pawnStructureEgScore == otherScore.pawnStructureEgScore;
        }
    }

}
