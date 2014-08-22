package com.eatthepath.jvptree;

import java.util.List;

public interface ThresholdSelectionStrategy<T> {
    double selectThreshold(T origin, List<T> points, DistanceFunction<T> distanceFunction);
}
