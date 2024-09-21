package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.ThreadData;
import com.kelseyde.calvin.tables.tt.TranspositionTable;

import java.util.List;
import java.util.Optional;

public class TestUtils {

    public static final EngineConfig PRD_CONFIG = new EngineConfig();
    public static final MoveGenerator MOVE_GENERATOR = new MoveGenerator();
    public static final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable(PRD_CONFIG.defaultHashSizeMb);
    public static final Searcher SEARCHER = new Searcher(PRD_CONFIG, TRANSPOSITION_TABLE, new ThreadData(true));

    public static Engine getEngine() {
        return new Engine(PRD_CONFIG, new Searcher(PRD_CONFIG, new TranspositionTable(PRD_CONFIG.defaultHashSizeMb), new ThreadData(true)));
    }

    public static Move getLegalMove(Board board, String from, String to) {
        Move move = Notation.fromNotation(from, to);
        List<Move> legalMoves = MOVE_GENERATOR.generateMoves(board);
        Optional<Move> legalMove = legalMoves.stream()
                .filter(m -> m.matches(move))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s%s", from, to));
        }
        return legalMove.get();
    }

    public static Move getLegalMove(Board board, Move move) {
        List<Move> legalMoves = MOVE_GENERATOR.generateMoves(board);
        Optional<Move> legalMove = legalMoves.stream()
                .filter(m -> m.matches(move))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s", move));
        }
        return legalMove.get();
    }

}
