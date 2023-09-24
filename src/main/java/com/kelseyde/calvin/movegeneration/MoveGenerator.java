package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardConstants;
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
        return PSEUDO_LEGAL_MOVE_GENERATORS.stream()
                .flatMap(generator -> generator.generatePseudoLegalMoves(board).stream())
                .filter(pseudoLegalMove -> !isKingCapturable(board, pseudoLegalMove))
                .collect(Collectors.toSet());
    }

    /**
     * Make a move, and then check if the friendly king can be captured on the next move (or stepped through a checked
     * square during castling).
     */
    public boolean isKingCapturable(Board board, Move move) {
        board.makeMove(move);
        long kingMask = switch (move.getMoveType()) {
            default -> board.isWhiteToMove() ? board.getBlackKing() : board.getWhiteKing();
            case KINGSIDE_CASTLE -> board.isWhiteToMove() ? BitBoardConstants.BLACK_KINGSIDE_CASTLE_SAFE_MASK : BitBoardConstants.WHITE_KINGSIDE_CASTLE_SAFE_MASK;
            case QUEENSIDE_CASTLE -> board.isWhiteToMove() ? BitBoardConstants.BLACK_QUEENSIDE_CASTLE_SAFE_MASK : BitBoardConstants.WHITE_QUEENSIDE_CASTLE_SAFE_MASK;
        };
        boolean isKingCapturable = isCheck(board, !board.isWhiteToMove(), kingMask);
        board.unmakeMove();
        return isKingCapturable;
    }

    /**
     * Makes a move, and then calculates whether that moves results in a check for the side making the move
     */
    public boolean isCheck(Board board, Move move) {
        board.makeMove(move);
        boolean isCheck = isCheck(board, board.isWhiteToMove());
        board.unmakeMove();
        return isCheck;
    }


    public boolean isCheck(Board board, boolean isWhite) {
        long kingMask = isWhite ? board.getWhiteKing() : board.getBlackKing();
        return isCheck(board, isWhite, kingMask);
    }

    private boolean isCheck(Board board, boolean isWhite, long kingMask) {
        long attackMask = PAWN_MOVE_GENERATOR.generateAttackMask(board, !isWhite) |
                KNIGHT_MOVE_GENERATOR.generateAttackMask(board, !isWhite) |
                BISHOP_MOVE_GENERATOR.generateAttackMask(board, !isWhite) |
                ROOK_MOVE_GENERATOR.generateAttackMask(board, !isWhite) |
                QUEEN_MOVE_GENERATOR.generateAttackMask(board, !isWhite) |
                KING_MOVE_GENERATOR.generateAttackMask(board, !isWhite);
        return (attackMask & kingMask) != 0;
    }


}
