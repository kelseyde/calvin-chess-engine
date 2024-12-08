package com.kelseyde.calvin.datagen.dataformat;

import java.util.List;

/**
 * Interface for serializing data points into a specific format.
 * @param <T>
 */
public interface DataFormat<T> {

    record DataPoint(String fen, int score, int result) {}

    T serialize(DataPoint dataPoint);

    T serialize(List<DataPoint> dataPoints);

}
