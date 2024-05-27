package com.kelseyde.calvin.generation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.picker.MovePicker;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovePickerTest {

    private MovePicker movePicker;
    private MoveGenerator moveGenerator = new MoveGenerator();
    private MoveOrderer moveOrderer = new MoveOrderer();

    @Test
    public void testPickHashMoveDoesNotGenerateAnyMoves() {

        Board board = new Board();

        Move previousBestMove = Notation.fromCombinedNotation("e2e4");

        movePicker = new MovePicker(moveGenerator, moveOrderer, board, 1);

        movePicker.setBestMove(previousBestMove);

        Move move = movePicker.pickNextMove();

        Assertions.assertNull(movePicker.getMoves());

    }

}