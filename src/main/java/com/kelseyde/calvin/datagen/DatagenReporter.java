package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.uci.Pretty;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.UCICommand;

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

    public void reportDatagenProgress() {
        // TODO
    }

}
