package com.kelseyde.calvin.api.uci;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UCICommandLineRunner implements CommandLineRunner {

    private static final Scanner in = new Scanner(System.in);

    private static final String[] POSITION_LABELS = new String[] { "position", "fen", "moves" };
    private static final String[] GO_LABELS = new String[] { "go", "movetime", "wtime", "btime", "winc", "binc", "movestogo" };

    private final ApplicationShutdownManager shutdownManager;

    private final Bot bot = new CalvinBot();

    @Override
    public void run(String... args) {
        String command = "";
        while (!command.equals("quit")) {
            command = readCommand();
            if (StringUtils.hasText(command)) {
                handleCommand(command);
            }
        }
    }

    public void handleCommand(String command) {

        command = command.trim().toLowerCase();
        String commandType = command.split(" ")[0];

        switch (commandType) {
            case "uci" ->         write("uciok");
            case "isready" ->     write("readyok");
            case "ucinewgame" ->  handleNewGame();
            case "position" ->    handlePosition(command);
            case "go" ->          handleGo(command);
            case "stop" ->        handleStop();
            case "quit" ->        handleQuit();
            default ->            write(String.format("Unrecognised command: %s", command));
        }

    }

    private void handleNewGame() {
        bot.newGame();
    }

    /**
     * There are two possible position formats: 'startpos' and 'fen'.
     * e.g. 'position startpos moves e2e4 e7e5'
     * e.g. 'position fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 moves e2e4 e7e5'
     * In both cases the 'moves' appended at the end are optional.
     */
    private void handlePosition(String command) {

        String fen;
        if (command.contains("startpos")) {
            fen = FEN.STARTING_POSITION;
        }
        else if (command.contains("fen")) {
            fen = getLabelString(command, "fen", POSITION_LABELS, "");
        }
        else {
            write("Invalid position command, expecting 'position' or 'fen'.");
            return;
        }
        List<Move> moves = new ArrayList<>();
        if (command.contains("moves")) {
            String moveString = getLabelString(command, "moves", POSITION_LABELS, "");
            if (StringUtils.hasText(moveString)) {
                moves.addAll(new ArrayList<>(Arrays.stream(moveString.split(" "))
                        .map(NotationUtils::fromCombinedNotation)
                        .toList()));
            }
            else {
                write("Invalid position command, 'moves' has no content.");
                return;
            }
        }
        bot.setPosition(fen, moves);

    }

    private void handleGo(String command) {

        if (command.contains("movetime")) {
            int moveTimeMs = getLabelInt(command, "movetime", GO_LABELS);
            bot.think(moveTimeMs, this::writeMove);
        }
        else {
            int timeWhiteMs = getLabelInt(command, "wtime", GO_LABELS);
            int timeBlackMs = getLabelInt(command, "btime", GO_LABELS);
            int incrementWhiteMs = getLabelInt(command, "winc", GO_LABELS);
            int incrementBlackMs = getLabelInt(command, "binc", GO_LABELS);

            int thinkTimeMs = bot.chooseThinkTime(timeWhiteMs, timeBlackMs, incrementWhiteMs, incrementBlackMs);
            bot.think(thinkTimeMs, this::writeMove);
        }

    }

    private void handleStop() {
        bot.stopThinking();
    }

    private void handleQuit() {
        bot.gameOver();
        shutdownManager.initiateShutdown(0);
    }

    private void writeMove(Move move) {
        String notation = NotationUtils.toNotation(move);
        write("bestmove " + notation);
    }

    private String getLabelString(String command, String label, String[] allLabels, String defaultValue) {
        if (command.contains(label)) {
            int valueStart = command.indexOf(label) + label.length();
            int valueEnd = command.length();
            for (String otherLabel : allLabels) {
                if (!otherLabel.equals(label) && command.contains(label)) {
                    int otherValueStart = command.indexOf(otherLabel);
                    if (otherValueStart > valueStart && otherValueStart < valueEnd) {
                        valueEnd = otherValueStart;
                    }
                }
            }
            return command.substring(valueStart, valueEnd).trim();
        }
        return defaultValue;
    }

    private int getLabelInt(String command, String label, String[] allLabels) {
        String valueString = getLabelString(command, label, allLabels, String.valueOf(0));
        return StringUtils.hasText(valueString) ? Integer.parseInt(valueString) : 0;
    }

    private String readCommand() {
        try {
            String line = in.nextLine();
            if (StringUtils.hasText(line)) {
                return line;
            }
        } catch (NoSuchElementException e) {
            // do nothing
        }
        return "";
    }

    private void write(String output) {
        System.out.println(output);
    }

}
