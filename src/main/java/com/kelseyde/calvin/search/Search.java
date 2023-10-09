package com.kelseyde.calvin.search;

import java.time.Duration;

public interface Search {

    SearchResult search(Duration duration);

    void clearHistory();

    void logStatistics();

}
