package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Move;

public class ThreadData {

    public final boolean mainThread;
    public int nodes;
    public int[][] nodesPerMove;
    public int depth;
    public int seldepth;
    public int nmpPly;

    private Move bestMovePrevious;
    private int bestScorePrevious;

    private Move bestMove;
    private int bestScore;

    public ThreadData(boolean mainThread) {
        this.mainThread = mainThread;
        this.nodes = 0;
        this.nodesPerMove = new int[Square.COUNT][Square.COUNT];
        this.depth = 1;
        this.seldepth = 0;
        this.nmpPly = 0;
        this.bestMove = null;
        this.bestScore = 0;
        this.bestMovePrevious = null;

    }

    public void updateBestMove(Move move, int score) {
        if (move != null) {
            this.bestMovePrevious = this.bestMove;
            this.bestScorePrevious = this.bestScore;
            this.bestMove = move;
            this.bestScore = score;
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

    public Move bestMovePrevious() {
        return bestMovePrevious;
    }

    public int bestScorePrevious() {
        return bestScorePrevious;
    }

    public int nodes(Move move) {
        if (move == null) return 0;
        return nodesPerMove[move.from()][move.to()];
    }

    public boolean isMainThread() {
        return mainThread;
    }

    public void resetIteration() {
        bestMovePrevious = bestMove;
        bestScorePrevious = bestScore;
        bestMove = null;
        bestScore = 0;
        seldepth = 0;
    }

    public void reset() {
        this.nodes = 0;
        this.nodesPerMove = new int[Square.COUNT][Square.COUNT];
        this.depth = 1;
        this.seldepth = 0;
        this.nmpPly = 0;
        this.bestMove = null;
        this.bestScore = 0;
        this.bestMovePrevious = null;
        this.bestScorePrevious = 0;
    }

}
