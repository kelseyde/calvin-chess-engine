package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.movegeneration.generator.*;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Generates all the legal moves in a given position. Using a hybrid of pseudo-legal and legal move generation: first
 * we calculate the bitboards for checking pieces and pinned pieces. If there is a check, we filter out all moves that
 * do not resolve the check. Finally we filter out all moves that leave the king in (a new) check.
 */
@Slf4j
public class MoveGenerator {

    private final PawnMoveGenerator pawnMoveGenerator = new PawnMoveGenerator();
    private final KnightMoveGenerator knightMoveGenerator = new KnightMoveGenerator();
    private final BishopMoveGenerator bishopMoveGenerator = new BishopMoveGenerator();
    private final RookMoveGenerator rookMoveGenerator = new RookMoveGenerator();
    private final QueenMoveGenerator queenMoveGenerator = new QueenMoveGenerator();
    private final KingMoveGenerator kingMoveGenerator = new KingMoveGenerator();

    private final PinCalculator pinCalculator = new PinCalculator();
    private final RayCalculator rayCalculator = new RayCalculator();

    /**
     * A bitboard containing all the pieces that are currently pinned for the side to move.
     */
    private long pinMask;

    /**
     * A bitboard containing all the enemy pieces that are currently checking the king.
     */
    private long checkersMask;

    public Move[] generateLegalMoves(Board board, boolean capturesOnly) {

        boolean isWhite = board.isWhiteToMove();
        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());

        checkersMask = calculateAttackerMask(board, isWhite, 1L << kingSquare);
        pinMask = pinCalculator.calculatePinMask(board, isWhite);
        int checkersCount = Long.bitCount(checkersMask);

        List<Move> allMoves = kingMoveGenerator.generatePseudoLegalMoves(board);

        // If we are in double-check, the only legal moves are king moves
        boolean isDoubleCheck = checkersCount == 2;
        if (isDoubleCheck) {
            return allMoves.stream()
                   .filter(move ->
                           // Filter out all king moves that leave the king in check
                           doesNotLeaveKingInCheck(board, move, kingSquare, isWhite) &&
                           // Optionally filter out non-capture moves
                           (!capturesOnly || isCapture(board, move))
                   )
                   .toArray(Move[]::new);
        }

        // Otherwise, generate all the other pseudo-legal moves
        allMoves.addAll(pawnMoveGenerator.generatePseudoLegalMoves(board));
        allMoves.addAll(knightMoveGenerator.generatePseudoLegalMoves(board));
        allMoves.addAll(bishopMoveGenerator.generatePseudoLegalMoves(board));
        allMoves.addAll(rookMoveGenerator.generatePseudoLegalMoves(board));
        allMoves.addAll(queenMoveGenerator.generatePseudoLegalMoves(board));

        boolean isCheck = checkersCount == 1;

        return allMoves.stream()
                .filter(move ->

                    // If we are in single-check, filter out all moves that do not resolve the check
                    (!isCheck || resolvesCheck(board, move, kingSquare, isWhite)) &&

                    // Additionally, filter out moves that leave the king in (a new) check
                    doesNotLeaveKingInCheck(board, move, kingSquare, isWhite) &&

                     // Finally, optionally filter out non-capture moves
                    (!capturesOnly || isCapture(board, move))

                )
                .toArray(Move[]::new);

    }

    private boolean resolvesCheck(Board board, Move move, int kingSquare, boolean isWhite) {
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

    public boolean doesNotLeaveKingInCheck(Board board, Move move, int kingSquare, boolean isWhite) {
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();

        // Check that none of the squares the king travels through to castle are attacked.
        if (move.isCastling()) {
            long kingMask = getCastlingKingTravelSquares(move, isWhite);
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

    private long calculateAttackerMask(Board board, boolean isWhite, long squareMask) {
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
        return calculateAttackerMask(board, isWhite, squareMask) != 0;
    }

    private boolean isCapture(Board board, Move move) {
        boolean isWhite = board.isWhiteToMove();
        int endSquare = move.getEndSquare();
        long opponents = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        return (opponents & (1L << endSquare)) != 0;
    }

    private long getCastlingKingTravelSquares(Move move, boolean isWhite) {
        if (BoardUtils.getFile(move.getEndSquare()) == 6) {
            return isWhite ? Bits.WHITE_KINGSIDE_CASTLE_SAFE_MASK : Bits.BLACK_KINGSIDE_CASTLE_SAFE_MASK;
        } else {
            return isWhite ? Bits.WHITE_QUEENSIDE_CASTLE_SAFE_MASK : Bits.BLACK_QUEENSIDE_CASTLE_SAFE_MASK;
        }
    }

}
