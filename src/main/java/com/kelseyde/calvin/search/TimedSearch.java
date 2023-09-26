package com.kelseyde.calvin.search;

import java.time.Duration;

public interface TimedSearch {

    SearchResult search(Duration duration);

}
