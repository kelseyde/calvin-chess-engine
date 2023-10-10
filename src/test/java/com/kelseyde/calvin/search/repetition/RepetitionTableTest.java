package com.kelseyde.calvin.search.repetition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RepetitionTableTest {

    @Test
    public void testCorrectlyIdentifyRepetition() {

        Board board = new Board();
        RepetitionTable table = new RepetitionTable(board);
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("g1", "f3"));
        table.push(board.getGameState().getZobristKey());
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("g8", "f6"));
        table.push(board.getGameState().getZobristKey());
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("f3", "g1"));
        table.push(board.getGameState().getZobristKey());
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("f6", "g8"));
        table.push(board.getGameState().getZobristKey());
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("g1", "f3"));
        table.push(board.getGameState().getZobristKey());
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("g8", "f6"));
        table.push(board.getGameState().getZobristKey());
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("f3", "g1"));
        table.push(board.getGameState().getZobristKey());
        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        board.makeMove(NotationUtils.fromNotation("f6", "g8"));
        table.push(board.getGameState().getZobristKey());

        Assertions.assertTrue(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

        table.pop(board.getGameState().getZobristKey());

        Assertions.assertFalse(table.isThreefoldRepetition(board.getGameState().getZobristKey()));

    }

}