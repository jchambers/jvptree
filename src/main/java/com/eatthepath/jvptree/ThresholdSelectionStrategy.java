package com.eatthepath.jvptree;

import java.util.List;

/**
 * A strategy for choosing a distance threshold for vp-tree nodes. The main feature of vp-trees is that they partition
 * collections of points into collections of points that are closer to a given point (the vantage point) than a certain
 * threshold or farther away from the vantage point than the threshold. Given a list of points, a
 * {@code ThresholdSelectionStrategy} chooses the distance that will be used by a vp-tree node to partition its points.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
public interface ThresholdSelectionStrategy<P, E extends P> {

    /**
     * Chooses a partitioning distance threshold appropriate for the given list of points. Implementations are allowed to
     * reorder the list of points, but must not add or remove points from the list.
     *
     * @param points the points for which to choose a partitioning distance threshold
     * @param origin the point from which the threshold distances should be calculated
     * @param distanceFunction the function to be used to calculate distances between points
     *
     * @return a partitioning threshold distance appropriate for the given list of points; ideally, some points should
     * be closer to the origin than the returned threshold, and some should be farther
     */
    double selectThreshold(List<E> points, P origin, DistanceFunction<P> distanceFunction);
}
