package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public record ScoredMove(Move move,
                         Piece piece,
                         Piece captured,
                         int score,
                         int historyScore,
                         MoveType moveType)
{}
