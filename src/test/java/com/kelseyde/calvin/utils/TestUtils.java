package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.ThreadData;
import com.kelseyde.calvin.tables.tt.TranspositionTable;

import java.util.List;
import java.util.Optional;

public class TestUtils {

    public static final Engine ENGINE = Engine.getInstance();
    public static final EngineConfig CONFIG = new EngineConfig();
    public static final MoveGenerator MOVEGEN = new MoveGenerator();
    public static final TranspositionTable TT = new TranspositionTable(CONFIG.defaultHashSizeMb);
    public static final Searcher SEARCHER = new Searcher(CONFIG, TT, new ThreadData(true));

    public static List<Move> legalMoves(Board board) {
        return legalMoves(board, MoveFilter.ALL);
    }

    public static List<Move> legalMoves(Board board, MoveFilter filter) {
        return MOVEGEN.generateMoves(board, filter).stream()
                .filter(m -> MOVEGEN.isLegal(board, m))
                .toList();
    }


    public static Move getLegalMove(Board board, String from, String to) {
        Move move = Move.fromUCI(from + to);
        List<Move> legalMoves = MOVEGEN.generateMoves(board);
        Optional<Move> legalMove = legalMoves.stream()
                .filter(m -> m.matches(move))
                .filter(m -> MOVEGEN.isLegal(board, m))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s%s", from, to));
        }
        return legalMove.get();
    }

    public static Move getLegalMove(Board board, Move move) {
        List<Move> pseudoLegals = MOVEGEN.generateMoves(board);
        Optional<Move> legalMove = pseudoLegals.stream()
                .filter(m -> m.matches(move))
                .filter(m -> MOVEGEN.isLegal(board, m))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s", move));
        }
        return legalMove.get();
    }

}
