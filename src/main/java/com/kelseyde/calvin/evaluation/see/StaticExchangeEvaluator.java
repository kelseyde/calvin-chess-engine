package com.kelseyde.calvin.evaluation.see;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.evaluation.PieceValues;
import com.kelseyde.calvin.movegeneration.MoveGenerator;

import java.util.NoSuchElementException;

/**
 * SEE, or 'Static Exchange Evaluation' function, calculates the change in material balance after a series of exchanges
 * on a single square. Very similar to the human player's heuristic of 'counting the attackers and defenders', it returns
 * an int value signifying the material loss or gain if all possible attackers and defenders of that square are traded
 * away.
 * Used in the quiescence search to prune capture nodes which are obviously detrimental to the side to move (like
 * exchanging your queen for a pawn). This improves search speed at the cost of potentially missing some tactical complications
 * elsewhere on the board.
 *
 * @see <a href="https://www.chessprogramming.org/Static_Exchange_Evaluation">Chess Programming Wiki</a>
 */
public class StaticExchangeEvaluator {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public int evaluate(Board board, Move move) {

        int score = 0;
        int square = move.getEndSquare();
        PieceType capturedPieceType = move.isEnPassant() ? PieceType.PAWN : board.pieceAt(square);
        if (capturedPieceType == null) {
            throw new NoSuchElementException("No piece to capture on square " + square);
        }
        score += PieceValues.valueOf(capturedPieceType);

        board.makeMove(move);
        Move leastValuableAttacker = getLeastValuableAttacker(board, square);
        if (leastValuableAttacker != null) {
            // The opponent should have the option of 'standing pat' - that is, declining to continue the capture
            // sequence if it would lead to a loss of material.
            // Therefore, we return the minimum of the stand-pat score and the capture score.
            score = Math.min(score, score - evaluate(board, leastValuableAttacker));
        }
        board.unmakeMove();
        return score;

    }

    private Move getLeastValuableAttacker(Board board, int square) {

        boolean isWhite = board.isWhiteToMove();

        long pawns = board.getPawns(isWhite);
        if (pawns > 0) {
            long pawnAttackMask = moveGenerator.getPawnAttacks(board, square, !isWhite);
            if ((pawnAttackMask & pawns) != 0) {
                int pawnStartSquare = BitboardUtils.getLSB(pawnAttackMask & pawns);
                return new Move(pawnStartSquare, square);
            }
        }

        long knights = board.getKnights(isWhite);
        if (knights > 0) {
            long knightAttackMask = moveGenerator.getKnightAttacks(board, square, !isWhite);
            if ((knightAttackMask & knights) != 0) {
                int knightStartSquare = BitboardUtils.getLSB(knightAttackMask & knights);
                return new Move(knightStartSquare, square);
            }
        }

        long bishops = board.getBishops(isWhite);
        if (bishops > 0) {
            long bishopAttackMask = moveGenerator.getBishopAttacks(board, square, !isWhite);
            if ((bishopAttackMask & bishops) != 0) {
                int bishopStartSquare = BitboardUtils.getLSB(bishopAttackMask & bishops);
                return new Move(bishopStartSquare, square);
            }
        }

        long rooks = board.getRooks(isWhite);
        if (rooks > 0) {
            long rookAttackMask = moveGenerator.getRookAttacks(board, square, !isWhite);
            if ((rookAttackMask & rooks) != 0) {
                int rookStartSquare = BitboardUtils.getLSB(rookAttackMask & rooks);
                return new Move(rookStartSquare, square);
            }
        }

        long queens = board.getQueens(isWhite);
        if (queens > 0) {
            long queenAttackMask = moveGenerator.getQueenAttacks(board, square, !isWhite);
            if ((queenAttackMask & queens) != 0) {
                int queenStartSquare = BitboardUtils.getLSB(queenAttackMask & queens);
                return new Move(queenStartSquare, square);
            }
        }

        long king = board.getKing(isWhite);
        long kingAttackMask = moveGenerator.getKingAttacks(board, square, !isWhite);
        if ((kingAttackMask & king) != 0) {
            int kingStartSquare = BitboardUtils.getLSB(kingAttackMask & king);
            return new Move(kingStartSquare, square);
        }
        return null;
    }

}
