package com.kelseyde.calvin.uci;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int labelIndex = Arrays.asList(args).indexOf(label);
        if (labelIndex == -1) {
            if (panic) throw new IllegalArgumentException("missing required label " + label);
            return new ArrayList<>();
        }
        int valueIndex = labelIndex + 1;
        if (valueIndex >= args.length) {
            if (panic) throw new IllegalArgumentException("missing value for label " + label);
            return new ArrayList<>();
        }
        return Arrays.asList(args).subList(valueIndex, args.length);
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

}
