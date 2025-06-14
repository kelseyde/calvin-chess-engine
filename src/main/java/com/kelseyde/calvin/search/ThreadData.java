package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Move;

public class ThreadData {

    public final int threadIndex;
    public int nodes;
    public int[][] nodesPerMove;
    public int depth;
    public int seldepth;
    public int nmpPly;
    public boolean abort;

    // The best overall move and score found so far
    private Move bestMove;
    private int bestScore;

    // The best move and score from the current iteration
    private Move bestMoveCurrent;
    private int bestScoreCurrent;

    // Stability counters for the best move and score
    private int bestMoveStability = 0;
    private int bestScoreStability = 0;

    public ThreadData(int threadIndex) {
        this.threadIndex = threadIndex;
        this.nodes = 0;
        this.nodesPerMove = new int[Square.COUNT][Square.COUNT];
        this.depth = 1;
        this.seldepth = 0;
        this.nmpPly = 0;
        this.bestMove = null;
        this.bestScore = 0;
        this.bestMoveCurrent = null;
        this.bestScoreCurrent = 0;
        this.bestMoveStability = 0;
        this.bestScoreStability = 0;
    }

    /**
     * Update the best move and score for the current iteration.
     */
    public void updateBestMoveCurrent(Move move, int score) {
        if (move != null) {
            bestMoveCurrent = move;
            bestScoreCurrent = score;
        }
    }

    /**
     * Update the best move and score for the overall search.
     */
    public void updateBestMove(Move move, int score) {
        if (move != null) {
            bestMove = move;
            bestScore = score;
        }
    }

    /**
     * If a best move was found during the current iteration, update the overall best move and score.
     */
    public void updateBestMove() {
        if (bestMoveCurrent != null) {
            updateBestMoveStability(bestMove, bestMoveCurrent);
            updateBestScoreStability(bestScore, bestScoreCurrent);
            updateBestMove(bestMoveCurrent, bestScoreCurrent);
        }
    }

    public void addNodes(Move move, int nodes) {
        nodesPerMove[move.from()][move.to()] += nodes;
    }

    public Move bestMove() {
        return bestMove;
    }

    public int bestScore() {
        return bestScore;
    }

    public Move bestMoveCurrent() {
        return bestMoveCurrent;
    }

    public int bestScoreCurrent() {
        return bestScoreCurrent;
    }

    public int bestMoveStability() {
        return bestMoveStability;
    }

    public int bestScoreStability() {
        return bestScoreStability;
    }

    public int nodes(Move move) {
        if (move == null) return 0;
        return nodesPerMove[move.from()][move.to()];
    }

    public boolean isMainThread() {
        return threadIndex == 0;
    }

    public void resetIteration() {
        bestMoveCurrent = null;
        bestScoreCurrent = 0;
        seldepth = 0;
    }

    public void updateBestMoveAndScore(ThreadData td) {
        if (td.bestMove() != null) {
            updateBestMoveStability(td.bestMove(), td.bestMoveCurrent());
            updateBestScoreStability(td.bestScore(), td.bestScoreCurrent());
        }
    }

    public void updateBestMoveStability(Move bestMovePrevious, Move bestMoveCurrent) {
        if (bestMovePrevious == null || bestMoveCurrent == null) {
            return;
        }
        bestMoveStability = bestMovePrevious.equals(bestMoveCurrent) ? bestMoveStability + 1 : 0;
    }

    public void updateBestScoreStability(int scorePrevious, int scoreCurrent) {
        bestScoreStability = scoreCurrent >= scorePrevious - 10 && scoreCurrent <= scorePrevious + 10 ? bestScoreStability + 1 : 0;
    }

    public void reset() {
        this.nodes = 0;
        this.nodesPerMove = new int[Square.COUNT][Square.COUNT];
        this.depth = 1;
        this.seldepth = 0;
        this.nmpPly = 0;
        this.bestMove = null;
        this.bestScore = 0;
        this.bestMoveCurrent = null;
        this.bestScoreCurrent = 0;
        this.bestMoveStability = 0;
        this.bestScoreStability = 0;
        this.abort = false;
    }

}
