package com.kelseyde.calvin.utils.notation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PGN {

    public static String toPGN(Board board) {

        List<Move> moves = Arrays.asList(board.getMoves());
        Collections.reverse(moves);
        Board boardCopy = new Board();

        StringBuilder pgn = new StringBuilder();
        pgn.append("[Event \"?\"]\n");
        pgn.append("[Site \"?\"]\n");
        pgn.append("[Date \"????.??.??\"]\n");
        pgn.append("[Round \"?\"]\n");
        pgn.append("[White \"?\"]\n");
        pgn.append("[Black \"?\"]\n");
        pgn.append("[Result \"*\"]\n");

        for (int plyCount = 0; plyCount < moves.size(); plyCount++) {
            Move move = moves.get(plyCount);
            String moveString = SAN.fromMove(move, boardCopy);
            if (plyCount % 2 == 0) {
                pgn.append((plyCount / 2 + 1)).append(". ");
            }
            pgn.append(moveString).append(" ");
            boardCopy.makeMove(move);
        }
        return pgn.toString();

    }

}
