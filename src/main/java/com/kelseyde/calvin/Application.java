package com.kelseyde.calvin;

import com.kelseyde.calvin.api.UCICommandLineRunner;

public class Application {

    private static final UCICommandLineRunner runner = new UCICommandLineRunner();

    public static void main(String[] args) {
        String command = "";
        while (!command.equals("quit")) {
            command = runner.readCommand();
            if (!command.isEmpty()) {
                runner.handleCommand(command);
            }
        }
    }

}
