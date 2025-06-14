package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.picker.ScoredMove;
import com.kelseyde.calvin.search.picker.StandardMovePicker;
import com.kelseyde.calvin.utils.Bench;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovePickerTest {

    @Test
    public void testMovegenFilters() {

        for (String fen : Bench.FENS) {
            Board board = FEN.parse(fen).toBoard();
            Assertions.assertEquals(TestUtils.MOVEGEN.generateMoves(board).size(),
                            TestUtils.MOVEGEN.generateMoves(board, MoveGenerator.MoveFilter.NOISY).size() +
                            TestUtils.MOVEGEN.generateMoves(board, MoveGenerator.MoveFilter.QUIET).size());
        }

    }

    @Test
    public void testInCheckDoesNotGenerateMovesTwice() {

        String fen = "rnbqkbnr/1p2pppp/p2p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 0 1";
        Board board = FEN.parse(fen).toBoard();

        SearchStack ss = new SearchStack();
        StandardMovePicker picker = new StandardMovePicker(TestUtils.CONFIG, TestUtils.MOVEGEN, ss, new SearchHistory(TestUtils.CONFIG, ss), board, 0, null, true);

        List<ScoredMove> moves = new ArrayList<>();
        while (true) {
            ScoredMove move = picker.next();
            if (move == null) break;
            moves.add(move);
        }

        Assertions.assertEquals(5, moves.size());
    }

    private Move randomQuiet(Board board, List<Move> legalMoves) {

        int tried = 0;
        while (tried < legalMoves.size()) {
            Move move = legalMoves.get(new Random().nextInt(legalMoves.size()));
            if (board.isQuiet(move)) {
                return move;
            }
            tried++;
        }

        return null;

    }

}