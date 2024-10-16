package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchHistory;

public class ScoreHistoryTable extends AbstractHistoryTable {

    private static final int SCALE_FACTOR = 1000;

    private int[][][] table = new int[2][Piece.COUNT][Square.COUNT];

    public ScoreHistoryTable(EngineConfig config) {
        super(config.scoreHistBonusMax.value,
                config.scoreHistBonusScale.value,
                config.scoreHistMalusMax.value,
                config.scoreHistMalusScale.value,
                config.scoreHistMaxScore.value);
    }

    public void update(Piece piece, Move move, boolean white, int score, int depth) {
        int colourIndex = Colour.index(white);
        int current = table[colourIndex][piece.index()][move.to()];
        int bonus = scoreBonus(score, depth);
        int update = gravity(current, bonus);
        table[colourIndex][piece.index()][move.to()] = update;
    }

    public int get(Move move, Piece piece, boolean white) {
        int colourIndex = Colour.index(white);
        return table[colourIndex][piece.index()][move.to()];
    }

    // Compute the bonus based on score and depth, capped to BONUS_MAX
    public int scoreBonus(int score, int depth) {
        int scaledScore = mapToSymmetricRange(score);
        return Math.min(bonusMax, scaledScore);
    }

    // Map score into symmetric range [-1200, 1200]
    private int mapToSymmetricRange(int score) {
        int scoreMax = 10000; // Assuming this is the absolute maximum score
        return score * bonusMax / scoreMax;  // Scales score to [-1200, 1200]
    }

    public void ageScores(boolean white) {
        int colourIndex = Colour.index(white);
        for (int from = 0; from < Piece.COUNT; from++) {
            for (int to = 0; to < Square.COUNT; to++) {
                table[colourIndex][from][to] /= 2;
            }
        }
    }

    public void clear() {
        table = new int[2][Piece.COUNT][Square.COUNT];
    }


}
