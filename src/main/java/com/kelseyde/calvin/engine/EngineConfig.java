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

    public ThreadConfig threadConfig = ThreadConfig.builder()
            .minThreads(1)
            .maxThreads(512)
            .defaultThreads(1)
            .build();

    public HashConfig hashConfig = HashConfig.builder()
            .minSizeMb(1)
            .maxSizeMb(1024)
            .defaultSizeMb(16)
            .build();

    public boolean ponderEnabled = false;
    public boolean pondering = false;
    public boolean searchCancelled = false;

    private final Tunable aspMinDepth            = new Tunable("AspMinDepth", 4, 0, 8, 1);
    private final Tunable aspDeltaBase           = new Tunable("AspDeltaBase", 12, 0, 20, 5);
    private final Tunable aspDeltaScale          = new Tunable("AspDeltaScale", 6, 0, 25, 5);
    private final Tunable aspDeltaMinExpansions  = new Tunable("AspDeltaMinExpansions", 4, 0, 8, 1);
    private final Tunable aspAlphaWideningFactor = new Tunable("AspAlphaWideningFactor", 137, 110, 200, 10);
    private final Tunable aspBetaWideningFactor  = new Tunable("AspBetaWideningFactor", 135, 110, 200, 10);
    private final Tunable aspMaxReduction        = new Tunable("AspMaxReduction", 0, 0, 5, 1);
    private final Tunable nmpDepth               = new Tunable("NmpDepth", 0, 0, 6, 1);
    private final Tunable nmpBase                = new Tunable("NmpBase", 3, 0, 6, 1);
    private final Tunable nmpDivisor             = new Tunable("NmpDivisor", 2, 1, 4, 1);
    private final Tunable nmpEvalScale           = new Tunable("NmpEvalScale", 204, 0, 400, 25);
    private final Tunable nmpEvalMaxReduction    = new Tunable("NmpEvalMaxReduction", 4, 2, 5, 1);
    private final Tunable fpDepth                = new Tunable("FpDepth", 8, 0, 8, 1);
    private final Tunable fpMargin               = new Tunable("FpMargin", 96, 0, 500, 25);
    private final Tunable fpScale                = new Tunable("FpScale", 76, 0, 100, 5);
    private final Tunable fpHistDivisor          = new Tunable("FpHistDivisor", 87, 1, 1000, 25);
    private final Tunable fpMoveMultiplier       = new Tunable("FpMoveMultiplier", 3, 0, 10, 1);
    private final Tunable seeMaxDepth            = new Tunable("SeeMaxDepth", 10, 6, 12, 1);
    private final Tunable seeQuietMargin         = new Tunable("SeeQuietMargin", -43, -250, -10, 25);
    private final Tunable seeNoisyMargin         = new Tunable("SeeNoisyMargin", -26, -250, -10, 25);
    private final Tunable seeNoisyOffset         = new Tunable("SeeNoisyOffset", -2, -100, 200, 50);
    private final Tunable seeNoisyDivisor        = new Tunable("SeeNoisyDivisor", 4, 2, 6, 1);
    private final Tunable seeQsNoisyOffset       = new Tunable("SeeQsNoisyOffset", 20, -100, 200, 50);
    private final Tunable seeQsNoisyDivisor      = new Tunable("SeeQsNoisyDivisor", 5, 2, 6, 1);
    private final Tunable seeHistoryDivisor      = new Tunable("SeeHistoryDivisor", 128, 50, 250, 25);
    private final Tunable qsFpMargin             = new Tunable("QsFpMargin", 116, 0, 250, 10);
    private final Tunable qsSeeThreshold         = new Tunable("QsSeeThreshold", -42, -300, 300, 100);
    private final Tunable rfpDepth               = new Tunable("RfpDepth", 9, 0, 12, 1);
    private final Tunable rfpMargin              = new Tunable("RfpMargin", 64, 0, 150, 25);
    private final Tunable rfpImprovingMargin     = new Tunable("RfpImprovingMargin", 60, 0, 150, 25);
    private final Tunable rfpNotImprovingMargin  = new Tunable("RfpNotImprovingMargin", -2, -50, 0, 10);
    private final Tunable rfpWorseningMargin     = new Tunable("RfpWorseningMargin", 13, 0, 100, 15);
    private final Tunable rfpNotWorseningMargin  = new Tunable("RfpNotWorseningMargin", -1, -50, 0, 10);
    private final Tunable rfpParentPvMargin      = new Tunable("RfpParentPvMargin", 16, 0, 75, 20);
    private final Tunable rfpNotParentPvMargin   = new Tunable("RfpNotParentPvMargin", -4, -50, 0, 10);
    private final Tunable lmrDepth               = new Tunable("LmrDepth", 2, 0, 8, 1);
    private final Tunable lmrBase                = new Tunable("LmrBase", 92, 50, 100, 5);
    private final Tunable lmrDivisor             = new Tunable("LmrDivisor", 309, 200, 400, 10);
    private final Tunable lmrCapBase             = new Tunable("LmrCapBase", 93, 50, 100, 5);
    private final Tunable lmrCapDivisor          = new Tunable("LmrCapDivisor", 307, 200, 400, 10);
    private final Tunable lmrMinMoves            = new Tunable("LmrMinMoves", 3, 2, 5, 1);
    private final Tunable lmrMinPvMoves          = new Tunable("LmrMinPvMoves", 4, 2, 5, 1);
    private final Tunable lmrPvNode              = new Tunable("LmrPvNode", 1264, 0, 2048, 150);
    private final Tunable lmrPvDistanceMult      = new Tunable("LmrPvDistanceMult", 63, 0, 256, 64);
    private final Tunable lmrPvDistanceMax       = new Tunable("LmrPvDistanceMax", 742, 0, 2048, 256);
    private final Tunable lmrCutNode             = new Tunable("LmrCutNode", 1910, 0, 3072, 150);
    private final Tunable lmrNotImproving        = new Tunable("LmrNotImproving", 275, 0, 2048, 150);
    private final Tunable lmrFutile              = new Tunable("LmrFutile", 966, 0, 2048, 150);
    private final Tunable lmrFailHighCount       = new Tunable("LmrCutoffCount", 1088, 0, 2048, 150);
    private final Tunable lmrQuietHistoryDiv     = new Tunable("LmrQuietHistoryDiv", 3419, 1536, 6144, 1000);
    private final Tunable lmrNoisyHistoryDiv     = new Tunable("LmrNoisyHistoryDiv", 3257, 1536, 6144, 1000);
    private final Tunable lmrFutileMargin        = new Tunable("LmrFutileMargin", 101, 0, 500, 25);
    private final Tunable lmrFutileScale         = new Tunable("LmrFutileScale", 80, 0, 100, 5);
    private final Tunable lmrFutileHistDivisor   = new Tunable("LmrFutileHistDivisor", 71, 1, 1000, 25);
    private final Tunable lmrComplexityDivisor   = new Tunable("LmrComplexityDivisor", 6096, 1536, 8192, 512);
    private final Tunable lmrDeeperBase          = new Tunable("LmrDeeperBase", 37, 20, 100, 10);
    private final Tunable lmrDeeperScale         = new Tunable("LmrDeeperScale", 5, 3, 12, 1);
    private final Tunable lmpDepth               = new Tunable("LmpDepth", 8, 0, 16, 1);
    private final Tunable lmpBase                = new Tunable("LmpBase", 3, 0, 50, 10);
    private final Tunable lmpScale               = new Tunable("LmpScale", 39, 10, 80, 10);
    private final Tunable lmpImpBase             = new Tunable("LmpImprovingBase", 1, 0, 50, 10);
    private final Tunable lmpImpScale            = new Tunable("LmpImprovingScale", 87, 10, 100, 10);
    private final Tunable iirDepth               = new Tunable("IirDepth", 4, 0, 8, 1);
    private final Tunable dpMargin               = new Tunable("DpMargin", 108, 0, 250, 10);
    private final Tunable razorDepth             = new Tunable("RazorDepth", 4, 0, 8, 1);
    private final Tunable razorMargin            = new Tunable("RazorMargin", 473, 0, 600, 10);
    private final Tunable hpMaxDepth             = new Tunable("HpMaxDepth", 5, 0, 10, 1);
    private final Tunable hpMargin               = new Tunable("HpMargin", -2366, -4000, -100, 50);
    private final Tunable hpOffset               = new Tunable("HpOffset", -1007, -3000, 0, 50);
    private final Tunable bnpDepth               = new Tunable("BnpDepth", 6, 0, 8, 1);
    private final Tunable bnpOffset              = new Tunable("BnpOffset", 383, 280, 480, 25);
    private final Tunable bnpScale               = new Tunable("BnpScale", 113, 50, 300, 25);
    private final Tunable bnpDivisor             = new Tunable("BnpDivisor", 133, 80, 200, 25);
    private final Tunable corrPawnWeight         = new Tunable("CorrPawnWeight", 99, 0, 200, 20);
    private final Tunable corrNonPawnWeight      = new Tunable("CorrNonPawnWeight", 100, 0, 200, 20);
    private final Tunable corrCounterWeight      = new Tunable("CorrCounterWeight", 96, 0, 200, 20);
    private final Tunable seDepth                = new Tunable("SeDepth", 8, 0, 10, 1);
    private final Tunable seTtDepthMargin        = new Tunable("SeTtDepthMargin", 3, 2, 6, 1);
    private final Tunable seBetaMargin           = new Tunable("SeBetaMargin", 31, 12, 40, 4);
    private final Tunable seReductionOffset      = new Tunable("SeReductionOffset", 1, 0, 3, 1);
    private final Tunable seReductionDivisor     = new Tunable("SeReductionDivisor", 3, 1, 4, 1);
    private final Tunable seDoubleExtMargin      = new Tunable("SeDoubleExtMargin", 19, 0, 32, 5);
    private final Tunable ttExtensionDepth       = new Tunable("TtExtDepth", 6, 0, 12, 1);
    private final Tunable hindsightExtLimit      = new Tunable("HindsightExtensionLimit", 3, 2, 5, 1);
    private final Tunable hindsightEvalDiff      = new Tunable("HindsightEvalDiff", 2, -100, 100, 20);
    private final Tunable alphaReductionMinDepth = new Tunable("AlphaReductionMinDepth", 2, 0, 6, 1);
    private final Tunable alphaReductionMaxDepth = new Tunable("AlphaReductionMaxDepth", 12, 8, 16, 1);
    private final Tunable dynamicPolicyMult      = new Tunable("DynamicPolicyMult", 10, 0, 20, 2);
    private final Tunable dynamicPolicyMin       = new Tunable("DynamicPolicyMin", -93, -100, 0, 25);
    private final Tunable dynamicPolicyMax       = new Tunable("DynamicPolicyMax", 199, 75, 200, 25);
    private final Tunable betaHistBonusMargin    = new Tunable("BetaHistoryBonusMargin", 53, 0, 100, 10);
    private final Tunable goodQuietThreshold     = new Tunable("GoodQuietThreshold", -217, -2048, 2048, 256);
    private final Tunable quietHistBonusMax      = new Tunable("QuietHistBonusMax", 1202, 100, 2000, 100);
    private final Tunable quietHistBonusScale    = new Tunable("QuietHistBonusScale", 207, 50, 400, 25);
    private final Tunable quietHistMalusMax      = new Tunable("QuietHistMalusMax", 1255, 100, 2000, 100);
    private final Tunable quietHistMalusScale    = new Tunable("QuietHistMalusScale", 200, 50, 400, 25);
    private final Tunable quietHistMaxScore      = new Tunable("QuietHistMaxScore", 8033, 1000, 12000, 100);
    private final Tunable captHistBonusMax       = new Tunable("CaptHistBonusMax", 1250, 100, 2000, 100);
    private final Tunable captHistBonusScale     = new Tunable("CaptHistBonusScale", 192, 50, 400, 25);
    private final Tunable captHistMalusMax       = new Tunable("CaptHistMalusMax", 1185, 100, 2000, 100);
    private final Tunable captHistMalusScale     = new Tunable("CaptHistMalusScale", 211, 50, 400, 25);
    private final Tunable captHistMaxScore       = new Tunable("CaptHistMaxScore", 8488, 1000, 12000, 100);
    private final Tunable contHistBonusMax       = new Tunable("ContHistBonusMax", 1229, 100, 2000, 100);
    private final Tunable contHistBonusScale     = new Tunable("ContHistBonusScale", 204, 50, 400, 25);
    private final Tunable contHistMalusMax       = new Tunable("ContHistMalusMax", 1067, 100, 2000, 100);
    private final Tunable contHistMalusScale     = new Tunable("ContHistMalusScale", 205, 50, 400, 25);
    private final Tunable contHistMaxScore       = new Tunable("ContHistMaxScore", 8003, 1000, 12000, 100);
    private final Tunable seeValuePawn           = new Tunable("SeeValuePawn", 102, 0, 200, 10);
    private final Tunable seeValueKnight         = new Tunable("SeeValueKnight", 320, 0, 500, 10);
    private final Tunable seeValueBishop         = new Tunable("SeeValueBishop", 338, 0, 500, 10);
    private final Tunable seeValueRook           = new Tunable("SeeValueRook", 493, 0, 1000, 10);
    private final Tunable seeValueQueen          = new Tunable("SeeValueQueen", 906, 0, 1500, 10);
    private final Tunable timeFactor             = new Tunable("TimeFactor", 6, 3, 10, 1);
    private final Tunable incrementFactor        = new Tunable("IncrementFactor", 80, 50, 100, 5);
    private final Tunable softTimeFactor         = new Tunable("SoftTimeFactor", 66, 50, 70, 10);
    private final Tunable hardTimeFactor         = new Tunable("HardTimeFactor", 199, 150, 250, 10);
    private final Tunable softTimeScaleMin       = new Tunable("SoftTimeScaleMin", 12, 10, 25, 2);
    private final Tunable softTimeScaleMax       = new Tunable("SoftTimeScaleMax", 245, 100, 250, 50);
    private final Tunable uciOverhead            = new Tunable("UciOverhead", 50, 0, 1000, 50);
    private final Tunable nodeTmMinDepth         = new Tunable("NodeTmMinDepth", 5, 0, 10, 1);
    private final Tunable nodeTmBase             = new Tunable("NodeTmBase", 170, 100, 200, 10);
    private final Tunable nodeTmScale            = new Tunable("NodeTmScale", 151, 100, 200, 10);
    private final Tunable complexityTmMinDepth   = new Tunable("ComplexityTmMinDepth", 4, 0, 10, 1);
    private final Tunable complexityTmDiffBase   = new Tunable("ComplexityTmDiffBase", 80, 50, 100, 10);
    private final Tunable complexityTmScaleBase  = new Tunable("ComplexityTmScaleBase", 70, 50, 100, 10);
    private final Tunable complexityTmScaleMax   = new Tunable("ComplexityTmScaleMax", 200, 100, 300, 10);
    private final Tunable complexityTmScaleDiv   = new Tunable("ComplexityTmScaleDivisor", 400, 300, 500, 10);
    private final Tunable bmStabilityMinDepth    = new Tunable("BmStabilityMinDepth", 0, 0, 10, 1);
    private final Tunable scoreStabilityMinDepth = new Tunable("ScoreStabilityMinDepth", 0, 0, 10, 1);

    private int[] seeValues;
    private int[][][] lmrReductions;
    private final int[] bmStabilityFactor = { 250, 120, 90, 80, 75 };
    private final int[] scoreStabilityFactor = { 125, 115, 100, 94, 88 };
    private final int[] contHistPlies = { 1, 2 };

    public Set<Tunable> getTunables() {
        return Set.of(
                aspMinDepth, aspDeltaBase, aspMaxReduction, nmpDepth, nmpEvalScale, nmpEvalMaxReduction, fpDepth,
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
                seBetaMargin, seReductionOffset, seReductionDivisor, seDoubleExtMargin, aspAlphaWideningFactor, fpMoveMultiplier,
                lmpImpBase, lmpImpScale, lmrFailHighCount, hindsightExtLimit, lmrFutileMargin, lmrFutileScale, lmrFutileHistDivisor,
                lmrComplexityDivisor, alphaReductionMinDepth, alphaReductionMaxDepth, dynamicPolicyMult, dynamicPolicyMin,
                dynamicPolicyMax, bnpDepth, bnpOffset, bnpScale, bnpDivisor, goodQuietThreshold, lmrDeeperBase, lmrDeeperScale,
                lmrPvDistanceMult, lmrPvDistanceMax, rfpParentPvMargin, betaHistBonusMargin, rfpWorseningMargin, aspDeltaScale,
                aspDeltaMinExpansions, seeValuePawn, seeValueKnight, seeValueBishop, seeValueRook, seeValueQueen,
                corrPawnWeight, corrNonPawnWeight, corrCounterWeight, hindsightEvalDiff, rfpNotImprovingMargin,
                rfpNotWorseningMargin, rfpNotParentPvMargin, complexityTmMinDepth, complexityTmDiffBase,
                complexityTmScaleBase, complexityTmScaleMax, complexityTmScaleDiv
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
        if (name.contains("SeeValue")) {
            updateSeeValues();
        }

        UCI.write("info string " + name + " " + value);
    }

    public void postInitialise() {
        calculateLmrTable();
        updateSeeValues();
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

    private void updateSeeValues() {
        seeValues = new int[] {
                seeValuePawn.value, seeValueKnight.value, seeValueBishop.value, seeValueRook.value, seeValueQueen.value, 0
        };
    }

    public int aspMinDepth() {
        return aspMinDepth.value;
    }

    public int aspDeltaBase() {
        return aspDeltaBase.value;
    }

    public int aspDeltaScale() {
        return aspDeltaScale.value;
    }

    public int aspDeltaMinExpansions() {
        return aspDeltaMinExpansions.value;
    }

    public int aspAlphaWideningFactor() {
        return aspAlphaWideningFactor.value;
    }

    public int aspBetaWideningFactor() {
        return aspBetaWideningFactor.value;
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

    public int rfpNotImprovingMargin() {
        return rfpNotImprovingMargin.value;
    }

    public int rfpParentPvMargin() {
        return rfpParentPvMargin.value;
    }

    public int rfpNotParentPvMargin() {
        return rfpNotParentPvMargin.value;
    }

    public int rfpWorseningMargin() {
        return rfpWorseningMargin.value;
    }

    public int rfpNotWorseningMargin() {
        return rfpNotWorseningMargin.value;
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

    public int lmrPvDistanceMult() {
        return lmrPvDistanceMult.value;
    }

    public int lmrPvDistanceMax() {
        return lmrPvDistanceMax.value;
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

    public int lmrDeeperBase() {
        return lmrDeeperBase.value;
    }

    public int lmrDeeperScale() {
        return lmrDeeperScale.value;
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

    public int corrPawnWeight() {
        return corrPawnWeight.value;
    }

    public int corrNonPawnWeight() {
        return corrNonPawnWeight.value;
    }

    public int corrCounterWeight() {
        return corrCounterWeight.value;
    }

    public int bnpDepth() {
        return bnpDepth.value;
    }

    public int bnpOffset() {
        return bnpOffset.value;
    }

    public int bnpScale() {
        return bnpScale.value;
    }

    public int bnpDivisor() {
        return bnpDivisor.value;
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

    public int hindsightEvalDiff() {
        return hindsightEvalDiff.value;
    }

    public int alphaReductionMinDepth() {
        return alphaReductionMinDepth.value;
    }

    public int alphaReductionMaxDepth() {
        return alphaReductionMaxDepth.value;
    }

    public int dynamicPolicyMult() {
        return dynamicPolicyMult.value;
    }

    public int dynamicPolicyMin() {
        return dynamicPolicyMin.value;
    }

    public int dynamicPolicyMax() {
        return dynamicPolicyMax.value;
    }

    public int betaHistBonusMargin() {
        return betaHistBonusMargin.value;
    }

    public int goodQuietThreshold() {
        return goodQuietThreshold.value;
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

    public int complexityTmMinDepth() {
        return complexityTmMinDepth.value;
    }

    public int complexityTmDiffBase() {
        return complexityTmDiffBase.value;
    }

    public int complexityTmScaleBase() {
        return complexityTmScaleBase.value;
    }

    public int complexityTmScaleMax() {
        return complexityTmScaleMax.value;
    }

    public int complexityTmScaleDiv() {
        return complexityTmScaleDiv.value;
    }

    public int bmStabilityMinDepth() {
        return bmStabilityMinDepth.value;
    }

    public int scoreStabilityMinDepth() {
        return scoreStabilityMinDepth.value;
    }

    public int[] seeValues() {
        return seeValues;
    }

    public void setSeeValues(int[] seeValues) {
        this.seeValues = seeValues;
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
