package com.kelseyde.calvin.bot;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;

import java.util.List;
import java.util.function.Consumer;

/**
 * The Bot is responsible for actually playing a game of chess. It manages the game state, updates the board, and 'thinks'
 * (executes a search) to find the best move. It can also select how long to think for in the case of a time-controlled
 * game, or it can 'ponder' (think indefinitely when it is the opponent's turn).
 */
public interface Bot {

    /**
     * @return The up-to-date internal board representation.
     */
    Board getBoard();

    /**
     * Reset the internal game state (board, search history, transposition tables), for a new game.
     */
    void newGame();

    /**
     * Shut down any remaining processes at the end of the game.
     */
    void gameOver();

    /**
     * Update the internal board state to match the starting FEN, and all prior game moves.
     */
    void setPosition(String fen, List<Move> moves);

    /**
     * Update the internal board state with the latest move.
     */
    void applyMove(Move move);

    /**
     * @return Whether the bot is currently thinking (either on its own turn, or 'pondering' on its opponent's turn.
     */
    boolean isThinking();

    /**
     * Think for the configured time period, and when finished invoke the provided {@link Consumer} with the chosen move (non-blocking).
     */
    void think(int thinkTimeMs, Consumer<Move> onSearchComplete);

    /**
     * Think for the configured time period and immediately return the move (blocking).
     */
    Move think(int thinkTimeMs);

    /**
     * Think indefinitely about the current position. Can be used to ponder on the opponent's turn.
     */
    void startPondering();

    /**
     * Calculate how long to think for on the next move, based on the remaining time for both sides and the time increment.
     */
    int chooseThinkTime(int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs);

    /**
     * Immediately terminate any thinking processes (but preserve any cached information, transposition tables etc. for the next search.
     */
    void stopThinking();

}
