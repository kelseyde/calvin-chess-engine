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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Evaluates the effect of a {@link Move} on a game. First checks if the move is legal. Then, checks if executing the
 * move results in the game ending, either due to checkmate or one of the draw conditions.
 */
@Slf4j
public class MoveGenerator {

    // The maximum number of possible legal moves is apparently 218 in this position:
    //R6R/3Q4/1Q4Q1/4Q3/2Q4Q/Q4Q2/pp1Q4/kBNNK1B1 b - - 0 1
    private static final int MAX_LEGAL_MOVES = 218;

    private final PawnMoveGenerator pawnMoveGenerator = new PawnMoveGenerator();
    private final KnightMoveGenerator knightMoveGenerator = new KnightMoveGenerator();
    private final BishopMoveGenerator bishopMoveGenerator = new BishopMoveGenerator();
    private final RookMoveGenerator rookMoveGenerator = new RookMoveGenerator();
    private final QueenMoveGenerator queenMoveGenerator = new QueenMoveGenerator();
    private final KingMoveGenerator kingMoveGenerator = new KingMoveGenerator();

    private final PinCalculator pinCalculator = new PinCalculator();
    private final RayCalculator rayCalculator = new RayCalculator();

    private final Set<PseudoLegalMoveGenerator> PSEUDO_LEGAL_MOVE_GENERATORS = Set.of(
            pawnMoveGenerator, knightMoveGenerator, bishopMoveGenerator, rookMoveGenerator, queenMoveGenerator, kingMoveGenerator
    );

    private long pinMask;
    private long opponentCheckers;
    private int opponentCheckersCount;

    public Move[] generateLegalMoves(Board board, boolean capturesOnly) {

        boolean isWhite = board.isWhiteToMove();
        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());

        calculateAttackData(board, kingSquare, isWhite);

        Set<Move> kingPseudoLegals = kingMoveGenerator.generatePseudoLegalMoves(board);

        // If we are in double-check, the only legal moves are king moves
        if (opponentCheckersCount == 2) {
            return kingPseudoLegals.stream()
                   .filter(pseudoLegalMove -> isLegal(board, pseudoLegalMove))
                   .filter(legalMove -> !capturesOnly || filterCapturesOnly(board, legalMove))
                   .toArray(Move[]::new);
        }

        // Otherwise, generate all the other pseudo-legal moves
        Set<Move> allPseudoLegals = Stream.of(pawnMoveGenerator, knightMoveGenerator, bishopMoveGenerator, rookMoveGenerator, queenMoveGenerator)
               .flatMap(generator -> generator.generatePseudoLegalMoves(board).stream())
               .collect(Collectors.toSet());
        allPseudoLegals.addAll(kingPseudoLegals);

        if (opponentCheckersCount == 1) {
            // capture checking piece, block checking piece, move king
            int checkerSquare = BitboardUtils.getLSB(opponentCheckers);
            allPseudoLegals = allPseudoLegals.stream()
                    .filter(move -> {

                        int endSquare = move.getEndSquare();

                        boolean isCapturingChecker = checkerSquare == endSquare;
                        if (isCapturingChecker) {
                            return true;
                        }

                        if (move.getMoveType().isEnPassant()) {
                            int enPassantSquare = isWhite ? endSquare - 8 : endSquare + 8;
                            if (enPassantSquare == checkerSquare) {
                                return true;
                            }
                        }

                        long checkingRay = rayCalculator.rayBetween(kingSquare, checkerSquare);
                        boolean isBlockingCheck = (checkingRay & 1L << endSquare) != 0;
                        if (isBlockingCheck) {
                            return true;
                        }

                        if (move.getPieceType().equals(PieceType.KING)) {
                            return true;
                        }
                        return false;

                    })
                    .collect(Collectors.toSet());
        }


        return allPseudoLegals.stream()
                .filter(pseudoLegalMove -> isLegal(board, pseudoLegalMove))
                .filter(legalMove -> !capturesOnly || filterCapturesOnly(board, legalMove))
                .toArray(Move[]::new);

    }

    public boolean isLegal(Board board, Move move) {

        // TODO multiple isAttacked
        boolean isWhite = board.isWhiteToMove();

        if (move.getMoveType().isCastling()) {
            long kingMask = getCastlingKingTravelSquares(move, isWhite);
            return !isAttacked(board, isWhite, kingMask);
        }
        else if (move.getMoveType().isEnPassant()) {
            board.makeMove(move);
            long kingMask = isWhite ? board.getWhiteKing() : board.getBlackKing();
            boolean isKingCapturable = isAttacked(board, isWhite, kingMask);
            board.unmakeMove();
            return !isKingCapturable;

        }
        else if (move.getPieceType().equals(PieceType.KING)) {
            board.makeMove(move);
            // TODO can be faster
            long endSquareMask = 1L << move.getEndSquare();
            boolean isNotAttacked = !isAttacked(board, isWhite, endSquareMask);
            board.unmakeMove();
            return isNotAttacked;

        }
        else {
            int startSquare = move.getStartSquare();
            int endSquare = move.getEndSquare();
            int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());
            return !isPinned(startSquare) || BoardUtils.isAligned(kingSquare, startSquare, endSquare);
        }

    }

    private void calculateAttackData(Board board, int kingSquare, boolean isWhite) {

        opponentCheckers = calculateAttackerMask(board, isWhite, 1L << kingSquare);
        opponentCheckersCount = Long.bitCount(opponentCheckers);
        pinMask = pinCalculator.calculatePinMask(board, isWhite);

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
