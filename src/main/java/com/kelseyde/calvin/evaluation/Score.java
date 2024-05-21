package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
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

    int whiteMiddlegameScore;
    int whiteEndgameScore;

    int blackMiddlegameScore;
    int blackEndgameScore;

    int whiteKingScore;
    int blackKingScore;

    int materialPhase;

    int whiteTempoBonus;
    int blackTempoBonus;

    float phase;

    public void add(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whiteMiddlegameScore += middlegameScore;
            whiteEndgameScore += endgameScore;
        } else {
            blackMiddlegameScore += middlegameScore;
            blackEndgameScore += endgameScore;
        }
    }

    public void addMaterial(Piece piece, EngineConfig config) {
        materialPhase += config.getPiecePhases()[piece.getIndex()];
    }

    public void addKingScore(int score, boolean white) {
        if (white) {
            whiteKingScore += score;
        } else {
            blackKingScore += score;
        }
    }

    public void addTempoBonus(int tempoBonus, boolean white) {
        if (white) {
            whiteTempoBonus = tempoBonus;
        } else {
            blackTempoBonus = tempoBonus;
        }
    }


    public int sum(EngineConfig config, boolean white) {
        float phase = materialPhase / config.getTotalPhase();
        int whiteScore = taperedEval(whiteMiddlegameScore, whiteEndgameScore, phase) + whiteTempoBonus;
        int blackScore = taperedEval(blackMiddlegameScore, blackEndgameScore, phase) + blackTempoBonus;
        int score = whiteScore - blackScore;
        int modifier = white ? 1 : -1;
        return score * modifier;
    }

    private int taperedEval(int middlegameScore, int endgameScore, float phase) {
        return (int) (phase * middlegameScore) + (int) ((1 - phase) * endgameScore);
    }

}
