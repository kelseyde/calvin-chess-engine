package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.UCICommand;

import java.util.Optional;
import java.util.Set;

public class EngineConfig {

    public EngineConfig() {
        postInitialise();
    }

    public final int minThreads = 1;
    public final int maxThreads = 12;
    public final int defaultThreads = 1;

    public final int minHashSizeMb = 8;
    public final int maxHashSizeMb = 1024;
    public final int defaultHashSizeMb = 256;

    public boolean ponderEnabled = false;
    public boolean pondering = false;
    public boolean searchCancelled = false;

    private final Tunable aspMinDepth            = new Tunable("AspMinDepth", 4, 0, 8, 1);
    private final Tunable aspDelta               = new Tunable("AspDelta", 17, 0, 250, 25);
    private final Tunable aspWideningFactor      = new Tunable("AspWideningFactor", 135, 110, 200, 10);
    private final Tunable aspMaxReduction        = new Tunable("AspMaxReduction", 0, 0, 5, 1);
    private final Tunable nmpDepth               = new Tunable("NmpDepth", 0, 0, 6, 1);
    private final Tunable nmpBase                = new Tunable("NmpBase", 3, 0, 6, 1);
    private final Tunable nmpDivisor             = new Tunable("NmpDivisor", 2, 1, 4, 1);
    private final Tunable nmpEvalScale           = new Tunable("NmpEvalScale", 191, 0, 400, 25);
    private final Tunable nmpEvalMaxReduction    = new Tunable("NmpEvalMaxReduction", 4, 2, 5, 1);
    private final Tunable fpDepth                = new Tunable("FpDepth", 8, 0, 8, 1);
    private final Tunable fpMargin               = new Tunable("FpMargin", 108, 0, 500, 25);
    private final Tunable fpScale                = new Tunable("FpScale", 82, 0, 100, 5);
    private final Tunable fpHistDivisor          = new Tunable("FpHistDivisor", 98, 1, 1000, 25);
    private final Tunable fpMoveMultiplier       = new Tunable("FpMoveMultiplier", 4, 0, 10, 1);
    private final Tunable seeMaxDepth            = new Tunable("SeeMaxDepth", 10, 6, 12, 1);
    private final Tunable seeQuietMargin         = new Tunable("SeeQuietMargin", -44, -250, -10, 25);
    private final Tunable seeNoisyMargin         = new Tunable("SeeNoisyMargin", -17, -250, -10, 25);
    private final Tunable seeNoisyOffset         = new Tunable("SeeNoisyOffset", -8, -100, 200, 50);
    private final Tunable seeNoisyDivisor        = new Tunable("SeeNoisyDivisor", 4, 2, 6, 1);
    private final Tunable seeQsNoisyOffset       = new Tunable("SeeQsNoisyOffset", 23, -100, 200, 50);
    private final Tunable seeQsNoisyDivisor      = new Tunable("SeeQsNoisyDivisor", 4, 2, 6, 1);
    private final Tunable seeHistoryDivisor      = new Tunable("SeeHistoryDivisor", 133, 50, 250, 25);
    private final Tunable qsFpMargin             = new Tunable("QsFpMargin", 114, 0, 250, 10);
    private final Tunable qsSeeThreshold         = new Tunable("QsSeeThreshold", -34, -300, 300, 100);
    private final Tunable rfpDepth               = new Tunable("RfpDepth", 9, 0, 12, 1);
    private final Tunable rfpMargin              = new Tunable("RfpMargin", 63, 0, 150, 25);
    private final Tunable rfpImprovingMargin     = new Tunable("RfpImprovingMargin", 69, 0, 150, 25);
    private final Tunable lmrDepth               = new Tunable("LmrDepth", 2, 0, 8, 1);
    private final Tunable lmrBase                = new Tunable("LmrBase", 92, 50, 100, 5);
    private final Tunable lmrDivisor             = new Tunable("LmrDivisor", 314, 200, 400, 10);
    private final Tunable lmrCapBase             = new Tunable("LmrCapBase", 93, 50, 100, 5);
    private final Tunable lmrCapDivisor          = new Tunable("LmrCapDivisor", 305, 200, 400, 10);
    private final Tunable lmrMinMoves            = new Tunable("LmrMinMoves", 3, 2, 5, 1);
    private final Tunable lmrMinPvMoves          = new Tunable("LmrMinPvMoves", 4, 2, 5, 1);
    private final Tunable lmrPvNode              = new Tunable("LmrPvNode", 911, 0, 2048, 150);
    private final Tunable lmrCutNode             = new Tunable("LmrCutNode", 2085, 0, 3072, 150);
    private final Tunable lmrNotImproving        = new Tunable("LmrNotImproving", 70, 0, 2048, 150);
    private final Tunable lmrFutile              = new Tunable("LmrFutile", 962, 0, 2048, 150);
    private final Tunable lmrFailHighCount       = new Tunable("LmrCutoffCount", 1035, 0, 2048, 150);
    private final Tunable lmrQuietHistoryDiv     = new Tunable("LmrQuietHistoryDiv", 3258, 1536, 6144, 1000);
    private final Tunable lmrNoisyHistoryDiv     = new Tunable("LmrNoisyHistoryDiv", 3155, 1536, 6144, 1000);
    private final Tunable lmrFutileMargin        = new Tunable("LmrFutileMargin", 108, 0, 500, 25);
    private final Tunable lmrFutileScale         = new Tunable("LmrFutileScale", 82, 0, 100, 5);
    private final Tunable lmrFutileHistDivisor   = new Tunable("LmrFutileHistDivisor", 98, 1, 1000, 25);
    private final Tunable lmrComplexityDivisor   = new Tunable("LmrComplexityDivisor", 73728, 49152, 86016, 18432);
    private final Tunable lmpDepth               = new Tunable("LmpDepth", 8, 0, 16, 1);
    private final Tunable lmpBase                = new Tunable("LmpBase", 3, 0, 50, 10);
    private final Tunable lmpScale               = new Tunable("LmpScale", 38, 10, 80, 10);
    private final Tunable lmpImpBase             = new Tunable("LmpImprovingBase", 1, 0, 50, 10);
    private final Tunable lmpImpScale            = new Tunable("LmpImprovingScale", 83, 10, 100, 10);
    private final Tunable iirDepth               = new Tunable("IirDepth", 4, 0, 8, 1);
    private final Tunable dpMargin               = new Tunable("DpMargin", 106, 0, 250, 10);
    private final Tunable razorDepth             = new Tunable("RazorDepth", 4, 0, 8, 1);
    private final Tunable razorMargin            = new Tunable("RazorMargin", 473, 0, 600, 10);
    private final Tunable hpMaxDepth             = new Tunable("HpMaxDepth", 5, 0, 10, 1);
    private final Tunable hpMargin               = new Tunable("HpMargin", -2282, -4000, -100, 50);
    private final Tunable hpOffset               = new Tunable("HpOffset", -1050, -3000, 0, 50);
    private final Tunable seDepth                = new Tunable("SeDepth", 8, 0, 10, 1);
    private final Tunable seTtDepthMargin        = new Tunable("SeTtDepthMargin", 3, 2, 6, 1);
    private final Tunable seBetaMargin           = new Tunable("SeBetaMargin", 32, 12, 40, 4);
    private final Tunable seReductionOffset      = new Tunable("SeReductionOffset", 1, 0, 3, 1);
    private final Tunable seReductionDivisor     = new Tunable("SeReductionDivisor", 2, 1, 4, 1);
    private final Tunable seDoubleExtMargin      = new Tunable("SeDoubleExtMargin", 20, 0, 32, 5);
    private final Tunable ttExtensionDepth       = new Tunable("TtExtDepth", 6, 0, 12, 1);
    private final Tunable hindsightExtLimit      = new Tunable("HindsightExtensionLimit", 3, 2, 5, 1);
    private final Tunable quietHistBonusMax      = new Tunable("QuietHistBonusMax", 1200, 100, 2000, 100);
    private final Tunable quietHistBonusScale    = new Tunable("QuietHistBonusScale", 200, 50, 400, 25);
    private final Tunable quietHistMalusMax      = new Tunable("QuietHistMalusMax", 1200, 100, 2000, 100);
    private final Tunable quietHistMalusScale    = new Tunable("QuietHistMalusScale", 200, 50, 400, 25);
    private final Tunable quietHistMaxScore      = new Tunable("QuietHistMaxScore", 8192, 1000, 12000, 100);
    private final Tunable captHistBonusMax       = new Tunable("CaptHistBonusMax", 1200, 100, 2000, 100);
    private final Tunable captHistBonusScale     = new Tunable("CaptHistBonusScale", 200, 50, 400, 25);
    private final Tunable captHistMalusMax       = new Tunable("CaptHistMalusMax", 1200, 100, 2000, 100);
    private final Tunable captHistMalusScale     = new Tunable("CaptHistMalusScale", 200, 50, 400, 25);
    private final Tunable captHistMaxScore       = new Tunable("CaptHistMaxScore", 8192, 1000, 12000, 100);
    private final Tunable contHistBonusMax       = new Tunable("ContHistBonusMax", 1200, 100, 2000, 100);
    private final Tunable contHistBonusScale     = new Tunable("ContHistBonusScale", 200, 50, 400, 25);
    private final Tunable contHistMalusMax       = new Tunable("ContHistMalusMax", 1200, 100, 2000, 100);
    private final Tunable contHistMalusScale     = new Tunable("ContHistMalusScale", 200, 50, 400, 25);
    private final Tunable contHistMaxScore       = new Tunable("ContHistMaxScore", 8192, 1000, 12000, 100);
    private final Tunable timeFactor             = new Tunable("TimeFactor", 5, 3, 10, 1);
    private final Tunable incrementFactor        = new Tunable("IncrementFactor", 78, 50, 100, 5);
    private final Tunable softTimeFactor         = new Tunable("SoftTimeFactor", 66, 50, 70, 10);
    private final Tunable hardTimeFactor         = new Tunable("HardTimeFactor", 200, 150, 250, 10);
    private final Tunable softTimeScaleMin       = new Tunable("SoftTimeScaleMin", 12, 10, 25, 2);
    private final Tunable softTimeScaleMax       = new Tunable("SoftTimeScaleMax", 245, 100, 250, 50);
    private final Tunable uciOverhead            = new Tunable("UciOverhead", 50, 0, 1000, 50);
    private final Tunable nodeTmMinDepth         = new Tunable("NodeTmMinDepth", 5, 0, 10, 1);
    private final Tunable nodeTmBase             = new Tunable("NodeTmBase", 165, 100, 200, 10);
    private final Tunable nodeTmScale            = new Tunable("NodeTmScale", 147, 100, 200, 10);
    private final Tunable bmStabilityMinDepth    = new Tunable("BmStabilityMinDepth", 0, 0, 10, 1);
    private final Tunable scoreStabilityMinDepth = new Tunable("ScoreStabilityMinDepth", 0, 0, 10, 1);

