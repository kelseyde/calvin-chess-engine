package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.GameResult;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.datagen.dataformat.DataFormat.DataPoint;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.*;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.notation.FEN;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A single worker thread that generates a batch of training data. The thread runs games until it has generated the
 * required number of positions.
 */
public class DatagenThread {

    private final Searcher searcher;
    private final MoveGenerator movegen;
    private final Random random;

    private final int batchSize;
    private final int softNodes;
    private final int hardNodes;
    private final int minPlies;
    private final int maxPlies;
    private final int maxInitialScore;
    private final int maxScore;
    private final int adjudicateScore;
    private final int adjudicateMoves;

    public DatagenThread(DatagenCommand command) {
        this.searcher = new Searcher(Engine.getInstance().getConfig(), new TranspositionTable(64), new ThreadData(false));
        this.movegen = new MoveGenerator();
        this.random = new Random();
        this.batchSize = 20000 / command.threads();
        this.softNodes = command.softNodes();
        this.hardNodes = command.hardNodes();
        this.minPlies = command.minPlies();
        this.maxPlies = command.maxPlies();
        this.maxInitialScore = command.maxInitialScore();
        this.maxScore = command.maxScore();
        this.adjudicateScore = command.adjudicateScore();
        this.adjudicateMoves = command.adjudicateMoves();
    }

    public List<DataPoint> run() {

        List<DataPoint> data = new ArrayList<>();

        try {
            while (data.size() < batchSize) {

                searcher.clearHistory();
                List<String> fens = new ArrayList<>();
                List<Integer> scores = new ArrayList<>();

                Board board = randomBoard();
                searcher.setPosition(board);
                int initialScore = searcher.search(initTimeControl()).score();
                if (Math.abs(initialScore) > maxInitialScore) {
                    continue;
                }

                GameResult result;
                int adjudicationCounter = 0;

                while (true) {

                    searcher.setPosition(board);
                    SearchResult searchResult = searcher.search(initTimeControl());
                    Move bestMove = searchResult.move();
                    int bestScore = searchResult.score() * (board.isWhite() ? 1 : -1);

                    if (Math.abs(searchResult.score()) >= adjudicateScore) {
                        if (++adjudicationCounter >= adjudicateMoves) {
                            result = (bestScore > 0) ? GameResult.WHITE_WIN : GameResult.BLACK_WIN;
                            break;
                        }
                    } else {
                        adjudicationCounter = 0;
                    }

                    boolean isCheck = movegen.isCheck(board, board.isWhite());
                    boolean isCapture = board.isCapture(bestMove);
                    boolean isBadScore = Math.abs(bestScore) > maxScore;

                    if (!isCheck && !isCapture && !isBadScore) {
                        fens.add(FEN.toFEN(board));
                        scores.add(bestScore);
                    }

                    board.makeMove(bestMove);

                    if (movegen.generateMoves(board).isEmpty()) {
                        result = isCheck ? (board.isWhite() ? GameResult.BLACK_WIN : GameResult.WHITE_WIN) : GameResult.DRAW;
                        break;
                    } else if (Score.isEffectiveDraw(board)) {
                        result = GameResult.DRAW;
                        break;
                    }

                }

                data.addAll(convertGame(fens, scores, result));

            }

        } catch (Exception e) {
            UCI.writeError("Error in datagen thread!", e);
        }

        return data;
    }

    private List<DataPoint> convertGame(List<String> fens, List<Integer> scores, GameResult result) {
        if (fens.size() != scores.size()) {
            throw new IllegalArgumentException("FEN and score lists must be the same size!");
        }
        List<DataPoint> data = new ArrayList<>();
        int wdl = result.value;
        for (int i = 0; i < fens.size(); i++) {
            data.add(new DataPoint(fens.get(i), scores.get(i), wdl));
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

    private TimeControl initTimeControl() {
        EngineConfig config = Engine.getInstance().getConfig();
        Instant start = Instant.now();
        Duration maxTime = Duration.ofSeconds(30);
        return new TimeControl(config, start, maxTime, maxTime, softNodes, hardNodes, -1);
    }

}
