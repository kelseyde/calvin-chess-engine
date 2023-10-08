package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.evaluation.material.Material;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Evaluation {

    Material material;

    int gamePhase;

    int pieceSquareScore;

    int pawnStructureScore;

    int kingSafetyScore;

    int mopUpEval;

    public int sum() {
        return material.eval() + pieceSquareScore + pawnStructureScore + kingSafetyScore + mopUpEval;
    }

}
