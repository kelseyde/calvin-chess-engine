package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.Score;
import com.kelseyde.calvin.tables.tt.TranspositionTable;

/**
 * Correction history tracks how much the static evaluation of a position matched the actual search score. We can use
 * this information to 'correct' the current static eval based on the diff between the static eval and the search score
 * of previously searched positions.
 * <p>
 * This is a similar heuristic to re-using the cached search score in the {@link TranspositionTable}, except rather than
 * using the score from the exact same position, we use a running average of eval diffs of previously searched positions
 * which share some feature, e.g. pawn structure, material balance, and so on.
 * <p>
 * The running average also gives more weight to positions that were searched to a greater depth, as these are more
 * likely to have a more accurate final search score.
 *
 * @see <a href="https://www.chessprogramming.org/Static_Evaluation_Correction_History">Chess Programming Wiki</a>
 *
 */
public class ScoreHistoryTable {

    int[][][] entries;

    public ScoreHistoryTable() {
        this.entries = new int[2][Piece.COUNT][Square.COUNT];
    }

    public void update(Move move, Piece piece, boolean white, int depth, int beta, int score) {

        if (Score.isMateScore(score)) {
            return;
        }

        // we retrieve the current entry for the move
        int entry = get(move, piece, white);

        // the average delta is stored in the bottom 16-bits, as a signed short
        short runningAverage = (short) (entry & 0xFFFF);

        // the number of searches is stored in the top 16-bits
        int searches = entry >> 16;

        // we calculate the delta between the score and beta
        int delta = score - beta;
        int newScore = delta / 32 * Math.min(32, depth);

        // we increment the number of searches
        searches++;

        // calculate the new running average
        int newAverage = (runningAverage * (searches - 1) + newScore) / searches;

        // we store the new average and number of searches
        put(move, piece, white, (searches << 16) | (newAverage & 0xFFFF));

    }

    public int getRunningAverage(Move move, Piece piece, boolean white) {
        int entry = get(move, piece, white);
        return (short) (entry & 0xFFFF); // Cast to short to preserve the signed value
    }

    public int get(Move move, Piece piece, boolean white) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int squareIndex = move.to();
        return entries[colourIndex][pieceIndex][squareIndex];
    }

    public void put(Move move, Piece piece, boolean white, int value) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int squareIndex = move.to();
        entries[colourIndex][pieceIndex][squareIndex] = value;
    }

    public void ageEntries(boolean white) {
        int colourIndex = Colour.index(white);
        for (int piece = 0; piece < Piece.COUNT; piece++) {
            for (int square = 0; square < Square.COUNT; square++) {
                int entry = entries[colourIndex][piece][square];
                int runningAverage = entry & 0xFFFF;
                int searches = entry >> 16;
                entries[colourIndex][piece][square] = (searches << 16) | (runningAverage / 2);
            }
        }
    }

    public void clear() {
        entries = new int[2][Piece.COUNT][Square.COUNT];
    }

//    public static void main(String[] args) {
//        ScoreHistoryTable sht = new ScoreHistoryTable();
//        Move move = Move.fromUCI("e2e4");
//        Piece piece = Piece.PAWN;
//        boolean white = true;
//        int beta = 0;
//        int score = 100;
//        sht.update(move, piece, white, beta, score);
//        System.out.println(sht.getRunningAverage(move, piece, white));
//
//        move = Move.fromUCI("d2d4");
//        piece = Piece.PAWN;
//        white = false;
//        beta = 0;
//        score = -4000;
//        sht.update(move, piece, white, beta, score);
//        System.out.println(sht.getRunningAverage(move, piece, white));
//
//        score = 100;
//        sht.update(move, piece, white, beta, score);
//        System.out.println(sht.getRunningAverage(move, piece, white));
//    }

}
