package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.uci.Pretty;
import com.kelseyde.calvin.uci.UCI;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Reports the progress of a data generation session.
 */
public class DatagenReporter {

    public void reportDatagenInfo(DatagenCommand command) {
        if (UCI.Options.pretty) {
            System.out.println();
            System.out.printf(String.format("""
                    %sBeginning Data Generation%s
                    Output File        : %s%s%s
                    Max Positions      : %s%d%s
                    Threads            : %s%d%s
                    Soft Node Limit    : %s%d%s
                    Hard Node Limit    : %s%d%s
                    Variant            : %sClassical%s
                    """,
                    Pretty.BLUE, Pretty.RESET,
                    Pretty.GREEN, command.file(), Pretty.RESET,
                    Pretty.RED, command.positions(), Pretty.RESET,
                    Pretty.RED, command.threads(), Pretty.RESET,
                    Pretty.RED, command.softNodes(), Pretty.RESET,
                    Pretty.RED, command.hardNodes(), Pretty.RESET,
                    Pretty.GREEN, Pretty.RESET)
            );
        } else {
            System.out.printf(String.format("info string generating data, file %s, positions %d, soft limit %d, hard limit %d",
                    command.file(), command.positions(), command.softNodes(), command.hardNodes()));
        }
    }

    public void reportDatagenProgress(DatagenCommand command, Instant start, int current) {

        if (current >= command.positions()) {
            System.out.printf("generated %d/%d positions\n", current, command.positions());
            return;
        }

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
