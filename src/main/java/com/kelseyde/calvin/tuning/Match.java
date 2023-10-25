package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
public class Match {

    private final Player player1;
    private final Player player2;

    private final MatchConfig config;

    private final ResultCalculator resultCalculator;
    private final Random random;

    public Match(MatchConfig config) {
        this.player1 = config.getPlayer1().get();
        this.player2 = config.getPlayer2().get();
        this.config = config;
        this.resultCalculator = new ResultCalculator();
        this.random = new Random();
        log.info("Creating new match with with {} games, {} threads, {} max moves per game, {} - {} think range",
                config.getGameCount(), config.getThreadCount(), config.getMaxMoves(), config.getMinThinkTimeMs(), config.getMaxThinkTimeMs());
    }

    public MatchResult run() {

        List<Player> players = List.of(player1, player2);

        int player1Wins = 0;
        int player2Wins = 0;
        int draws = 0;

        int gameCount = 1;
        Board board = new Board();

        while (gameCount <= config.getGameCount()) {

            board = new Board();

            int whitePlayerRandom = new Random().nextInt(2);
            Player whitePlayer = players.get(whitePlayerRandom);
            Player blackPlayer = whitePlayerRandom == 1 ? players.get(0) : players.get(1);

            whitePlayer.getBot().newGame();
            blackPlayer.getBot().newGame();
            whitePlayer.getBot().setPosition(FEN.STARTING_POSITION, Collections.emptyList());
            blackPlayer.getBot().setPosition(FEN.STARTING_POSITION, Collections.emptyList());

            GameResult result = GameResult.IN_PROGRESS;

            Move whiteMove = whitePlayer.getBot().think(getThinkTime());
            int moveCount = 1;

            while (moveCount <= config.getMaxMoves()) {

                board.makeMove(whiteMove);
                whitePlayer.getBot().applyMove(whiteMove);
                blackPlayer.getBot().applyMove(whiteMove);

                result = resultCalculator.calculateResult(board);

                if (!result.equals(GameResult.IN_PROGRESS)) {
                    if (result.isWin()) {
                        if (whitePlayer.getName().equals(player1.getName())) {
                            player1Wins++;
                        } else {
                            player2Wins++;
                        }
                    } else {
                        draws++;
                    }
                    break;
                }

                Move blackMove = blackPlayer.getBot().think(getThinkTime());

                board.makeMove(blackMove);
                whitePlayer.getBot().applyMove(blackMove);
                blackPlayer.getBot().applyMove(blackMove);

                result = resultCalculator.calculateResult(board);

                if (!result.equals(GameResult.IN_PROGRESS)) {
                    if (result.isWin()) {
                        if (blackPlayer.getName().equals(player1.getName())) {
                            player1Wins++;
                        } else {
                            player2Wins++;
                        }
                    } else {
                        draws++;
                    }
                    break;
                }

                whiteMove = whitePlayer.getBot().think(getThinkTime());

                moveCount++;
                if (moveCount > config.getMaxMoves()) {
                    draws++;
                }
            }
            whitePlayer.getBot().gameOver();
            blackPlayer.getBot().gameOver();
            gameCount++;

        }

        return new MatchResult(player1Wins, player2Wins, draws);
    }

    private int getThinkTime() {
        return random.nextInt(config.getMinThinkTimeMs(), config.getMaxThinkTimeMs());
    }

}
