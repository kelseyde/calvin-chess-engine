package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.uci.Pretty;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.UCICommand;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reports the progress of a data generation session.
 */
public class DatagenReporter {

    public void reportDatagenInfo(DatagenCommand command) {
        if (UCI.Options.pretty) {
            UCI.write("");
            UCI.write(String.format("""
                    %sBeginning Data Generation%s
                    Output File   : %s%s%s
                    Max Positions : %s%d%s
                    Soft Limit    : %s%d%s
                    Hard Limit    : %s%d%s
                    """,
                    Pretty.BLUE, Pretty.RESET,
                    Pretty.GREEN, command.file(), Pretty.RESET,
                    Pretty.GREEN, command.positions(), Pretty.RESET,
                    Pretty.RED, command.softNodes(), Pretty.RESET,
                    Pretty.RED, command.hardNodes(), Pretty.RESET
            ));
        } else {
            UCI.write(String.format("info string generating data, file %s, positions %d, soft limit %d, hard limit %d",
                    command.file(), command.positions(), command.softNodes(), command.hardNodes()));
        }
    }

    public void reportDatagenProgress(DatagenCommand command, Instant start, int current) {
        Duration duration = Duration.between(start, Instant.now()).truncatedTo(ChronoUnit.SECONDS);
        int total = command.positions();
        int remaining = total - current;
        double rate = (double) current / duration.getSeconds();
        String rateFormatted = String.format("%.0f", rate);
        Duration estimate = Duration.ofSeconds((long) (remaining / rate)).truncatedTo(ChronoUnit.SECONDS);

        if (UCI.Options.pretty) {
            System.out.printf("generated %s%d/%d positions %s, time %s%s%s, pos/s %s%s%s, remaining pos %s%s%s remaining time %s%s%s\n",
                    Pretty.CYAN, current, total, Pretty.RESET,
                    Pretty.CYAN, duration, Pretty.RESET,
                    Pretty.CYAN, rateFormatted, Pretty.RESET,
                    Pretty.CYAN, remaining, Pretty.RESET,
                    Pretty.CYAN, estimate, Pretty.RESET);
        } else {
            System.out.printf("info string generated %d/%d positions, time %s, pos/s %s, remaining pos %s remaining time %s\n",
                    current, total, duration, rateFormatted, remaining, estimate);
        }
    }

}
