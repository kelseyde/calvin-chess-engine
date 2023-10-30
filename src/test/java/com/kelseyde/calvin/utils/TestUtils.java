package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.notation.NotationUtils;

import java.util.List;
import java.util.Optional;

public class TestUtils {

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

        board.getGameState().setCastlingRights(0b0000);

        board.recalculatePieces();

        return board;
    }

    public static Move getLegalMove(Board board, String startSquare, String endSquare) {
        Move move = NotationUtils.fromNotation(startSquare, endSquare);
        List<Move> legalMoves = MOVE_GENERATOR.generateMoves(board, false);
        Optional<Move> legalMove = legalMoves.stream()
                .filter(m -> m.matches(move))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s%s", startSquare, endSquare));
        }
        return legalMove.get();
    }

    public static Move getLegalMove(Board board, Move move) {
        List<Move> legalMoves = MOVE_GENERATOR.generateMoves(board, false);
        Optional<Move> legalMove = legalMoves.stream()
                .filter(m -> m.matches(move))
                .findAny();
        if (legalMove.isEmpty()) {
            throw new IllegalMoveException(String.format("Illegal move! %s", move));
        }
        return legalMove.get();
    }

}
