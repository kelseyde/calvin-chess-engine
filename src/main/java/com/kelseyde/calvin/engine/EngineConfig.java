package com.kelseyde.calvin.engine;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EngineConfig {

    int minThreadCount;
    int maxThreadCount;
    int defaultThreadCount;

    int minHashSizeMb;
    int maxHashSizeMb;
    int defaultHashSizeMb;

    boolean ownBookEnabled;
    int maxBookMoves;
    boolean ponderEnabled;

    boolean pondering = false;
    boolean searchCancelled = false;

    float hardTimeBoundMultiplier;
    float softTimeBoundMultiplier;
    float softTimeBaseIncrementMultiplier;
    int defaultMovesToGo;

    int maxDepth;
    int aspMargin;
    int aspFailMargin;
    int nmpDepth;
    int fpDepth;
    int rfpDepth;
    int lmrDepth;
    int iirDepth;
    int nmpMargin;
    int dpMargin;
    int[] fpMargin;
    int[] rfpMargin;

    int[][] pieceValues;

    int[][] middlegameTables;
    int[][] endgameTables;

    int[][] middlegameMobilityBonus;
    int[][] endgameMobilityBonus;

    int[][] isolatedPawnPenalty;
    int[][] doubledPawnPenalty;
    int[][] passedPawnBonus;
    int protectedPassedPawnBonus;

    int bishopPairBonus;
    int[] rookOpenFileBonus;
    int[] rookSemiOpenFileBonus;

    int[] kingPawnShieldPenalty;
    int kingSemiOpenFilePenalty;
    int kingSemiOpenAdjacentFilePenalty;
    int kingOpenFilePenalty;
    int kingOpenAdjacentFilePenalty;
    int kingLostCastlingRightsPenalty;

    int kingManhattanDistanceMultiplier;
    int kingChebyshevDistanceMultiplier;
    int kingCenterManhattanDistanceMultiplier;

    int tempoBonus;

}
