package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.movegeneration.attacks.Attacks;
import com.kelseyde.calvin.movegeneration.check.PinCalculator;
import com.kelseyde.calvin.movegeneration.check.PinCalculator.PinData;
import com.kelseyde.calvin.movegeneration.check.RayCalculator;
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

    private final PinCalculator pinCalculator = new PinCalculator();
    private final RayCalculator rayCalculator = new RayCalculator();

    private long checkersMask;
    private long pinMask;
    private long[] pinRayMasks;
    private long captureMask;
    private long pushMask;

    private List<Move> legalMoves;

    @Override
    public List<Move> generateMoves(Board board, boolean capturesOnly) {

        boolean isWhite = board.isWhiteToMove();
        int kingSquare = BitboardUtils.getLSB(board.getKing(isWhite));

        captureMask = Bits.ALL_SQUARES;
        pushMask = Bits.ALL_SQUARES;

        PinData pinData = pinCalculator.calculatePinMask(board, isWhite);
        pinMask = pinData.pinMask();
        pinRayMasks = pinData.pinRayMasks();

        checkersMask = calculateAttackerMask(board, isWhite, 1L << kingSquare);
        long checkersCount = Long.bitCount(checkersMask);

        legalMoves = new ArrayList<>();

        generateKingMoves(board, capturesOnly);

        if (checkersCount == 2) {
            // If we are in double-check, the only legal moves are king moves
            return legalMoves;
        }

        if (checkersCount == 1) {
            // If only one checker, we can evade check by capturing it
            captureMask = checkersMask;

            int checkerSquare = BitboardUtils.getLSB(checkersMask);
            if (board.pieceAt(checkerSquare).isSlider()) {
                // If the piece giving check is a slider, we can evade check by blocking it
                pushMask = rayCalculator.rayBetween(checkerSquare, kingSquare);
            } else {
                // If the piece is not a slider, we can only evade check by capturing it
                // Therefore all non-capture 'push' moves are illegal.
                pushMask = 0L;
            }
        }

        // Generate all the other legal moves using the capture and push masks
        generatePawnMoves(board, capturesOnly);
        generateKnightMoves(board, capturesOnly);
        generateBishopMoves(board, capturesOnly);
        generateRookMoves(board, capturesOnly);
        generateQueenMoves(board, capturesOnly);
        generateCastlingMoves(board, capturesOnly);

        return legalMoves;

    }

    @Override
    public boolean isCheck(Board board, boolean isWhite) {
        long king = board.getKing(isWhite);
        return isAttacked(board, isWhite, king);
    }

    private void generatePawnMoves(Board board, boolean capturesOnly) {

        boolean isWhite = board.isWhiteToMove();
        long pawns = board.getPawns(isWhite);
        long opponents = board.getPieces(!isWhite);
        long occupied = board.getOccupied();
        long enPassantFile = BitboardUtils.getFileBitboard(board.getGameState().getEnPassantFile());

        if (!capturesOnly) {
            long singleMoves = BitboardUtils.pawnSingleMoves(pawns, occupied, isWhite) & pushMask;
            long doubleMoves = BitboardUtils.pawnDoubleMoves(pawns, occupied, isWhite) & pushMask;
            long pushPromotions = BitboardUtils.pawnPushPromotions(pawns, occupied, isWhite) & pushMask;

            while (singleMoves != 0) {
                int endSquare = BitboardUtils.getLSB(singleMoves);
                int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
                if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                    legalMoves.add(new Move(startSquare, endSquare));
                }
                singleMoves = BitboardUtils.popLSB(singleMoves);
            }
            while (doubleMoves != 0) {
                int endSquare = BitboardUtils.getLSB(doubleMoves);
                int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
                if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                    legalMoves.add(new Move(startSquare, endSquare, Move.PAWN_DOUBLE_MOVE_FLAG));
                }
                doubleMoves = BitboardUtils.popLSB(doubleMoves);
            }
            while (pushPromotions != 0) {
                int endSquare = BitboardUtils.getLSB(pushPromotions);
                int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
                if (!isPinned(startSquare)) {
                    legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
                }
                pushPromotions = BitboardUtils.popLSB(pushPromotions);
            }
        }

        long leftCaptures = BitboardUtils.pawnLeftCaptures(pawns, opponents, isWhite) & captureMask;
        long rightCaptures = BitboardUtils.pawnRightCaptures(pawns, opponents, isWhite) & captureMask;
        long leftEnPassants = BitboardUtils.pawnLeftEnPassants(pawns, enPassantFile, isWhite);
        long rightEnPassants = BitboardUtils.pawnRightEnPassants(pawns, enPassantFile, isWhite);
        long leftCapturePromotions = BitboardUtils.pawnLeftCapturePromotions(pawns, opponents, isWhite) & (captureMask | pushMask);
        long rightCapturePromotions = BitboardUtils.pawnRightCapturePromotions(pawns, opponents, isWhite) & (captureMask | pushMask);

        while (leftCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.add(new Move(startSquare, endSquare));
            }
            leftCaptures = BitboardUtils.popLSB(leftCaptures);
        }
        while (rightCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.add(new Move(startSquare, endSquare));
            }
            rightCaptures = BitboardUtils.popLSB(rightCaptures);
        }
        while (leftEnPassants != 0) {
            int endSquare = BitboardUtils.getLSB(leftEnPassants);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            // En passant is complicated; just test legality by making the move on the board and checking
            // whether the king is attacked.
            Move move = new Move(startSquare, endSquare, Move.EN_PASSANT_FLAG);
            if (!leavesKingInCheck(board, move, isWhite)) {
                legalMoves.add(move);
            }
            leftEnPassants = BitboardUtils.popLSB(leftEnPassants);
        }
        while (rightEnPassants != 0) {
            int endSquare = BitboardUtils.getLSB(rightEnPassants);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            // En passant is complicated; just test legality by making the move on the board and checking
            // whether the king is attacked.
            Move move = new Move(startSquare, endSquare, Move.EN_PASSANT_FLAG);
            if (!leavesKingInCheck(board, move, isWhite)) {
                legalMoves.add(move);
            }
            rightEnPassants = BitboardUtils.popLSB(rightEnPassants);
        }
        while (leftCapturePromotions != 0) {
            int endSquare = BitboardUtils.getLSB(leftCapturePromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            }
            leftCapturePromotions = BitboardUtils.popLSB(leftCapturePromotions);
        }
        while (rightCapturePromotions != 0) {
            int endSquare = BitboardUtils.getLSB(rightCapturePromotions);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            }
            rightCapturePromotions = BitboardUtils.popLSB(rightCapturePromotions);
        }

    }

    private void generateKnightMoves(Board board, boolean capturesOnly) {
        long knights = board.getKnights(board.isWhiteToMove());
        long opponents = board.getPieces(!board.isWhiteToMove());
        long unpinnedKnights = knights &~ pinMask;

        while (unpinnedKnights != 0) {
            int startSquare = BitboardUtils.getLSB(unpinnedKnights);
            long possibleMoves = getKnightAttacks(board, startSquare, board.isWhiteToMove()) & (pushMask | captureMask);
            if (capturesOnly) {
                possibleMoves = possibleMoves & opponents;
            }
            while (possibleMoves != 0) {
                int endSquare = BitboardUtils.getLSB(possibleMoves);
                legalMoves.add(new Move(startSquare, endSquare));
                possibleMoves = BitboardUtils.popLSB(possibleMoves);
            }
            unpinnedKnights = BitboardUtils.popLSB(unpinnedKnights);
        }
    }

    private void generateKingMoves(Board board, boolean capturesOnly) {
        boolean isWhite = board.isWhiteToMove();
        long king = board.getKing(isWhite);
        int startSquare = BitboardUtils.getLSB(king);
        long friendlies = board.getPieces(isWhite);
        long kingMoves = Attacks.kingAttacks(startSquare) &~ friendlies;
        if (capturesOnly) {
            long opponents = isWhite ? board.getBlackPieces() : board.getWhitePieces();
            kingMoves = kingMoves & opponents;
        }
        while (kingMoves != 0) {
            int endSquare = BitboardUtils.getLSB(kingMoves);
            Move move = new Move(startSquare, endSquare);
            board.makeMove(move);
            boolean isAttacked = isAttacked(board, isWhite, 1L << endSquare);
            board.unmakeMove();
            if (!isAttacked) {
                legalMoves.add(move);
            }
            kingMoves = BitboardUtils.popLSB(kingMoves);
        }
    }

    private void generateCastlingMoves(Board board, boolean capturesOnly) {
        boolean isWhite = board.isWhiteToMove();
        long king = board.getKing(isWhite);
        int startSquare = BitboardUtils.getLSB(king);
        long occupied = board.getOccupied();

        boolean isCheck = checkersMask != 0;
        if (!isCheck && !capturesOnly) {
            boolean isKingsideAllowed = board.getGameState().isKingsideCastlingAllowed(board.isWhiteToMove());
            if (isKingsideAllowed) {
                long travelSquares = board.isWhiteToMove() ? Bits.WHITE_KINGSIDE_CASTLE_TRAVEL_MASK : Bits.BLACK_KINGSIDE_CASTLE_TRAVEL_MASK;
                long blockedSquares = travelSquares & occupied;
                long safeSquares = isWhite ? Bits.WHITE_KINGSIDE_CASTLE_SAFE_MASK : Bits.BLACK_KINGSIDE_CASTLE_SAFE_MASK;
                boolean isAttacked = isAttacked(board, isWhite, safeSquares);
                if (blockedSquares == 0 && !isAttacked) {
                    int endSquare = board.isWhiteToMove() ? 6 : 62;
                    legalMoves.add(new Move(startSquare, endSquare, Move.CASTLE_FLAG));
                }
            }
            boolean isQueensideAllowed = board.getGameState().isQueensideCastlingAllowed(board.isWhiteToMove());
            if (isQueensideAllowed) {
                long travelSquares = board.isWhiteToMove() ? Bits.WHITE_QUEENSIDE_CASTLE_TRAVEL_MASK : Bits.BLACK_QUEENSIDE_CASTLE_TRAVEL_MASK;
                long blockedSquares = travelSquares & occupied;
                long safeSquares = isWhite ? Bits.WHITE_QUEENSIDE_CASTLE_SAFE_MASK : Bits.BLACK_QUEENSIDE_CASTLE_SAFE_MASK;
                boolean isAttacked = isAttacked(board, isWhite, safeSquares);
                if (blockedSquares == 0 && !isAttacked) {
                    int endSquare = board.isWhiteToMove() ? 2 : 58;
                    legalMoves.add(new Move(startSquare, endSquare, Move.CASTLE_FLAG));
                }
            }
        }
    }

    private void generateSlidingMoves(Board board, boolean capturesOnly, long sliders, boolean isOrthogonal, boolean isDiagonal) {
        boolean isWhite = board.isWhiteToMove();
        while (sliders != 0) {
            int startSquare = BitboardUtils.getLSB(sliders);
            long attackMask = getSlidingAttacks(board, startSquare, isWhite, isDiagonal, isOrthogonal);
            attackMask &= pushMask | captureMask;
            if (capturesOnly) {
                long opponents = board.getPieces(!board.isWhiteToMove());
                attackMask = attackMask & opponents;
            }
            if (isPinned(startSquare)) {
                attackMask &= (pinRayMasks[startSquare]);
            }
            sliders = BitboardUtils.popLSB(sliders);
            while (attackMask != 0) {
                int endSquare = BitboardUtils.getLSB(attackMask);
                legalMoves.add(new Move(startSquare, endSquare));
                attackMask = BitboardUtils.popLSB(attackMask);
            }
        }
    }

    private void generateBishopMoves(Board board, boolean capturesOnly) {
        long bishops = board.getBishops(board.isWhiteToMove());
        generateSlidingMoves(board, capturesOnly, bishops, false, true);
    }

    private void generateRookMoves(Board board, boolean capturesOnly) {
        long rooks = board.getRooks(board.isWhiteToMove());
        generateSlidingMoves(board, capturesOnly, rooks, true, false);
    }

    private void generateQueenMoves(Board board, boolean capturesOnly) {
        long queens = board.getQueens(board.isWhiteToMove());
        generateSlidingMoves(board, capturesOnly, queens, true, true);
    }

    public long getPawnAttacks(Board board, int square, boolean isWhite) {
        long attackMask = 0L;
        long squareBB = 1L << square;
        long friendlies = board.getPieces(isWhite);

        long leftCapture = isWhite ?
                BitboardUtils.shiftNorthWest(squareBB) &~ friendlies &~ Bits.FILE_H :
                BitboardUtils.shiftSouthWest(squareBB) &~ friendlies &~ Bits.FILE_H;
        attackMask |= leftCapture;

        long rightCapture = isWhite ?
                BitboardUtils.shiftNorthEast(squareBB) &~ friendlies &~ Bits.FILE_A :
                BitboardUtils.shiftSouthEast(squareBB) &~ friendlies &~ Bits.FILE_A;
        attackMask |= rightCapture;

        return attackMask;
    }

    public long getKnightAttacks(Board board, int square, boolean isWhite) {
        long friendlies = board.getPieces(isWhite);
        return Attacks.knightAttacks(square) &~ friendlies;
    }

    public long getBishopAttacks(Board board, int square, boolean isWhite) {
        return getSlidingAttacks(board, square, isWhite, true, false);
    }

    public long getRookAttacks(Board board, int square, boolean isWhite) {
        return getSlidingAttacks(board, square, isWhite, false, true);
    }

    public long getQueenAttacks(Board board, int square, boolean isWhite) {
        return getSlidingAttacks(board, square, isWhite, true, true);
    }

    public long getKingAttacks(Board board, int square, boolean isWhite) {
        long friendlies = board.getPieces(isWhite);
        return Attacks.kingAttacks(square) &~ friendlies;
    }

    private long getSlidingAttacks(Board board, int square, boolean isWhite, boolean isDiagonal, boolean isOrthogonal) {
        long attackMask = 0L;
        long occ = board.getOccupied();
        long friendlies = board.getPieces(isWhite);
        if (isOrthogonal) {
            attackMask |= Attacks.rookAttacks(square, occ) &~ friendlies;
        }
        if (isDiagonal) {
            attackMask |= Attacks.bishopAttacks(square, occ) &~ friendlies;
        }
        return attackMask;
    }

    private long calculateAttackerMask(Board board, boolean isWhite, long squareMask) {
        long attackerMask = 0L;
        while (squareMask != 0) {
            int square = BitboardUtils.getLSB(squareMask);

            long opponentPawns = board.getPawns(!isWhite);
            long pawnAttackMask = getPawnAttacks(board, square, isWhite);
            attackerMask |= pawnAttackMask & opponentPawns;

            long opponentKnights = board.getKnights(!isWhite);
            long knightAttackMask = getKnightAttacks(board, square, isWhite);
            attackerMask |= knightAttackMask & opponentKnights;

            long opponentBishops = board.getBishops(!isWhite);
            long bishopAttackMask = getBishopAttacks(board, square, isWhite);
            attackerMask |= bishopAttackMask & opponentBishops;

            long opponentRooks = board.getRooks(!isWhite);
            long rookAttackMask = getRookAttacks(board, square, isWhite);
            attackerMask |= rookAttackMask & opponentRooks;

            long opponentQueens = board.getQueens(!isWhite);
            attackerMask |= (bishopAttackMask | rookAttackMask) & opponentQueens;

            long opponentKing = board.getKing(!isWhite);
            long kingAttackMask = getKingAttacks(board, square, isWhite);
            attackerMask |= kingAttackMask & opponentKing;

            squareMask = BitboardUtils.popLSB(squareMask);
        }
        return attackerMask;
    }

    private boolean isAttacked(Board board, boolean isWhite, long squareMask) {
        while (squareMask != 0) {
            int square = BitboardUtils.getLSB(squareMask);

            long opponentPawns = board.getPawns(!isWhite);
            if (opponentPawns != 0) {
                long pawnAttackMask = getPawnAttacks(board, square, isWhite);
                if ((pawnAttackMask & opponentPawns) != 0) {
                    return true;
                }
            }

            long opponentKnights = board.getKnights(!isWhite);
            if (opponentKnights != 0) {
                long knightAttackMask = getKnightAttacks(board, square, isWhite);
                if ((knightAttackMask & opponentKnights) != 0) {
                    return true;
                }
            }

            long opponentBishops = board.getBishops(!isWhite);
            long opponentQueens = board.getQueens(!isWhite);
            long diagonalSliders = opponentBishops | opponentQueens;
            if (diagonalSliders != 0) {
                long bishopAttackMask = getBishopAttacks(board, square, isWhite);
                if ((bishopAttackMask & diagonalSliders) != 0) {
                    return true;
                }
            }

            long opponentRooks = board.getRooks(!isWhite);
            long orthogonalSliders = opponentRooks | opponentQueens;
            if (orthogonalSliders != 0) {
                long rookAttackMask = getRookAttacks(board, square, isWhite);
                if ((rookAttackMask & orthogonalSliders) != 0) {
                    return true;
                }
            }

            long opponentKing = board.getKing(!isWhite);
            long kingAttackMask = getKingAttacks(board, square, isWhite);
            if ((kingAttackMask & opponentKing) != 0) {
                return true;
            }

            squareMask = BitboardUtils.popLSB(squareMask);
        }
        return false;
    }

    private List<Move> getPromotionMoves(int startSquare, int endSquare) {
        return List.of(
                new Move(startSquare, endSquare, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(startSquare, endSquare, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(startSquare, endSquare, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(startSquare, endSquare, Move.PROMOTE_TO_KNIGHT_FLAG));
    }

    private boolean leavesKingInCheck(Board board, Move move, boolean isWhite) {
        board.makeMove(move);
        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());
        boolean isAttacked = isAttacked(board, isWhite, 1L << kingSquare);
        board.unmakeMove();
        return isAttacked;
    }

    private boolean isPinned(int startSquare) {
        return (1L << startSquare & pinMask) != 0;
    }

    private boolean isMovingAlongPinRay(int startSquare, int endSquare) {
        long pinRay = pinRayMasks[startSquare];
        return (1L << endSquare & pinRay) != 0;
    }

}
