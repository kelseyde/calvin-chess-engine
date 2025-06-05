package com.kelseyde.calvin.engine;

public class HashConfig {
    public int minSizeMb;
    public int maxSizeMb;
    public int defaultSizeMb;

    public HashConfig(int minSizeMb, int maxSizeMb, int defaultSizeMb) {
        this.minSizeMb = minSizeMb;
        this.maxSizeMb = maxSizeMb;
        this.defaultSizeMb = defaultSizeMb;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int minSizeMb;
        private int maxSizeMb;
        private int defaultSizeMb;

        public Builder() {}

        public Builder minSizeMb(int minSizeMb) {
            this.minSizeMb = minSizeMb;
            return this;
        }

        public Builder maxSizeMb(int maxSizeMb) {
            this.maxSizeMb = maxSizeMb;
            return this;
        }

        public Builder defaultSizeMb(int defaultSizeMb) {
            this.defaultSizeMb = defaultSizeMb;
            return this;
        }

        public HashConfig build() {
            return new HashConfig(minSizeMb, maxSizeMb, defaultSizeMb);
        }

    }

}
