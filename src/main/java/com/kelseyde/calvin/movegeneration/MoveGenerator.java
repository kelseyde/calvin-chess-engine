package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardConstants;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.generator.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evaluates the effect of a {@link Move} on a game. First checks if the move is legal. Then, checks if executing the
 * move results in the game ending, either due to checkmate or one of the draw conditions.
 */
@Slf4j
public class MoveGenerator {

    private static final PawnMoveGenerator PAWN_MOVE_GENERATOR = new PawnMoveGenerator();
    private static final KnightMoveGenerator KNIGHT_MOVE_GENERATOR = new KnightMoveGenerator();
    private static final BishopMoveGenerator BISHOP_MOVE_GENERATOR = new BishopMoveGenerator();
    private static final RookMoveGenerator ROOK_MOVE_GENERATOR = new RookMoveGenerator();
    private static final QueenMoveGenerator QUEEN_MOVE_GENERATOR = new QueenMoveGenerator();
    private static final KingMoveGenerator KING_MOVE_GENERATOR = new KingMoveGenerator();

    private static final Set<PseudoLegalMoveGenerator> PSEUDO_LEGAL_MOVE_GENERATORS = Set.of(
        PAWN_MOVE_GENERATOR, KNIGHT_MOVE_GENERATOR, BISHOP_MOVE_GENERATOR, ROOK_MOVE_GENERATOR, QUEEN_MOVE_GENERATOR, KING_MOVE_GENERATOR
    );

    public Set<Move> generateLegalMoves(Board board) {
        return generatePseudoLegalMoves(board).stream()
                .filter(pseudoLegalMove -> isFullyLegal(board, pseudoLegalMove))
                .collect(Collectors.toSet());
    }

    private boolean isFullyLegal(Board board, Move move) {

        board.makeMove(move);

        long kingMask = switch (move.getMoveType()) {
            default -> board.isWhiteToMove() ? board.getBlackKing() : board.getWhiteKing();
            case KINGSIDE_CASTLE -> board.isWhiteToMove() ? BitBoardConstants.BLACK_KINGSIDE_CASTLE_SAFE_MASK : BitBoardConstants.WHITE_KINGSIDE_CASTLE_SAFE_MASK;
            case QUEENSIDE_CASTLE -> board.isWhiteToMove() ? BitBoardConstants.BLACK_QUEENSIDE_CASTLE_SAFE_MASK : BitBoardConstants.WHITE_QUEENSIDE_CASTLE_SAFE_MASK;
        };

        // TODO perhaps can be done without a stream
        long attackedSquares = 0L;
        for (Move opponentMove : generatePseudoLegalMoves(board)) {
            attackedSquares |= (1L << opponentMove.getEndSquare());
        }
        boolean isLegalMove = (kingMask & attackedSquares) == 0;

        board.unmakeMove();

        return isLegalMove;

    }

    public boolean isCheck(Board board) {
        board.setWhiteToMove(!board.isWhiteToMove());
        int enPassantFile = board.getCurrentGameState().getEnPassantFile();
        board.getCurrentGameState().setEnPassantFile(-1);
        int opponentKingSquare = board.isWhiteToMove() ? BitBoardUtils.scanForward(board.getBlackKing()) : BitBoardUtils.scanForward(board.getWhiteKing());
        Set<Integer> attackingSquares = generatePseudoLegalMoves(board).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        board.setWhiteToMove(!board.isWhiteToMove());
        board.getCurrentGameState().setEnPassantFile(enPassantFile);
        return attackingSquares.contains(opponentKingSquare);
    }

    private Set<Move> generatePseudoLegalMoves(Board board) {
        return PSEUDO_LEGAL_MOVE_GENERATORS.stream()
                .flatMap(generator -> generator.generatePseudoLegalMoves(board).stream())
                .collect(Collectors.toSet());
    }

}
