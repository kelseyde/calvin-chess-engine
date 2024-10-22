package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.*;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates all the pseudo-legal moves in a given position.
 */
public class MoveGenerator {

    private MoveFilter filter;
    private boolean white;

    private long pawns;
    private long knights;
    private long bishops;
    private long rooks;
    private long queens;
    private long king;

    private List<Move> legalMoves;

    public List<Move> generateLegalMoves(Board board) {
        return generateMoves(board, MoveFilter.ALL).stream()
                .filter(move -> isLegal(board, move))
                .toList();
    }

    public List<Move> generateMoves(Board board) {
        return generateMoves(board, MoveFilter.ALL);
    }

    public List<Move> generateMoves(Board board, MoveFilter filter) {

        this.white = board.isWhite();
        this.filter = filter;
        this.legalMoves = new ArrayList<>(estimateLegalMoves());
        initPieces(board, white);

        generateKingMoves(board);
        generatePawnMoves(board);
        generateKnightMoves(board);
        generateAllSlidingMoves(board);
        generateCastlingMoves(board);

        return legalMoves;

    }

    public boolean isCheck(Board board, boolean white) {
        final long king = board.getKing(white);
        return isAttacked(board, white, king);
    }

    private void generatePawnMoves(Board board) {
        if (pawns == 0) return;

        final long opponents = board.getPieces(!white);
        final long occupied = board.getOccupied();
        final int opponentKing = Bits.next(board.getKing(!white));

        // Precompute attack and filter masks
        final long opponentAttackMask = Attacks.pawnAttacks(Bits.of(opponentKing), !white);
        final long filterMask = getFilterMask(opponents, opponentAttackMask);

        if (filterMask == Square.NONE) return;

        // Single and double pawn pushes
        if (filter != MoveFilter.CAPTURES_ONLY) {
            generatePawnPushes(occupied, filterMask);
        }

        // Pawn captures, en passant, and promotions
        if (filter != MoveFilter.QUIET) {
            generatePawnCaptures(opponents, filterMask);
            generatePromotions(opponents, occupied);
            generateEnPassant(board);
        }
    }

    private void generatePawnPushes(long occupied, long filterMask) {
        // Single and double pawn pushes combined
        long singleMoves = Attacks.pawnSingleMoves(pawns, occupied, white) & filterMask;
        long doubleMoves = Attacks.pawnDoubleMoves(pawns, occupied, white) & filterMask;

        while (singleMoves != 0) {
            final int to = Bits.next(singleMoves);
            final int from = white ? to - 8 : to + 8;
            legalMoves.add(new Move(from, to));
            singleMoves = Bits.pop(singleMoves);
        }

        while (doubleMoves != 0) {
            final int to = Bits.next(doubleMoves);
            final int from = white ? to - 16 : to + 16;
            legalMoves.add(new Move(from, to, Move.PAWN_DOUBLE_MOVE_FLAG));
            doubleMoves = Bits.pop(doubleMoves);
        }
    }

    private void generatePawnCaptures(long opponents, long filterMask) {
        long leftCaptures = Attacks.pawnLeftCaptures(pawns, opponents, white) & filterMask;
        long rightCaptures = Attacks.pawnRightCaptures(pawns, opponents, white) & filterMask;

        while (leftCaptures != 0) {
            final int to = Bits.next(leftCaptures);
            final int from = white ? to - 7 : to + 9;
            legalMoves.add(new Move(from, to));
            leftCaptures = Bits.pop(leftCaptures);
        }

        while (rightCaptures != 0) {
            final int to = Bits.next(rightCaptures);
            final int from = white ? to - 9 : to + 7;
            legalMoves.add(new Move(from, to));
            rightCaptures = Bits.pop(rightCaptures);
        }
    }

    private void generatePromotions(long opponents, long occupied) {
        final long pushPromotions = Attacks.pawnPushPromotions(pawns, occupied, white);
        final long leftCapturePromotions = Attacks.pawnLeftCapturePromotions(pawns, opponents, white);
        final long rightCapturePromotions = Attacks.pawnRightCapturePromotions(pawns, opponents, white);

        generatePromotionMoves(pushPromotions, 8, 8);
        generatePromotionMoves(leftCapturePromotions, 7, 9);
        generatePromotionMoves(rightCapturePromotions, 9, 7);
    }

    private void generatePromotionMoves(long promotionMask, int offsetWhite, int offsetBlack) {
        while (promotionMask != 0) {
            final int to = Bits.next(promotionMask);
            final int from = white ? to - offsetWhite : to + offsetBlack;
            legalMoves.addAll(getPromotionMoves(from, to));
            promotionMask = Bits.pop(promotionMask);
        }
    }

