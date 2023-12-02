package com.kelseyde.calvin.opening;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineInitializer;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpeningBookTest {

    @Test
    public void testOpeningBook() {

        OpeningBook book = EngineInitializer.loadDefaultOpeningBook();
        Assertions.assertNotNull(book);

        Board board = new Board();
        Assertions.assertTrue(book.hasBookMove(board.getGameState().getZobristKey()));


    }

}