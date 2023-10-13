package com.kelseyde.calvin.evaluation.material;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.EvaluationUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MaterialEvaluator {

    private static final int OPENING_MATERIAL = (PieceValues.KNIGHT * 2) + (PieceValues.BISHOP * 2) + (PieceValues.ROOK * 2) + PieceValues.QUEEN;

    public Material calculate(Board board, boolean isWhite) {

        int eval = 0;

        int pawns = Long.bitCount(isWhite ? board.getWhitePawns() : board.getBlackPawns());
        int knights = Long.bitCount(isWhite ? board.getWhiteKnights() : board.getBlackKnights());
        int bishops = Long.bitCount(isWhite ? board.getWhiteBishops() : board.getBlackBishops());
        int rooks = Long.bitCount(isWhite ? board.getWhiteRooks() : board.getBlackRooks());
        int queens = Long.bitCount(isWhite ? board.getWhiteQueens() : board.getBlackQueens());

        eval += knights * PieceValues.KNIGHT;
        eval += bishops * PieceValues.BISHOP;
        eval += rooks * PieceValues.ROOK;
        eval += queens * PieceValues.QUEEN;

        // Calculate the game 'phase' based on how much material is remaining (1 = starting position, 0 = only king and pawns left)
        float phase = (float) eval / OPENING_MATERIAL;

        eval += pawns * PieceValues.PAWN;

        // small bonus for the bishop pair
        if (bishops == 2) {
            eval += PieceValues.BISHOP_PAIR;
        }

        return new Material(pawns, knights, bishops, rooks, queens, phase, eval);

    }

}
