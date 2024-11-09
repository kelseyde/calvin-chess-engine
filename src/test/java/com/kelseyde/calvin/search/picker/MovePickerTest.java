package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.utils.Bench;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovePickerTest {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Test
    @Disabled
    public void testMoveOrder() {

        List<MoveType> expectedOrder = List.of(MoveType.TT_MOVE, MoveType.GOOD_NOISY, MoveType.KILLER, MoveType.QUIET, MoveType.BAD_NOISY);

        SearchHistory history = new SearchHistory(new EngineConfig());
        List<String> fens = Bench.FENS;
        for (String fen : fens) {
            System.out.println(fen);
            Board board = FEN.toBoard(fen);
            SearchStack ss = new SearchStack();
            List<Move> legalMoves = moveGenerator.generateMoves(board);

            Move ttMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
            Move killer1 = randomQuiet(board, legalMoves);
            Move killer2 = randomQuiet(board, legalMoves);
            history.getKillerTable().add(0, killer1);
            history.getKillerTable().add(0, killer2);

            MovePicker picker = new MovePicker(moveGenerator, ss, history, board, 0, ttMove, false);

            int maxIndex = -1;
            List<Move> tried = new ArrayList<>();
            while (true) {
                ScoredMove move = picker.next();
                if (move == null) break;  // No more moves to pick

                // Get the move type from the current move
                MoveType currentMoveType = move.moveType();

                // Ensure the move type is in the expected order
                int currentIndex = expectedOrder.indexOf(currentMoveType);
                Assertions.assertTrue(currentIndex >= 0, "Unknown move type encountered.");
                Assertions.assertTrue(currentIndex >= maxIndex, "Move types are out of order.");

                // Update the highest index seen
                maxIndex = currentIndex;
                tried.add(move.move());
            }
            if (tried.size() != legalMoves.size()) {
                Assertions.fail("Tried moves do not match legal moves.");
            }
        }

    }

    @Test
    @Disabled
    public void testMoveOrderQsearch() {

        List<MoveType> expectedOrder = List.of(MoveType.TT_MOVE, MoveType.GOOD_NOISY, MoveType.BAD_NOISY, MoveType.QUIET);

        SearchHistory history = new SearchHistory(new EngineConfig());
        List<String> fens = Bench.FENS;
        for (String fen : fens) {
            System.out.println(fen);
            Board board = FEN.toBoard(fen);
            SearchStack ss = new SearchStack();
            List<Move> legalMoves = moveGenerator.generateMoves(board);

            Move ttMove = legalMoves.get(new Random().nextInt(legalMoves.size()));

            QuiescentMovePicker picker = new QuiescentMovePicker(moveGenerator, ss, history, board, 0, ttMove, false);
            picker.setFilter(MoveGenerator.MoveFilter.NOISY);

            int maxIndex = -1;
            List<Move> tried = new ArrayList<>();
            while (true) {
                ScoredMove move = picker.next();
                if (move == null) break;  // No more moves to pick

                // Get the move type from the current move
                MoveType currentMoveType = move.moveType();

                // Ensure the move type is in the expected order
                int currentIndex = expectedOrder.indexOf(currentMoveType);
                Assertions.assertTrue(currentIndex >= 0, "Unknown move type encountered.");
                Assertions.assertTrue(currentIndex >= maxIndex, "Move types are out of order.");

                // Update the highest index seen
                maxIndex = currentIndex;
                tried.add(move.move());
            }
        }

    }

    @Test
    public void testDebugSingle() {

        List<MoveType> expectedOrder = List.of(MoveType.TT_MOVE, MoveType.GOOD_NOISY, MoveType.KILLER, MoveType.QUIET, MoveType.BAD_NOISY);

        SearchHistory history = new SearchHistory(new EngineConfig());

        String fen = "8/8/1p2k1p1/3p3p/1p1P1P1P/1P2PK2/8/8 w - - 3 54";
        Board board = FEN.toBoard(fen);

        Move ttMove = Move.fromUCI("f3e2");
        Move killer1 = Move.fromUCI("f4f5");
        Move killer2 = Move.fromUCI("f3e2");

        history.getKillerTable().add(0, killer1);
        history.getKillerTable().add(0, killer2);

        SearchStack ss = new SearchStack();
        MovePicker picker = new MovePicker(moveGenerator, ss, history, board, 0, ttMove, false);
        List<Move> legalMoves = moveGenerator.generateMoves(board);

        int maxIndex = -1;
        List<Move> tried = new ArrayList<>();
        while (true) {
            ScoredMove move = picker.next();
            if (move == null) break;  // No more moves to pick

            // Get the move type from the current move
            MoveType currentMoveType = move.moveType();

            // Ensure the move type is in the expected order
            int currentIndex = expectedOrder.indexOf(currentMoveType);
            Assertions.assertTrue(currentIndex >= 0, "Unknown move type encountered.");
            Assertions.assertTrue(currentIndex >= maxIndex, "Move types are out of order.");

            // Update the highest index seen
            maxIndex = currentIndex;
            tried.add(move.move());
        }
        if (tried.size() != legalMoves.size()) {
            Assertions.fail("Tried moves do not match legal moves.");
        }


    }

    @Test
    public void testMovegenFilters() {

        for (String fen : Bench.FENS) {
            Board board = FEN.toBoard(fen);
            Assertions.assertEquals(moveGenerator.generateMoves(board).size(),
                    moveGenerator.generateMoves(board, MoveGenerator.MoveFilter.NOISY).size() +
                    moveGenerator.generateMoves(board, MoveGenerator.MoveFilter.QUIET).size());
        }

    }

    @Test
    public void testInCheckDoesNotGenerateMovesTwice() {

        String fen = "rnbqkbnr/1p2pppp/p2p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 0 1";
        Board board = FEN.toBoard(fen);

        MovePicker picker = new MovePicker(moveGenerator, new SearchStack(), new SearchHistory(new EngineConfig()), board, 0, null, true);

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