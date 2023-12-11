package com.kelseyde.calvin.generation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.check.PinCalculator;
import com.kelseyde.calvin.generation.check.PinCalculator.PinData;
import com.kelseyde.calvin.generation.check.RayCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates all the legal moves in a given position.
 * Using a hybrid of pseudo-legal and legal move generation: first we calculate the bitboards for checking pieces and
 * pinned pieces. If there is a check, we filter out all moves that do not resolve the check. Finally, we filter out all
 * moves that leave the king in (a new) check.
 */
public class MoveGenerator implements MoveGeneration {

    private final PinCalculator pinCalculator = new PinCalculator();
    private final RayCalculator rayCalculator = new RayCalculator();

    private long checkersMask;
    private long pinMask;
    private long[] pinRayMasks;
    private long captureMask;
    private long pushMask;
    private MoveFilter filter;

    private List<Move> legalMoves;

    @Override
    public List<Move> generateMoves(Board board) {
        return generateMoves(board, MoveFilter.ALL);
    }

    @Override
    public List<Move> generateMoves(Board board, MoveFilter filter) {

        boolean isWhite = board.isWhiteToMove();
        int kingSquare = Bitwise.getNextBit(board.getKing(isWhite));
        this.filter = filter;

        captureMask = Bits.ALL_SQUARES;
        pushMask = Bits.ALL_SQUARES;

        PinData pinData = pinCalculator.calculatePinMask(board, isWhite);
        pinMask = pinData.pinMask();
        pinRayMasks = pinData.pinRayMasks();

        checkersMask = calculateAttackerMask(board, isWhite, 1L << kingSquare);
        long checkersCount = Bitwise.countBits(checkersMask);

        legalMoves = new ArrayList<>();

        generateKingMoves(board);

        if (checkersCount == 2) {
            // If we are in double-check, the only legal moves are king moves
            return legalMoves;
        }

        if (checkersCount == 1) {
            // If only one checker, we can evade check by capturing it
            captureMask = checkersMask;

            int checkerSquare = Bitwise.getNextBit(checkersMask);
            if (board.pieceAt(checkerSquare).isSlider()) {
                // If the piece giving check is a slider, we can evade check by blocking it
                pushMask = rayCalculator.rayBetween(checkerSquare, kingSquare);
            } else {
                // If the piece is not a slider, we can only evade check by capturing it
                // Therefore all non-capture 'push' moves are illegal.
                pushMask = Bits.NO_SQUARES;
            }
        }

        // Generate all the other legal moves using the capture and push masks
        generatePawnMoves(board);
        generateKnightMoves(board);
        generateAllSlidingMoves(board);
        generateCastlingMoves(board);

        return legalMoves;

    }

    @Override
    public boolean isCheck(Board board, boolean isWhite) {
        long king = board.getKing(isWhite);
        return isAttacked(board, isWhite, king);
    }

