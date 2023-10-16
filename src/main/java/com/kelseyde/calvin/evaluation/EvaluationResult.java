package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.evaluation.material.Material;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementScore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {

    Material material;

    PiecePlacementScore piecePlacementScore;

    int pawnStructureScore;

    int mopUpEval;

    public int sum() {
        return material.eval() + piecePlacementScore.sum() + pawnStructureScore + mopUpEval;
    }

}
