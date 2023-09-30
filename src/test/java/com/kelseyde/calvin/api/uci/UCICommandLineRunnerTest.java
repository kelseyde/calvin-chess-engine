package com.kelseyde.calvin.api.uci;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.NotationUtils;
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

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final MoveGenerator moveGenerator = new MoveGenerator();

    private UCICommandLineRunner runner;
    private Bot bot;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
        bot = new CalvinBot();
        runner = new UCICommandLineRunner(bot);
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
    public void testStartWithWhite() throws InterruptedException {
        runner.handleCommand("uci");
        runner.handleCommand("ucinewgame");
        runner.handleCommand("isready");
        runner.handleCommand("position startpos");
        runner.handleCommand("go movetime 1000");
        Thread.sleep(1010);
        List<String> lines = Arrays.asList(outputStream.toString().split("\n"));
        Assertions.assertEquals(3, lines.size());
        String moveResponse = lines.get(lines.size() - 1).split(" ")[1].trim();
        Move move = NotationUtils.fromCombinedNotation(moveResponse);

        Board testBoard = new Board();
        List<Move> legalMoves = Arrays.asList(moveGenerator.generateLegalMoves(testBoard, false));
        Assertions.assertTrue(legalMoves.stream().anyMatch(move::matches));
    }

    @Test
    public void testRespondWithBlack() throws InterruptedException {
        runner.handleCommand("uci");
        runner.handleCommand("ucinewgame");
        runner.handleCommand("isready");
        runner.handleCommand("position startpos moves e2e4");
        runner.handleCommand("go movetime 1000");

        String output = outputStream.toString();
        List<String> lines = Arrays.asList(output.split("\n"));
        Assertions.assertEquals(3, lines.size());
        String moveResponse = lines.get(lines.size() - 1).split(" ")[1].trim();
        Move move = NotationUtils.fromCombinedNotation(moveResponse);

        Board testBoard = new Board();
        testBoard.makeMove(NotationUtils.fromNotation("e2", "e4", PieceType.PAWN));
        List<Move> legalMoves = Arrays.asList(moveGenerator.generateLegalMoves(testBoard, false));
        Assertions.assertTrue(legalMoves.stream().anyMatch(move::matches));
    }


}