    private void generatePawnMoves(Board board) {

        boolean isWhite = board.isWhiteToMove();
        long pawns = board.getPawns(isWhite);
        long opponents = board.getPieces(!isWhite);
        long occupied = board.getOccupied();
        long enPassantFile = Bitwise.getFileBitboard(board.getGameState().getEnPassantFile());
        int opponentKing = Bitwise.getNextBit(board.getKing(!isWhite));

        long filterMask = Bits.ALL_SQUARES;
        long promotionFilterMask = Bits.ALL_SQUARES;
        if (filter == MoveFilter.CAPTURES_ONLY) {
            filterMask = opponents;
            promotionFilterMask = opponents;
        } else if (filter == MoveFilter.CAPTURES_AND_CHECKS) {
            filterMask = opponents | Attacks.pawnAttacks(1L << opponentKing, !isWhite);
            promotionFilterMask = opponents | Attacks.bishopAttacks(opponentKing, occupied) | Attacks.rookAttacks(opponentKing, occupied);
        }

        if (filter != MoveFilter.CAPTURES_ONLY) {

            long singleMoves = Bitwise.pawnSingleMoves(pawns, occupied, isWhite) & pushMask & filterMask;
            long doubleMoves = Bitwise.pawnDoubleMoves(pawns, occupied, isWhite) & pushMask & filterMask;

            while (singleMoves != 0) {
                int endSquare = Bitwise.getNextBit(singleMoves);
                int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
                if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                    legalMoves.add(Move.of(startSquare, endSquare));
                }
                singleMoves = Bitwise.popBit(singleMoves);
            }
            while (doubleMoves != 0) {
                int endSquare = Bitwise.getNextBit(doubleMoves);
                int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
                if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                    legalMoves.add(Move.of(startSquare, endSquare, Move.PAWN_DOUBLE_MOVE_FLAG));
                }
                doubleMoves = Bitwise.popBit(doubleMoves);
            }
        }

        long leftCaptures = Bitwise.pawnLeftCaptures(pawns, opponents, isWhite) & captureMask & filterMask;
        long rightCaptures = Bitwise.pawnRightCaptures(pawns, opponents, isWhite) & captureMask & filterMask;
        long leftEnPassants = Bitwise.pawnLeftEnPassants(pawns, enPassantFile, isWhite);
        long rightEnPassants = Bitwise.pawnRightEnPassants(pawns, enPassantFile, isWhite);
        long pushPromotions = Bitwise.pawnPushPromotions(pawns, occupied, isWhite) & pushMask & promotionFilterMask;
        long leftCapturePromotions = Bitwise.pawnLeftCapturePromotions(pawns, opponents, isWhite) & (captureMask | pushMask) & promotionFilterMask;
        long rightCapturePromotions = Bitwise.pawnRightCapturePromotions(pawns, opponents, isWhite) & (captureMask | pushMask) & promotionFilterMask;

        while (leftCaptures != 0) {
            int endSquare = Bitwise.getNextBit(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.add(Move.of(startSquare, endSquare));
            }
            leftCaptures = Bitwise.popBit(leftCaptures);
        }
        while (rightCaptures != 0) {
            int endSquare = Bitwise.getNextBit(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.add(Move.of(startSquare, endSquare));
            }
            rightCaptures = Bitwise.popBit(rightCaptures);
        }
        while (leftEnPassants != 0) {
            int endSquare = Bitwise.getNextBit(leftEnPassants);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            // En passant is complicated; just test legality by making the move on the board and checking
            // whether the king is attacked.
            Move move = Move.of(startSquare, endSquare, Move.EN_PASSANT_FLAG);
            if (!leavesKingInCheck(board, move, isWhite)) {
                legalMoves.add(move);
            }
            leftEnPassants = Bitwise.popBit(leftEnPassants);
        }
        while (rightEnPassants != 0) {
            int endSquare = Bitwise.getNextBit(rightEnPassants);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            // En passant is complicated; just test legality by making the move on the board and checking
            // whether the king is attacked.
            Move move = Move.of(startSquare, endSquare, Move.EN_PASSANT_FLAG);
            if (!leavesKingInCheck(board, move, isWhite)) {
                legalMoves.add(move);
            }
            rightEnPassants = Bitwise.popBit(rightEnPassants);
        }
        while (pushPromotions != 0) {
            int endSquare = Bitwise.getNextBit(pushPromotions);
            int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
            if (!isPinned(startSquare)) {
                legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            }
            pushPromotions = Bitwise.popBit(pushPromotions);
        }
        while (leftCapturePromotions != 0) {
            int endSquare = Bitwise.getNextBit(leftCapturePromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            }
            leftCapturePromotions = Bitwise.popBit(leftCapturePromotions);
        }
        while (rightCapturePromotions != 0) {
            int endSquare = Bitwise.getNextBit(rightCapturePromotions);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            }
            rightCapturePromotions = Bitwise.popBit(rightCapturePromotions);
        }

    }

    private void generateKnightMoves(Board board) {
        boolean isWhite = board.isWhiteToMove();
        long knights = board.getKnights(isWhite);
        long opponents = board.getPieces(!isWhite);

        long filterMask = Bits.ALL_SQUARES;
        if (filter == MoveFilter.CAPTURES_ONLY) {
            filterMask = opponents;
        } else if (filter == MoveFilter.CAPTURES_AND_CHECKS) {
            int opponentKing = Bitwise.getNextBit(board.getKing(!isWhite));
            filterMask = opponents | Attacks.knightAttacks(opponentKing);
        }

        long unpinnedKnights = knights &~ pinMask;

        while (unpinnedKnights != 0) {
            int startSquare = Bitwise.getNextBit(unpinnedKnights);
            long possibleMoves = getKnightAttacks(board, startSquare, board.isWhiteToMove()) & (pushMask | captureMask) & filterMask;
            while (possibleMoves != 0) {
                int endSquare = Bitwise.getNextBit(possibleMoves);
                legalMoves.add(Move.of(startSquare, endSquare));
                possibleMoves = Bitwise.popBit(possibleMoves);
            }
            unpinnedKnights = Bitwise.popBit(unpinnedKnights);
        }
    }

    private void generateKingMoves(Board board) {
        boolean isWhite = board.isWhiteToMove();
        long king = board.getKing(isWhite);
        int startSquare = Bitwise.getNextBit(king);
        long friendlies = board.getPieces(isWhite);
        long opponents = board.getPieces(!isWhite);
        long filterMask = filter == MoveFilter.ALL ? Bits.ALL_SQUARES : opponents;
        long kingMoves = Attacks.kingAttacks(startSquare) &~ friendlies & filterMask;
        while (kingMoves != 0) {
            int endSquare = Bitwise.getNextBit(kingMoves);
            Move move = Move.of(startSquare, endSquare);
            board.makeMove(move);
            boolean isAttacked = isAttacked(board, isWhite, 1L << endSquare);
            board.unmakeMove();
            if (!isAttacked) {
                legalMoves.add(move);
            }
            kingMoves = Bitwise.popBit(kingMoves);
        }
    }

    private void generateCastlingMoves(Board board) {
        if (filter != MoveFilter.ALL) {
            return;
        }
        boolean isWhite = board.isWhiteToMove();
        long king = board.getKing(isWhite);
        int startSquare = Bitwise.getNextBit(king);
        long occupied = board.getOccupied();

        boolean isCheck = checkersMask != 0;
        if (!isCheck) {
            boolean isKingsideAllowed = board.getGameState().isKingsideCastlingAllowed(board.isWhiteToMove());
            if (isKingsideAllowed) {
                long travelSquares = board.isWhiteToMove() ? Bits.WHITE_KINGSIDE_CASTLE_TRAVEL_MASK : Bits.BLACK_KINGSIDE_CASTLE_TRAVEL_MASK;
                long blockedSquares = travelSquares & occupied;
                long safeSquares = isWhite ? Bits.WHITE_KINGSIDE_CASTLE_SAFE_MASK : Bits.BLACK_KINGSIDE_CASTLE_SAFE_MASK;
                boolean isAttacked = isAttacked(board, isWhite, safeSquares);
                if (blockedSquares == 0 && !isAttacked) {
                    int endSquare = board.isWhiteToMove() ? 6 : 62;
                    legalMoves.add(Move.of(startSquare, endSquare, Move.CASTLE_FLAG));
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
                    legalMoves.add(Move.of(startSquare, endSquare, Move.CASTLE_FLAG));
                }
            }
        }
    }

    private void generateAllSlidingMoves(Board board) {
        boolean isWhite = board.isWhiteToMove();
        long bishops = board.getBishops(isWhite);
        long rooks = board.getRooks(isWhite);
        long queens = board.getQueens(isWhite);
        long diagonalSliders = bishops | queens;
        long orthogonalSliders = rooks | queens;
        if (filter == MoveFilter.ALL) {
            generateSlidingMoves(board, diagonalSliders, false, true);
            generateSlidingMoves(board, orthogonalSliders, true, false);
        } else {
            generateSlidingMoves(board, bishops, false, true);
            generateSlidingMoves(board, rooks, true, false);
            generateSlidingMoves(board, queens, true, true);
        }
    }

    private void generateSlidingMoves(Board board, long sliders, boolean isOrthogonal, boolean isDiagonal) {
        boolean isWhite = board.isWhiteToMove();
        long opponents = board.getPieces(!board.isWhiteToMove());
        long occupied = board.getOccupied();

        while (sliders != 0) {
            int startSquare = Bitwise.getNextBit(sliders);
            long attackMask = getSlidingAttacks(board, startSquare, isWhite, isDiagonal, isOrthogonal);
            attackMask &= pushMask | captureMask;
            if (filter == MoveFilter.CAPTURES_ONLY) {
                attackMask &= opponents;
            } else if (filter == MoveFilter.CAPTURES_AND_CHECKS) {
                int opponentKing = Bitwise.getNextBit(board.getKing(!isWhite));
                long filterMask = opponents;
                if (isDiagonal) filterMask |= Attacks.bishopAttacks(opponentKing, occupied);
                if (isOrthogonal) filterMask |= Attacks.rookAttacks(opponentKing, occupied);
                attackMask &= filterMask;
            }
            if (isPinned(startSquare)) {
                attackMask &= (pinRayMasks[startSquare]);
            }
            sliders = Bitwise.popBit(sliders);
            while (attackMask != 0) {
                int endSquare = Bitwise.getNextBit(attackMask);
                legalMoves.add(Move.of(startSquare, endSquare));
                attackMask = Bitwise.popBit(attackMask);
            }
        }
    }

    public long getPawnAttacks(Board board, int square, boolean isWhite) {
        long attackMask = 0L;
        long squareBB = 1L << square;
        long friendlies = board.getPieces(isWhite);

        long leftCapture = isWhite ?
                Bitwise.shiftNorthWest(squareBB) &~ friendlies &~ Bits.FILE_H :
                Bitwise.shiftSouthWest(squareBB) &~ friendlies &~ Bits.FILE_H;
        attackMask |= leftCapture;

        long rightCapture = isWhite ?
                Bitwise.shiftNorthEast(squareBB) &~ friendlies &~ Bits.FILE_A :
                Bitwise.shiftSouthEast(squareBB) &~ friendlies &~ Bits.FILE_A;
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
            int square = Bitwise.getNextBit(squareMask);

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

            squareMask = Bitwise.popBit(squareMask);
        }
        return attackerMask;
    }

    private boolean isAttacked(Board board, boolean isWhite, long squareMask) {
        while (squareMask != 0) {
            int square = Bitwise.getNextBit(squareMask);

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

            squareMask = Bitwise.popBit(squareMask);
        }
        return false;
    }

    private List<Move> getPromotionMoves(int startSquare, int endSquare) {
        return filter != MoveFilter.ALL ?
                List.of(Move.of(startSquare, endSquare, Move.PROMOTE_TO_QUEEN_FLAG)) :
                List.of(Move.of(startSquare, endSquare, Move.PROMOTE_TO_QUEEN_FLAG),
                        Move.of(startSquare, endSquare, Move.PROMOTE_TO_ROOK_FLAG),
                        Move.of(startSquare, endSquare, Move.PROMOTE_TO_BISHOP_FLAG),
                        Move.of(startSquare, endSquare, Move.PROMOTE_TO_KNIGHT_FLAG));
    }

    private boolean leavesKingInCheck(Board board, Move move, boolean isWhite) {
        board.makeMove(move);
        int kingSquare = isWhite ? Bitwise.getNextBit(board.getWhiteKing()) : Bitwise.getNextBit(board.getBlackKing());
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