    private int[][][] lmrReductions;
    private final int[] bmStabilityFactor = { 250, 120, 90, 80, 75 };
    private final int[] scoreStabilityFactor = { 125, 115, 100, 94, 88 };
    private final int[] contHistPlies = { 1, 2 };

    public Set<Tunable> getTunables() {
        return Set.of(
                aspMinDepth, aspDelta, aspMaxReduction, nmpDepth, nmpEvalScale, nmpEvalMaxReduction, fpDepth,
                fpHistDivisor, rfpDepth, lmrDepth, lmrBase, lmrDivisor, lmrCapBase, lmrCapDivisor, lmrMinMoves,
                lmrMinPvMoves, lmpDepth, lmpBase, lmpScale, iirDepth, nmpBase, nmpDivisor, dpMargin,
                qsFpMargin, qsSeeThreshold, fpMargin, fpScale, rfpMargin, rfpImprovingMargin, razorDepth, razorMargin,
                hpMaxDepth, hpMargin, hpOffset, lmrPvNode, lmrCutNode, lmrNotImproving, lmrFutile, quietHistBonusMax,
                quietHistBonusScale, quietHistMalusMax, quietHistMalusScale, quietHistMaxScore, captHistBonusMax,
                captHistBonusScale, captHistMalusMax, captHistMalusScale, captHistMaxScore, contHistBonusMax,
                contHistBonusScale, contHistMalusMax, contHistMalusScale, contHistMaxScore, nodeTmMinDepth, nodeTmBase,
                nodeTmScale, ttExtensionDepth, seeMaxDepth, seeQuietMargin, seeNoisyMargin, seeNoisyOffset,
                seeHistoryDivisor, timeFactor, incrementFactor, softTimeFactor, hardTimeFactor, softTimeScaleMin,
                softTimeScaleMax, uciOverhead, bmStabilityMinDepth, scoreStabilityMinDepth, seeNoisyDivisor,
                seeQsNoisyDivisor, seeQsNoisyOffset, lmrQuietHistoryDiv, lmrNoisyHistoryDiv, seDepth, seTtDepthMargin,
                seBetaMargin, seReductionOffset, seReductionDivisor, seDoubleExtMargin, aspWideningFactor, fpMoveMultiplier,
                lmpImpBase, lmpImpScale, lmrFailHighCount, hindsightExtLimit, lmrFutileMargin, lmrFutileScale, lmrFutileHistDivisor,
                lmrComplexityDivisor
        );
    }