    private void generateEnPassant(Board board) {
        if (board.getState().getEnPassantFile() < 0) return;

        final long enPassantFile = File.toBitboard(board.getState().getEnPassantFile());
        final long leftEnPassants = Attacks.pawnLeftEnPassants(pawns, enPassantFile, white);
        final long rightEnPassants = Attacks.pawnRightEnPassants(pawns, enPassantFile, white);

        generateEnPassantMoves(board, leftEnPassants, 7, 9);
        generateEnPassantMoves(board, rightEnPassants, 9, 7);
    }

    private void generateEnPassantMoves(Board board, long enPassantMask, int offsetWhite, int offsetBlack) {
        while (enPassantMask != 0) {
            final int to = Bits.next(enPassantMask);
            final int from = white ? to - offsetWhite : to + offsetBlack;
            legalMoves.add(new Move(from, to, Move.EN_PASSANT_FLAG));
            enPassantMask = Bits.pop(enPassantMask);
        }
    }

    private long getFilterMask(long opponents, long opponentAttackMask) {
        return switch (filter) {
            case ALL -> Square.ALL;
            case CAPTURES_ONLY -> opponents;
            case NOISY -> opponents | opponentAttackMask;
            case QUIET -> ~opponents & ~opponentAttackMask;
        };
    }

    private void generateKnightMoves(Board board) {
        if (knights == 0) return;
        final long opponents = board.getPieces(!white);
        final int opponentKing = Bits.next(board.getKing(!white));

        // Initialize filter mask based on move filter type
        final long filterMask = switch (filter) {
            case ALL -> Square.ALL;
            case CAPTURES_ONLY -> opponents;
            case NOISY -> opponents | Attacks.knightAttacks(opponentKing);
            case QUIET -> ~opponents & ~Attacks.knightAttacks(opponentKing);
        };
        if (filterMask == Square.NONE) {
            return;
        }

        // Exclude pinned knights from generating moves
        long unpinnedKnights = knights;

        // Generate legal knight moves
        while (unpinnedKnights != 0) {
            final int from = Bits.next(unpinnedKnights);
            long possibleMoves = getKnightAttacks(board, from, white) & filterMask;
            while (possibleMoves != 0) {
                final int to = Bits.next(possibleMoves);
                legalMoves.add(new Move(from, to));
                possibleMoves = Bits.pop(possibleMoves);
            }
            unpinnedKnights = Bits.pop(unpinnedKnights);
        }
    }

    private void generateKingMoves(Board board) {
        final int from = Bits.next(king);
        final long friendlies = board.getPieces(white);
        final long opponents = board.getPieces(!white);

        final long filterMask = switch (filter) {
            case ALL -> Square.ALL;
            case CAPTURES_ONLY, NOISY -> opponents;
            case QUIET -> ~opponents;
        };
        if (filterMask == Square.NONE) {
            return;
        }

        long kingMoves = Attacks.kingAttacks(from) & ~friendlies & filterMask;

        while (kingMoves != 0) {
            final int to = Bits.next(kingMoves);
            legalMoves.add(new Move(from, to));
            kingMoves = Bits.pop(kingMoves);
        }

    }

    private void generateCastlingMoves(Board board) {
        if ((filter != MoveFilter.ALL && filter != MoveFilter.QUIET)) {
            return;
        }
        final int from = Bits.next(king);

        int to = getCastleEndSquare(white, true);
        legalMoves.add(new Move(from, to, Move.CASTLE_FLAG));

        to = getCastleEndSquare(white, false);
        legalMoves.add(new Move(from, to, Move.CASTLE_FLAG));

    }

    private void generateAllSlidingMoves(Board board) {
        if (filter == MoveFilter.ALL) {
            final long diagonalSliders = bishops | queens;
            final long orthogonalSliders = rooks | queens;
            generateSlidingMoves(board, diagonalSliders, false, true);
            generateSlidingMoves(board, orthogonalSliders, true, false);
        } else {
            generateSlidingMoves(board, bishops, false, true);
            generateSlidingMoves(board, rooks, true, false);
            generateSlidingMoves(board, queens, true, true);
        }
    }

