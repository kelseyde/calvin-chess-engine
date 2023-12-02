package com.kelseyde.calvin.engine;

public class TimeManager {

    public static int chooseThinkTime(boolean isWhite, int timeWhiteMs, int timeBlackMs, int incrementWhiteMs, int incrementBlackMs) {
        int timeRemainingMs = isWhite ? timeWhiteMs : timeBlackMs;
        int incrementMs = isWhite ? incrementWhiteMs : incrementBlackMs;
        // A game lasts on average 40 moves, so start with a simple fraction of the remaining time.
        double thinkTimeMs = timeRemainingMs / 40.0;
        if (thinkTimeMs > incrementMs * 2) {
            thinkTimeMs += incrementMs * 0.8;
        }
        double minThinkTimeMs = Math.min(50, timeRemainingMs * 0.25);
        thinkTimeMs = Math.max(thinkTimeMs, minThinkTimeMs);
        return (int) thinkTimeMs;
    }

}
