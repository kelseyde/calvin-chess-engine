package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.movegeneration.check.PinCalculator;
import com.kelseyde.calvin.movegeneration.check.RayCalculator;
import com.kelseyde.calvin.movegeneration.generator.*;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates all the legal moves in a given position.
 * Using a hybrid of pseudo-legal and legal move generation: first we calculate the bitboards for checking pieces and
 * pinned pieces. If there is a check, we filter out all moves that do not resolve the check. Finally, we filter out all
 * moves that leave the king in (a new) check.
 */
@Slf4j
public class MoveGenerator implements MoveGeneration {

    private final PawnMoveGenerator pawnMoveGenerator = new PawnMoveGenerator();
    private final KnightMoveGenerator knightMoveGenerator = new KnightMoveGenerator();
    private final BishopMoveGenerator bishopMoveGenerator = new BishopMoveGenerator();
    private final RookMoveGenerator rookMoveGenerator = new RookMoveGenerator();
    private final QueenMoveGenerator queenMoveGenerator = new QueenMoveGenerator();
    private final KingMoveGenerator kingMoveGenerator = new KingMoveGenerator();

    private final PinCalculator pinCalculator = new PinCalculator();
    private final RayCalculator rayCalculator = new RayCalculator();

    /**
     * The current board state.
     */
    private Board board;

    /**
     * Which side is to move.
     */
    private boolean isWhite;

    /**
     * The square of the king for the side to move.
     */
    private int kingSquare;

    /**
     * A bitboard containing all the pieces that are currently pinned, for the side to move.
     */
    private long pinMask;

    /**
     * A bitboard containing all the enemy pieces that are currently checking the king.
     */
    private long checkersMask;

    /**
     * How many pieces are currently checking the king.
     */
    private int checkersCount;

    /**
     * The pseudo-legal moves in the current position.
     */
    private List<Move> pseudoLegalMoves;

    /**
     * The legal moves in the current position.
     */
    private List<Move> legalMoves;

    @Override
    public List<Move> generateMoves(Board board, boolean capturesOnly) {

        init(board);

        pseudoLegalMoves = kingMoveGenerator.generatePseudoLegalMoves(board, capturesOnly);

        if (checkersCount == 2) {
            // If we are in double-check, the only legal moves are king moves.
            for (Move move : pseudoLegalMoves) {
                if (doesNotLeaveKingInCheck(move)) {
                    legalMoves.add(move);
                }
            }
            return legalMoves;
        }

        // Otherwise, generate all the other pseudo-legal moves
        pseudoLegalMoves.addAll(pawnMoveGenerator.generatePseudoLegalMoves(board, capturesOnly));
        pseudoLegalMoves.addAll(knightMoveGenerator.generatePseudoLegalMoves(board, capturesOnly));
        pseudoLegalMoves.addAll(bishopMoveGenerator.generatePseudoLegalMoves(board, capturesOnly));
        pseudoLegalMoves.addAll(rookMoveGenerator.generatePseudoLegalMoves(board, capturesOnly));
        pseudoLegalMoves.addAll(queenMoveGenerator.generatePseudoLegalMoves(board, capturesOnly));

        boolean isCheck = checkersCount == 1;

        for (Move move : pseudoLegalMoves) {
            // If we are in single-check, filter out all moves that do not resolve the check
            if ((!isCheck || resolvesCheck(move) &&
                // Additionally, filter out moves that leave the king in (a new) check
                doesNotLeaveKingInCheck(move))) {
                legalMoves.add(move);
            }
        }
        return legalMoves;

    }

    @Override
    public boolean isCheck(Board board, boolean isWhite) {
        init(board);
        long kingMask = isWhite ? board.getWhiteKing() : board.getBlackKing();
        return isAttacked(board, isWhite, kingMask);
    }