    private void generateSlidingMoves(Board board, long sliders, boolean isOrthogonal, boolean isDiagonal) {
        final long opponents = board.getPieces(!white);
        final long occupied = board.getOccupied();
        final long friendlies = board.getPieces(white);

        // Apply move filters
        final long filterMask = switch (filter) {
            case ALL -> Square.ALL;
            case CAPTURES_ONLY -> opponents;
            case NOISY -> getCaptureAndCheckMask(board, white, opponents, occupied, isDiagonal, isOrthogonal);
            case QUIET -> ~getCaptureAndCheckMask(board, white, opponents, occupied, isDiagonal, isOrthogonal);
        };
        if (filterMask == Square.NONE) {
            return;
        }

        while (sliders != 0) {
            final int from = Bits.next(sliders);
            long attackMask = getSlidingAttacks(from, friendlies, occupied, isDiagonal, isOrthogonal);

            attackMask &= filterMask;

            sliders = Bits.pop(sliders);
            while (attackMask != 0) {
                final int to = Bits.next(attackMask);
                legalMoves.add(new Move(from, to));
                attackMask = Bits.pop(attackMask);
            }
        }
    }

    private long getCaptureAndCheckMask(Board board, boolean white, long opponents, long occupied, boolean isDiagonal, boolean isOrthogonal) {
        final int opponentKing = Bits.next(board.getKing(!white));
        long filterMask = opponents;
        if (isDiagonal) {
            filterMask |= Attacks.bishopAttacks(opponentKing, occupied);
        }
        if (isOrthogonal) {
            filterMask |= Attacks.rookAttacks(opponentKing, occupied);
        }
        return filterMask;
    }

    public long getPawnAttacks(Board board, int square, boolean white) {
        long attackMask = 0L;
        final long squareBB = Bits.of(square);
        final long friendlies = board.getPieces(white);

        long leftCapture = white ?
                Bits.northWest(squareBB) &~ friendlies &~ File.H :
                Bits.southWest(squareBB) &~ friendlies &~ File.H;
        attackMask |= leftCapture;

        long rightCapture = white ?
                Bits.northEast(squareBB) &~ friendlies &~ File.A :
                Bits.southEast(squareBB) &~ friendlies &~ File.A;
        attackMask |= rightCapture;

        return attackMask;
    }

    public long getKnightAttacks(Board board, int square, boolean white) {
        final long friendlies = board.getPieces(white);
        return Attacks.knightAttacks(square) &~ friendlies;
    }

    public long getBishopAttacks(Board board, int square, boolean white) {
        final long occupied = board.getOccupied();
        final long friendlies = board.getPieces(white);
        return getSlidingAttacks(square, friendlies, occupied, true, false);
    }

    public long getRookAttacks(Board board, int square, boolean white) {
        final long occupied = board.getOccupied();
        final long friendlies = board.getPieces(white);
        return getSlidingAttacks(square, friendlies, occupied, false, true);
    }

    public long getQueenAttacks(Board board, int square, boolean white) {
        final long occupied = board.getOccupied();
        final long friendlies = board.getPieces(white);
        return getSlidingAttacks(square, friendlies, occupied, true, true);
    }

    public long getKingAttacks(Board board, int square, boolean white) {
        final long friendlies = board.getPieces(white);
        return Attacks.kingAttacks(square) &~ friendlies;
    }

    private long getSlidingAttacks(int square, long friendlies, long occ, boolean isDiagonal, boolean isOrthogonal) {
        long attackMask = 0L;
        if (isOrthogonal) {
            attackMask |= Attacks.rookAttacks(square, occ);
        }
        if (isDiagonal) {
            attackMask |= Attacks.bishopAttacks(square, occ);
        }
        return attackMask &~ friendlies;
    }

    private boolean isAttacked(Board board, boolean white, long squareMask) {

        final long opponentPawns = board.getPawns(!white);
        if (opponentPawns != 0) {
            long pawnAttackMask = Attacks.pawnAttacks(squareMask, white);
            if ((pawnAttackMask & opponentPawns) != 0) {
                return true;
            }
        }

        final long occupied = board.getOccupied();

        while (squareMask != 0) {
            final int square = Bits.next(squareMask);

            final long opponentKnights = board.getKnights(!white);
            if (opponentKnights != 0) {
                if ((Attacks.knightAttacks(square) & opponentKnights) != 0) {
                    return true;
                }
            }

            final long opponentBishops = board.getBishops(!white);
            final long opponentQueens = board.getQueens(!white);
            final long diagonalSliders = opponentBishops | opponentQueens;
            if (diagonalSliders != 0) {
                if ((Attacks.bishopAttacks(square, occupied) & diagonalSliders) != 0) {
                    return true;
                }
            }

            final long opponentRooks = board.getRooks(!white);
            final long orthogonalSliders = opponentRooks | opponentQueens;
            if (orthogonalSliders != 0) {
                if ((Attacks.rookAttacks(square, occupied) & orthogonalSliders) != 0) {
                    return true;
                }
            }

            final long opponentKing = board.getKing(!white);
            if ((Attacks.kingAttacks(square) & opponentKing) != 0) {
                return true;
            }

            squareMask = Bits.pop(squareMask);
        }
        return false;
    }


