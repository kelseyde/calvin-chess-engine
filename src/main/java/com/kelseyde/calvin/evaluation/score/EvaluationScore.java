package com.kelseyde.calvin.evaluation.score;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvaluationScore {

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

    float phase;

    public int sum(boolean isWhite) {
        int whiteScore = whiteMaterialScore + whitePiecePlacementScore + whiteMobilityScore +
                whitePawnStructureScore + whiteKingSafetyScore + whiteBishopScore + whiteRookScore + whiteMopUpScore;
        int blackScore = blackMaterialScore + blackPiecePlacementScore + blackMobilityScore +
                blackPawnStructureScore + blackKingSafetyScore + blackBishopScore + blackRookScore + blackMopUpScore;
        int modifier = isWhite ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

}
