package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.BitBoard;
import com.kelseyde.calvin.model.BitBoards;
import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.move.Move;

import java.util.HashSet;
import java.util.Set;

public abstract class SlidingMoveGenerator implements PseudoLegalMoveGenerator {

    /**
     * @return the bitboard containing all the pieces of this type and colour.
     */
    protected abstract long getPieceBitboard(Board board);

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
        long pieceBitboard = getPieceBitboard(board);
        while (pieceBitboard != 0) {
            int square = BitBoard.scanForward(pieceBitboard);
            long moveBitboard = 0L;
            if (isOrthogonal()) {
                moveBitboard |= calculateOrthogonalMoves(board, square);
            }
            if (isDiagonal()) {
                moveBitboard |= calculateDiagonalMoves(board, square);
            }
            pieceBitboard = BitBoard.popLSB(pieceBitboard);
            moves.addAll(addMoves(square, moveBitboard));
        }
        return moves;
    }

    private long calculateOrthogonalMoves(Board board, int s) {
        long slider = 1L << s;
        long occ = board.getOccupied();
        long friendlies = board.getTurn().isWhite() ? board.getWhitePieces() : board.getBlackPieces();
        long[] ranks = BitBoards.RANK_MASKS;
        long[] files = BitBoards.FILE_MASKS;

        long horizontalMoves = (occ - 2 * slider) ^ Long.reverse(Long.reverse(occ) - 2 * Long.reverse(slider));
        long verticalMoves = ((occ & files[s % 8]) - (2 * slider)) ^ Long.reverse(Long.reverse(occ & files[s % 8]) - (2 * Long.reverse(slider)));
        return ((horizontalMoves & ranks[s / 8] | verticalMoves & files[s % 8])) &~ friendlies;
    }

    private long calculateDiagonalMoves(Board board, int s) {
        long slider = 1L << s;
        long occ = board.getOccupied();
        long friendlies = board.getTurn().isWhite() ? board.getWhitePieces() : board.getBlackPieces();
        long[] diagonals = BitBoards.DIAGONAL_MASKS;
        long[] antiDiagonals = BitBoards.ANTI_DIAGONAL_MASKS;

        long diagonalMoves = ((occ & diagonals[(s / 8) + (s % 8)]) - (2 * slider)) ^
                Long.reverse(Long.reverse(occ & diagonals[(s / 8) + (s % 8)]) - (2 * Long.reverse(slider)));
        long antiDiagonalMoves = ((occ & antiDiagonals[(s / 8) + 7 - (s % 8)]) - (2 * slider)) ^
                Long.reverse(Long.reverse(occ & antiDiagonals[(s / 8) + 7 - (s % 8)]) - (2 * Long.reverse(slider)));
        return ((diagonalMoves & diagonals[(s / 8) + (s % 8)]) | (antiDiagonalMoves & antiDiagonals[(s / 8) + 7 - (s % 8)])) &~ friendlies;
    }

    private Set<Move> addMoves(int startSquare, long moveBitboard) {
        Set<Move> moves = new HashSet<>();
        while (moveBitboard != 0) {
            int endSquare = BitBoard.scanForward(moveBitboard);
            moves.add(Move.builder()
                    .pieceType(getPieceType())
                    .startSquare(startSquare)
                    .endSquare(endSquare)
                    .build());
            moveBitboard = BitBoard.popLSB(moveBitboard);
        }
        return moves;
    }





}
