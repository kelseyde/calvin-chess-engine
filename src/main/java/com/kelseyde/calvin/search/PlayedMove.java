package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public record PlayedMove(Move move, Piece piece, Piece captured) {}
