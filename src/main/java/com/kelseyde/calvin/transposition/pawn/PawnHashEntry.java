package com.kelseyde.calvin.transposition.pawn;

public record PawnHashEntry(long key,
                            PawnScore whiteScore,
                            PawnScore blackScore) {

    public record PawnScore(int mgScore, int egScore) {

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PawnScore otherScore)) {
                return false;
            }
            return this.mgScore == otherScore.mgScore && this.egScore == otherScore.egScore;
        }
    }

}
