package com.kelseyde.calvin.search;

import com.kelseyde.calvin.Application;

public class ThreadManager {

    int depthLogged;

    public ThreadManager() {
        depthLogged = 0;
    }

    public synchronized void handleSearchResult(SearchResult searchResult) {
        if (searchResult.depth() > depthLogged) {
            Application.writeSearchInfo(searchResult);
            depthLogged++;
        }
    }

    public synchronized void reset() {
        depthLogged = 0;
    }

}
