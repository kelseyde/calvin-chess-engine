package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.board.Piece;
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
    int defaultPawnHashSizeMb;

    boolean ownBookEnabled;
    int maxBookMoves;

    boolean ownTablebaseEnabled;
    int maxTablebaseSupportedPieces;
    String lichessTablebaseBaseUrl;
    boolean lichessTablebaseDebugEnabled;
    long lichessTablebaseTimeoutMs;

    boolean ponderEnabled;
    int principalVariationLength;

    boolean pondering = false;
    boolean searchCancelled = false;

    int maxDepth;
    int maxPossibleMoves = 250;
    int aspMargin;
    int aspFailMargin;
    int nmpDepth;
    int fpDepth;
    int rfpDepth;
    int lmrDepth;
    float lmrBase;
    float lmrDivisor;
    int lmrMinSearchedMoves;
    int lmpDepth;
    int lmpMultiplier;
    int iirDepth;
    int nmpMargin;
    int dpMargin;
    int[][] lmrReductions;
    int[] fpMargin;
    int[] rfpMargin;

    int[] piecePhases;
    float totalPhase;
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

    int[][] knightOutpostBonus;
    int[][] bishopOutpostBonus;

    int[] kingPawnShieldPenalty;
    int kingSemiOpenFilePenalty;
    int kingSemiOpenAdjacentFilePenalty;
    int kingOpenFilePenalty;
    int kingOpenAdjacentFilePenalty;
    int kingLostCastlingRightsPenalty;
    int[] kingAttackZonePenaltyTable;
    int[] kingSafetyScaleFactor;
    int[][] virtualKingMobilityPenalty;

    int kingManhattanDistanceMultiplier;
    int kingChebyshevDistanceMultiplier;
    int kingCenterManhattanDistanceMultiplier;
    int[] mopUpScaleFactor;

    int drawishScaleFactor;
    int tempoBonus;

    public void postInitialise() {
        calculateLmrReductions();
        calculateTotalPhase();
    }

    private void calculateLmrReductions() {
        // Credit to Lynx (https://github.com/lynx-chess/Lynx) for this formula for determining the optimal late move reduction depth
        lmrReductions = new int[maxDepth][];
        for (int depth = 1; depth < maxDepth; ++depth) {
            lmrReductions[depth] = new int[maxPossibleMoves];
            for (int movesSearched = 1; movesSearched < maxPossibleMoves; ++movesSearched) {
                lmrReductions[depth][movesSearched] = (int) Math.round(lmrBase + (Math.log(movesSearched) * Math.log(depth) / lmrDivisor));
            }
        }
    }

    private void calculateTotalPhase() {
        int knightPhase = piecePhases[Piece.KNIGHT.getIndex()];
        int bishopPhase = piecePhases[Piece.BISHOP.getIndex()];
        int rookPhase = piecePhases[Piece.ROOK.getIndex()];
        int queenPhase = piecePhases[Piece.QUEEN.getIndex()];
        totalPhase = (knightPhase * 4) + (bishopPhase * 4) + (rookPhase * 4) + (queenPhase * 2);
    }

}
