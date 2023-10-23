package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.movegeneration.check.PinCalculator;
import com.kelseyde.calvin.movegeneration.check.RayCalculator;
import com.kelseyde.calvin.movegeneration.magic.Magics;
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

    private final PinCalculator pinCalculator = new PinCalculator();
    private final RayCalculator rayCalculator = new RayCalculator();

    private long pinMask;
    private long checkersMask;

    private List<Move> pseudoLegalMoves;

    @Override
    public List<Move> generateMoves(Board board, boolean capturesOnly) {

        pseudoLegalMoves = new ArrayList<>();
        List<Move> legalMoves = new ArrayList<>();
        boolean isWhite = board.isWhiteToMove();
        int kingSquare = isWhite ? BitboardUtils.getLSB(board.getWhiteKing()) : BitboardUtils.getLSB(board.getBlackKing());

        checkersMask = calculateAttackerMask(board, isWhite, 1L << kingSquare);
        pinMask = pinCalculator.calculatePinMask(board, isWhite);
        int checkersCount = Long.bitCount(checkersMask);

        generateKingMoves(board, capturesOnly);

        // If we are in double-check, the only legal moves are king moves
        boolean isDoubleCheck = checkersCount == 2;
        if (isDoubleCheck) {
            for (Move move : pseudoLegalMoves) {
                // Filter out moves that leave the king in check
                if (doesNotLeaveKingInCheck(board, move, kingSquare, isWhite)) {
                    legalMoves.add(move);
                }
            }
            return legalMoves;
        }

        // Otherwise, generate all the other pseudo-legal moves
        generatePawnMoves(board, capturesOnly);
        generateKnightMoves(board, capturesOnly);
        generateBishopMoves(board, capturesOnly);
        generateRookMoves(board, capturesOnly);
        generateQueenMoves(board, capturesOnly);

        boolean isCheck = checkersCount == 1;

        for (Move move : pseudoLegalMoves) {
            // If we are in single-check, filter out all moves that do not resolve the check
            if ((!isCheck || resolvesCheck(board, move, kingSquare, isWhite)) &&
                // Additionally, filter out moves that leave the king in (a new) check
                doesNotLeaveKingInCheck(board, move, kingSquare, isWhite)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;

    }

    @Override
    public boolean isCheck(Board board, boolean isWhite) {
        long kingMask = isWhite ? board.getWhiteKing() : board.getBlackKing();
        return isAttacked(board, isWhite, kingMask);
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

    private boolean doesNotLeaveKingInCheck(Board board, Move move, int kingSquare, boolean isWhite) {
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();

        // Check that none of the squares the king travels through to castle are attacked.
        if (move.isCastling()) {
            long castlingMask;
            if (BoardUtils.getFile(move.getEndSquare()) == 6) {
                castlingMask = isWhite ? Bits.WHITE_KINGSIDE_CASTLE_SAFE_MASK : Bits.BLACK_KINGSIDE_CASTLE_SAFE_MASK;
            } else {
                castlingMask = isWhite ? Bits.WHITE_QUEENSIDE_CASTLE_SAFE_MASK : Bits.BLACK_QUEENSIDE_CASTLE_SAFE_MASK;
            }
            return !isAttacked(board, isWhite, castlingMask);
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
            while (singleAdvancesCopy != 0) {
                int endSquare = BitboardUtils.getLSB(singleAdvancesCopy);
                int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
                pseudoLegalMoves.add(new Move(startSquare, endSquare));
                singleAdvancesCopy = BitboardUtils.popLSB(singleAdvancesCopy);
            }

            long doubleAdvances = isWhite ?
                    BitboardUtils.shiftNorth(singleAdvances) &~ occupied & Bits.RANK_4 :
                    BitboardUtils.shiftSouth(singleAdvances) &~ occupied & Bits.RANK_5;
            while (doubleAdvances != 0) {
                int endSquare = BitboardUtils.getLSB(doubleAdvances);
                int startSquare = isWhite ? endSquare - 16 : endSquare + 16;
                pseudoLegalMoves.add(new Move(startSquare, endSquare, Move.PAWN_DOUBLE_MOVE_FLAG));
                doubleAdvances = BitboardUtils.popLSB(doubleAdvances);
            }

            long advancePromotions = isWhite ?
                    BitboardUtils.shiftNorth(pawns) &~ occupied & Bits.RANK_8 :
                    BitboardUtils.shiftSouth(pawns) &~ occupied & Bits.RANK_1;
            while (advancePromotions != 0) {
                int endSquare = BitboardUtils.getLSB(advancePromotions);
                int startSquare = isWhite ? endSquare - 8 : endSquare + 8;
                pseudoLegalMoves.addAll(getPromotionMoves(startSquare, endSquare));
                advancePromotions = BitboardUtils.popLSB(advancePromotions);
            }
        }

        long leftCaptures = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_1;
        while (leftCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(leftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            pseudoLegalMoves.add(new Move(startSquare, endSquare));
            leftCaptures = BitboardUtils.popLSB(leftCaptures);
        }

        long rightCaptures = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_8:
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_1;
        while (rightCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(rightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            pseudoLegalMoves.add(new Move(startSquare, endSquare));
            rightCaptures = BitboardUtils.popLSB(rightCaptures);
        }

        long enPassantLeftCaptures = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_H :
                BitboardUtils.shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_H;
        while (enPassantLeftCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(enPassantLeftCaptures);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            pseudoLegalMoves.add(new Move(startSquare, endSquare, Move.EN_PASSANT_FLAG));
            enPassantLeftCaptures = BitboardUtils.popLSB(enPassantLeftCaptures);
        }

        long enPassantRightCaptures = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_6 :
                BitboardUtils.shiftSouthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_3;
        while (enPassantRightCaptures != 0) {
            int endSquare = BitboardUtils.getLSB(enPassantRightCaptures);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            pseudoLegalMoves.add(new Move(startSquare, endSquare, Move.EN_PASSANT_FLAG));
            enPassantRightCaptures = BitboardUtils.popLSB(enPassantRightCaptures);
        }

        long captureLeftPromotions = isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_1;
        while (captureLeftPromotions != 0) {
            int endSquare = BitboardUtils.getLSB(captureLeftPromotions);
            int startSquare = isWhite ? endSquare - 7 : endSquare + 9;
            pseudoLegalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            captureLeftPromotions = BitboardUtils.popLSB(captureLeftPromotions);
        }

        long captureRightPromotions = isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_8 :
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_1;
        while (captureRightPromotions != 0) {
            int endSquare = BitboardUtils.getLSB(captureRightPromotions);
            int startSquare = isWhite ? endSquare - 9 : endSquare + 7;
            pseudoLegalMoves.addAll(getPromotionMoves(startSquare, endSquare));
            captureRightPromotions = BitboardUtils.popLSB(captureRightPromotions);
        }

    }

    private void generateKnightMoves(Board board, boolean capturesOnly) {
        long knights = board.isWhiteToMove() ? board.getWhiteKnights() : board.getBlackKnights();
        long opponents = board.isWhiteToMove() ? board.getBlackPieces() : board.getWhitePieces();

        while (knights != 0) {
            int startSquare = BitboardUtils.getLSB(knights);
            long possibleMoves = getKnightAttacks(board, startSquare, board.isWhiteToMove());
            if (capturesOnly) {
                possibleMoves = possibleMoves & opponents;
            }
            while (possibleMoves != 0) {
                int endSquare = BitboardUtils.getLSB(possibleMoves);
                pseudoLegalMoves.add(new Move(startSquare, endSquare));
                possibleMoves = BitboardUtils.popLSB(possibleMoves);
            }
            knights = BitboardUtils.popLSB(knights);
        }
    }

    private void generateKingMoves(Board board, boolean capturesOnly) {

        long king = board.isWhiteToMove() ? board.getWhiteKing() : board.getBlackKing();
        if (king == 0L) {
            return;
        }
        long friendlyPieces = board.isWhiteToMove() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupied();

        int startSquare = BitboardUtils.getLSB(king);

        long kingMoves = Bits.KING_ATTACKS[startSquare] &~ friendlyPieces;
        if (capturesOnly) {
            long opponents = board.isWhiteToMove() ? board.getBlackPieces() : board.getWhitePieces();
            kingMoves = kingMoves & opponents;
        }
        while (kingMoves != 0) {
            int endSquare = BitboardUtils.getLSB(kingMoves);
            pseudoLegalMoves.add(new Move(startSquare, endSquare));
            kingMoves = BitboardUtils.popLSB(kingMoves);
        }
        if (!capturesOnly) {
            boolean isKingsideAllowed = board.getGameState().isKingsideCastlingAllowed(board.isWhiteToMove());
            if (isKingsideAllowed) {
                long travelSquares = board.isWhiteToMove() ? Bits.WHITE_KINGSIDE_CASTLE_TRAVEL_MASK : Bits.BLACK_KINGSIDE_CASTLE_TRAVEL_MASK;
                long blockedSquares = travelSquares & occupied;
                if (blockedSquares == 0) {
                    int endSquare = board.isWhiteToMove() ? 6 : 62;
                    pseudoLegalMoves.add(new Move(startSquare, endSquare, Move.CASTLE_FLAG));
                }
            }
            boolean isQueensideAllowed = board.getGameState().isQueensideCastlingAllowed(board.isWhiteToMove());
            if (isQueensideAllowed) {
                long travelSquares = board.isWhiteToMove() ? Bits.WHITE_QUEENSIDE_CASTLE_TRAVEL_MASK : Bits.BLACK_QUEENSIDE_CASTLE_TRAVEL_MASK;
                long blockedSquares = travelSquares & occupied;
                if (blockedSquares == 0) {
                    int endSquare = board.isWhiteToMove() ? 2 : 58;
                    pseudoLegalMoves.add(new Move(startSquare, endSquare, Move.CASTLE_FLAG));
                }
            }
        }
    }

    private void generateSlidingMoves(Board board, boolean capturesOnly, long sliders, boolean isOrthogonal, boolean isDiagonal) {
        boolean isWhite = board.isWhiteToMove();
        while (sliders != 0) {
            int startSquare = BitboardUtils.getLSB(sliders);
            long attackMask = getSlidingAttacks(board, startSquare, isWhite, isDiagonal, isOrthogonal);
            if (capturesOnly) {
                long opponents = board.isWhiteToMove() ? board.getBlackPieces() : board.getWhitePieces();
                attackMask = attackMask & opponents;
            }
            sliders = BitboardUtils.popLSB(sliders);
            while (attackMask != 0) {
                int endSquare = BitboardUtils.getLSB(attackMask);
                pseudoLegalMoves.add(new Move(startSquare, endSquare));
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

}
