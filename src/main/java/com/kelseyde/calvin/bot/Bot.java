package com.kelseyde.calvin.bot;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;

import java.util.List;
import java.util.function.Consumer;

public interface Bot {

    Board getBoard();

    void newGame();

    void gameOver();

    void setPosition(String fen, List<Move> moves);

    void applyMove(Move move);

    boolean isThinking();

    void think(int thinkTimeMs, Consumer<Move> onSearchComplete);

    Move think(int thinkTimeMs);

    int chooseThinkTime(int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs);

    void stopThinking();

}
