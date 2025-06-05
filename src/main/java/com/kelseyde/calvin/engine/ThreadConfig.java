package com.kelseyde.calvin.engine;

public class ThreadConfig {
    public int minThreads;
    public int maxThreads;
    public int defaultThreads;

    public ThreadConfig(int minThreads, int maxThreads, int defaultThreads) {
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.defaultThreads = defaultThreads;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int minThreads;
        private int maxThreads;
        private int defaultThreads;

        public Builder() {}

        public Builder minThreads(int minThreads) {
            this.minThreads = minThreads;
            return this;
        }

        public Builder maxThreads(int maxThreads) {
            this.maxThreads = maxThreads;
            return this;
        }

        public Builder defaultThreads(int defaultThreads) {
            this.defaultThreads = defaultThreads;
            return this;
        }

        public ThreadConfig build() {
            return new ThreadConfig(minThreads, maxThreads, defaultThreads);
        }
    }
}
