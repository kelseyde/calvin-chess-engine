package com.kelseyde.calvin.generation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.picker.MovePicker;
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

        movePicker.setPreviousBestMove(previousBestMove);

        Move move = movePicker.pickNextMove();

        Assertions.assertNull(movePicker.getMoves());

    }

    @Test
    @Disabled
    public void testAll() throws IOException {

        List<Board> positions = TestUtils.loadFens().stream()
                .map(FEN::toBoard)
                .toList()
                .subList(0, 10000);

        for (Board board : positions) {
            List<Move> moves = moveGenerator.generateMoves(board);

            List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, null, 0);

            movePicker = new MovePicker(moveGenerator, moveOrderer, board, 0);
            List<Move> pickedMoves = new ArrayList<>();
            while (true) {
                Move move = movePicker.pickNextMove();
                if (move == null) break;
                pickedMoves.add(move);
            }

            Assertions.assertEquals(orderedMoves.size(), pickedMoves.size());
            Assertions.assertEquals(orderedMoves, pickedMoves);
        }


    }

}