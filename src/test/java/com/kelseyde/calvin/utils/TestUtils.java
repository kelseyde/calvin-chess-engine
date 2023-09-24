package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.PositionEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.engine.MinimaxSearch;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestUtils {

    public static final List<PositionEvaluator> ALL_EVALUATORS = List.of(new MaterialEvaluator());

    public static final Search CURRENT_ENGINE = new MinimaxSearch(ALL_EVALUATORS);

    private static final MoveGenerator MOVE_GENERATOR = new MoveGenerator();

    public static Board emptyBoard() {
        Board board = new Board();
        board.setWhitePawns(0L);
        board.setWhiteKnights(0L);
        board.setWhiteBishops(0L);
        board.setWhiteRooks(0L);
        board.setWhiteQueens(0L);
        board.setWhiteKing(0L);

        board.setBlackPawns(0L);
        board.setBlackKnights(0L);
        board.setBlackBishops(0L);
        board.setBlackRooks(0L);
        board.setBlackQueens(0L);
        board.setBlackKing(0L);

        board.getCurrentGameState().setCastlingRights(0b0000);

        board.recalculatePieces();

        return board;
    }

    public static Move getLegalMove(Board board, String startSquare, String endSquare) {
        Move move = NotationUtils.fromNotation(startSquare, endSquare);
        Move[] legalMoves = MOVE_GENERATOR.generateLegalMoves(board);
        Optional<Move> legalMove = Arrays.stream(legalMoves)
                .filter(m -> m.matches(move))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s%s", startSquare, endSquare));
        }
        return legalMove.get();
    }

}
