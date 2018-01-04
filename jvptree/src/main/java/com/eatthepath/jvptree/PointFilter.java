package com.eatthepath.jvptree;

/**
 * A stateless filter that can determine whether points should be included in a spatial index's result set when
 * searching for nearby neighbors.
 *
 * @param <T> the type of point to which this filter applies
 *
 * @see SpatialIndex#getNearestNeighbors(Object, int, PointFilter)
 * @see SpatialIndex#getAllWithinDistance(Object, double, PointFilter)
 */
public interface PointFilter<T> {

    /**
     * Tests whether a point should be included in a spatial index's result set when searching for nearby neighbors.
     *
     * @param point the point to test
     *
     * @return {@code true} if the point may be included in the result set or {@code false} if it should be excluded
     */
    boolean allowPoint(T point);
}
