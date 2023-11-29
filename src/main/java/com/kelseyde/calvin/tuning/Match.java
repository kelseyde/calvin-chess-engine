package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.result.Result;
import com.kelseyde.calvin.evaluation.result.ResultCalculator;
import com.kelseyde.calvin.generation.MoveGenerator;
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

            whitePlayer.getEngine().newGame();
            blackPlayer.getEngine().newGame();
            whitePlayer.getEngine().setPosition(FEN.STARTING_POSITION, Collections.emptyList());
            blackPlayer.getEngine().setPosition(FEN.STARTING_POSITION, Collections.emptyList());

            Result result;

            Move whiteMove = whitePlayer.getEngine().think(getThinkTime());
            int moveCount = 1;

            while (moveCount <= config.getMaxMoves()) {

                board.makeMove(whiteMove);
                whitePlayer.getEngine().applyMove(whiteMove);
                blackPlayer.getEngine().applyMove(whiteMove);

                result = resultCalculator.calculateResult(board);

                if (!result.equals(Result.IN_PROGRESS)) {
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

                Move blackMove = blackPlayer.getEngine().think(getThinkTime());

                board.makeMove(blackMove);
                whitePlayer.getEngine().applyMove(blackMove);
                blackPlayer.getEngine().applyMove(blackMove);

                result = resultCalculator.calculateResult(board);

                if (!result.equals(Result.IN_PROGRESS)) {
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

                whiteMove = whitePlayer.getEngine().think(getThinkTime());

                moveCount++;
                if (moveCount > config.getMaxMoves()) {
                    draws++;
                    printMatchReport(player1Wins, player2Wins, draws);
                    break;
                }
            }
            whitePlayer.getEngine().gameOver();
            blackPlayer.getEngine().gameOver();
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
