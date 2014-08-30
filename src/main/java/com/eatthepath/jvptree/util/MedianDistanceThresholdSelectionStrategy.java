package com.eatthepath.jvptree.util;

import java.util.List;

import com.eatthepath.jvptree.DistanceComparator;
import com.eatthepath.jvptree.DistanceFunction;
import com.eatthepath.jvptree.ThresholdSelectionStrategy;

public class MedianDistanceThresholdSelectionStrategy<T> implements ThresholdSelectionStrategy<T> {

    public double selectThreshold(final List<T> points, final T origin, final DistanceFunction<T> distanceFunction) {
        // TODO This is terrible and should be replaced with quickselect or introselect
        java.util.Collections.sort(points, new DistanceComparator<T>(origin, distanceFunction));
        return distanceFunction.getDistance(origin, points.get(points.size() / 2));
    }
}
