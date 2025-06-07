package com.kelseyde.calvin.uci;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.notation.FEN;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record UCICommand(UCICommandType type, String[] args) {

    public static UCICommand parse(String input) {
        String[] args = input.trim().split(" ");
        UCICommandType type = UCICommandType.parse(args[0]);
        return new UCICommand(type, args);
    }

    public String getString(String label, String defaultValue, boolean panic) {
        int labelIndex = Arrays.asList(args).indexOf(label);
        if (labelIndex == -1) {
            if (panic) throw new IllegalArgumentException("missing required label " + label);
            return defaultValue;
        }
        int valueIndex = labelIndex + 1;
        if (valueIndex >= args.length) {
            if (panic) throw new IllegalArgumentException("missing value for label " + label);
            return defaultValue;
        }
        return args[valueIndex];
    }

    public List<String> getStrings(String label, boolean panic) {
        int endIndex = args.length;
        return getStrings(label, endIndex, panic);
    }

    public List<String> getStrings(String label, String endLabelExclusive, boolean panic) {
        int endIndex = Arrays.asList(args).indexOf(endLabelExclusive);
        if (endIndex == -1) {
            endIndex = args.length;
        }
        return getStrings(label, endIndex, panic);
    }

    private List<String> getStrings(String label, int endIndex, boolean panic) {
        int labelIndex = Arrays.asList(args).indexOf(label);
        if (labelIndex == -1) {
            if (panic) throw new IllegalArgumentException("missing required label " + label);
            return new ArrayList<>();
        }
        int valueIndex = labelIndex + 1;
        if (valueIndex >= args.length || valueIndex >= endIndex) {
            if (panic) throw new IllegalArgumentException("missing value for label " + label);
            return new ArrayList<>();
        }
        return Arrays.asList(args).subList(valueIndex, Math.min(endIndex, args.length));
    }

    public int getInt(String label, int defaultValue, boolean panic) {
        String valueString = getString(label, String.valueOf(defaultValue), panic);
        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            if (panic) throw new IllegalArgumentException("invalid value for label " + label + ": " + valueString);
            return defaultValue;
        }
    }

    public boolean getBool(String label, boolean defaultValue, boolean panic) {
        String valueString = getString(label, String.valueOf(defaultValue), panic);
        return Boolean.parseBoolean(valueString);
    }

    public void execute() {
        type.consumer.accept(this);
    }

    public boolean contains(String label) {
        return Arrays.asList(args).contains(label);
    }

    public record GoCommand(int movetime, int wtime, int btime, int winc, int binc, int nodes, int depth, int perft, boolean ponder) {

        public static GoCommand parse(UCICommand command) {
            int movetime =      command.getInt("movetime", Integer.MIN_VALUE, false);
            int wtime =         command.getInt("wtime", Integer.MIN_VALUE, false);
            int btime =         command.getInt("btime", Integer.MIN_VALUE, false);
            int winc =          command.getInt("winc", Integer.MIN_VALUE, false);
            int binc =          command.getInt("binc", Integer.MIN_VALUE, false);
            int nodes =         command.getInt("nodes", Integer.MIN_VALUE, false);
            int depth =         command.getInt("depth", Integer.MIN_VALUE, false);
            int perft =         command.getInt("perft", Integer.MIN_VALUE, false);
            boolean ponder =    command.contains("ponder");
            return new GoCommand(movetime, wtime, btime, winc, binc, nodes, depth, perft, ponder);
        }

        public boolean isPerft() {
            return perft > 0;
        }

        public boolean isMovetime() {
            return movetime > 0;
        }

        public boolean isTimeAndInc() {
            return wtime > Integer.MIN_VALUE && btime > Integer.MIN_VALUE;
        }

    }

    public record PositionCommand(String fen, List<Move> moves) {

        public static PositionCommand parse(UCICommand command) {
            String fen;
            if (command.contains("startpos")) {
                fen = FEN.STARTPOS;
            } else if (command.contains("fen")) {
                fen = String.join(" ", command.getStrings("fen", "moves", true));
            } else {
                UCI.write("info error invalid position command; expecting 'startpos' or 'fen'.");
                fen = FEN.STARTPOS;
            }
            List<Move> moves = command.getStrings("moves", false).stream()
                    .map(Move::fromUCI)
                    .toList();
            return new PositionCommand(fen, moves);
        }

    }

    public record ScoreDataCommand(String inputFile, String outputFile, int softNodes, int hardNodes, int resumeOffset) {

        private static final int DEFAULT_SOFT_NODES = 5000;
        private static final int DEFAULT_HARD_NODES = 100000;
        private static final int DEFAULT_RESUME_OFFSET = 0;

        public static Optional<ScoreDataCommand> parse(UCICommand command) {
            String inputFile = command.getString("input", null, true);
            String outputFile = command.getString("output", null, true);

            if (!Files.exists(Path.of(inputFile))) {
                UCI.write("info error input file " + inputFile + " does not exist");
                return Optional.empty();
            }

            int softNodes = command.getInt("soft", DEFAULT_SOFT_NODES, false);
            int hardNodes = command.getInt("hard", DEFAULT_HARD_NODES, false);
            int resume = command.getInt("resume", DEFAULT_RESUME_OFFSET, false);

            return Optional.of(new ScoreDataCommand(inputFile, outputFile, softNodes, hardNodes, resume));
        }

    }

}
