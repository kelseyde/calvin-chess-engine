package com.kelseyde.calvin.evaluation.see;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.evaluation.material.PieceValues;
import com.kelseyde.calvin.movegeneration.generator.*;

import java.util.NoSuchElementException;

/**
 * SEE, or 'Static Exchange Evaluation' function, calculates the change in material balance after a series of exchanges
 * on a single square. Very similar to the human player's heuristic of 'counting the attackers and defenders', it returns
 * an int value signifying the material loss or gain if all possible attackers and defenders of that square are traded
 * away.
 * Used in the quiescence search to prune capture nodes which are obviously detrimental to the side to move (like
 * queen takes pawn followed by pawn takes queen.). This improves search speed at the cost of potentially missing some
 * tactical complications elsewhere on the board.
 *
 * @see <a href="https://www.chessprogramming.org/Static_Exchange_Evaluation">Chess Programming Wiki</a>
 */
public class StaticExchangeEvaluator {

    private static final PawnMoveGenerator PAWN_MOVE_GENERATOR = new PawnMoveGenerator();
    private static final KnightMoveGenerator KNIGHT_MOVE_GENERATOR = new KnightMoveGenerator();
    private static final BishopMoveGenerator BISHOP_MOVE_GENERATOR = new BishopMoveGenerator();
    private static final RookMoveGenerator ROOK_MOVE_GENERATOR = new RookMoveGenerator();
    private static final QueenMoveGenerator QUEEN_MOVE_GENERATOR = new QueenMoveGenerator();
    private static final KingMoveGenerator KING_MOVE_GENERATOR = new KingMoveGenerator();

    public int evaluate(Board board, Move move) {

        int score = 0;
        int square = move.getEndSquare();
        PieceType capturedPieceType = board.pieceAt(square);
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

        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        long pawnAttackMask = PAWN_MOVE_GENERATOR.generateAttackMaskFromSquare(board, square, !isWhite);
        if ((pawnAttackMask & pawns) != 0) {
            int pawnStartSquare = BitboardUtils.getLSB(pawnAttackMask & pawns);
            return Move.builder().startSquare(pawnStartSquare).endSquare(square).pieceType(PieceType.PAWN).build();
        }

        long knights = isWhite ? board.getWhiteKnights() : board.getBlackKnights();
        long knightAttackMask = KNIGHT_MOVE_GENERATOR.generateAttackMaskFromSquare(board, square, !isWhite);
        if ((knightAttackMask & knights) != 0) {
            int knightStartSquare = BitboardUtils.getLSB(knightAttackMask & knights);
            return Move.builder().startSquare(knightStartSquare).endSquare(square).pieceType(PieceType.KNIGHT).build();
        }

        long bishops = isWhite ? board.getWhiteBishops() : board.getBlackBishops();
        long bishopAttackMask = BISHOP_MOVE_GENERATOR.generateAttackMaskFromSquare(board, square, !isWhite);
        if ((bishopAttackMask & bishops) != 0) {
            int bishopStartSquare = BitboardUtils.getLSB(bishopAttackMask & bishops);
            return Move.builder().startSquare(bishopStartSquare).endSquare(square).pieceType(PieceType.BISHOP).build();
        }

        long rooks = isWhite ? board.getWhiteRooks() : board.getBlackRooks();
        long rookAttackMask = ROOK_MOVE_GENERATOR.generateAttackMaskFromSquare(board, square, !isWhite);
        if ((rookAttackMask & rooks) != 0) {
            int rookStartSquare = BitboardUtils.getLSB(rookAttackMask & rooks);
            return Move.builder().startSquare(rookStartSquare).endSquare(square).pieceType(PieceType.ROOK).build();
        }

        long queens = isWhite ? board.getWhiteQueens() : board.getBlackQueens();
        long queenAttackMask = QUEEN_MOVE_GENERATOR.generateAttackMaskFromSquare(board, square, !isWhite);
        if ((queenAttackMask & queens) != 0) {
            int queenStartSquare = BitboardUtils.getLSB(queenAttackMask & queens);
            return Move.builder().startSquare(queenStartSquare).endSquare(square).pieceType(PieceType.QUEEN).build();
        }

        long king = isWhite ? board.getWhiteKing() : board.getBlackKing();
        long kingAttackMask = KING_MOVE_GENERATOR.generateAttackMaskFromSquare(board, square, !isWhite);
        if ((kingAttackMask & king) != 0) {
            int kingStartSquare = BitboardUtils.getLSB(kingAttackMask & king);
            return Move.builder().startSquare(kingStartSquare).endSquare(square).pieceType(PieceType.KING).build();
        }
        return null;
    }

}
