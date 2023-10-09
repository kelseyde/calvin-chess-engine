package com.kelseyde.calvin.api.uci;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

@Disabled
public class UCICommandLineRunnerTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final MoveGenerator moveGenerator = new MoveGenerator();

    private UCICommandLineRunner runner;
    private ApplicationShutdownManager shutdownManager;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
        Bot bot = new CalvinBot();
        runner = new UCICommandLineRunner(shutdownManager, bot);
    }

    @Test
    public void testUci() {
        runner.handleCommand("uci");
        String output = outputStream.toString();
        Assertions.assertEquals("uciok\n", output);
    }

    @Test
    public void testIsReady() {
        runner.handleCommand("isready");
        String output = outputStream.toString();
        Assertions.assertEquals("readyok\n", output);
    }

    @Test
    public void testPlayingWithWhite() throws InterruptedException {
        runner.handleCommand("uci");
        runner.handleCommand("ucinewgame");
        runner.handleCommand("isready");
        runner.handleCommand("position startpos");
        runner.handleCommand("go movetime 500");
        Thread.sleep(550);

        List<String> lines = Arrays.asList(outputStream.toString().split("\n"));
        Assertions.assertEquals(3, lines.size());
        String moveResponse = lines.get(lines.size() - 1).split(" ")[1].trim();
        Move move = NotationUtils.fromCombinedNotation(moveResponse);

        Board testBoard = new Board();
        List<Move> legalMoves = Arrays.asList(moveGenerator.generateLegalMoves(testBoard, false));
        Assertions.assertTrue(legalMoves.stream().anyMatch(move::matches));
    }

    @Test
    public void testPlayingWithBlack() throws InterruptedException {
        runner.handleCommand("uci");
        runner.handleCommand("ucinewgame");
        runner.handleCommand("isready");
        runner.handleCommand("position startpos moves e2e4");
        runner.handleCommand("go movetime 500");
        Thread.sleep(575);

        String output = outputStream.toString();
        List<String> lines = Arrays.asList(output.split("\n"));
        Assertions.assertEquals(3, lines.size());
        String moveNotation = lines.get(2).split(" ")[1].trim();
        Move move = NotationUtils.fromCombinedNotation(moveNotation);

        Board testBoard = new Board();
        testBoard.makeMove(NotationUtils.fromNotation("e2", "e4"));

        List<Move> legalMoves = Arrays.asList(moveGenerator.generateLegalMoves(testBoard, false));
        Assertions.assertTrue(legalMoves.stream().anyMatch(move::matches));

        testBoard.makeMove(TestUtils.getLegalMove(testBoard, move));

        runner.handleCommand(String.format("position startpos moves e2e4 %s d2d4", moveNotation));
        runner.handleCommand("go movetime 500");
        Thread.sleep(575);
        output = outputStream.toString();
        lines = Arrays.asList(output.split("\n"));
        Assertions.assertEquals(4, lines.size());

        moveNotation = lines.get(3).split(" ")[1].trim();
        move = NotationUtils.fromCombinedNotation(moveNotation);

        testBoard.makeMove(NotationUtils.fromNotation("e2", "e4"));
        legalMoves = Arrays.asList(moveGenerator.generateLegalMoves(testBoard, false));
        Assertions.assertTrue(legalMoves.stream().anyMatch(move::matches));

    }

    @Test
    public void testGameContinuationMoveWhite() throws InterruptedException {
        runner.handleCommand("uci");
        runner.handleCommand("ucinewgame");
        runner.handleCommand("isready");
        runner.handleCommand("position startpos");
        runner.handleCommand("go movetime 500");
        Thread.sleep(550);
    }


}