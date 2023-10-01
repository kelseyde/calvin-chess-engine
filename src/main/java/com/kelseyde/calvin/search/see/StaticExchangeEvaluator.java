package com.kelseyde.calvin.search.see;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;

// TODO
public class StaticExchangeEvaluator {

    public int see(Board board, Move move) {

        int square = move.getEndSquare();


        return 0;

    }


    /*

    STEP 1: Find obvious attackers. Delta start square to all possible attack squares, check for pieces of both sides.
    STEP 2: Simulate initial capture. Keep track of score. Update value of piece currently on capture square and side to move
    STEP 3: Find hidden attacker. Based on last capture, check for pieces hiding behind the capturing piece (not knights and kings)
    STEP 4: Play out sequence. Capture (least valuable attacker), check for hidden attacker, update score.

     */

}
