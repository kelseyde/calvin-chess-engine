package com.kelseyde.calvin.search.moveordering;

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
 * exchanging your queen for a pawn). This speeds up search at the cost of potentially missing tactical complications.
 *
 * @see <a href="https://www.chessprogramming.org/Static_Exchange_Evaluation">Chess Programming Wiki</a>
 */
public class SEE {

    private static final MoveGenerator MOVEGEN = new MoveGenerator();

    public static boolean see(Board board, Move move, int threshold) {

        boolean white = board.isWhiteToMove();
        int from = move.getFrom();
        int to = move.getTo();
        Piece nextVictim = move.isPromotion() ? move.getPromotionPiece() : board.pieceAt(from);
        if (nextVictim == null)
            throw new IllegalArgumentException("SEE called with an illegal move");
        int score = moveScore(board, move) - threshold;

        if (score < 0)
            return false;

        score -= nextVictim.getValue();

        if (score >= 0)
            return true;

        long bishops = board.getBishops() | board.getQueens();
        long rooks = board.getRooks() | board.getQueens();

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
            if (friendlyAttackers == 0) {
                break;
            }
            nextVictim = getLeastValuableAttacker(board, friendlyAttackers, white, bishops, rooks);
            long pieces = board.getPieces(nextVictim, white);
            occ ^= 1L << Bitwise.getNextBit(friendlyAttackers & pieces);
            attackers &= occ;
            white = !white;
            score = -score - 1 - nextVictim.getValue();
            if (score >= 0) {
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

    private static Piece getLeastValuableAttacker(Board board, long myAttackers, boolean white, long bishops, long rooks) {
        Piece nextVictim;
        if ((myAttackers & board.getPawns(white)) != 0)             nextVictim = Piece.PAWN;
        else if ((myAttackers & board.getKnights(white)) != 0)      nextVictim = Piece.KNIGHT;
        else if ((myAttackers & bishops) != 0)                      nextVictim = Piece.BISHOP;
        else if ((myAttackers & rooks) != 0)                        nextVictim = Piece.ROOK;
        else if ((myAttackers & board.getQueens(white)) != 0)       nextVictim = Piece.QUEEN;
        else if ((myAttackers & board.getKing(white)) != 0)         nextVictim = Piece.KING;
        else throw new IllegalArgumentException("Invalid piece type");
        return nextVictim;
    }

    private static long getAttackers(Board board, int square, boolean white) {
        return MOVEGEN.getPawnAttacks(board, square, !white) & board.getPawns(white) |
                MOVEGEN.getKnightAttacks(board, square, white) & board.getKnights(white) |
                MOVEGEN.getBishopAttacks(board, square, white) & board.getBishops(white) |
                MOVEGEN.getRookAttacks(board, square, white) & board.getRooks(white) |
                MOVEGEN.getQueenAttacks(board, square, white) & board.getQueens(white) |
                MOVEGEN.getKingAttacks(board, square, white) & board.getKing(white);
    }


    private static int toEnPassantSquare(int enPassantFile, boolean white) {
        int rank = white ? 2 : 5;
        if (enPassantFile == -1) {
            return -1;
        }
        return Board.squareIndex(rank, enPassantFile);
    }

}
