package com.eatthepath.jvptree;

import java.util.Comparator;

public class DistanceComparator<T> implements Comparator<T> {
    private final T origin;
    private final DistanceFunction<T> distanceFunction;

    public DistanceComparator(final T origin, final DistanceFunction<T> distanceFunction) {
        this.origin = origin;
        this.distanceFunction = distanceFunction;
    }

    public int compare(final T o1, final T o2) {
        return Double.compare(
                this.distanceFunction.getDistance(this.origin, o1),
                this.distanceFunction.getDistance(this.origin, o2));
    }
}
