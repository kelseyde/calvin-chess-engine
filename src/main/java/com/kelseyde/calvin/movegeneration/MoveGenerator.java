package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.generator.*;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Evaluates the effect of a {@link Move} on a game. First checks if the move is legal. Then, checks if executing the
 * move results in the game ending, either due to checkmate or one of the draw conditions.
 */
@Slf4j
public class MoveGenerator {

    // The maximum number of possible legal moves is apparently 218 in this position:
    //R6R/3Q4/1Q4Q1/4Q3/2Q4Q/Q4Q2/pp1Q4/kBNNK1B1 b - - 0 1
    private static final int MAX_LEGAL_MOVES = 218;

    private static final PawnMoveGenerator PAWN_MOVE_GENERATOR = new PawnMoveGenerator();
    private static final KnightMoveGenerator KNIGHT_MOVE_GENERATOR = new KnightMoveGenerator();
    private static final BishopMoveGenerator BISHOP_MOVE_GENERATOR = new BishopMoveGenerator();
    private static final RookMoveGenerator ROOK_MOVE_GENERATOR = new RookMoveGenerator();
    private static final QueenMoveGenerator QUEEN_MOVE_GENERATOR = new QueenMoveGenerator();
    private static final KingMoveGenerator KING_MOVE_GENERATOR = new KingMoveGenerator();

    private static final Set<PseudoLegalMoveGenerator> PSEUDO_LEGAL_MOVE_GENERATORS = Set.of(
        PAWN_MOVE_GENERATOR, KNIGHT_MOVE_GENERATOR, BISHOP_MOVE_GENERATOR, ROOK_MOVE_GENERATOR, QUEEN_MOVE_GENERATOR, KING_MOVE_GENERATOR
    );

    private long pinMask;

    public Move[] generateLegalMoves(Board board, boolean capturesOnly) {
        return PSEUDO_LEGAL_MOVE_GENERATORS.stream()
                .flatMap(generator -> generator.generatePseudoLegalMoves(board).stream())
                .filter(pseudoLegalMove -> !isKingCapturable(board, pseudoLegalMove))
                .filter(legalMove -> !capturesOnly || filterCapturesOnly(board, legalMove))
                .toArray(Move[]::new);
    }

    public boolean isLegal(Board board, Move move) {

        boolean isWhite = board.isWhiteToMove();

        if (move.getPieceType().equals(PieceType.KING)) {
            long endSquareMask = BitboardUtils.getSquareMask(move.getEndSquare());
            return !isAttacked(board, board.isWhiteToMove(), endSquareMask);

        }
        else if (move.getMoveType().isEnPassant()) {
            board.makeMove(move);
            long kingMask = isWhite ? board.getWhiteKing() : board.getBlackKing();
            boolean isKingCapturable = isAttacked(board, !board.isWhiteToMove(), kingMask);
            board.unmakeMove();
            return !isKingCapturable;

        }
        else if (move.getMoveType().isCastling()) {
            long kingMask = getCastlingKingTravelSquares(move, isWhite);
            return isAttacked(board, board.isWhiteToMove(), kingMask);

        }
        else {
            int startSquare = move.getStartSquare();
            int endSquare = move.getEndSquare();
            int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());
            return !isPinned(startSquare) || BoardUtils.isAligned(kingSquare, startSquare, endSquare);

        }

    }

    /**
     * Make a move, and then check if the friendly king can be captured on the next move (or stepped through a checked
     * square during castling).
     */
    public boolean isKingCapturable(Board board, Move move) {
        board.makeMove(move);
        long kingMask = switch (move.getMoveType()) {
            case KINGSIDE_CASTLE -> board.isWhiteToMove() ? Bits.BLACK_KINGSIDE_CASTLE_SAFE_MASK : Bits.WHITE_KINGSIDE_CASTLE_SAFE_MASK;
            case QUEENSIDE_CASTLE -> board.isWhiteToMove() ? Bits.BLACK_QUEENSIDE_CASTLE_SAFE_MASK : Bits.WHITE_QUEENSIDE_CASTLE_SAFE_MASK;
            default -> board.isWhiteToMove() ? board.getBlackKing() : board.getWhiteKing();
        };
        boolean isKingCapturable = isAttacked(board, !board.isWhiteToMove(), kingMask);
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
        return isAttacked(board, isWhite, kingMask);
    }

    private boolean isAttacked(Board board, boolean isWhite, long kingMask) {
        while (kingMask != 0) {
            int kingSquare = BitboardUtils.getLSB(kingMask);

            long opponentPawns = isWhite ? board.getBlackPawns() : board.getWhitePawns();
            long pawnAttackMask = PAWN_MOVE_GENERATOR.generateAttackMaskFromSquare(board, kingSquare, isWhite);
            if ((pawnAttackMask & opponentPawns) != 0) {
                return true;
            }

            long opponentKnights = isWhite ? board.getBlackKnights() : board.getWhiteKnights();
            long knightAttackMask = KNIGHT_MOVE_GENERATOR.generateAttackMaskFromSquare(board, kingSquare, isWhite);
            if ((knightAttackMask & opponentKnights) != 0) {
                return true;
            }

            long opponentBishops = isWhite ? board.getBlackBishops() : board.getWhiteBishops();
            long bishopAttackMask = BISHOP_MOVE_GENERATOR.generateAttackMaskFromSquare(board, kingSquare, isWhite);
            if ((bishopAttackMask & opponentBishops) != 0) {
                return true;
            }

            long opponentRooks = isWhite ? board.getBlackRooks() : board.getWhiteRooks();
            long rookAttackMask = ROOK_MOVE_GENERATOR.generateAttackMaskFromSquare(board, kingSquare, isWhite);
            if ((rookAttackMask & opponentRooks) != 0) {
                return true;
            }

            long opponentQueens = isWhite ? board.getBlackQueens() : board.getWhiteQueens();
            long queenAttackMask = QUEEN_MOVE_GENERATOR.generateAttackMaskFromSquare(board, kingSquare, isWhite);
            if ((queenAttackMask & opponentQueens) != 0) {
                return true;
            }

            long opponentKing = isWhite ? board.getBlackKing() : board.getWhiteKing();
            long kingAttackMask = KING_MOVE_GENERATOR.generateAttackMaskFromSquare(board, kingSquare, isWhite);
            if ((kingAttackMask & opponentKing) != 0) {
                return true;
            }
            kingMask = BitboardUtils.popLSB(kingMask);
        }
        return false;
    }

    private boolean filterCapturesOnly(Board board, Move move) {
        boolean isWhite = board.isWhiteToMove();
        int endSquare = move.getEndSquare();
        long opponents = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        return (opponents & (1L << endSquare)) != 0;
    }

    private long getCastlingKingTravelSquares(Move move, boolean isWhite) {
        return switch (move.getMoveType()) {
            case KINGSIDE_CASTLE -> isWhite ? Bits.WHITE_KINGSIDE_CASTLE_SAFE_MASK : Bits.BLACK_KINGSIDE_CASTLE_SAFE_MASK;
            case QUEENSIDE_CASTLE -> isWhite ? Bits.WHITE_QUEENSIDE_CASTLE_SAFE_MASK : Bits.BLACK_QUEENSIDE_CASTLE_SAFE_MASK;
            default -> throw new IllegalArgumentException("Not a castling move!");
        };
    }

    private boolean isPinned(int square) {
        return (pinMask & 1L << square) != 0;
    }

}
