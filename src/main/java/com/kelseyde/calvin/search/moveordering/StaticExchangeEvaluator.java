package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.generation.Attacks;
import com.kelseyde.calvin.generation.MoveGenerator;

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

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        boolean white = board.isWhiteToMove();
        Piece currentVictim = move.isPromotion() ? move.getPromotionPiece() : board.pieceAt(startSquare);

        int score = 0;

        if (move.isCastling()) {
            return 0;
        }

        if (move.isPromotion()) {
            score -= Piece.PAWN.getValue();
            score += move.getPromotionPiece().getValue();
        }

        Piece capturedPiece = board.pieceAt(endSquare);
        if (capturedPiece != null) {
            score += capturedPiece.getValue();
        }

        long remaining = board.getOccupied();
        remaining ^= 1L << startSquare;
        remaining |= 1L << endSquare;

        if (move.isEnPassant()) {
            remaining ^= board.isWhiteToMove() ? endSquare - 8 : endSquare + 8;
        }

        long diagonalSliders = (board.getDiagonalSliders(white) | board.getDiagonalSliders(!white)) & remaining;
        long orthogonalSliders = (board.getOrthogonalSliders(white) | board.getOrthogonalSliders(!white)) & remaining;

        // Get all attackers, regardless of colour
        long attackers = moveGenerator.allAttackers(board, endSquare, remaining) & remaining;

        boolean originalSide = white;
        boolean currentSide = white;

        // Start exchanging pieces
        while (true) {

            currentSide = !currentSide;
            if ((currentSide == originalSide && score >= 0)
                || currentSide != originalSide && score <= 0) {
                break;
            }

            int attackerSquare = lva(board, attackers, currentSide);
            if (attackerSquare < 0) break;

            Piece lva = board.pieceAt(attackerSquare);
            if (lva == Piece.KING && (attackers & board.getPieces(!currentSide)) != 0) {
                break;
            }

            // Remove the attacker from the boards
            remaining ^= 1L << attackerSquare;
            attackers &= remaining;
            diagonalSliders &= remaining;
            orthogonalSliders &= remaining;

            if (lva == Piece.PAWN || (lva == Piece.BISHOP || lva == Piece.QUEEN)) {
                attackers |= Attacks.bishopAttacks(endSquare, remaining) & diagonalSliders;
            }
            if ((lva == Piece.ROOK || lva == Piece.QUEEN)) {
                attackers |= Attacks.rookAttacks(endSquare, remaining) & orthogonalSliders;
            }

            if (currentSide == originalSide) {
                score += currentVictim.getValue();
            } else {
                score -= currentVictim.getValue();
            }

            currentVictim = lva;

        }
        return score;

    }

    /**
     * Get the square of the least-valuable-attacker
     */
    private int lva(Board board, long attackers, boolean white) {

        int square = -1;
        int lowestValue = Integer.MAX_VALUE;
        long sideAttackers = attackers & board.getPieces(white);
        while (sideAttackers != 0) {
            int attackerSquare = Bitwise.getNextBit(sideAttackers);
            Piece attacker = board.pieceAt(attackerSquare);
            int attackerValue = attacker.getValue();
            if (attackerValue < lowestValue) {
                square = attackerSquare;
                lowestValue = attackerValue;
            }
            sideAttackers = Bitwise.popBit(sideAttackers);
        }
        return square;
    }

    /**
     * The same SEE evaluation, but with the first move already made on the board. Used during search to evaluate whether
     * a check should be extended
     */
    public int evaluateAfterMove(Board board, Move move) {

        int score = 0;
        int square = move.getEndSquare();
        Piece capturedPiece = board.getGameState().getCapturedPiece();
        score += capturedPiece != null ? capturedPiece.getValue() : 0;

        Move leastValuableAttacker = getLeastValuableAttacker(board, square);
        if (leastValuableAttacker != null) {
            /* The opponent should have the option of 'standing pat' - that is, declining to continue the capture
             sequence if it would lead to a loss of material.
             Therefore, we return the minimum of the stand-pat score and the capture score. */
            score = Math.min(score, score - evaluate(board, leastValuableAttacker));
        }
        return score;

    }


    private Move getLeastValuableAttacker(Board board, int square) {

        boolean white = board.isWhiteToMove();

        long pawns = board.getPawns(white);
        if (pawns > 0) {
            long pawnAttackMask = moveGenerator.getPawnAttacks(board, square, !white);
            if ((pawnAttackMask & pawns) != 0) {
                int pawnStartSquare = Bitwise.getNextBit(pawnAttackMask & pawns);
                return new Move(pawnStartSquare, square);
            }
        }

        long knights = board.getKnights(white);
        if (knights > 0) {
            long knightAttackMask = moveGenerator.getKnightAttacks(board, square, !white);
            if ((knightAttackMask & knights) != 0) {
                int knightStartSquare = Bitwise.getNextBit(knightAttackMask & knights);
                return new Move(knightStartSquare, square);
            }
        }

        long bishops = board.getBishops(white);
        if (bishops > 0) {
            long bishopAttackMask = moveGenerator.getBishopAttacks(board, square, !white);
            if ((bishopAttackMask & bishops) != 0) {
                int bishopStartSquare = Bitwise.getNextBit(bishopAttackMask & bishops);
                return new Move(bishopStartSquare, square);
            }
        }

        long rooks = board.getRooks(white);
        if (rooks > 0) {
            long rookAttackMask = moveGenerator.getRookAttacks(board, square, !white);
            if ((rookAttackMask & rooks) != 0) {
                int rookStartSquare = Bitwise.getNextBit(rookAttackMask & rooks);
                return new Move(rookStartSquare, square);
            }
        }

        long queens = board.getQueens(white);
        if (queens > 0) {
            long queenAttackMask = moveGenerator.getQueenAttacks(board, square, !white);
            if ((queenAttackMask & queens) != 0) {
                int queenStartSquare = Bitwise.getNextBit(queenAttackMask & queens);
                return new Move(queenStartSquare, square);
            }
        }

        long king = board.getKing(white);
        long kingAttackMask = moveGenerator.getKingAttacks(board, square, !white);
        if ((kingAttackMask & king) != 0) {
            int kingStartSquare = Bitwise.getNextBit(kingAttackMask & king);
            return new Move(kingStartSquare, square);
        }
        return null;
    }

}
