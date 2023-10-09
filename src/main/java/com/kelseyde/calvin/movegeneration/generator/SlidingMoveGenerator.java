package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.movegeneration.magic.Magics;

import java.util.ArrayList;
import java.util.List;

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
    public List<Move> generatePseudoLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        boolean isWhite = board.isWhiteToMove();
        long pieceBitboard = getSliders(board, isWhite);
        while (pieceBitboard != 0) {
            int startSquare = BitboardUtils.getLSB(pieceBitboard);
            long moveBitboard = generateAttackMaskFromSquare(board, startSquare, isWhite);
            pieceBitboard = BitboardUtils.popLSB(pieceBitboard);
            while (moveBitboard != 0) {
                int endSquare = BitboardUtils.getLSB(moveBitboard);
                moves.add(new Move(startSquare, endSquare));
                moveBitboard = BitboardUtils.popLSB(moveBitboard);
            }
        }
        return moves;
    }

    @Override
    public long generateAttackMask(Board board, boolean isWhite) {
        long sliders = getSliders(board, isWhite);
        long attackMask = 0L;
        while (sliders != 0) {
            int slider = BitboardUtils.getLSB(sliders);
            attackMask |= generateAttackMaskFromSquare(board, slider, isWhite);
            sliders = BitboardUtils.popLSB(sliders);
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
        return Magics.getRookAttacks(s, occ) &~ friendlies;
    }

    private long calculateDiagonalMoves(Board board, int s, boolean isWhite) {
        long occ = board.getOccupied();
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        return Magics.getBishopAttacks(s, occ) &~ friendlies;
    }

}
