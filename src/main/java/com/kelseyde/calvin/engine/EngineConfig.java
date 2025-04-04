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
    private final Tunable aspMargin              = new Tunable("AspMargin", 15, 0, 250, 25);
    private final Tunable aspMaxReduction        = new Tunable("AspMaxReduction", 0, 0, 5, 1);
    private final Tunable nmpDepth               = new Tunable("NmpDepth", 0, 0, 6, 1);
    private final Tunable nmpBase                = new Tunable("NmpBase", 3, 0, 6, 1);
    private final Tunable nmpDivisor             = new Tunable("NmpDivisor", 2, 1, 4, 1);
    private final Tunable nmpEvalScale           = new Tunable("NmpEvalScale", 190, 0, 400, 25);
    private final Tunable nmpEvalMaxReduction    = new Tunable("NmpEvalMaxReduction", 4, 2, 5, 1);
    private final Tunable fpDepth                = new Tunable("FpDepth", 8, 0, 8, 1);
    private final Tunable fpMargin               = new Tunable("FpMargin", 137, 0, 500, 25);
    private final Tunable fpScale                = new Tunable("FpScale", 82, 0, 100, 5);
    private final Tunable fpHistDivisor          = new Tunable("FpHistDivisor", 103, 1, 1000, 25);
    private final Tunable seeMaxDepth            = new Tunable("SeeMaxDepth", 10, 6, 12, 1);
    private final Tunable seeQuietMargin         = new Tunable("SeeQuietMargin", -40, -250, -10, 25);
    private final Tunable seeNoisyMargin         = new Tunable("SeeNoisyMargin", -24, -250, -10, 25);
    private final Tunable seeNoisyOffset         = new Tunable("SeeNoisyOffset", -4, -100, 200, 50);
    private final Tunable seeNoisyDivisor        = new Tunable("SeeNoisyDivisor", 4, 2, 6, 1);
    private final Tunable seeQsNoisyOffset       = new Tunable("SeeQsNoisyOffset", 21, -100, 200, 50);
    private final Tunable seeQsNoisyDivisor      = new Tunable("SeeQsNoisyDivisor", 4, 2, 6, 1);
    private final Tunable seeHistoryDivisor      = new Tunable("SeeHistoryDivisor", 128, 50, 250, 25);
    private final Tunable qsFpMargin             = new Tunable("QsFpMargin", 116, 0, 250, 10);
    private final Tunable qsSeeThreshold         = new Tunable("QsSeeThreshold", -6, -300, 300, 100);
    private final Tunable rfpDepth               = new Tunable("RfpDepth", 9, 0, 12, 1);
    private final Tunable rfpMargin              = new Tunable("RfpMargin", 69, 0, 150, 25);
    private final Tunable lmrDepth               = new Tunable("LmrDepth", 2, 0, 8, 1);
    private final Tunable lmrBase                = new Tunable("LmrBase", 91, 50, 100, 5);
    private final Tunable lmrDivisor             = new Tunable("LmrDivisor", 308, 200, 400, 10);
    private final Tunable lmrCapBase             = new Tunable("LmrCapBase", 93, 50, 100, 5);
    private final Tunable lmrCapDivisor          = new Tunable("LmrCapDivisor", 303, 200, 400, 10);
    private final Tunable lmrMinMoves            = new Tunable("LmrMinMoves", 3, 2, 5, 1);
    private final Tunable lmrMinPvMoves          = new Tunable("LmrMinPvMoves", 4, 2, 5, 1);
    private final Tunable lmrPvNode              = new Tunable("LmrPvNode", 963, 0, 2048, 150);
    private final Tunable lmrCutNode             = new Tunable("LmrCutNode", 2106, 0, 3072, 150);
    private final Tunable lmrNotImproving        = new Tunable("LmrNotImproving", 94, 0, 2048, 150);
    private final Tunable lmrFutile              = new Tunable("LmrFutile", 1012, 0, 2048, 150);
    private final Tunable lmrQuietHistoryDiv     = new Tunable("LmrQuietHistoryDiv", 3037, 1536, 6144, 1000);
    private final Tunable lmrNoisyHistoryDiv     = new Tunable("LmrNoisyHistoryDiv", 3122, 1536, 6144, 1000);
    private final Tunable lmpDepth               = new Tunable("LmpDepth", 8, 0, 16, 1);
    private final Tunable lmpMultiplier          = new Tunable("LmpMultiplier", 8, 1, 20, 1);
    private final Tunable iirMinDepth            = new Tunable("IirMinDepth", 4, 0, 8, 1);
    private final Tunable iirReduction           = new Tunable("IirReduction", 1, 0, 3, 1);
    private final Tunable dpMargin               = new Tunable("DpMargin", 98, 0, 250, 10);
    private final Tunable razorDepth             = new Tunable("RazorDepth", 4, 0, 8, 1);
    private final Tunable razorMargin            = new Tunable("RazorMargin", 470, 0, 600, 10);
    private final Tunable hpMaxDepth             = new Tunable("HpMaxDepth", 5, 0, 10, 1);
    private final Tunable hpMargin               = new Tunable("HpMargin", -2271, -4000, -100, 50);
    private final Tunable hpOffset               = new Tunable("HpOffset", -1157, -3000, 0, 50);
    private final Tunable seDepth                = new Tunable("SeDepth", 8, 0, 10, 1);
    private final Tunable seTtDepthMargin        = new Tunable("SeTtDepthMargin", 3, 2, 6, 1);
    private final Tunable seBetaMargin           = new Tunable("SeBetaMargin", 32, 12, 40, 4);
    private final Tunable seReductionOffset      = new Tunable("SeReductionOffset", 1, 0, 3, 1);
    private final Tunable seReductionDivisor     = new Tunable("SeReductionDivisor", 2, 1, 4, 1);
    private final Tunable seDoubleExtMargin      = new Tunable("SeDoubleExtMargin", 20, 0, 32, 5);
    private final Tunable ttExtensionMaxDepth    = new Tunable("TtExtensionMaxDepth", 6, 0, 12, 1);
    private final Tunable ttExtension            = new Tunable("TtExtension", 1, 0, 3, 1);
    private final Tunable ttCutoffPvReduction    = new Tunable("TtCutoffPvReduction", 1, 0, 3, 1);
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
    private final Tunable incrementFactor        = new Tunable("IncrementFactor", 77, 50, 100, 5);
    private final Tunable softTimeFactor         = new Tunable("SoftTimeFactor", 66, 50, 70, 10);
    private final Tunable hardTimeFactor         = new Tunable("HardTimeFactor", 202, 150, 250, 10);
    private final Tunable softTimeScaleMin       = new Tunable("SoftTimeScaleMin", 12, 10, 25, 2);
    private final Tunable softTimeScaleMax       = new Tunable("SoftTimeScaleMax", 244, 100, 250, 50);
    private final Tunable uciOverhead            = new Tunable("UciOverhead", 50, 0, 1000, 50);
    private final Tunable nodeTmMinDepth         = new Tunable("NodeTmMinDepth", 5, 0, 10, 1);
    private final Tunable nodeTmBase             = new Tunable("NodeTmBase", 158, 100, 200, 10);
    private final Tunable nodeTmScale            = new Tunable("NodeTmScale", 140, 100, 200, 10);
    private final Tunable bmStabilityMinDepth    = new Tunable("BmStabilityMinDepth", 0, 0, 10, 1);
    private final Tunable scoreStabilityMinDepth = new Tunable("ScoreStabilityMinDepth", 0, 0, 10, 1);

    private int[][][] lmrReductions;
    private final int[] bmStabilityFactor = { 250, 120, 90, 80, 75 };
    private final int[] scoreStabilityFactor = { 125, 115, 100, 94, 88 };
    private final int[] contHistPlies = { 1, 2 };

    public Set<Tunable> getTunables() {
        return Set.of(
                aspMinDepth, aspMargin, aspMaxReduction, nmpDepth, nmpEvalScale, nmpEvalMaxReduction, fpDepth,
                fpHistDivisor, rfpDepth, lmrDepth, lmrBase, lmrDivisor, lmrCapBase, lmrCapDivisor, lmrMinMoves,
                lmrMinPvMoves, lmpDepth, lmpMultiplier, iirMinDepth, iirReduction, nmpBase, nmpDivisor, dpMargin,
                qsFpMargin, qsSeeThreshold, fpMargin, fpScale, rfpMargin, razorDepth, razorMargin, hpMaxDepth,
                hpMargin, hpOffset, lmrPvNode, lmrCutNode, lmrNotImproving, lmrFutile, quietHistBonusMax,
                quietHistBonusScale, quietHistMalusMax, quietHistMalusScale, quietHistMaxScore, captHistBonusMax,
                captHistBonusScale, captHistMalusMax, captHistMalusScale, captHistMaxScore, contHistBonusMax,
                contHistBonusScale, contHistMalusMax, contHistMalusScale, contHistMaxScore, nodeTmMinDepth, nodeTmBase,
                nodeTmScale, ttExtensionMaxDepth, ttExtension, ttCutoffPvReduction, seeMaxDepth, seeQuietMargin, seeNoisyMargin,
                seeNoisyOffset, seeHistoryDivisor, timeFactor, incrementFactor, softTimeFactor, hardTimeFactor, softTimeScaleMin,
                softTimeScaleMax, uciOverhead, bmStabilityMinDepth, scoreStabilityMinDepth, seeNoisyDivisor,
                seeQsNoisyDivisor, seeQsNoisyOffset, lmrQuietHistoryDiv, lmrNoisyHistoryDiv, seDepth, seTtDepthMargin,
                seBetaMargin, seReductionOffset, seReductionDivisor, seDoubleExtMargin
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

    public int aspMargin() {
        return aspMargin.value;
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

    public int lmrQuietHistoryDiv() {
        return lmrQuietHistoryDiv.value;
    }

    public int lmrNoisyHistoryDiv() {
        return lmrNoisyHistoryDiv.value;
    }

    public int lmpDepth() {
        return lmpDepth.value;
    }

    public int lmpMultiplier() {
        return lmpMultiplier.value;
    }

    public int iirMinDepth() {
        return iirMinDepth.value;
    }

    public int iirReduction() {
        return iirReduction.value;
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

    public int ttExtensionMaxDepth() {
        return ttExtensionMaxDepth.value;
    }

    public int ttExtension() {
        return ttExtension.value;
    }

    public int ttCutoffPvReduction() {
        return ttCutoffPvReduction.value;
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