    private void init(Board board) {
        this.board = board;
        this.isWhite = board.isWhiteToMove();
        this.kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());
        this.checkersMask = calculateAttackerMask(1L << kingSquare);
        this.pinMask = pinCalculator.calculatePinMask(board, isWhite);
        this.checkersCount = Long.bitCount(checkersMask);
        this.pseudoLegalMoves = new ArrayList<>();
        this.legalMoves = new ArrayList<>();
    }

    private boolean resolvesCheck(Move move) {
        int checkerSquare = BitboardUtils.getLSB(checkersMask);
        int endSquare = move.getEndSquare();

        // Three options to resolve a single check:

        // 1. Capture the piece checking the king
        boolean isCapturingChecker = checkerSquare == endSquare
                || move.isEnPassant() && (isWhite ? endSquare - 8 : endSquare + 8) == checkerSquare;
        if (isCapturingChecker) {
            return true;
        }

        // 2. Block the check with another piece
        long checkingRay = rayCalculator.rayBetween(kingSquare, checkerSquare);
        boolean isBlockingCheck = (checkingRay & 1L << endSquare) != 0;
        if (isBlockingCheck) {
            return true;
        }

        // 3. Move the king (the legality of the king destination square is checked later).
        return board.pieceAt(move.getStartSquare()).equals(PieceType.KING);
    }

    private boolean doesNotLeaveKingInCheck(Move move) {
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();

        // Check that none of the squares the king travels through to castle are attacked.
        if (move.isCastling()) {
            long kingMask;
            if (BoardUtils.getFile(move.getEndSquare()) == 6) {
                kingMask = isWhite ? Bits.WHITE_KINGSIDE_CASTLE_SAFE_MASK : Bits.BLACK_KINGSIDE_CASTLE_SAFE_MASK;
            } else {
                kingMask = isWhite ? Bits.WHITE_QUEENSIDE_CASTLE_SAFE_MASK : Bits.BLACK_QUEENSIDE_CASTLE_SAFE_MASK;
            }
            return !isAttacked(board, isWhite, kingMask);
        }
        // For en passant and king moves, just make the move on the board and check the king is not attacked.
        else if (move.isEnPassant() || board.pieceAt(startSquare).equals(PieceType.KING)) {
            board.makeMove(move);
            long kingMask = isWhite ? board.getWhiteKing() : board.getBlackKing();
            boolean isAttacked = isAttacked(board, isWhite, kingMask);
            board.unmakeMove();
            return !isAttacked;
        }
        // All other moves are legal if and only if the piece is not pinned (or is pinned, but is moving along the pin ray)
        else {
            boolean isPinned = (pinMask & 1L << startSquare) != 0;
            return !isPinned || BoardUtils.isAligned(kingSquare, startSquare, endSquare);
        }

    }

    private long calculateAttackerMask(long squareMask) {
        long attackerMask = 0L;
        while (squareMask != 0) {
            int square = BitboardUtils.getLSB(squareMask);

            long opponentPawns = isWhite ? board.getBlackPawns() : board.getWhitePawns();
            long pawnAttackMask = pawnMoveGenerator.generateAttackMaskFromSquare(board, square, isWhite);
            attackerMask |= pawnAttackMask & opponentPawns;

            long opponentKnights = isWhite ? board.getBlackKnights() : board.getWhiteKnights();
            long knightAttackMask = knightMoveGenerator.generateAttackMaskFromSquare(board, square, isWhite);
            attackerMask |= knightAttackMask & opponentKnights;

            long opponentBishops = isWhite ? board.getBlackBishops() : board.getWhiteBishops();
            long bishopAttackMask = bishopMoveGenerator.generateAttackMaskFromSquare(board, square, isWhite);
            attackerMask |= bishopAttackMask & opponentBishops;

            long opponentRooks = isWhite ? board.getBlackRooks() : board.getWhiteRooks();
            long rookAttackMask = rookMoveGenerator.generateAttackMaskFromSquare(board, square, isWhite);
            attackerMask |= rookAttackMask & opponentRooks;

            long opponentQueens = isWhite ? board.getBlackQueens() : board.getWhiteQueens();
            long queenAttackMask = queenMoveGenerator.generateAttackMaskFromSquare(board, square, isWhite);
            attackerMask |= queenAttackMask & opponentQueens;

            long opponentKing = isWhite ? board.getBlackKing() : board.getWhiteKing();
            long kingAttackMask = kingMoveGenerator.generateAttackMaskFromSquare(board, square, isWhite);
            attackerMask |= kingAttackMask & opponentKing;

            squareMask = BitboardUtils.popLSB(squareMask);
        }
        return attackerMask;
    }

    private boolean isAttacked(Board board, boolean isWhite, long squareMask) {
        return calculateAttackerMask(squareMask) != 0;
    }

}
