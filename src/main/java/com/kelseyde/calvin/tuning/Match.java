package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.Result;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.utils.notation.FEN;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match {

    private final Player player1;
    private final Player player2;

    private final MatchConfig config;

    private final MoveGenerator moveGenerator;
    private final Random random;

    public Match(MatchConfig config) {
        this.player1 = config.getPlayer1().get();
        this.player2 = config.getPlayer2().get();
        this.config = config;
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

            String fen = FEN.STARTING_POSITION;
            List<Move> moves = new ArrayList<>();

            board = new Board();

            int whitePlayerRandom = new Random().nextInt(2);
            Player whitePlayer = players.get(whitePlayerRandom);
            Player blackPlayer = whitePlayerRandom == 1 ? players.get(0) : players.get(1);

            whitePlayer.engine().newGame();
            blackPlayer.engine().newGame();
            whitePlayer.engine().setPosition(fen, moves);
            blackPlayer.engine().setPosition(fen, moves);

            GameResult result;

            Move whiteMove = whitePlayer.engine().think(getThinkTime()).move();
            int moveCount = 1;

            while (moveCount <= config.getMaxMoves()) {

                board.makeMove(whiteMove);
                moves.add(whiteMove);
                whitePlayer.engine().setPosition(fen, moves);
                blackPlayer.engine().setPosition(fen, moves);

                result = calculateResult(board);

                if (!result.equals(GameResult.IN_PROGRESS)) {
                    if (result.isWin()) {
                        if (whitePlayer.name().equals(player1.name())) {
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

                Move blackMove = blackPlayer.engine().think(getThinkTime()).move();

                board.makeMove(blackMove);
                moves.add(blackMove);
                whitePlayer.engine().setPosition(fen, moves);
                blackPlayer.engine().setPosition(fen, moves);

                result = calculateResult(board);

                if (!result.equals(GameResult.IN_PROGRESS)) {
                    if (result.isWin()) {
                        if (blackPlayer.name().equals(player1.name())) {
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

                whiteMove = whitePlayer.engine().think(getThinkTime()).move();

                moveCount++;
                if (moveCount > config.getMaxMoves()) {
                    draws++;
                    printMatchReport(player1Wins, player2Wins, draws);
                    break;
                }
            }
            whitePlayer.engine().gameOver();
            blackPlayer.engine().gameOver();
            gameCount++;

        }

        return new MatchResult(player1Wins, player2Wins, draws);
    }

    private GameResult calculateResult(Board board) {
        List<Move> legalMoves = moveGenerator.generateMoves(board);
        if (legalMoves.isEmpty()) {
            if (moveGenerator.isCheck(board, board.isWhite())) {
                return board.isWhite() ? GameResult.BLACK_WINS_BY_CHECKMATE : GameResult.WHITE_WINS_BY_CHECKMATE;
            } else {
                return GameResult.DRAW_BY_STALEMATE;
            }
        }
        if (Result.isThreefoldRepetition(board)) {
            return GameResult.DRAW_BY_REPETITION;
        }
        if (Result.isInsufficientMaterial(board)) {
            return GameResult.DRAW_BY_INSUFFICIENT_MATERIAL;
        }
        if (Result.isFiftyMoveRule(board)) {
            return GameResult.DRAW_BY_FIFTY_MOVE_RULE;
        }
        return GameResult.IN_PROGRESS;
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
