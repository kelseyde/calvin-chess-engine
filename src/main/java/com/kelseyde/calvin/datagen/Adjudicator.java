package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.uci.UCICommand.DatagenCommand;

import java.util.Optional;

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

    private final int winThreshold;
    private final int winPliesThreshold;
    private final int drawThreshold;
    private final int drawPliesThreshold;
    private final int initialScoreThreshold;

    private int winPlies;
    private int drawPlies;

    public Adjudicator(DatagenCommand command) {
        this.winThreshold = command.winThreshold();
        this.winPliesThreshold = command.winPliesThreshold();
        this.drawThreshold = command.drawThreshold();
        this.drawPliesThreshold = command.drawPliesThreshold();
        this.initialScoreThreshold = command.initialScoreThreshold();
    }

    public Optional<GameResult> adjudicate(Board board, Move move, int score) {
        int wdlScore = board.isWhite() ? score : -score;
        if (wdlScore >= winThreshold) {
            winPlies++;
            if (winPlies >= winPliesThreshold) {
                return Optional.of(board.isWhite() ? GameResult.WHITE_WIN : GameResult.BLACK_WIN);
            }
        }
        else if (wdlScore <= -winThreshold) {
            winPlies++;
            if (winPlies >= winPliesThreshold) {
                return Optional.of(board.isWhite() ? GameResult.BLACK_WIN : GameResult.WHITE_WIN);
            }
        }
        else if (wdlScore <= drawThreshold && wdlScore >= -drawThreshold) {
            drawPlies++;
            if (drawPlies >= drawPliesThreshold) {
                return Optional.of(GameResult.DRAW);
            }
        }
        else {
            winPlies = 0;
            drawPlies = 0;
        }
        return Optional.empty();
    }

    public void reset() {
        winPlies = 0;
        drawPlies = 0;
    }

}
