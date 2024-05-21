package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Piece;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@NoArgsConstructor
public class Score {

    public static final int MATE_SCORE = 1000000;
    public static final int DRAW_SCORE = 0;

    public static boolean isMateScore(int eval) {
        return Math.abs(eval) >= Score.MATE_SCORE - 100;
    }

    int whiteMgScore;
    int blackMgScore;

    int whiteEgScore;
    int blackEgScore;

    int whiteTempoBonus;
    int blackTempoBonus;

    int scaleFactor = 1;

    float phase;

    public void addScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whiteMgScore += middlegameScore;
            whiteEgScore += endgameScore;
        } else {
            blackMgScore += middlegameScore;
            blackEgScore += endgameScore;
        }
    }

    public void setTempoBonus(int score, boolean white) {
        if (white) {
            whiteTempoBonus = score;
        } else {
            blackTempoBonus = score;
        }
    }

    public int sum(boolean white) {
        int whiteScore = Phase.taperedEval(whiteMgScore, whiteEgScore, phase) + whiteTempoBonus;
        int blackScore = Phase.taperedEval(blackMgScore, blackEgScore, phase) + blackTempoBonus;
        int score = whiteScore - blackScore;
        int modifier = white ? 1 : -1;
        return score * modifier;
    }

}
