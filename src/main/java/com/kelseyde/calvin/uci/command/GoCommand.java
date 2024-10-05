package com.kelseyde.calvin.uci.command;

import com.kelseyde.calvin.uci.UCICommand;

public record GoCommand(int movetime, int wtime, int btime, int winc, int binc, int nodes, int depth, int perft,
                        boolean ponder) {

    public static GoCommand parse(UCICommand command) {
        int movetime = command.getInt("movetime", Integer.MIN_VALUE, false);
        int wtime = command.getInt("wtime", Integer.MIN_VALUE, false);
        int btime = command.getInt("btime", Integer.MIN_VALUE, false);
        int winc = command.getInt("winc", Integer.MIN_VALUE, false);
        int binc = command.getInt("binc", Integer.MIN_VALUE, false);
        int nodes = command.getInt("nodes", Integer.MIN_VALUE, false);
        int depth = command.getInt("depth", Integer.MIN_VALUE, false);
        int perft = command.getInt("perft", Integer.MIN_VALUE, false);
        boolean ponder = command.contains("ponder");
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
