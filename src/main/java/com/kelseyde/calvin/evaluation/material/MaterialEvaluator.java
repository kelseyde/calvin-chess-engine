package com.kelseyde.calvin.evaluation.material;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.PieceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MaterialEvaluator {

    private static final int OPENING_MATERIAL = (PieceValues.KNIGHT * 2) + (PieceValues.BISHOP * 2) + (PieceValues.ROOK * 2) + PieceValues.QUEEN;

    public Material evaluate(Board board, boolean isWhite) {

        int pawns = Long.bitCount(isWhite ? board.getWhitePawns() : board.getBlackPawns());
        int knights = Long.bitCount(isWhite ? board.getWhiteKnights() : board.getBlackKnights());
        int bishops = Long.bitCount(isWhite ? board.getWhiteBishops() : board.getBlackBishops());
        int rooks = Long.bitCount(isWhite ? board.getWhiteRooks() : board.getBlackRooks());
        int queens = Long.bitCount(isWhite ? board.getWhiteQueens() : board.getBlackQueens());

        // Get the summed value of the pieces * piece values
        int eval = calculateEval(pawns, knights, bishops, rooks, queens);

        // Calculate the game 'phase' based on how many pieces (excluding pawns) remain (1 = starting material, 0 = only king and pawns left)
        float phase = calculatePhase(knights, bishops, rooks, queens);

        return new Material(pawns, knights, bishops, rooks, queens, phase, eval);

    }

    public Material updateCapture(Material material, PieceType capturedPieceType) {
        int pawns = material.pawns();
        int knights = material.knights();
        int bishops = material.bishops();
        int rooks = material.rooks();
        int queens = material.queens();
        switch (capturedPieceType) {
            case PAWN -> --pawns;
            case KNIGHT -> --knights;
            case BISHOP -> --bishops;
            case ROOK -> --rooks;
            case QUEEN -> --queens;
        }
        float phase = calculatePhase(knights, bishops, rooks, queens);
        int eval = calculateEval(pawns, knights, bishops, rooks, queens);
        return new Material(pawns, knights, bishops, rooks, queens, phase, eval);
    }

    public Material updatePromotion(Material material, PieceType promotionPieceType) {
        int pawns = material.pawns() - 1;
        int knights = material.knights();
        int bishops = material.bishops();
        int rooks = material.rooks();
        int queens = material.queens();
        switch (promotionPieceType) {
            case QUEEN -> ++queens;
            case KNIGHT -> ++knights;
            case ROOK -> ++rooks;
            case BISHOP -> ++bishops;
        }
        float phase = calculatePhase(knights, bishops, rooks, queens);
        int eval = calculateEval(pawns, knights, bishops, rooks, queens);
        return new Material(pawns, knights, bishops, rooks, queens, phase, eval);
    }

    private int calculateEval(int pawns, int knights, int bishops, int rooks, int queens) {
        // small bonus for the bishop pair
        int bishopPairBonus = bishops == 2 ? PieceValues.BISHOP_PAIR : 0;
        return (pawns * PieceValues.PAWN) +
                (knights * PieceValues.KNIGHT) +
                (bishops * PieceValues.BISHOP) +
                (rooks * PieceValues.ROOK) +
                (queens * PieceValues.QUEEN) +
                bishopPairBonus;
    }

    private float calculatePhase(int knights, int bishops, int rooks, int queens) {
        return (float) ((knights * PieceValues.KNIGHT) +
                (bishops * PieceValues.BISHOP) +
                (rooks * PieceValues.ROOK) +
                (queens * PieceValues.QUEEN)) / OPENING_MATERIAL;
    }

}
