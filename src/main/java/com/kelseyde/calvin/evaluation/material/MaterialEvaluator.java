package com.kelseyde.calvin.evaluation.material;

import com.kelseyde.calvin.board.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Calculates the material score, based on the number of pieces and pawns remaining for one side. Each piece type has its
 * own value defined in {@link PieceValues}, with a small added bonus for the bishop pair.
 */
@Slf4j
@Service
public class MaterialEvaluator {

    // Endgame tapering starts after the piece value equivalent of a queen is removed from the board
    private static final float ENDGAME_MATERIAL_START = (PieceValues.KNIGHT * 2) + (PieceValues.BISHOP * 2) + (PieceValues.ROOK * 2);

    public Material evaluate(Board board, boolean isWhite) {

        int pawns = Long.bitCount(board.getPawns(isWhite));
        int knights = Long.bitCount(board.getKnights(isWhite));
        int bishops = Long.bitCount(board.getBishops(isWhite));
        int rooks = Long.bitCount(board.getRooks(isWhite));
        int queens = Long.bitCount(board.getQueens(isWhite));

        // Get the summed value of the pieces * piece values
        int eval = calculateEval(pawns, knights, bishops, rooks, queens);

        // Calculate the game 'phase' based on how many pieces (excluding pawns) remain (1 = starting material, 0 = only king and pawns left)
        float phase = calculatePhase(knights, bishops, rooks, queens);

        return new Material(pawns, knights, bishops, rooks, queens, phase, eval);

    }

    private int calculateEval(int pawns, int knights, int bishops, int rooks, int queens) {
        // small bonus for the bishop pair
        int bishopPairBonus = bishops == 2 ? PieceValues.BISHOP_PAIR_BONUS : 0;
        return (pawns * PieceValues.PAWN) +
                (knights * PieceValues.KNIGHT) +
                (bishops * PieceValues.BISHOP) +
                (rooks * PieceValues.ROOK) +
                (queens * PieceValues.QUEEN) +
                bishopPairBonus;
    }

    private float calculatePhase(int knights, int bishops, int rooks, int queens) {
        int pieceScore = (knights * PieceValues.KNIGHT) +
                (bishops * PieceValues.BISHOP) +
                (rooks * PieceValues.ROOK) +
                (queens * PieceValues.QUEEN);
        float multiplier = 1 / ENDGAME_MATERIAL_START;
        return Math.min(1, pieceScore * multiplier);
    }



}
