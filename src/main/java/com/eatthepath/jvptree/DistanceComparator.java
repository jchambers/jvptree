package com.eatthepath.jvptree;

import java.util.Comparator;

/**
 * A {@code Comparator} that orders points by their distance (as determined by a given distance function) from a given
 * origin point.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
public class DistanceComparator<T> implements Comparator<T> {
    private final T origin;
    private final DistanceFunction<? super T> distanceFunction;

    /**
     * Constructs a new distance comparator with the given origin point and distance function.
     *
     * @param origin the point from which distances to other points will be calculated
     * @param distanceFunction the function that calculates the distance between the origin and the given points
     */
    public DistanceComparator(final T origin, final DistanceFunction<? super T> distanceFunction) {
        this.origin = origin;
        this.distanceFunction = distanceFunction;
    }

    /**
     * Compares two points by their distance from this distance comparator's origin point.
     *
     * @param o1 the first point to be compared
     * @param o2 the second point to be compared
     *
     * @return a negative integer if o1 is closer to the origin than o2, a positive integer if o2 is closer to the
     * origin than o1, or zero if o1 and o2 are equidistant from the origin
     */
    public int compare(final T o1, final T o2) {
        return Double.compare(
                this.distanceFunction.getDistance(this.origin, o1),
                this.distanceFunction.getDistance(this.origin, o2));
    }
}
