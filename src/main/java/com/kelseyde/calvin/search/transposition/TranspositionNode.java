package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Move;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A single entry in the {@link TranspositionTable}.
 */
@Data
@AllArgsConstructor
public class TranspositionNode {

    /**
     * The zobrist hash for this position.
     */
    private long zobristKey;

    /**
     * The 'type' of node this was evaluated to be in the previous search (exact, lower bound or upper bound).
     */
    private NodeType type;

    /**
     * The best move that the previous search selected from this position
     */
    private Move bestMove;

    /**
     * How many plies ahead were searched from this position.
     */
    private int depth;

    /**
     * The evaluation score from the previous search for this position (note: may not be exact, see {@link #type}).
     */
    private int value;

}
