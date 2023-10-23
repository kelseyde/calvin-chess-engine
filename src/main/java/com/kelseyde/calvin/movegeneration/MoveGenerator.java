package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.movegeneration.check.PinCalculator;
import com.kelseyde.calvin.movegeneration.check.PinCalculator.PinData;
import com.kelseyde.calvin.movegeneration.check.RayCalculator;
import com.kelseyde.calvin.movegeneration.magic.Magics;
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
    private long checkersCount;

    private long pinMask;
    private long[] pinRayMasks;

    private long captureMask;
    private long pushMask;

    private List<Move> legalMoves;

    @Override
    public List<Move> generateMoves(Board board, boolean capturesOnly) {

        boolean isWhite = board.isWhiteToMove();
        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());

        captureMask = Bits.ALL_SQUARES;
        pushMask = Bits.ALL_SQUARES;

        PinData pinData = pinCalculator.calculatePinMask(board, isWhite);
        pinMask = pinData.pinMask();
        pinRayMasks = pinData.pinRayMasks();

        checkersMask = calculateAttackerMask(board, isWhite, 1L << kingSquare);
        checkersCount = Long.bitCount(checkersMask);

        legalMoves = new ArrayList<>();

        generateKingMoves(board, capturesOnly);

        // If we are in double-check, the only legal moves are king moves
        if (checkersCount == 2) {
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

        // Otherwise, generate all the other pseudo-legal moves
        generatePawnMoves(board, capturesOnly);
        generateKnightMoves(board, capturesOnly);
        generateBishopMoves(board, capturesOnly);
        generateRookMoves(board, capturesOnly);
        generateQueenMoves(board, capturesOnly);

        return legalMoves;

    }

    @Override
    public boolean isCheck(Board board, boolean isWhite) {
        long kingMask = isWhite ? board.getWhiteKing() : board.getBlackKing();
        return isAttacked(board, isWhite, kingMask);
    }

    private void generatePawnMoves(Board board, boolean capturesOnly) {

        boolean isWhite = board.isWhiteToMove();

        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();

        long opponents = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        long occupied = board.getOccupied();
        long enPassantFile = BitboardUtils.getFileBitboard(board.getGameState().getEnPassantFile());

        if (!capturesOnly) {
            long singleAdvances = isWhite ?
                    BitboardUtils.shiftNorth(pawns) &~ occupied &~ Bits.RANK_8 :
                    BitboardUtils.shiftSouth(pawns) &~ occupied &~ Bits.RANK_1;

            long singleAdvancesCopy = singleAdvances;
            singleAdvancesCopy &= pushMask;
            while (singleAdvancesCopy != 0) {
                int endSquare = BitboardUtils.getLSB(singleAdvancesCopy);
                int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
                if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                    legalMoves.add(new Move(startSquare, endSquare));
                }
                singleAdvancesCopy = BitboardUtils.popLSB(singleAdvancesCopy);
            }

            long doubleAdvances = isWhite ?
                    BitboardUtils.shiftNorth(singleAdvances) &~ occupied & Bits.RANK_4 :
                    BitboardUtils.shiftSouth(singleAdvances) &~ occupied & Bits.RANK_5;
            doubleAdvances &= pushMask;
            while (doubleAdvances != 0) {
                int endSquare = BitboardUtils.getLSB(doubleAdvances);
                int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
                if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                    legalMoves.add(new Move(startSquare, endSquare, Move.PAWN_DOUBLE_MOVE_FLAG));
                }
                doubleAdvances = BitboardUtils.popLSB(doubleAdvances);
            }

            long advancePromotions = isWhite ?
                    BitboardUtils.shiftNorth(pawns) &~ occupied & Bits.RANK_8 :
                    BitboardUtils.shiftSouth(pawns) &~ occupied & Bits.RANK_1;
            advancePromotions &= pushMask;
            while (advancePromotions != 0) {
                int endSquare = BitboardUtils.getLSB(advancePromotions);
                int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
                if (!isPinned(startSquare)) {
                    legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
                }
                advancePromotions = BitboardUtils.popLSB(advancePromotions);
            }
        }

        long leftCaptures = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_1;
        leftCaptures &= captureMask;
        while (leftCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.add(new Move(startSquare, endSquare));
            }
            leftCaptures = BitboardUtils.popLSB(leftCaptures);
        }

        long rightCaptures = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_8:
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_1;
        rightCaptures &= captureMask;
        while (rightCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.add(new Move(startSquare, endSquare));
            }
            rightCaptures = BitboardUtils.popLSB(rightCaptures);
        }

        long leftEnPassants = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_H :
                BitboardUtils.shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_H;
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

        long rightEnPassants = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_6 :
                BitboardUtils.shiftSouthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_3;
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

        long leftCapturePromotions = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_1;
        leftCapturePromotions &= (captureMask | pushMask);
        while (leftCapturePromotions != 0) {
            int endSquare = BitboardUtils.getLSB(leftCapturePromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            if (!isPinned(startSquare) || isMovingAlongPinRay(startSquare, endSquare)) {
                legalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            }
            leftCapturePromotions = BitboardUtils.popLSB(leftCapturePromotions);
        }

        long rightCapturePromotions = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_8 :
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_1;
        rightCapturePromotions &= (captureMask | pushMask);
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
        long knights = board.isWhiteToMove() ? board.getWhiteKnights() : board.getBlackKnights();
        long opponents = board.isWhiteToMove() ? board.getBlackPieces() : board.getWhitePieces();
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

        long king = board.isWhiteToMove() ? board.getWhiteKing() : board.getBlackKing();
        if (king == 0L) {
            return;
        }
        long friendlyPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupied();

        int startSquare = BitboardUtils.getLSB(king);

        long kingMoves = Bits.KING_ATTACKS[startSquare] &~ friendlyPieces;
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
        if (!capturesOnly && checkersCount == 0) {
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
                long opponents = board.isWhiteToMove() ? board.getBlackPieces() : board.getWhitePieces();
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
        long bishops = board.isWhiteToMove() ? board.getWhiteBishops() : board.getBlackBishops();
        generateSlidingMoves(board, capturesOnly, bishops, false, true);
    }

    private void generateRookMoves(Board board, boolean capturesOnly) {
        long rooks = board.isWhiteToMove() ? board.getWhiteRooks() : board.getBlackRooks();
        generateSlidingMoves(board, capturesOnly, rooks, true, false);
    }

    private void generateQueenMoves(Board board, boolean capturesOnly) {
        long queens = board.isWhiteToMove() ? board.getWhiteQueens() : board.getBlackQueens();
        generateSlidingMoves(board, capturesOnly, queens, true, true);
    }

    public long getPawnAttacks(Board board, int square, boolean isWhite) {
        long attackMask = 0L;
        long squareBB = 1L << square;
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();

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
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        return Bits.KNIGHT_ATTACKS[square] &~ friendlies;
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
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        return Bits.KING_ATTACKS[square] &~ friendlies;
    }

    private long getSlidingAttacks(Board board, int square, boolean isWhite, boolean isDiagonal, boolean isOrthogonal) {
        long attackMask = 0L;
        long occ = board.getOccupied();
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        if (isOrthogonal) {
            attackMask |= Magics.getRookAttacks(square, occ) &~ friendlies;
        }
        if (isDiagonal) {
            attackMask |= Magics.getBishopAttacks(square, occ) &~ friendlies;
        }
        return attackMask;
    }

    private long calculateAttackerMask(Board board, boolean isWhite, long squareMask) {
        long attackerMask = 0L;
        while (squareMask != 0) {
            int square = BitboardUtils.getLSB(squareMask);

            long opponentPawns = isWhite ? board.getBlackPawns() : board.getWhitePawns();
            long pawnAttackMask = getPawnAttacks(board, square, isWhite);
            attackerMask |= pawnAttackMask & opponentPawns;

            long opponentKnights = isWhite ? board.getBlackKnights() : board.getWhiteKnights();
            long knightAttackMask = getKnightAttacks(board, square, isWhite);
            attackerMask |= knightAttackMask & opponentKnights;

            long opponentBishops = isWhite ? board.getBlackBishops() : board.getWhiteBishops();
            long bishopAttackMask = getBishopAttacks(board, square, isWhite);
            attackerMask |= bishopAttackMask & opponentBishops;

            long opponentRooks = isWhite ? board.getBlackRooks() : board.getWhiteRooks();
            long rookAttackMask = getRookAttacks(board, square, isWhite);
            attackerMask |= rookAttackMask & opponentRooks;

            long opponentQueens = isWhite ? board.getBlackQueens() : board.getWhiteQueens();
            attackerMask |= (bishopAttackMask | rookAttackMask) & opponentQueens;

            long opponentKing = isWhite ? board.getBlackKing() : board.getWhiteKing();
            long kingAttackMask = getKingAttacks(board, square, isWhite);
            attackerMask |= kingAttackMask & opponentKing;

            squareMask = BitboardUtils.popLSB(squareMask);
        }
        return attackerMask;
    }

    private boolean isAttacked(Board board, boolean isWhite, long squareMask) {
        while (squareMask != 0) {
            int square = BitboardUtils.getLSB(squareMask);

            long opponentPawns = isWhite ? board.getBlackPawns() : board.getWhitePawns();
            if (opponentPawns != 0) {
                long pawnAttackMask = getPawnAttacks(board, square, isWhite);
                if ((pawnAttackMask & opponentPawns) != 0) {
                    return true;
                }
            }

            long opponentKnights = isWhite ? board.getBlackKnights() : board.getWhiteKnights();
            if (opponentKnights != 0) {
                long knightAttackMask = getKnightAttacks(board, square, isWhite);
                if ((knightAttackMask & opponentKnights) != 0) {
                    return true;
                }
            }

            long opponentBishops = isWhite ? board.getBlackBishops() : board.getWhiteBishops();
            long opponentQueens = isWhite ? board.getBlackQueens() : board.getWhiteQueens();
            long diagonalSliders = opponentBishops | opponentQueens;
            if (diagonalSliders != 0) {
                long bishopAttackMask = getBishopAttacks(board, square, isWhite);
                if ((bishopAttackMask & diagonalSliders) != 0) {
                    return true;
                }
            }

            long opponentRooks = isWhite ? board.getBlackRooks() : board.getWhiteRooks();
            long orthogonalSliders = opponentRooks | opponentQueens;
            if (orthogonalSliders != 0) {
                long rookAttackMask = getRookAttacks(board, square, isWhite);
                if ((rookAttackMask & orthogonalSliders) != 0) {
                    return true;
                }
            }

            long opponentKing = isWhite ? board.getBlackKing() : board.getWhiteKing();
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
