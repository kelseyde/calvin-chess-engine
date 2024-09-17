package com.kelseyde.calvin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.endgame.LichessTablebase;
import com.kelseyde.calvin.endgame.Tablebase;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.engine.EngineInitializer;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.opening.OpeningBook;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.ThreadData;
import com.kelseyde.calvin.tables.tt.TranspositionTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class TestUtils {

    public static final String PRD_CONFIG_LOCATION = "src/main/resources/engine_config.json";
    public static final String TST_CONFIG_LOCATION = "src/test/resources/engine_config.json";
    public static final EngineConfig PRD_CONFIG = loadConfig(PRD_CONFIG_LOCATION);
    public static final EngineConfig TST_CONFIG = loadConfig(TST_CONFIG_LOCATION);
    public static final OpeningBook OPENING_BOOK = EngineInitializer.loadDefaultOpeningBook(PRD_CONFIG);
    public static final Tablebase TABLEBASE = new LichessTablebase(PRD_CONFIG);
    public static final MoveGenerator MOVE_GENERATOR = new MoveGenerator();
    public static final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable(PRD_CONFIG.getDefaultHashSizeMb());
    public static final Searcher SEARCHER = new Searcher(TST_CONFIG, TRANSPOSITION_TABLE, new ThreadData(true));

    public static Engine getEngine() {
        return new Engine(PRD_CONFIG, OPENING_BOOK, TABLEBASE, new Searcher(PRD_CONFIG, new TranspositionTable(PRD_CONFIG.getDefaultHashSizeMb()), new ThreadData(true)));
    }

    private static EngineConfig loadConfig(String configLocation) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Path path = Paths.get(configLocation);
            String json = Files.readString(path);
            EngineConfig config = mapper.readValue(json, EngineConfig.class);
            config.postInitialise();
            return config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Board emptyBoard() {
        Board board = new Board();
        board.setPawns(0L);
        board.setKnights(0L);
        board.setBishops(0L);
        board.setRooks(0L);
        board.setQueens(0L);
        board.setKings(0L);

        board.setWhitePieces(0L);
        board.setBlackPieces(0L);
        board.setOccupied(0L);
        board.setPieces(new Piece[64]);

        board.getState().setRights(0b0000);

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

    public static List<String> loadFens(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }


}
