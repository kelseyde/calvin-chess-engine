package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MovePickerTest {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Test
    public void testInCheckDoesNotGenerateMovesTwice() {

        String fen = "rnbqkbnr/1p2pppp/p2p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 0 1";
        Board board = FEN.toBoard(fen);

        MovePicker picker = new MovePicker(moveGenerator, new SearchStack(), new SearchHistory(), board, 0, null, true);

        List<Move> moves = new ArrayList<>();
        while (true) {
            Move move = picker.pickNextMove();
            if (move == null) break;
            moves.add(move);
        }

        Assertions.assertEquals(5, moves.size());
    }

    @Test
    public void testWithKillersAndBadNoisies() {

        String kiwipete = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";
        Board board = FEN.toBoard(kiwipete);

        SearchStack ss = new SearchStack();
        SearchHistory history = new SearchHistory();
        int ply = 0;
        history.getKillerTable().add(ply, Move.fromUCI("g2g4", Move.PAWN_DOUBLE_MOVE_FLAG));

        MovePicker picker = new MovePicker(moveGenerator, ss, history, board, ply, null, false);

        List<Move> moves = new ArrayList<>();
        while (true) {
            Move move = picker.pickNextMove();
            if (move == null) break;
            moves.add(move);
        }

        Assertions.assertEquals(48, moves.size());
        Assertions.assertEquals(48, moveGenerator.generateMoves(board).size());

    }

    @Test
    public void testDontTryAlreadyTriedKiller() {

        String fen = "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d6 0 1";
        Board board = FEN.toBoard(fen);

        SearchStack ss = new SearchStack();
        SearchHistory history = new SearchHistory();
        Move ttMove = null;

        history.getKillerTable().add(0, Move.fromUCI("d7d5", Move.PAWN_DOUBLE_MOVE_FLAG));
        history.getKillerTable().add(0, Move.fromUCI("a7a6"));
        history.getKillerTable().add(0, Move.fromUCI("a7a6"));

        MovePicker picker = new MovePicker(moveGenerator, ss, history, board, 0, ttMove, false);
        List<Move> pickerMoves = new ArrayList<>();
        while (true) {
            Move move = picker.pickNextMove();
            if (move == null) break;
            pickerMoves.add(move);
        }

        Assertions.assertEquals(20, pickerMoves.size());


    }

}