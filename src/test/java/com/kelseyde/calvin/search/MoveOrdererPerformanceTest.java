package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Disabled
public class MoveOrdererPerformanceTest {

    // kiwipete
    private final String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();

    @Test
    public void tesSortingAlgorithms() {

        Board board1 = FEN.toBoard(fen);

        Instant start = Instant.now();
        IntStream.range(0, 100000).forEach(i -> {
            List<Move> moves = moveGenerator.generateMoves(board1, false);
            moves.sort(Comparator.comparingInt(move -> -moveOrderer.scoreMove(board1, move, null, true, 0)));
        });
        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

    }

}
