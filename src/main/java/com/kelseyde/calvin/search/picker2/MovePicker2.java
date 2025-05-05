package com.kelseyde.calvin.search.picker2;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.picker.MovePicker;
import com.kelseyde.calvin.search.picker.MoveScorer;
import com.kelseyde.calvin.search.picker.MoveType;
import com.kelseyde.calvin.search.picker.ScoredMove;

public abstract class MovePicker2<T extends Stage<MovePicker2<?>>> {

    protected SearchHistory history;
    protected MoveGenerator movegen;
    protected MoveScorer scorer;

    protected Board board;
    protected Move ttMove;
    protected int ply;

    protected int moveIndex;
    protected T stage;

    public abstract ScoredMove next();

    protected abstract ScoredMove generate(MoveFilter filter, T nextStage);

    protected abstract boolean isSpecial(Move move);

    protected final ScoredMove pickTTMove(T nextStage) {
        stage = nextStage;
        final Piece piece = board.pieceAt(ttMove.from());
        final Piece captured = board.captured(ttMove);
        return new ScoredMove(ttMove, piece, captured, 0, 0, MoveType.TT_MOVE);
    }

    protected ScoredMove pick(ScoredMove[] moves) {
        if (moveIndex >= moves.length) {
            return null;
        }
        ScoredMove best = moves[moveIndex];
        if (best == null) {
            return null;
        }
        int bestScore = best.score();
        int bestIndex = moveIndex;
        for (int j = moveIndex + 1; j < moves.length; j++) {
            ScoredMove current = moves[j];
            if (current == null) {
                break;
            }
            if (current.score() > bestScore) {
                bestScore = current.score();
                bestIndex = j;
            }
        }
        if (bestIndex != moveIndex) {
            swap(moves, moveIndex, bestIndex);
        }
        ScoredMove scoredMove = moves[moveIndex];
        if (scoredMove == null || isSpecial(scoredMove.move())) {
            moveIndex++;
            return pick(moves);
        }
        return scoredMove;
    }

    protected final void swap(ScoredMove[] moves, int i, int j) {
        ScoredMove temp = moves[i];
        moves[i] = moves[j];
        moves[j] = temp;
    }

    protected ScoredMove nextStage(T nextStage) {
        moveIndex = 0;
        stage = nextStage;
        return null;
    }

}
