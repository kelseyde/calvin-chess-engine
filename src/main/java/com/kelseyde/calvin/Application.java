package com.kelseyde.calvin;

import com.kelseyde.calvin.uci.UCI;

public class Application {

    public static void main(String[] args) {
        System.out.println("Args: " + String.join(", ", args));
        UCI.run(args);
    }

}
