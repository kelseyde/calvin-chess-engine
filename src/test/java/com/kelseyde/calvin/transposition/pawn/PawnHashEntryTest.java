package com.kelseyde.calvin.transposition.pawn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PawnHashEntryTest {

    @Test
    public void testScoreEncoding() {
        int whiteMgScore = 103;
        int blackMgScore = -103;
        int whiteEgScore = -22;
        int blackEgScore = 22;

        int whiteScore = PawnHashEntry.encode(whiteMgScore, whiteEgScore);
        Assertions.assertEquals(103, PawnHashEntry.mgScore(whiteScore));
        Assertions.assertEquals(-22, PawnHashEntry.egScore(whiteScore));

        int blackScore = PawnHashEntry.encode(blackMgScore, blackEgScore);
        Assertions.assertEquals(-103, PawnHashEntry.mgScore(blackScore));
        Assertions.assertEquals(22, PawnHashEntry.egScore(blackScore));

        PawnHashEntry entry = PawnHashEntry.of(1L, whiteScore, blackScore);

        int hashWhiteScore = entry.whiteScore();
        int hashBlackScore = entry.blackScore();
        int hashWhiteMgScore = PawnHashEntry.mgScore(hashWhiteScore);
        int hashWhiteEgScore = PawnHashEntry.egScore(hashWhiteScore);
        int hashBlackMgScore = PawnHashEntry.mgScore(hashBlackScore);
        int hashBlackEgScore = PawnHashEntry.egScore(hashBlackScore);
        Assertions.assertEquals(103, hashWhiteMgScore);
        Assertions.assertEquals(-103, hashBlackMgScore);
        Assertions.assertEquals(-22, hashWhiteEgScore);
        Assertions.assertEquals(22, hashBlackEgScore);
    }

}