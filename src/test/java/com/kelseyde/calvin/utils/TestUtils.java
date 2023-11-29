package com.kelseyde.calvin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.utils.notation.Notation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class TestUtils {

    public static final String TEST_CONFIG_LOCATION = "src/test/resources/engine_config.json";
    public static final EngineConfig TEST_CONFIG = loadConfig();

    public static final MoveGenerator MOVE_GENERATOR = new MoveGenerator();

    public static Engine getTestEngine() {
        return new Engine(TEST_CONFIG);
    }

    private static EngineConfig loadConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Path path = Paths.get(TEST_CONFIG_LOCATION);
            String json = Files.readString(path);
            return mapper.readValue(json, EngineConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Board emptyBoard() {
        Board board = new Board();
        board.setWhitePawns(0L);
        board.setWhiteKnights(0L);
        board.setWhiteBishops(0L);
        board.setWhiteRooks(0L);
        board.setWhiteQueens(0L);
        board.setWhiteKing(0L);

        board.setBlackPawns(0L);
        board.setBlackKnights(0L);
        board.setBlackBishops(0L);
        board.setBlackRooks(0L);
        board.setBlackQueens(0L);
        board.setBlackKing(0L);

        board.getGameState().setCastlingRights(0b0000);

        board.recalculatePieces();

        return board;
    }

    public static Move getLegalMove(Board board, String startSquare, String endSquare) {
        Move move = Notation.fromNotation(startSquare, endSquare);
        List<Move> legalMoves = MOVE_GENERATOR.generateMoves(board);
        Optional<Move> legalMove = legalMoves.stream()
                .filter(m -> m.matches(move))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s%s", startSquare, endSquare));
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
