package com.kelseyde.calvin.evaluation.old;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.evaluation.PieceSquareTable;
import org.springframework.stereotype.Service;

/**
 * Calculates the score each piece gets for the square it is currently on based on that piece type's {@link PieceSquareTable}.
 * Pawns and kings have two tables, for the opening and the endgame, and the score they get is weighted between the two tables
 * as the game progresses.
 */
@Service
public class PiecePlacementEvaluator {

    public PiecePlacementScore evaluate(Board board, float gamePhase, boolean isWhite) {
        return new PiecePlacementScore(
                scorePawns(board, gamePhase, isWhite),
                scoreKnights(board, gamePhase, isWhite),
                scoreBishops(board, gamePhase, isWhite),
                scoreRooks(board, gamePhase, isWhite),
                scoreQueens(board, gamePhase, isWhite),
                scoreKing(board, gamePhase, isWhite)
        );
    }

    public PiecePlacementScore handleMove(Board board, float gamePhase, PiecePlacementScore score, Move move) {

        // Assuming the move is already made on the board, so side to score != side to move
        boolean isWhite = !board.isWhiteToMove();

        int pawnScore = score.pawnScore();
        int knightScore = score.knightScore();
        int bishopScore = score.bishopScore();
        int rookScore = score.rookScore();
        int queenScore = score.queenScore();
        int kingScore = score.kingScore();

        PieceType pieceType = board.pieceAt(move.getEndSquare());
        switch (pieceType) {
            case PAWN -> pawnScore = scorePawns(board, gamePhase, isWhite);
            case KNIGHT -> knightScore = scoreKnights(board, gamePhase, isWhite);
            case BISHOP -> bishopScore = scoreBishops(board, gamePhase, isWhite);
            case ROOK -> rookScore = scoreRooks(board, gamePhase, isWhite);
            case QUEEN -> queenScore = scoreQueens(board, gamePhase, isWhite);
            case KING -> kingScore = scoreKing(board, gamePhase, isWhite);
        }

        if (move.isPromotion()) {
            // remove the score for the pawn that just promoted
            pawnScore = scorePawns(board, gamePhase, isWhite);
        }
        else if (move.isCastling()) {
            // update the rook score
            rookScore = scoreRooks(board, gamePhase, isWhite);
        }

       return new PiecePlacementScore(pawnScore, knightScore, bishopScore, rookScore, queenScore, kingScore);

    }

    private int scorePawns(Board board, float gamePhase, boolean isWhite) {
        long pawns = board.getPawns(isWhite);
        return scorePieces(pawns, PieceType.PAWN, isWhite, gamePhase);
    }

    private int scoreKnights(Board board, float gamePhase, boolean isWhite) {
        long knights = board.getKnights(isWhite);
        return scorePieces(knights, PieceType.KNIGHT, isWhite, gamePhase);
    }

    private int scoreBishops(Board board, float gamePhase, boolean isWhite) {
        long bishops = board.getBishops(isWhite);
        return scorePieces(bishops, PieceType.BISHOP, isWhite, gamePhase);
    }

    private int scoreRooks(Board board, float gamePhase, boolean isWhite) {
        long rooks = board.getRooks(isWhite);
        return scorePieces(rooks, PieceType.ROOK, isWhite, gamePhase);
    }

    private int scoreQueens(Board board, float gamePhase, boolean isWhite) {
        long queens = board.getQueens(isWhite);
        return scorePieces(queens, PieceType.QUEEN, isWhite, gamePhase);
    }

    private int scoreKing(Board board, float gamePhase, boolean isWhite) {
        long king = board.getKing(isWhite);
        return scorePieces(king, PieceType.KING, isWhite, gamePhase);
    }

    private int scorePieces(long pieces, PieceType pieceType, boolean isWhite, float gamePhase) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = BitboardUtils.getLSB(pieces);
            pieceTypeScore += PieceSquareTable.getScore(square, pieceType, isWhite, gamePhase);
            pieces = BitboardUtils.popLSB(pieces);
        }
        return pieceTypeScore;
    }

}
