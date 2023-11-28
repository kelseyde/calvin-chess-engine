package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.notation.FEN;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Match {

    private final Player player1;
    private final Player player2;

    private final MatchConfig config;

    private final ResultCalculator resultCalculator;
    private final MoveGenerator moveGenerator;
    private final Random random;

    public Match(MatchConfig config) {
        this.player1 = config.getPlayer1().get();
        this.player2 = config.getPlayer2().get();
        this.config = config;
        this.resultCalculator = new ResultCalculator();
        this.moveGenerator = new MoveGenerator();
        this.random = new Random();
        System.out.printf("Creating new match with with %s games, %s threads, %s max moves per game, %s - %s think range %n",
                config.getGameCount(), config.getThreadCount(), config.getMaxMoves(), config.getMinThinkTimeMs(), config.getMaxThinkTimeMs());
    }

    public MatchResult run() {

        List<Player> players = List.of(player1, player2);

        int player1Wins = 0;
        int player2Wins = 0;
        int draws = 0;

        int gameCount = 1;
        Board board;

        while (gameCount <= config.getGameCount()) {

            board = new Board();

            int whitePlayerRandom = new Random().nextInt(2);
            Player whitePlayer = players.get(whitePlayerRandom);
            Player blackPlayer = whitePlayerRandom == 1 ? players.get(0) : players.get(1);

            whitePlayer.getBot().newGame();
            blackPlayer.getBot().newGame();
            whitePlayer.getBot().setPosition(FEN.STARTING_POSITION, Collections.emptyList());
            blackPlayer.getBot().setPosition(FEN.STARTING_POSITION, Collections.emptyList());

            GameResult result;

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
                            printMatchReport(player1Wins, player2Wins, draws);
                        } else {
                            player2Wins++;
                            printMatchReport(player1Wins, player2Wins, draws);
                        }
                    } else {
                        draws++;
                        printMatchReport(player1Wins, player2Wins, draws);
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
                            printMatchReport(player1Wins, player2Wins, draws);
                        } else {
                            player2Wins++;
                            printMatchReport(player1Wins, player2Wins, draws);
                        }
                    } else {
                        draws++;
                        printMatchReport(player1Wins, player2Wins, draws);
                    }
                    break;
                }

                whiteMove = whitePlayer.getBot().think(getThinkTime());

                moveCount++;
                if (moveCount > config.getMaxMoves()) {
                    draws++;
                    printMatchReport(player1Wins, player2Wins, draws);
                    break;
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

    private void printMatchReport(int player1Wins, int player2Wins, int draws) {
        System.out.println("player1 wins: " + player1Wins);
        System.out.println("player2 wins: " + player2Wins);
        System.out.println("draws: " + draws);
        System.out.println("---");
    }

}
