package com.kelseyde.calvin.uci.command;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.UCICommand;
import com.kelseyde.calvin.utils.notation.FEN;

import java.util.List;

public record PositionCommand(String fen, List<Move> moves) {

    public static PositionCommand parse(UCICommand command) {
        String fen;
        if (command.contains("startpos")) {
            fen = FEN.STARTPOS;
        } else if (command.contains("fen")) {
            fen = String.join(" ", command.getStrings("fen", true));
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