    public boolean isPseudoLegal(Board board, Move move) {

        if (move == null)
            return false;

        final boolean white = board.isWhite();
        final int from = move.from();
        final int to = move.to();
        final Piece piece = board.pieceAt(from);
        final long occupied = board.getOccupied();

        // Can't move without a piece
        if (piece == null)
            return false;

        // Can't move from an empty square
        if (!Bits.contains(board.getPieces(white), from))
            return false;

        Piece captured = board.pieceAt(to);
        if (captured != null) {

            // Can't capture our own piece
            if (Bits.contains(board.getPieces(white), to))
                return false;

            // Can't capture a king
            if (Bits.contains(board.getKings(), to))
                return false;

        }

        if (move.isCastling()) {

            // Can only castle with a king
            if (piece != Piece.KING)
                return false;

            // Must be castling on the home rank
            long rank = white ? Rank.FIRST : Rank.EIGHTH;
            if (!Bits.contains(rank, from) || !Bits.contains(rank, to))
                return false;

            int kingsideCastleSquare = white ? 6 : 62;
            int queensideCastleSquare = white ? 2 : 58;

            // Must be valid castling squares
            return to == kingsideCastleSquare || to == queensideCastleSquare;

        }

        if (piece == Piece.PAWN) {

            if (move.isEnPassant()) {

                // Can't en passant if there's no en passant square
                if (board.getState().getEnPassantFile() < 0)
                    return false;

                final int epSquare = white ? to - 8 : to + 8;

                // Can't en passant if there's no enemy pawn to capture
                if (!Bits.contains(board.getPawns(!white), epSquare))
                    return false;

            }

            int fromRank = Rank.of(from);
            int toRank = Rank.of(to);

            // Can't move backwards
            if ((white && fromRank >= toRank) || (!white && fromRank <= toRank))
                return false;

            // Must promote on the promo rank, and can't promote on any other rank
            long promoRank = white ? Rank.EIGHTH : Rank.FIRST;
            if (move.isPromotion() != Bits.contains(promoRank, to))
                return false;

            int fromFile = File.of(from);
            int toFile = File.of(to);

            // Pawn captures
            if (fromFile != toFile) {

                // Must capture on an adjacent file
                if (toFile != fromFile + 1 && toFile != fromFile - 1)
                    return false;

                // Must be capturing a piece
                return captured != null || move.isEnPassant();

            } else {
                // Can't capture a piece with a pawn push
                if (captured != null)
                    return false;

                if (move.isPawnDoubleMove()) {

                    // Can't double push from the wrong rank
                    long startRank = white ? Rank.SECOND : Rank.SEVENTH;
                    if (!Bits.contains(startRank, from))
                        return false;

                    // Can't double push if there's a piece in the way
                    int betweenSquare = white ? from + 8 : from - 8;
                    return !Bits.contains(occupied, betweenSquare);

                }
            }

        } else {

            // Can't make pawn-specific moves with a non-pawn
            if (move.isPawnDoubleMove() || move.isEnPassant() || move.isPromotion())
                return false;

            long attacks = switch (piece) {
                case KNIGHT ->  getKnightAttacks(board, from, white);
                case BISHOP ->  getBishopAttacks(board, from, white);
                case ROOK ->    getRookAttacks(board, from, white);
                case QUEEN ->   getQueenAttacks(board, from, white);
                case KING ->    getKingAttacks(board, from, white);
                default -> 0L;
            };

            // Must be a valid move for the piece
            return Bits.contains(attacks, to);

        }

        return true;

    }

