package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.search.Search;

public class EngineConfig {

    public EngineConfig() {
        postInitialise();
    }

    public int minThreadCount = 1;
    public int maxThreadCount = 12;
    public int defaultThreadCount = 1;

    public int minHashSizeMb = 16;
    public int maxHashSizeMb = 1024;
    public int defaultHashSizeMb = 256;

    public boolean ponderEnabled = false;

    public boolean pondering = false;
    public boolean searchCancelled = false;

    public int aspMargin = 25;
    public int aspFailMargin = 150;
    public int aspMaxReduction = 3;
    public int nmpDepth = 0;
    public int fpDepth = 6;
    public int rfpDepth = 5;
    public int lmrDepth = 2;
    public float lmrBase = 0.85f;
    public float lmrDivisor = 3.12f;
    public int lmrMinSearchedMoves = 3;
    public int lmpDepth = 2;
    public int lmpMultiplier = 10;
    public int iirDepth = 4;
    public int nmpMargin = 70;
    public int dpMargin = 140;
    public int qsFpMargin = 100;
    public int fpMargin = 275;
    public int fpScale = 65;
    public int[] rfpMargin = { 74, 40 };
    public int[][] lmrReductions;

    public void postInitialise() {
        calculateLmrReductions();
    }

    private void calculateLmrReductions() {
        // Credit to Lynx (https://github.com/lynx-chess/Lynx) for this formula for determining the optimal late move reduction depth
        lmrReductions = new int[Search.MAX_DEPTH][];
        for (int depth = 1; depth < Search.MAX_DEPTH; ++depth) {
            lmrReductions[depth] = new int[250];
            for (int movesSearched = 1; movesSearched < 250; ++movesSearched) {
                lmrReductions[depth][movesSearched] = (int) Math.round(lmrBase + (Math.log(movesSearched) * Math.log(depth) / lmrDivisor));
            }
        }
    }

}
