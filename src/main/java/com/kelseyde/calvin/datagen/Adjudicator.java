package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator;

import java.util.List;
import java.util.Optional;

/**
 * Adjudicates games based on the score of the position. Games are adjudicated if the position is terminal (i.e. checkmate
 * or stalemate), or if the score is above a certain threshold for a certain number of plies, or if the score is drawn for
 * a certain number of plies.
 */
public class Adjudicator {

    public enum GameResult {
        WHITE_WIN(1),
        BLACK_WIN(-1),
        DRAW(0);

        public final int value;

        GameResult(int value) {
            this.value = value;
        }
    }

    private final MoveGenerator movegen;

    private final int winThreshold;
    private final int winPliesThreshold;
    private final int drawThreshold;
    private final int drawPliesThreshold;

    private int winPlies;
    private int lossPlies;
    private int drawPlies;

    public Adjudicator(DatagenCommand command, MoveGenerator movegen) {
        this.movegen = movegen;
        this.winThreshold = command.winThreshold();
        this.winPliesThreshold = command.winPliesThreshold();
        this.drawThreshold = command.drawThreshold();
        this.drawPliesThreshold = command.drawPliesThreshold();
    }

    public Optional<GameResult> adjudicate(Board board, Move move, int score, boolean isCheck) {
        List<Move> moves = legalMoves(board, move);
        if (moves.isEmpty()) {
            if (isCheck) {
                return Optional.of(board.isWhite() ? GameResult.WHITE_WIN : GameResult.BLACK_WIN);
            } else {
                return Optional.of(GameResult.DRAW);
            }
        }
        int whiteScore = board.isWhite() ? score : -score;
        if (whiteScore >= winThreshold) {
            winPlies++;
            lossPlies = 0;
            drawPlies = 0;
            if (winPlies >= winPliesThreshold) {
                return Optional.of(GameResult.WHITE_WIN);
            }
        }
        else if (whiteScore <= -winThreshold) {
            lossPlies++;
            winPlies = 0;
            drawPlies = 0;
            if (lossPlies >= winPliesThreshold) {
                return Optional.of(GameResult.BLACK_WIN);
            }
        }
        else if (whiteScore <= drawThreshold && whiteScore >= -drawThreshold) {
            drawPlies++;
            winPlies = 0;
            lossPlies = 0;
            if (drawPlies >= drawPliesThreshold) {
                return Optional.of(GameResult.DRAW);
            }
        }
        else {
            winPlies = 0;
            lossPlies = 0;
            drawPlies = 0;
        }
        return Optional.empty();
    }

    public void reset() {
        winPlies = 0;
        lossPlies = 0;
        drawPlies = 0;
    }

    private List<Move> legalMoves(Board board, Move move) {
        board.makeMove(move);
        List<Move> legalMoves = movegen.generateMoves(board);
        board.unmakeMove();
        return legalMoves;
    }

}