    public boolean isLegal(Board board, Move move) {

        //System.out.println(Move.toUCI(move));

        final boolean white = board.isWhite();
        final int from = move.from();
        final int to = move.to();
        final long occupied = board.getOccupied();
        final long king = board.getKing(white);
        final int kingSq = Bits.next(king);

        if (move.isCastling()) {

            boolean kingside = to == (white ? 6 : 62);

            // Must have kingside rights
            if (kingside && !board.getState().isKingsideCastlingAllowed(white))
                return false;

            // Must have queenside rights
            if (!kingside && !board.getState().isQueensideCastlingAllowed(white))
                return false;

            final long travelSquares = getCastleTravelSquares(white, kingside);
            final long blockedSquares = travelSquares & occupied;
            final long safeSquares = getCastleSafeSquares(white, kingside);

            // Can't castle through check
            return blockedSquares == 0 && !isAttacked(board, white, safeSquares);

        }
        else if (move.isEnPassant()) {

            int captureSquare = white ? to - 8 : to + 8;
            long occAfterMove = occupied
                    ^ Bits.of(from)
                    ^ Bits.of(to)
                    ^ Bits.of(captureSquare);

            long queens = board.getQueens(!white);
            long bishops = board.getBishops(!white);

            boolean diagonalCheck = (Attacks.bishopAttacks(kingSq, occAfterMove) & (queens | bishops)) != 0;
            if (diagonalCheck)
                return false;

            long rooks = board.getRooks(!white);
            boolean orthogonalCheck = (Attacks.rookAttacks(kingSq, occAfterMove) & (queens | rooks)) != 0;
            return !orthogonalCheck;

        }

        final Piece piece = board.pieceAt(from);

        if (piece == Piece.KING) {

            long occWithoutKing = occupied ^ Bits.of(from);

            long queens = board.getQueens(!white);
            long bishops = board.getBishops(!white);
            long rooks = board.getRooks(!white);
            long threats = board.state.threats;

            return (threats & Bits.of(to)) == 0
                    && (Attacks.bishopAttacks(to, occWithoutKing) & (queens | bishops)) == 0
                    && (Attacks.rookAttacks(to, occWithoutKing) & (queens | rooks)) == 0;

        }

        final long checkers = board.state.checkers;
        final long pinned = board.state.pinned;
        if (Bits.count(checkers) > 1) {
            return false;
        }

        if (Bits.contains(pinned, from)) {
            int pinSquare = Bits.next(pinned & Bits.of(from));
            long ray = Ray.intersecting(pinSquare, kingSq);
            if (!Bits.contains(ray, to))
                return false;
        }

        if (Bits.empty(checkers)) {
            return true;
        }

        int checker = Bits.next(checkers);
        long ray = checkers | Ray.between(checker, kingSq);

        return Bits.contains(ray, to);
    }

    private List<Move> getPromotionMoves(int from, int to) {
        return List.of(new Move(from, to, Move.PROMOTE_TO_QUEEN_FLAG),
                        new Move(from, to, Move.PROMOTE_TO_ROOK_FLAG),
                        new Move(from, to, Move.PROMOTE_TO_BISHOP_FLAG),
                        new Move(from, to, Move.PROMOTE_TO_KNIGHT_FLAG));
    }


    private boolean leavesKingInCheck(Board board, Move move, boolean white) {
        board.makeMove(move);
        final int kingSquare = white ? Bits.next(board.getKing(true)) : Bits.next(board.getKing(false));
        final boolean isAttacked = isAttacked(board, white, Bits.of(kingSquare));
        board.unmakeMove();
        return isAttacked;
    }

    private long getCastleTravelSquares(boolean white, boolean isKingside) {
        if (isKingside) return white ? Castling.WHITE_KINGSIDE_TRAVEL_MASK : Castling.BLACK_KINGSIDE_TRAVEL_MASK;
        else return white ? Castling.WHITE_QUEENSIDE_TRAVEL_MASK : Castling.BLACK_QUEENSIDE_TRAVEL_MASK;
    }

    private long getCastleSafeSquares(boolean white, boolean isKingside) {
        if (isKingside) return white ? Castling.WHITE_KINGSIDE_SAFE_MASK : Castling.BLACK_KINGSIDE_SAFE_MASK;
        else return white ? Castling.WHITE_QUEENSIDE_SAFE_MASK : Castling.BLACK_QUEENSIDE_SAFE_MASK;
    }

    private int getCastleEndSquare(boolean white, boolean isKingside) {
        if (isKingside) return white ? 6 : 62;
        else return white ? 2 : 58;
    }

    /**
     * Estimate the number of legal moves in the current position, based on the piece count and
     * the average number of legal moves per piece. Used to initialise the legal moves ArrayList
     * with a 'best guess', to reduce the number of times the ArrayList has to grow during move
     * generation, yielding a small increase in performance.
     */
    private int estimateLegalMoves() {
        return (Bits.count(pawns) * 2) +
                (Bits.count(knights) * 3) +
                (Bits.count(bishops) * 3) +
                (Bits.count(rooks) * 6) +
                (Bits.count(queens) * 9) +
                (Bits.count(king) * 3);
    }

    private void initPieces(Board board, boolean white) {
        this.pawns = board.getPawns(white);
        this.knights = board.getKnights(white);
        this.bishops = board.getBishops(white);
        this.rooks = board.getRooks(white);
        this.queens = board.getQueens(white);
        this.king = board.getKing(white);
    }

    public enum MoveFilter {
        ALL,
        NOISY,
        QUIET,
        CAPTURES_ONLY,
    }
}
