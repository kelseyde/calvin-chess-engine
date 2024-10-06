package com.kelseyde.calvin.uci;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.Score;
import com.kelseyde.calvin.search.SearchResult;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Pretty {

    public static final String RED =          "\u001B[31m";
    public static final String GREEN =        "\u001B[32m";
    public static final String YELLOW =       "\u001B[33m";
    public static final String BLUE =         "\u001B[34m";
    public static final String CYAN =         "\u001B[36m";
    public static final String GRAY =         "\u001B[90m";
    public static final String ITALIC_ON =    "\033[3m";
    public static final String ITALIC_OFF =   "\033[23m";
    public static final String RESET =        "\u001B[0m";

    // Pretty print code is horrible - there is no other way
    public static final String BANNER =
            String.format(
                    """
                    
                            %s...............................................
                           ...............................................
                          ...............................................
                         ...............................................
                        ............%s Calvin, %s**************%s............
                       ............%s****** %sa chess engine. %s............
                      ...............................................
                     ...............................................
                    ...............................................%s
                    """,
            Pretty.CYAN, Pretty.RESET, Pretty.RED, Pretty.CYAN, Pretty.YELLOW, Pretty.RESET, Pretty.CYAN, Pretty.RESET);

    public static void printEngineInfo() {
        UCI.write(Pretty.BANNER);
        UCI.write(Pretty.RED + "Engine" + Pretty.RESET + ": Calvin 4.3.0");
        UCI.write(Pretty.RED + "Author" + Pretty.RESET + ": Dan Kelsey");
        UCI.write(Pretty.RED + "Source" + Pretty.RESET + ": https://github.com/kelseyde/calvin-chess-engine");
        UCI.write("");
        UCI.write("type " + Pretty.YELLOW + "'help'" + Pretty.RESET + " for a list of commands");
        UCI.write("");
    }

    public static void writeSearchInfo(int depth, int score, long time, int nodes, long nps, List<Move> pv) {

        String formattedDepth = formatDepth(depth);
        String formattedScore = formatScore(score);
        String formattedTime = formatTime(time);
        String formattedNodes = formatNodes(nodes);
        String formattedNps = formatNps(nps);
        String formattedPv = formatPv(pv);
        UCI.write(String.format(" %s  %s  %s  %s  %s  %s",
                formattedDepth, formattedScore, formattedTime, formattedNodes, formattedNps, formattedPv));

    }

    private static String formatDepth(int depth) {
        final int depthLength = 3;
        return BLUE + " ".repeat(depthLength - String.valueOf(depth).length()) + depth + RESET;
    }

    private static String formatScore(int score) {

        final int scoreLength = 7;

        String colour = "";
        if (score > 0) {
            colour = Pretty.GREEN;
        } else if (score < 0) {
            colour = Pretty.RED;
        }

        String paddedScore = "";
        if (Score.isMateScore(score)) {
            int moves = Math.max((Score.MATE - Math.abs(score)) / 2, 1);
            if (score < 0) moves = -moves;
            String mateString =  "M" + moves;
            paddedScore = " ".repeat(scoreLength - mateString.length()) + mateString;
        } else {
            float scoreFloat = (float) score / 100;
            String sign = scoreFloat >= 0 ? "+" : "";
            String scoreString = String.format(Locale.ROOT, "%.2f", scoreFloat);
            paddedScore = " ".repeat(scoreLength - scoreString.length()) + sign + scoreString;
        }

        return colour + paddedScore + RESET;

    }

    private static String formatTime(long time) {
        final int timeLength = 8;
        String formattedTime = String.format(Locale.ROOT, "%sms", time);
        return GRAY + " ".repeat(timeLength - formattedTime.length()) + formattedTime + RESET;
    }

    private static String formatNodes(int nodes) {
        final int nodesLength = 12;
        int knodes = nodes / 1000;
        String formattedNodes = String.format(Locale.ROOT, "%dkn", knodes);
        return GRAY + " ".repeat(nodesLength - formattedNodes.length()) + formattedNodes + RESET;
    }

    private static String formatNps(long nps) {
        final int npsLength = 12;
        double knps = (double) nps / 1000;
        String formattedKnps = String.format(Locale.ROOT, "%.1f", knps);
        return GRAY + " ".repeat(npsLength - formattedKnps.length()) + formattedKnps + "knps" + RESET;
    }

    private static String formatPv(List<Move> pv) {
        return ITALIC_ON + pv.stream().map(Move::toUCI).collect(Collectors.joining("  ")) + ITALIC_OFF;
    }

}
