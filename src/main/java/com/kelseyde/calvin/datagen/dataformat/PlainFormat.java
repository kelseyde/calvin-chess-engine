package com.kelseyde.calvin.datagen.dataformat;

import java.util.List;
import java.util.stream.Collectors;

public class PlainFormat implements DataFormat<String> {

    @Override
    public String serialize(DataPoint dataPoint) {
        final String fen = dataPoint.fen();
        final String score = String.valueOf(dataPoint.score());
        final String result = formatResult(dataPoint.result());
        return String.format("%s | %s | %s\n", fen, score, result);
    }

    @Override
    public String serialize(List<DataPoint> dataPoints) {
        return dataPoints.stream()
                .map(this::serialize)
                .collect(Collectors.joining());
    }

    private String formatResult(int result) {
        return switch (result) {
            case 1 -> "1.0";
            case 0 -> "0.5";
            case -1 -> "0.0";
            default -> throw new IllegalArgumentException("Invalid result: " + result);
        };
    }

}
