package com.kelseyde.calvin.search;

import java.time.Duration;

public record TimeLimit(Duration softLimit, Duration hardLimit) {}
