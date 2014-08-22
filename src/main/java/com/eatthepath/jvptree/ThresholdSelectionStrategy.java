package com.eatthepath.jvptree;

import java.util.List;

public interface ThresholdSelectionStrategy<T> {
    double selectThreshold(List<T> points, T origin, DistanceFunction<T> distanceFunction);
}
