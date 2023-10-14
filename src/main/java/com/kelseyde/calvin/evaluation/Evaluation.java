package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.evaluation.material.Material;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementScore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Evaluation {

    Material material;

    PiecePlacementScore piecePlacementScore;

    int pawnStructureScore;

    int kingSafetyScore;

    int mopUpEval;

    public int sum() {
        return material.eval() + piecePlacementScore.sum() + pawnStructureScore + kingSafetyScore + mopUpEval;
    }

    public Evaluation copy() {
        Evaluation evaluation = new Evaluation();
        evaluation.setMaterial(material);
        evaluation.setPiecePlacementScore(piecePlacementScore);
        evaluation.setPawnStructureScore(pawnStructureScore);
        evaluation.setKingSafetyScore(kingSafetyScore);
        evaluation.setMopUpEval(mopUpEval);
        return evaluation;
    }

}
