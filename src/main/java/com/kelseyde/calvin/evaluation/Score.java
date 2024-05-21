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
    public static final int[] SIMPLE_PIECE_VALUES = Arrays.stream(Piece.values()).mapToInt(Piece::getValue).toArray();

    public static boolean isMateScore(int eval) {
        return Math.abs(eval) >= Score.MATE_SCORE - 100;
    }

    int whiteMaterialMgScore;
    int whiteMaterialEgScore;
    int blackMaterialMgScore;
    int blackMaterialEgScore;

    int whitePiecePlacementMgScore;
    int whitePiecePlacementEgScore;
    int blackPiecePlacementMgScore;
    int blackPiecePlacementEgScore;

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

    int whiteKnightMgScore;
    int whiteKnightEgScore;
    int blackKnightMgScore;
    int blackKnightEgScore;

    int whiteBishopMgScore;
    int whiteBishopEgScore;
    int blackBishopMgScore;
    int blackBishopEgScore;

    int whiteRookMgScore;
    int whiteRookEgScore;
    int blackRookMgScore;
    int blackRookEgScore;

    int whiteMopUpScore;
    int blackMopUpScore;

    int whiteTempoBonus;
    int blackTempoBonus;

    int scaleFactor = 1;

    float phase;

    public void addMaterialScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whiteMaterialMgScore += middlegameScore;
            whiteMaterialEgScore += endgameScore;
        } else {
            blackMaterialMgScore += middlegameScore;
            blackMaterialEgScore += endgameScore;
        }
    }

    public void addPiecePlacementScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whitePiecePlacementMgScore += middlegameScore;
            whitePiecePlacementEgScore += endgameScore;
        } else {
            blackPiecePlacementMgScore += middlegameScore;
            blackPiecePlacementEgScore += endgameScore;
        }
    }

    public void addMobilityScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whiteMobilityMgScore += middlegameScore;
            whiteMobilityEgScore += endgameScore;
        } else {
            blackMobilityMgScore += middlegameScore;
            blackMobilityEgScore += endgameScore;
        }
    }

    public void addPawnStructureScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whitePawnStructureMgScore += middlegameScore;
            whitePawnStructureEgScore += endgameScore;
        } else {
            blackPawnStructureMgScore += middlegameScore;
            blackPawnStructureEgScore += endgameScore;
        }
    }

    public void addKnightScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whiteKnightMgScore += middlegameScore;
            whiteKnightEgScore += endgameScore;
        } else {
            blackKnightMgScore += middlegameScore;
            blackKnightEgScore += endgameScore;
        }
    }

    public void addBishopScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whiteBishopMgScore += middlegameScore;
            whiteBishopEgScore += endgameScore;
        } else {
            blackBishopMgScore += middlegameScore;
            blackBishopEgScore += endgameScore;
        }
    }

    public void addRookScore(int middlegameScore, int endgameScore, boolean white) {
        if (white) {
            whiteRookMgScore += middlegameScore;
            whiteRookEgScore += endgameScore;
        } else {
            blackRookMgScore += middlegameScore;
            blackRookEgScore += endgameScore;
        }
    }

    public void setKingSafetyScore(int score, boolean white) {
        if (white) {
            whiteKingSafetyScore = score;
        } else {
            blackKingSafetyScore = score;
        }
    }

    public void setMopUpScore(int score, boolean white) {
        if (white) {
            whiteMopUpScore = score;
        } else {
            blackMopUpScore = score;
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

        int whiteMiddlegameScore = whiteMaterialMgScore + whitePiecePlacementMgScore + whiteMobilityMgScore + whitePawnStructureMgScore + whiteKnightMgScore + whiteBishopMgScore + whiteRookMgScore;
        int whiteEndgameScore = whiteMaterialEgScore + whitePiecePlacementEgScore + whiteMobilityEgScore + whitePawnStructureEgScore + whiteKnightEgScore + whiteBishopEgScore + whiteRookEgScore;
        int whiteScore = Phase.taperedEval(whiteMiddlegameScore, whiteEndgameScore, phase) + whiteKingSafetyScore + whiteMopUpScore + whiteTempoBonus;

        int blackMiddlegameScore = blackMaterialMgScore + blackPiecePlacementMgScore + blackMobilityMgScore + blackPawnStructureMgScore + blackKnightMgScore + blackBishopMgScore + blackRookMgScore;
        int blackEndgameScore = blackMaterialEgScore + blackPiecePlacementEgScore + blackMobilityEgScore + blackPawnStructureEgScore + blackKnightEgScore + blackBishopEgScore + blackRookEgScore;
        int blackScore = Phase.taperedEval(blackMiddlegameScore, blackEndgameScore, phase) + blackKingSafetyScore + blackMopUpScore + blackTempoBonus;

        int score = whiteScore - blackScore;
        int modifier = white ? 1 : -1;
        return score * modifier;

    }

}
