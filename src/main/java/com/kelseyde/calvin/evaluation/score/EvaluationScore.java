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

    int whitePawnStructureScore;
    int blackPawnStructureScore;

    int whiteKingSafetyScore;
    int blackKingSafetyScore;

    int whiteMopUpScore;
    int blackMopUpScore;

    float phase;

    public int sum(boolean isWhite) {
        int whiteScore = whiteMaterialScore + whitePiecePlacementScore + whitePawnStructureScore + whiteKingSafetyScore + whiteMopUpScore;
        int blackScore = blackMaterialScore + blackPiecePlacementScore + blackPawnStructureScore + blackKingSafetyScore + blackMopUpScore;
        int modifier = isWhite ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

}