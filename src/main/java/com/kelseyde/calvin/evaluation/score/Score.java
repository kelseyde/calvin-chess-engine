package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Piece;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;

@Data
@Builder
public class Score {

    public static final int MATE_SCORE = 1000000;
    public static final int DRAW_SCORE = 0;
    public static final int[] SIMPLE_PIECE_VALUES = Arrays.stream(Piece.values()).mapToInt(Piece::getValue).toArray();

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
