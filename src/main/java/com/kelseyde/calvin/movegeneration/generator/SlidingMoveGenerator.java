package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.movegeneration.magic.MagicBitboards;
import com.kelseyde.calvin.board.move.Move;

import java.util.HashSet;
import java.util.Set;

public abstract class SlidingMoveGenerator implements PseudoLegalMoveGenerator {

    /**
     * @return the bitboard containing all the pieces of this type and colour.
     */
    protected abstract long getSliders(Board board, boolean isWhite);

    /**
     * @return whether this slider can move orthogonally.
     */
    protected abstract boolean isOrthogonal();

    /**
     * @return whether this slider can move diagonally.
     */
    protected abstract boolean isDiagonal();

    /**
     * Generate the possible moves for sliding pieces of this type. Uses a strategy called 'hyperbola quintessence' to
     * quickly calculate the bitboards for vertical, horizontal, diagonal and anti-diagonal moves.
     *
     * @see <a href="https://www.chessprogramming.org/Hyperbola_Quintessence">Chess Programming Wiki</a>
     */
    @Override
    public Set<Move> generatePseudoLegalMoves(Board board) {
        Set<Move> moves = new HashSet<>();
        long pieceBitboard = getSliders(board, board.isWhiteToMove());
        while (pieceBitboard != 0) {
            int square = BitboardUtils.scanForward(pieceBitboard);
            long moveBitboard = 0L;
            if (isOrthogonal()) {
                moveBitboard |= calculateOrthogonalMoves(board, square, board.isWhiteToMove());
            }
            if (isDiagonal()) {
                moveBitboard |= calculateDiagonalMoves(board, square, board.isWhiteToMove());
            }
            pieceBitboard = BitboardUtils.popLSB(pieceBitboard);
            moves.addAll(addMoves(square, moveBitboard));
        }
        return moves;
    }

    @Override
    public long generateAttackMask(Board board, boolean isWhite) {
        long sliders = getSliders(board, isWhite);
        long attackMask = 0L;
        while (sliders != 0) {
            int slider = BitboardUtils.scanForward(sliders);
            attackMask |= generateAttackMaskFromSquare(board, slider, isWhite);
            sliders = BitboardUtils.popLSB(sliders);;
        }
        return attackMask;
    }

    @Override
    public long generateAttackMaskFromSquare(Board board, int square, boolean isWhite) {
        long attackMask = 0L;
        if (isOrthogonal()) {
            attackMask |= calculateOrthogonalMoves(board, square, isWhite);
        }
        if (isDiagonal()) {
            attackMask |= calculateDiagonalMoves(board, square, isWhite);
        }
        return attackMask;
    }

    private long calculateOrthogonalMoves(Board board, int s, boolean isWhite) {
        long occ = board.getOccupied();
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        return MagicBitboards.getRookAttacks(s, occ) &~ friendlies;
    }

    private long calculateDiagonalMoves(Board board, int s, boolean isWhite) {
        long occ = board.getOccupied();
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        return MagicBitboards.getBishopAttacks(s, occ) &~ friendlies;
    }

    private Set<Move> addMoves(int startSquare, long moveBitboard) {
        Set<Move> moves = new HashSet<>();
        while (moveBitboard != 0) {
            int endSquare = BitboardUtils.scanForward(moveBitboard);
            moves.add(Move.builder()
                    .pieceType(getPieceType())
                    .startSquare(startSquare)
                    .endSquare(endSquare)
                    .build());
            moveBitboard = BitboardUtils.popLSB(moveBitboard);
        }
        return moves;
    }

}
