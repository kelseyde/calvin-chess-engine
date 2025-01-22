package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

/**
 * A history table that stores the history of noisy moves (checks, captures and promotions).
 * This table is used to improve move ordering for these moves.
 */
public class NoisyHistoryTable extends AbstractHistoryTable {

    /**
     * The noisy history table is indexed by [stm][piece][to][noisyIndex].
     * The noisy index is determined by the move type: captures use the captured piece index, promotions
     * use the promotion piece index, and non-capture non-promotion checks use the king index.
     */
    int[][][][] table = new int[2][Piece.COUNT][Square.COUNT][Piece.COUNT + 4];

    public NoisyHistoryTable(EngineConfig config) {
        super(config.noisyHistBonusMax.value,
                config.noisyHistBonusScale.value,
                config.noisyHistMalusMax.value,
                config.noisyHistMalusScale.value,
                config.noisyHistMaxScore.value);
    }

    public void update(Move move, Piece piece, Piece captured, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int noisyIndex = noisyIndex(move, captured);
        int current = table[colourIndex][pieceIndex][move.to()][noisyIndex];
        int bonus = good ? bonus(depth) : malus(depth);
        int update = gravity(current, bonus);
        table[colourIndex][pieceIndex][move.to()][noisyIndex] = update;
    }

    public int get(Move move, Piece piece, Piece captured, boolean white) {
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        int noisyIndex = noisyIndex(move, captured);
        return table[colourIndex][pieceIndex][move.to()][noisyIndex];
    }

    /**
     * Determines the index into the 'noisy piece' table based on the move type.
     */
    private int noisyIndex(Move move, Piece captured) {
        if (captured != null) {
            // For captures, we use the captured piece as the index.
            return captured.index();
        }
        else if (move.promoPiece() != null) {
            // For non-capture promotions, we use the promoted piece as the index.
            return move.promoPiece().index();
        }
        else {
            // For non-capture non-promotion checks, we use the king as the index.
            return Piece.KING.index();
        }
    }

    public void clear() {
        table = new int[2][Piece.COUNT][Square.COUNT][Piece.COUNT];
    }

}
