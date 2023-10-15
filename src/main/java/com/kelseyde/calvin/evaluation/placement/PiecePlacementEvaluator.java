package com.kelseyde.calvin.evaluation.placement;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import org.springframework.stereotype.Service;

@Service
public class PiecePlacementEvaluator {

    public PiecePlacementScore evaluate(Board board, float gamePhase, boolean isWhite) {
        return new PiecePlacementScore(
                scorePawns(board, gamePhase, isWhite),
                scoreKnights(board, isWhite),
                scoreBishops(board, isWhite),
                scoreRooks(board, isWhite),
                scoreQueens(board, isWhite),
                scoreKing(board, gamePhase, isWhite)
        );
    }

    /**
     * Update only the pieces whose evaluation changes in relation to the endgame weight: pawns and kings.
     * Used to efficiently recalculate piece placement scores as captures on the board change the endgame weight.
     */
    public PiecePlacementScore updateWeightedPieces(Board board, float gamePhase, PiecePlacementScore score, boolean isWhite) {
        return new PiecePlacementScore(
                scorePawns(board, gamePhase, isWhite),
                score.knightScore(),
                score.bishopScore(),
                score.rookScore(),
                score.queenScore(),
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
            case KNIGHT -> knightScore = scoreKnights(board, isWhite);
            case BISHOP -> bishopScore = scoreBishops(board, isWhite);
            case ROOK -> rookScore = scoreRooks(board, isWhite);
            case QUEEN -> queenScore = scoreQueens(board, isWhite);
            case KING -> kingScore = scoreKing(board, gamePhase, isWhite);
        }

        if (move.isPromotion()) {
            // remove the score for the pawn that just promoted
            pawnScore = scorePawns(board, gamePhase, isWhite);
        }
        else if (move.isCastling()) {
            // update the rook score
            rookScore = scoreRooks(board, isWhite);
        }

       return new PiecePlacementScore(pawnScore, knightScore, bishopScore, rookScore, queenScore, kingScore);

    }

    public PiecePlacementScore handleCapture(Board board, float gamePhase, PiecePlacementScore score, PieceType capturedPiece) {
        // Assuming the move is already made on the board, so side to score != side to move
        boolean isWhite = board.isWhiteToMove();

        int pawnScore = score.pawnScore();
        int knightScore = score.knightScore();
        int bishopScore = score.bishopScore();
        int rookScore = score.rookScore();
        int queenScore = score.queenScore();
        int kingScore = score.kingScore();

        switch (capturedPiece) {
            case PAWN -> pawnScore = scorePawns(board, gamePhase, isWhite);
            case KNIGHT -> {
                knightScore = scoreKnights(board, isWhite);
                // With every captured piece, we need to re-evaluate the king and pawns score as the tapered evaluation will change
                pawnScore = scorePawns(board, gamePhase, isWhite);
                kingScore = scoreKing(board, gamePhase, isWhite);
            }
            case BISHOP -> {
                bishopScore = scoreBishops(board, isWhite);
                pawnScore = scorePawns(board, gamePhase, isWhite);
                kingScore = scoreKing(board, gamePhase, isWhite);
            }
            case ROOK -> {
                rookScore = scoreRooks(board, isWhite);
                pawnScore = scorePawns(board, gamePhase, isWhite);
                kingScore = scoreKing(board, gamePhase, isWhite);
            }
            case QUEEN -> {
                queenScore = scoreQueens(board, isWhite);
                pawnScore = scorePawns(board, gamePhase, isWhite);
                kingScore = scoreKing(board, gamePhase, isWhite);
            }
            case KING -> kingScore = scoreKing(board, gamePhase, isWhite);
        }
        return new PiecePlacementScore(pawnScore, knightScore, bishopScore, rookScore, queenScore, kingScore);
    }

    private int scorePawns(Board board, float gamePhase, boolean isWhite) {
        long pawns = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        int[] pawnStartTable = isWhite ? PieceSquareTable.WHITE_PAWN_START_TABLE : PieceSquareTable.BLACK_PAWN_START_TABLE;
        int[] pawnEndTable = isWhite ? PieceSquareTable.WHITE_PAWN_END_TABLE : PieceSquareTable.BLACK_PAWN_END_TABLE;
        return scoreMultiTablePieces(pawns, gamePhase, pawnStartTable, pawnEndTable);
    }

    private int scoreKnights(Board board, boolean isWhite) {
        long knights = isWhite ? board.getWhiteKnights() : board.getBlackKnights();
        int[] knightTable = isWhite ? PieceSquareTable.WHITE_KNIGHT_TABLE : PieceSquareTable.BLACK_KNIGHT_TABLE;
        return scoreSingleTablePieces(knights, knightTable);
    }

    private int scoreBishops(Board board, boolean isWhite) {
        long bishops = isWhite ? board.getWhiteBishops() : board.getBlackBishops();
        int[] bishopTable = isWhite ? PieceSquareTable.WHITE_BISHOP_TABLE : PieceSquareTable.BLACK_BISHOP_TABLE;
        return scoreSingleTablePieces(bishops, bishopTable);
    }

    private int scoreRooks(Board board, boolean isWhite) {
        long rooks = isWhite ? board.getWhiteRooks() : board.getBlackRooks();
        int[] rookTable = isWhite ? PieceSquareTable.WHITE_ROOK_TABLE : PieceSquareTable.BLACK_ROOK_TABLE;
        return scoreSingleTablePieces(rooks, rookTable);
    }

    private int scoreQueens(Board board, boolean isWhite) {
        long queens = isWhite ? board.getWhiteQueens() : board.getBlackQueens();
        int[] queenTable = isWhite ? PieceSquareTable.WHITE_QUEEN_TABLE : PieceSquareTable.BLACK_QUEEN_TABLE;
        return scoreSingleTablePieces(queens, queenTable);
    }

    private int scoreKing(Board board, float gamePhase, boolean isWhite) {
        int[] kingStartTable = isWhite ? PieceSquareTable.WHITE_KING_START_TABLE : PieceSquareTable.BLACK_KING_START_TABLE;
        int[] kingEndTable = isWhite ? PieceSquareTable.WHITE_KING_END_TABLE : PieceSquareTable.BLACK_KING_END_TABLE;
        long king = isWhite ? board.getWhiteKing() : board.getBlackKing();
        return scoreMultiTablePieces(king, gamePhase, kingStartTable, kingEndTable);
    }

    private int scoreSingleTablePieces(long pieces, int[] pieceTable) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = BitboardUtils.getLSB(pieces);
            pieceTypeScore += pieceTable[square];
            pieces = BitboardUtils.popLSB(pieces);
        }
        return pieceTypeScore;
    }

    private int scoreMultiTablePieces(long pieces, float gamePhase, int[] openingTable, int[] endgameTable) {
        int pieceTypeScore = 0;
        while (pieces != 0) {
            int square = BitboardUtils.getLSB(pieces);
            pieceTypeScore += gamePhase * openingTable[square];
            pieceTypeScore += (1 - gamePhase) * endgameTable[square];
            pieces = BitboardUtils.popLSB(pieces);
        }
        return pieceTypeScore;
    }

}
