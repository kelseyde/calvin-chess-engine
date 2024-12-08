package com.kelseyde.calvin.datagen.dataformat;

import java.util.List;

public interface DataFormat<T> {

    record DataPoint(String fen, int score, int result) {}

    T serialize(DataPoint dataPoint);

    T serialize(List<DataPoint> dataPoints);

}