    public void setTunable(UCICommand command) {
        String name = command.getString("name", "", false);
        if (name.isBlank()) {
            UCI.write("info error missing required option name");
            return;
        }
        int value = command.getInt("value", -1, false);
        if (value == -1) {
            UCI.write("info error missing required option value");
            return;
        }
        Optional<Tunable> tunable = getTunables().stream().filter(t -> t.name.equals(name)).findFirst();
        if (tunable.isEmpty()) {
            UCI.write("info error no option found with name " + name);
            return;
        }
        Tunable option = tunable.get();
        if (value < option.min || value > option.max) {
            UCI.write("info error value " + value + " is out of range for option " + name);
        }
        option.value = value;
        if (name.equals("LmrBase") || name.equals("LmrDivisor")
                || name.equals("LmrCapBase") || name.equals("LmrCapDivisor")) {
            calculateLmrTable();
        }

        UCI.write("info string " + name + " " + value);
    }

    public void postInitialise() {
        calculateLmrTable();
    }

    private void calculateLmrTable() {
        float quietBase = (float) lmrBase.value / 100;
        float quietDivisor = (float) lmrDivisor.value / 100;
        float capBase = (float) lmrCapBase.value / 100;
        float capDivisor = (float) lmrCapDivisor.value / 100;
        lmrReductions = new int[2][][];

        for (int quiet = 0; quiet < 2; quiet++) {
            lmrReductions[quiet] = new int[Search.MAX_DEPTH][];
            for (int depth = 1; depth < Search.MAX_DEPTH; ++depth) {
                lmrReductions[quiet][depth] = new int[250];
                float base = quiet == 0 ? quietBase : capBase;
                float divisor = quiet == 0 ? quietDivisor : capDivisor;
                for (int movesSearched = 1; movesSearched < 250; ++movesSearched) {
                    lmrReductions[quiet][depth][movesSearched] = (int) Math.round(base + (Math.log(movesSearched) * Math.log(depth) / divisor));
                }
            }
        }
    }

