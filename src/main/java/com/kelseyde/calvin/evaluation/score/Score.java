package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Piece;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@NoArgsConstructor
public class Score {

    public static final int MATE_SCORE = 1000000;
    public static final int DRAW_SCORE = 0;
    public static final int[] SIMPLE_PIECE_VALUES = Arrays.stream(Piece.values()).mapToInt(Piece::getValue).toArray();

    int whiteMaterialMgScore;
    int whiteMaterialEgScore;
    int blackMaterialMgScore;
    int blackMaterialEgScore;

    int whitePiecePlacementScore;
    int blackPiecePlacementScore;

    int whiteMobilityMgScore;
    int whiteMobilityEgScore;
    int blackMobilityMgScore;
    int blackMobilityEgScore;

    int whitePawnStructureMgScore;
    int whitePawnStructureEgScore;
    int blackPawnStructureMgScore;
    int blackPawnStructureEgScore;

    int whiteKingSafetyScore;
    int blackKingSafetyScore;

    int whiteRookMgScore;
    int whiteRookEgScore;
    int blackRookMgScore;
    int blackRookEgScore;

    int whiteMopUpScore;
    int blackMopUpScore;

    int whiteTempoBonus;
    int blackTempoBonus;

    float phase;

    public void addMaterialScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            whiteMaterialMgScore += middlegameScore;
            whiteMaterialEgScore += endgameScore;
        } else {
            blackMaterialMgScore += middlegameScore;
            blackMaterialEgScore += endgameScore;
        }
    }

    public void addPiecePlacementScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            whitePiecePlacementScore += Phase.taperedEval(middlegameScore, endgameScore, phase);
        } else {
            blackPiecePlacementScore += Phase.taperedEval(middlegameScore, endgameScore, phase);
        }
    }

    public void addMobilityScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            whiteMobilityMgScore += middlegameScore;
            whiteMobilityEgScore += endgameScore;
        } else {
            blackMobilityMgScore += middlegameScore;
            blackMobilityEgScore += endgameScore;
        }
    }

    public void addPawnStructureScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            whitePawnStructureMgScore += middlegameScore;
            whitePawnStructureEgScore += endgameScore;
        } else {
            blackPawnStructureMgScore += middlegameScore;
            blackPawnStructureEgScore += endgameScore;
        }
    }

    public void addRookScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            whiteRookMgScore += middlegameScore;
            whiteRookEgScore += endgameScore;
        } else {
            blackRookMgScore += middlegameScore;
            blackRookEgScore += endgameScore;
        }
    }

    public void setKingSafetyScore(int score, boolean isWhite) {
        if (isWhite) {
            whiteKingSafetyScore = score;
        } else {
            blackKingSafetyScore = score;
        }
    }

    public void setMopUpScore(int score, boolean isWhite) {
        if (isWhite) {
            whiteMopUpScore = score;
        } else {
            blackMopUpScore = score;
        }
    }

    public int sum(boolean isWhite) {

        int whiteMaterialScore = Phase.taperedEval(whiteMaterialMgScore, whiteMaterialEgScore, phase);
        int blackMaterialScore = Phase.taperedEval(blackMaterialMgScore, blackMaterialEgScore, phase);

        int whiteMobilityScore = Phase.taperedEval(whiteMobilityMgScore, whiteMobilityEgScore, phase);
        int blackMobilityScore = Phase.taperedEval(blackMobilityMgScore, blackMobilityEgScore, phase);

        int whitePawnStructureScore = Phase.taperedEval(whitePawnStructureMgScore, whitePawnStructureEgScore, phase);
        int blackPawnStructureScore = Phase.taperedEval(blackPawnStructureMgScore, blackPawnStructureEgScore, phase);

        int whiteRookScore = Phase.taperedEval(whiteRookMgScore, whiteRookEgScore, phase);
        int blackRookScore = Phase.taperedEval(blackRookMgScore, blackRookEgScore, phase);

        int whiteScore = whiteMaterialScore + whitePiecePlacementScore + whiteMobilityScore + whitePawnStructureScore +
                whiteKingSafetyScore + whiteRookScore + whiteMopUpScore + whiteTempoBonus;

        int blackScore = blackMaterialScore + blackPiecePlacementScore + blackMobilityScore + blackPawnStructureScore +
                blackKingSafetyScore + blackRookScore + blackMopUpScore + blackTempoBonus;

        int modifier = isWhite ? 1 : -1;
        return modifier * (whiteScore - blackScore);

    }

}
