package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.datagen.dataformat.DataFormat;
import com.kelseyde.calvin.datagen.dataformat.DataFormat.DataPoint;
import com.kelseyde.calvin.datagen.dataformat.MarlinFormat;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.ThreadData;
import com.kelseyde.calvin.search.TimeControl;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.uci.UCICommand;
import com.kelseyde.calvin.uci.UCICommand.DatagenCommand;
import com.kelseyde.calvin.utils.notation.FEN;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DatagenThread {

    private final Searcher searcher;
    private final MoveGenerator moveGenerator;
    private final TimeControl tc;
    private final Random random;
    private final int batchSize;
    private final int minPlies;
    private final int maxPlies;

    public DatagenThread(DatagenCommand command) {
        this.searcher = new Searcher(Engine.getInstance().getConfig(), new TranspositionTable(16), new ThreadData(false));
        this.moveGenerator = new MoveGenerator();
        this.tc = initTimeControl(command);
        this.random = new Random();
        this.batchSize = command.batchSize();
        this.minPlies = command.minPlies();
        this.maxPlies = command.maxPlies();
    }

    public List<DataPoint> run() {
        List<DataPoint> data = new ArrayList<>();
        while (data.size() < batchSize) {
            data.addAll(runGame());
        }
        return data;
    }

    private List<DataPoint> runGame() {
        List<DataPoint> data = new ArrayList<>();
        Board board = randomBoard();
        searcher.setPosition(board);
        while (true) {
            SearchResult result = searcher.search(tc);
            if (result == null) break;

            Move bestMove = result.move();
            int score = result.eval();


        }
        return null;
    }

    /**
     * Creates a starting position for the game by playing random moves up to the configured ply limit.
     */
    private Board randomBoard() {
        Board board = Board.from(FEN.STARTPOS);
        int plies = random.nextInt(maxPlies, minPlies + 1);
        for (int i = 0; i < plies; i++) {
            Move move = randomMove(board);
            // If we reached a terminal position, start over
            if (move == null) return randomBoard();
            board.makeMove(move);
        }
        if (randomMove(board) == null) {
            // If the game ended in a terminal position, start over
            return randomBoard();
        }
        return board;
    }

    private Move randomMove(Board board) {
        List<Move> legalMoves = moveGenerator.generateMoves(board);
        return !legalMoves.isEmpty() ? legalMoves.get(random.nextInt(legalMoves.size())) : null;
    }

    private TimeControl initTimeControl(DatagenCommand command) {
        EngineConfig config = Engine.getInstance().getConfig();
        int softNodes = command.softNodes();
        int hardNodes = command.hardNodes();
        Instant start = Instant.now();
        Duration maxTime = Duration.ofSeconds(30);
        return new TimeControl(config, start, maxTime, maxTime, softNodes, hardNodes, -1);
    }

}
