package com.kelseyde.calvin.engine;

public class MultiPvConfig {
    public int minPvs;
    public int maxPvs;
    public int defaultPvs;

    public MultiPvConfig(int minPvs, int maxPvs, int defaultPvs) {
        this.minPvs = minPvs;
        this.maxPvs = maxPvs;
        this.defaultPvs = defaultPvs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int minPvs;
        private int maxPvs;
        private int defaultPvs;

        public Builder() {}

        public Builder minPvs(int minPvs) {
            this.minPvs = minPvs;
            return this;
        }

        public Builder maxPvs(int maxPvs) {
            this.maxPvs = maxPvs;
            return this;
        }

        public Builder defaultPvs(int defaultPvs) {
            this.defaultPvs = defaultPvs;
            return this;
        }

        public MultiPvConfig build() {
            return new MultiPvConfig(minPvs, maxPvs, defaultPvs);
        }
    }
}
