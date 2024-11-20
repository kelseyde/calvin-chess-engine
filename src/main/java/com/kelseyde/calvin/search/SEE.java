package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.movegen.Attacks;
import com.kelseyde.calvin.movegen.MoveGenerator;

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

    private static final MoveGenerator MOVEGEN = new MoveGenerator();

    public static int[] SEE_PIECE_VALUES = { 100, 320, 330, 500, 900, 0 };

    public static int value(Piece piece) {
        return SEE_PIECE_VALUES[piece.index()];
    }

    public static boolean see(Board board, Move move, int threshold) {

        boolean white = board.isWhite();
        final int from = move.from();
        final int to = move.to();

        int score = -threshold;
        Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(to);
        score += captured != null ? SEE_PIECE_VALUES[captured.index()] : 0;

        if (move.isPromotion()) {
            score += SEE_PIECE_VALUES[move.promoPiece().index()] - SEE_PIECE_VALUES[Piece.PAWN.index()];
        }

        if (score < 0) return false;

        Piece nextVictim = move.isPromotion() ? move.promoPiece() : board.pieceAt(from);
        score -= SEE_PIECE_VALUES[nextVictim.index()];

        if (score >= 0) return true;

        long occ = board.getOccupied() ^ Bits.of(from) ^ Bits.of(to);

        if (move.isEnPassant()) {
            int epFile = board.getState().getEnPassantFile();
            int epSquare = toEnPassantSquare(epFile, white);
            occ &= ~(1L << epSquare);
        }

        long attackers = (getAttackers(board, to, white) | getAttackers(board, to, !white)) & occ;
        long diagonalAttackers = board.getBishops(white) | board.getQueens(white)
                | board.getBishops(!white) | board.getQueens(!white);
        long orthogonalAttackers = board.getRooks(white) | board.getQueens(white)
                | board.getRooks(!white) | board.getQueens(!white);

        white = !white;

        while (true) {
            long friendlyAttackers = attackers & board.getPieces(white);

            if (friendlyAttackers == 0) break;

            nextVictim = getLeastValuableAttacker(board, friendlyAttackers, white);
            long pieces = board.getPieces(nextVictim, white);
            int sq = Bits.next(friendlyAttackers & pieces);
            occ = Bits.pop(occ, sq);

            if (nextVictim == Piece.PAWN || nextVictim == Piece.BISHOP || nextVictim == Piece.QUEEN) {
                attackers |= Attacks.bishopAttacks(to, occ) & diagonalAttackers;
            }

            if (nextVictim == Piece.ROOK || nextVictim == Piece.QUEEN) {
                attackers |= Attacks.rookAttacks(to, occ) & orthogonalAttackers;
            }

            attackers &= occ;
            score = -score - 1 - SEE_PIECE_VALUES[nextVictim.index()];
            white = !white;

            if (score >= 0) {
                if (nextVictim == Piece.KING && (attackers & board.getPieces(white)) != 0) {
                    white = !white;
                }
                break;
            }
        }

        return board.isWhite() != white;

    }

    public static int moveScore(Board board, Move move) {

        if (move.isPromotion()) {
            return SEE_PIECE_VALUES[move.promoPiece().index()] - SEE_PIECE_VALUES[Piece.PAWN.index()];
        }
        else if (move.isEnPassant()) {
            return SEE_PIECE_VALUES[Piece.PAWN.index()];
        }
        else {
            Piece targetPiece = board.pieceAt(move.to());
            return targetPiece != null ? SEE_PIECE_VALUES[targetPiece.index()] : 0;
        }

    }

    private static Piece getLeastValuableAttacker(Board board, long attackers, boolean white) {
        Piece nextVictim;
        if ((attackers & board.getPawns(white)) != 0)         nextVictim = Piece.PAWN;
        else if ((attackers & board.getKnights(white)) != 0)  nextVictim = Piece.KNIGHT;
        else if ((attackers & board.getBishops(white)) != 0)  nextVictim = Piece.BISHOP;
        else if ((attackers & board.getRooks(white)) != 0)    nextVictim = Piece.ROOK;
        else if ((attackers & board.getQueens(white)) != 0)   nextVictim = Piece.QUEEN;
        else if ((attackers & board.getKing(white)) != 0)     nextVictim = Piece.KING;
        else throw new IllegalArgumentException("Invalid piece type");
        return nextVictim;
    }

    private static long getAttackers(Board board, int square, boolean white) {
        return MOVEGEN.getPawnAttacks(board, square, !white) & board.getPawns(white) |
                MOVEGEN.getKnightAttacks(board, square, !white) & board.getKnights(white) |
                MOVEGEN.getBishopAttacks(board, square, !white) & board.getBishops(white) |
                MOVEGEN.getRookAttacks(board, square, !white) & board.getRooks(white) |
                MOVEGEN.getQueenAttacks(board, square, !white) & board.getQueens(white) |
                MOVEGEN.getKingAttacks(board, square, !white) & board.getKing(white);
    }


    private static int toEnPassantSquare(int enPassantFile, boolean white) {
        int rank = white ? 2 : 5;
        if (enPassantFile == -1) {
            return -1;
        }
        return Bits.Square.of(rank, enPassantFile);
    }

}