    public static class Tunable {
        public final String name;
        public int value;
        public final int min;
        public final int max;
        public final int step;

        public Tunable(String name, int value, int min, int max, int step) {
            this.name = name;
            this.value = value;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        public String toUCI() {
            return String.format("option name %s type spin default %d min %d max %d", name, value, min, max);
        }

        public String toSPSA() {
            final float spsaStep = (float) Math.max(0.5, Math.round((float) (max - min) / 20));
            final float learningRate = 0.002f;

            return String.format("%s, int, %s, %s, %s, %s, %s", name, value, min, max, spsaStep, learningRate);
        }

    }

    public int aspMinDepth() {
        return aspMinDepth.value;
    }

    public int aspDelta() {
        return aspDelta.value;
    }

    public int aspWideningFactor() {
        return aspWideningFactor.value;
    }

    public int aspMaxReduction() {
        return aspMaxReduction.value;
    }

    public int nmpDepth() {
        return nmpDepth.value;
    }

    public int nmpBase() {
        return nmpBase.value;
    }

    public int nmpDivisor() {
        return nmpDivisor.value;
    }

    public int nmpEvalScale() {
        return nmpEvalScale.value;
    }

    public int nmpEvalMaxReduction() {
        return nmpEvalMaxReduction.value;
    }

