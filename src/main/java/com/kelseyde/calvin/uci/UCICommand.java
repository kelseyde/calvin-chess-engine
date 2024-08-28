package com.kelseyde.calvin.uci;

import java.util.Arrays;

public record UCICommand(UCICommandType type, String[] args) {

    public static UCICommand parse(String input) {
        String[] args = input.trim().split(" ");
        UCICommandType type = UCICommandType.parse(args[0]);
        return new UCICommand(type, args);
    }

    public void execute() {
        type.consumer.accept(this);
    }

    public boolean contains(String label) {
        return Arrays.asList(args).contains(label);
    }

}
