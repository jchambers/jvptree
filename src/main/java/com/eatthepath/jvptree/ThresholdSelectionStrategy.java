package com.eatthepath.jvptree;

public interface ThresholdSelectionStrategy<T> {
    double selectThreshold(T origin, T[] points, int fromIndex, int toIndex, DistanceFunction<T> distanceFunction);
}
