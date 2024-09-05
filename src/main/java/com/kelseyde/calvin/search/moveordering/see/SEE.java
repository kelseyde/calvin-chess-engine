package com.kelseyde.calvin.search.moveordering.see;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
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
public class SEE {

    private static final MoveGenerator MOVE_GENERATOR = new MoveGenerator();

    public static boolean see(Board board, Move move, int threshold) {

        boolean white = board.isWhiteToMove();
        int from = move.getFrom();
        int to = move.getTo();
        Piece nextVictim = move.isPromotion() ? move.getPromotionPiece() : board.pieceAt(from);
        int balance = moveScore(board, move) - threshold;

        if (balance < 0) return false;
        balance -= nextVictim.getValue();
        if (balance >= 0) return true;

        long occ = board.getOccupied();
        occ = (occ ^ (1L << from)) | (1L << to);
        if (move.isEnPassant()) {
            int epFile = board.getGameState().getEnPassantFile();
            int epSquare = toEnPassantSquare(epFile, white);
            occ &= ~(1L << epSquare);
        }

        long attackers = getAttackers(board, to, white) | getAttackers(board, to, !white) & occ;

        white = !white;

        while (true) {
            long friendlyAttackers = attackers & board.getPieces(white);
            if (friendlyAttackers == 0) break;
            nextVictim = getLeastValuableAttacker(board, friendlyAttackers, white);
            occ ^= 1L << Bitwise.getNextBit(friendlyAttackers & board.getPieces(nextVictim, white));
            attackers &= occ;
            white = !white;
            balance = -balance - 1 - nextVictim.getValue();
            if (balance >= 0) {
                if (nextVictim == Piece.KING & (attackers & board.getPieces(white)) != 0) {
                    white = !white;
                }
                break;
            }
        }

        return board.isWhiteToMove() != white;

    }

    public static int moveScore(Board board, Move move) {

        if (move.isPromotion()) {
            return move.getPromotionPiece().getValue() - Piece.PAWN.getValue();
        }
        else if (move.isEnPassant()) {
            return Piece.PAWN.getValue();
        }
        else {
            Piece targetPiece = board.pieceAt(move.getTo());
            return targetPiece != null ? targetPiece.getValue() : 0;
        }

    }

    private static Piece getLeastValuableAttacker(Board board, long attackers, boolean white) {
        Piece nextVictim;
        if ((attackers & board.getPawns(white)) != 0) {
            nextVictim = Piece.PAWN;
        }
        else if ((attackers & board.getKnights(white)) != 0) {
            nextVictim = Piece.KNIGHT;
        }
        else if ((attackers & (board.getBishops() | board.getQueens())) != 0) {
            nextVictim = Piece.BISHOP;
        }
        else if ((attackers & (board.getRooks() | board.getQueens())) != 0) {
            nextVictim = Piece.ROOK;
        }
        else if ((attackers & board.getQueens(white)) != 0) {
            nextVictim = Piece.QUEEN;
        }
        else if ((attackers & board.getKing(white)) != 0) {
            nextVictim = Piece.KING;
        }
        else {
            throw new IllegalArgumentException("Invalid piece type");
        }
        return nextVictim;
    }

    private static long getAttackers(Board board, int square, boolean white) {
        return MOVE_GENERATOR.getPawnAttacks(board, square, !white) & board.getPawns(white) |
               MOVE_GENERATOR.getKnightAttacks(board, square, white) & board.getKnights(white) |
               MOVE_GENERATOR.getBishopAttacks(board, square, white) & board.getBishops(white) |
               MOVE_GENERATOR.getRookAttacks(board, square, white) & board.getRooks(white) |
               MOVE_GENERATOR.getQueenAttacks(board, square, white) & board.getQueens(white) |
               MOVE_GENERATOR.getKingAttacks(board, square, white) & board.getKing(white);
    }


    private static int toEnPassantSquare(int enPassantFile, boolean white) {
        int rank = white ? 2 : 5;
        if (enPassantFile == -1) {
            return -1;
        }
        return Board.squareIndex(rank, enPassantFile);
    }

}