    public int fpDepth() {
        return fpDepth.value;
    }

    public int fpMargin() {
        return fpMargin.value;
    }

    public int fpScale() {
        return fpScale.value;
    }

    public int fpHistDivisor() {
        return fpHistDivisor.value;
    }

    public int fpMoveMultiplier() {
        return fpMoveMultiplier.value;
    }

    public int seeMaxDepth() {
        return seeMaxDepth.value;
    }

    public int seeQuietMargin() {
        return seeQuietMargin.value;
    }

    public int seeNoisyMargin() {
        return seeNoisyMargin.value;
    }

    public int seeNoisyOffset() {
        return seeNoisyOffset.value;
    }

    public int seeNoisyDivisor() {
        return seeNoisyDivisor.value;
    }

    public int seeQsNoisyOffset() {
        return seeQsNoisyOffset.value;
    }

    public int seeQsNoisyDivisor() {
        return seeQsNoisyDivisor.value;
    }

    public int seeHistoryDivisor() {
        return seeHistoryDivisor.value;
    }

    public int qsFpMargin() {
        return qsFpMargin.value;
    }

    public int qsSeeThreshold() {
        return qsSeeThreshold.value;
    }

    public int rfpDepth() {
        return rfpDepth.value;
    }

    public int rfpMargin() {
        return rfpMargin.value;
    }

    public int rfpImprovingMargin() {
        return rfpImprovingMargin.value;
    }

    public int lmrDepth() {
        return lmrDepth.value;
    }

    public int lmrBase() {
        return lmrBase.value;
    }

    public int lmrDivisor() {
        return lmrDivisor.value;
    }

    public int lmrCapBase() {
        return lmrCapBase.value;
    }

    public int lmrCapDivisor() {
        return lmrCapDivisor.value;
    }

    public int lmrMinMoves() {
        return lmrMinMoves.value;
    }

    public int lmrMinPvMoves() {
        return lmrMinPvMoves.value;
    }

    public int lmrPvNode() {
        return lmrPvNode.value;
    }

    public int lmrCutNode() {
        return lmrCutNode.value;
    }

    public int lmrNotImproving() {
        return lmrNotImproving.value;
    }

    public int lmrFutile() {
        return lmrFutile.value;
    }

    public int lmrFailHighCount() {
        return lmrFailHighCount.value;
    }

    public int lmrQuietHistoryDiv() {
        return lmrQuietHistoryDiv.value;
    }

    public int lmrNoisyHistoryDiv() {
        return lmrNoisyHistoryDiv.value;
    }

    public int lmrComplexityDivisor() {
        return lmrComplexityDivisor.value;
    }

    public int lmrFutileMargin() {
        return lmrFutileMargin.value;
    }

    public int lmrFutileScale() {
        return lmrFutileScale.value;
    }

