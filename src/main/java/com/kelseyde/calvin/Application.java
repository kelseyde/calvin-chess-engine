package com.kelseyde.calvin;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.engine.EngineInitializer;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.*;

/**
 * Entrypoint for the Calvin chess engine. Calvin communicates using the Universal Chess Interface protocol (UCI).
 * This adapter acts as a UCI interface which translates the incoming commands to instructions for the {@link Engine},
 * which is responsible for actually playing the game of chess.
 * @see <a href="https://www.chessprogramming.org/UCI">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Application {

    static final Engine ENGINE = EngineInitializer.loadEngine();
    static final Scanner SCANNER = new Scanner(System.in);
    static final String[] POSITION_LABELS = new String[] { "position", "fen", "moves" };
    static final String[] GO_LABELS = new String[] { "go", "movetime", "wtime", "btime", "winc", "binc", "movestogo" };
    static final String[] SETOPTION_LABELS = new String[] { "setoption", "name", "value" };

    public static void main(String[] args) {
        String command = "";
        while (!command.equals("quit")) {
            command = readCommand();
            if (!command.isEmpty()) {
                String commandType = command.trim().toLowerCase().split(" ")[0];
                switch (commandType) {
                    case "uci" ->         handleUci();
                    case "isready" ->     handleIsReady();
                    case "setoption" ->   handleSetOption(command);
                    case "ucinewgame" ->  handleNewGame();
                    case "position" ->    handlePosition(command);
                    case "go" ->          handleGo(command);
                    case "stop" ->        handleStop();
                    case "quit" ->        handleQuit();
                    default ->            write("Unrecognised command: " + command);
                }
            }
        }
    }

    private static void handleUci() {
        write("id name Calvin");
        write("id author Dan Kelsey");
        EngineConfig config = ENGINE.getConfig();
        write(String.format("option name Hash type spin default %s min %s max %s",
                config.getDefaultHashSizeMb(), config.getMinHashSizeMb(), config.getMaxHashSizeMb()));
        write(String.format("option name Threads type spin default %s min %s max %s",
                config.getDefaultThreadCount(), config.getMinThreadCount(), config.getMaxThreadCount()));
        write(String.format("option name OwnBook type check default %s", config.isOwnBookEnabled()));
        write("uciok");
    }

    private static void handleIsReady() {
        write("readyok");
    }

    private static void handleSetOption(String command) {
        String optionType = getLabelString(command, "name", SETOPTION_LABELS, "");
        switch (optionType) {
            case "Hash":     setHashSize(command); break;
            case "Threads":  setThreadCount(command); break;
            case "OwnBook":  setOwnBook(command); break;
            default:         write("unrecognised option name " + optionType);
        }
    }

    private static void handleNewGame() {
        ENGINE.gameOver();
        ENGINE.newGame();
    }

    private static void handlePosition(String command) {
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
            if (!moveString.isBlank()) {
                moves.addAll(new ArrayList<>(Arrays.stream(moveString.split(" "))
                        .map(Notation::fromCombinedNotation)
                        .toList()));
            }
            else {
                write("Invalid position command, 'moves' has no content.");
                return;
            }
        }
        ENGINE.setPosition(fen, moves);

    }

    private static void handleGo(String command) {
        if (command.contains("movetime")) {
            int moveTimeMs = getLabelInt(command, "movetime", GO_LABELS);
            ENGINE.think(moveTimeMs, Application::writeMove);
        }
        else {
            int timeWhiteMs = getLabelInt(command, "wtime", GO_LABELS);
            int timeBlackMs = getLabelInt(command, "btime", GO_LABELS);
            int incrementWhiteMs = getLabelInt(command, "winc", GO_LABELS);
            int incrementBlackMs = getLabelInt(command, "binc", GO_LABELS);
            ENGINE.think(timeWhiteMs, timeBlackMs, incrementWhiteMs, incrementBlackMs, Application::writeMove);
        }
    }

    private static void handleStop() {
        ENGINE.stopThinking();
    }

    private static void handleQuit() {
        ENGINE.gameOver();
        System.exit(0);
    }

    private static void writeMove(Move move) {
        String notation = Notation.toNotation(move);
        write("bestmove " + notation);
    }

    private static void setHashSize(String command) {
        int hashSizeMb = getLabelInt(command, "value", SETOPTION_LABELS);
        int minHashSizeMb = ENGINE.getConfig().getMinHashSizeMb();
        int maxHashSizeMb = ENGINE.getConfig().getMaxHashSizeMb();
        if (hashSizeMb >= minHashSizeMb && hashSizeMb <= maxHashSizeMb) {
            ENGINE.setHashSize(hashSizeMb);
            write("info string Hash " + hashSizeMb);
        } else {
            write(String.format("hash size %s not in valid range %s - %s", hashSizeMb, minHashSizeMb, maxHashSizeMb));
        }
    }

    private static void setThreadCount(String command) {
        int threadCount = getLabelInt(command, "value", SETOPTION_LABELS);
        int minThreadCount = ENGINE.getConfig().getMinThreadCount();
        int maxThreadCount = ENGINE.getConfig().getMaxThreadCount();
        if (threadCount >= minThreadCount && threadCount <= maxThreadCount) {
            ENGINE.setThreadCount(threadCount);
            write("info string Threads " + threadCount);
        } else {
            write(String.format("thread count %s not in valid range %s - %s", threadCount, minThreadCount, maxThreadCount));
        }
    }

    private static void setOwnBook(String command) {
        boolean ownBookEnabled = Boolean.parseBoolean(getLabelString(command, "value", SETOPTION_LABELS, "false"));
        ENGINE.setOwnBookEnabled(ownBookEnabled);
        write("info string OwnBook " + ownBookEnabled);
    }

    private static String getLabelString(String command, String label, String[] allLabels, String defaultValue) {
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

    private static int getLabelInt(String command, String label, String[] allLabels) {
        String valueString = getLabelString(command, label, allLabels, String.valueOf(0));
        return !valueString.isBlank() ? Integer.parseInt(valueString) : 0;
    }

    public static String readCommand() {
        try {
            String line = SCANNER.nextLine();
            if (!line.isBlank()) {
                return line;
            }
        } catch (NoSuchElementException e) {
            // do nothing
        }
        return "";
    }

    private static void write(String output) {
        System.out.println(output);
    }

}
