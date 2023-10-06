package com.kelseyde.calvin.evaluation;

import lombok.Builder;
import lombok.Data;

// TODO
@Data
@Builder
public class Evaluation {

    int materialScore;

    int pieceSquareScore;

    int pawnStructureScore;

    int kingSafetyScore;

}