    public int lmrFutileHistDivisor() {
        return lmrFutileHistDivisor.value;
    }

    public int lmpDepth() {
        return lmpDepth.value;
    }

    public int lmpBase() {
        return lmpBase.value;
    }

    public int lmpScale() {
        return lmpScale.value;
    }

    public int lmpImpBase() {
        return lmpImpBase.value;
    }

    public int lmpImpScale() {
        return lmpImpScale.value;
    }

    public int iirDepth() {
        return iirDepth.value;
    }

    public int dpMargin() {
        return dpMargin.value;
    }

    public int razorDepth() {
        return razorDepth.value;
    }

    public int razorMargin() {
        return razorMargin.value;
    }

    public int hpMaxDepth() {
        return hpMaxDepth.value;
    }

    public int hpMargin() {
        return hpMargin.value;
    }

    public int hpOffset() {
        return hpOffset.value;
    }

    public int seDepth() {
        return seDepth.value;
    }

    public int seTtDepthMargin() {
        return seTtDepthMargin.value;
    }

    public int seBetaMargin() {
        return seBetaMargin.value;
    }

    public int seReductionOffset() {
        return seReductionOffset.value;
    }

    public int seReductionDivisor() {
        return seReductionDivisor.value;
    }

    public int seDoubleExtMargin() {
        return seDoubleExtMargin.value;
    }

    public int ttExtensionDepth() {
        return ttExtensionDepth.value;
    }

    public int hindsightExtLimit() {
        return hindsightExtLimit.value;
    }

    public int quietHistBonusMax() {
        return quietHistBonusMax.value;
    }

    public int quietHistBonusScale() {
        return quietHistBonusScale.value;
    }

    public int quietHistMalusMax() {
        return quietHistMalusMax.value;
    }

    public int quietHistMalusScale() {
        return quietHistMalusScale.value;
    }

    public int quietHistMaxScore() {
        return quietHistMaxScore.value;
    }

    public int captHistBonusMax() {
        return captHistBonusMax.value;
    }

    public int captHistBonusScale() {
        return captHistBonusScale.value;
    }

    public int captHistMalusMax() {
        return captHistMalusMax.value;
    }

    public int captHistMalusScale() {
        return captHistMalusScale.value;
    }

    public int captHistMaxScore() {
        return captHistMaxScore.value;
    }

    public int contHistBonusMax() {
        return contHistBonusMax.value;
    }

    public int contHistBonusScale() {
        return contHistBonusScale.value;
    }

    public int contHistMalusMax() {
        return contHistMalusMax.value;
    }

    public int contHistMalusScale() {
        return contHistMalusScale.value;
    }

    public int contHistMaxScore() {
        return contHistMaxScore.value;
    }

    public int timeFactor() {
        return timeFactor.value;
    }

    public int incrementFactor() {
        return incrementFactor.value;
    }

    public int softTimeFactor() {
        return softTimeFactor.value;
    }

    public int hardTimeFactor() {
        return hardTimeFactor.value;
    }

    public int softTimeScaleMin() {
        return softTimeScaleMin.value;
    }

    public int softTimeScaleMax() {
        return softTimeScaleMax.value;
    }

    public int uciOverhead() {
        return uciOverhead.value;
    }

    public int nodeTmMinDepth() {
        return nodeTmMinDepth.value;
    }

    public int nodeTmBase() {
        return nodeTmBase.value;
    }

    public int nodeTmScale() {
        return nodeTmScale.value;
    }

    public int bmStabilityMinDepth() {
        return bmStabilityMinDepth.value;
    }

    public int scoreStabilityMinDepth() {
        return scoreStabilityMinDepth.value;
    }

    public int[][][] lmrReductions() {
        return lmrReductions;
    }

    public int[] bmStabilityFactor() {
        return bmStabilityFactor;
    }

    public int[] scoreStabilityFactor() {
        return scoreStabilityFactor;
    }

    public int[] contHistPlies() {
        return contHistPlies;
    }

}
