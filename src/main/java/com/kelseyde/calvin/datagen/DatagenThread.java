package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.datagen.Adjudicator.GameResult;
import com.kelseyde.calvin.datagen.dataformat.DataFormat.DataPoint;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.ThreadData;
import com.kelseyde.calvin.search.TimeControl;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.notation.FEN;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * A single worker thread that generates a batch of training data. The thread runs games until it has generated the
 * required number of positions.
 */
public class DatagenThread {

    private final Searcher searcher;
    private final MoveGenerator movegen;
    private final Adjudicator adjudicator;
    private final TimeControl tc;
    private final Random random;

    private final int batchSize;
    private final int minPlies;
    private final int maxPlies;
    private final int maxGameLength;
    private final int initialScoreThreshold;

    public DatagenThread(DatagenCommand command) {
        this.searcher = new Searcher(Engine.getInstance().getConfig(), new TranspositionTable(16), new ThreadData(false));
        this.movegen = new MoveGenerator();
        this.adjudicator = new Adjudicator(command);
        this.tc = initTimeControl(command);
        this.random = new Random();
        this.batchSize = command.batchSize();
        this.minPlies = command.minPlies();
        this.maxPlies = command.maxPlies();
        this.maxGameLength = command.maxGameLength();
        this.initialScoreThreshold = command.initialScoreThreshold();
    }

    public List<DataPoint> run() {
        List<DataPoint> data = new ArrayList<>();
        while (data.size() < batchSize) {
            data.addAll(runGame());
        }
        return data;
    }

    private List<DataPoint> runGame() {

        try {
            searcher.clearHistory();
            adjudicator.reset();
            String[] fens = new String[maxGameLength];
            Integer[] scores = new Integer[maxGameLength];
            List<DataPoint> data;
            Board board = randomBoard();
            searcher.setPosition(board);
            int initialScore = searcher.search(tc).eval();
            if (Math.abs(initialScore) > initialScoreThreshold) {
                return runGame();
            }

            int positionCount = 0;

            while (true) {

                searcher.setPosition(board);
                SearchResult searchResult = searcher.search(tc);

                Move move = searchResult.move();
                boolean isCheck = movegen.isCheck(board, board.isWhite());

                if (move == null) {
                    GameResult result = adjudicator.finalResult(board, isCheck);
                    data = convertGame(fens, scores, result);
                    break;
                }

                int score = searchResult.eval();
                boolean isCapture = board.pieceAt(move.to()) != null || move.isEnPassant();

                Optional<GameResult> gameResult = adjudicator.adjudicate(board, move, score);
                if (gameResult.isPresent()) {
                    data = convertGame(fens, scores, gameResult.get());
                    break;
                }

                if (!isCheck && !isCapture) {
                    fens[positionCount] = FEN.toFEN(board);
                    scores[positionCount] = board.isWhite() ? score : -score;
                    positionCount++;
                }

                board.makeMove(move);

            }

            return data;
        } catch (Exception e) {
            UCI.writeError("Error during datagen!", e);
            return new ArrayList<>();
        }

    }

    private List<DataPoint> convertGame(String[] fens, Integer[] scores, GameResult result) {
        List<DataPoint> data = new ArrayList<>();
        int wdl = result.value;
        for (int i = 0; i < fens.length; i++) {
            if (fens[i] == null) break;
            data.add(new DataPoint(fens[i], scores[i], wdl));
        }
        return data;
    }

    private Board randomBoard() {
        Board board = Board.from(FEN.STARTPOS);
        int plies = random.nextInt(minPlies, maxPlies + 1);
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
        List<Move> legalMoves = movegen.generateMoves(board);
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
