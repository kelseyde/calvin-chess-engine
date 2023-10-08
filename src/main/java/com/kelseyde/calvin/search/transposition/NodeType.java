package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.evaluation.Evaluator;

public enum NodeType {

    /**
     * The value for this node is the exact evaluation as determined by the {@link Evaluator} position evaluators.
     */
    EXACT,

    /**
     * This node resulted in a beta-cutoff for the opponent, meaning they discarded the move that resulted in this position
     * because they evaluated it as being worse than other options they had already considered. So, we know there is probably
     * a 'good' move in this position. However, the opponent's search cut off as soon as they found one move matching this
     * criterion, so there may be better moves. The evaluation is therefore the 'lower bound' of the true evaluation.
     */
    LOWER_BOUND,

    /**
     * This node was evaluated by the player as being inferior to alternatives they already considered - i.e. it did not
     * improve their alpha score. Basically, everything sucks here. There is no good move for me in the position, *and*
     * I have already discovered an alternative move I can use to avoid reaching this position at all. The node value is
     * an upper bound because it is the maximum score you can hope to achieve from all the moves this position.
     */
    UPPER_BOUND

}
