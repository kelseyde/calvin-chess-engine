package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardConstants;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;

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
     * @see <a href="https://www.chessprogramming.org/Hyperbola_Quintessence">Chess Programming Wiki</a>
     */
    @Override
    public Set<Move> generatePseudoLegalMoves(Board board) {
        Set<Move> moves = new HashSet<>();
        long pieceBitboard = getSliders(board, board.isWhiteToMove());
        while (pieceBitboard != 0) {
            int square = BitBoardUtils.scanForward(pieceBitboard);
            long moveBitboard = 0L;
            if (isOrthogonal()) {
                moveBitboard |= calculateOrthogonalMoves(board, square, board.isWhiteToMove());
            }
            if (isDiagonal()) {
                moveBitboard |= calculateDiagonalMoves(board, square, board.isWhiteToMove());
            }
            pieceBitboard = BitBoardUtils.popLSB(pieceBitboard);
            moves.addAll(addMoves(square, moveBitboard));
        }
        return moves;
    }

    @Override
    public long generateAttackMask(Board board, boolean isWhite) {
        long sliders = getSliders(board, isWhite);
        long attackMask = 0L;
        while (sliders != 0) {
            int slider = BitBoardUtils.scanForward(sliders);
            if (isOrthogonal()) {
                attackMask |= calculateOrthogonalMoves(board, slider, isWhite);
            }
            if (isDiagonal()) {
                attackMask |= calculateDiagonalMoves(board, slider, isWhite);
            }
            sliders = BitBoardUtils.popLSB(sliders);;
        }
        return attackMask;
    }

    private long calculateOrthogonalMoves(Board board, int s, boolean isWhite) {
        long slider = 1L << s;
        long occ = board.getOccupied();
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        long[] ranks = BitBoardConstants.RANK_MASKS;
        long[] files = BitBoardConstants.FILE_MASKS;

        long horizontalMoves = (occ - 2 * slider) ^ Long.reverse(Long.reverse(occ) - 2 * Long.reverse(slider));
        long verticalMoves = ((occ & files[s % 8]) - (2 * slider)) ^ Long.reverse(Long.reverse(occ & files[s % 8]) - (2 * Long.reverse(slider)));
        return ((horizontalMoves & ranks[s / 8] | verticalMoves & files[s % 8])) &~ friendlies;
    }

    private long calculateDiagonalMoves(Board board, int s, boolean isWhite) {
        long slider = 1L << s;
        long occ = board.getOccupied();
        long friendlies = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        long[] diagonals = BitBoardConstants.DIAGONAL_MASKS;
        long[] antiDiagonals = BitBoardConstants.ANTI_DIAGONAL_MASKS;

        long diagonalMoves = ((occ & diagonals[(s / 8) + (s % 8)]) - (2 * slider)) ^
                Long.reverse(Long.reverse(occ & diagonals[(s / 8) + (s % 8)]) - (2 * Long.reverse(slider)));
        long antiDiagonalMoves = ((occ & antiDiagonals[(s / 8) + 7 - (s % 8)]) - (2 * slider)) ^
                Long.reverse(Long.reverse(occ & antiDiagonals[(s / 8) + 7 - (s % 8)]) - (2 * Long.reverse(slider)));
        return ((diagonalMoves & diagonals[(s / 8) + (s % 8)]) | (antiDiagonalMoves & antiDiagonals[(s / 8) + 7 - (s % 8)])) &~ friendlies;
    }

    private Set<Move> addMoves(int startSquare, long moveBitboard) {
        Set<Move> moves = new HashSet<>();
        while (moveBitboard != 0) {
            int endSquare = BitBoardUtils.scanForward(moveBitboard);
            moves.add(Move.builder()
                    .pieceType(getPieceType())
                    .startSquare(startSquare)
                    .endSquare(endSquare)
                    .build());
            moveBitboard = BitBoardUtils.popLSB(moveBitboard);
        }
        return moves;
    }

}
