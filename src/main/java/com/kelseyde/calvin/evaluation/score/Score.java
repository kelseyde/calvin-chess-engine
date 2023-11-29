package com.kelseyde.calvin.evaluation.score;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Score {

    Material whiteMaterial;
    Material blackMaterial;

    int whiteMaterialScore;
    int blackMaterialScore;

    int whitePiecePlacementScore;
    int blackPiecePlacementScore;

    int whiteMobilityScore;
    int blackMobilityScore;

    int whitePawnStructureScore;
    int blackPawnStructureScore;

    int whiteKingSafetyScore;
    int blackKingSafetyScore;

    int whiteBishopScore;
    int blackBishopScore;

    int whiteRookScore;
    int blackRookScore;

    int whiteMopUpScore;
    int blackMopUpScore;

    int whiteTempoBonus;
    int blackTempoBonus;

    float phase;

    public int sum(boolean isWhite) {
        int whiteScore = whiteMaterialScore + whitePiecePlacementScore + whiteMobilityScore + whitePawnStructureScore +
                whiteKingSafetyScore + whiteBishopScore + whiteRookScore + whiteMopUpScore + whiteTempoBonus;
        int blackScore = blackMaterialScore + blackPiecePlacementScore + blackMobilityScore + blackPawnStructureScore +
                blackKingSafetyScore + blackBishopScore + blackRookScore + blackMopUpScore + blackTempoBonus;
        int modifier = isWhite ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

}
