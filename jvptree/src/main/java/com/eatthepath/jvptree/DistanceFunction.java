package com.eatthepath.jvptree;

/**
 * <p>A function that calculates the distance between two points. For the purposes of vp-trees, distance functions must
 * conform to the rules of a metric space, namely:</p>
 *
 * <ol>
 *  <li>d(x, y) &ge; 0</li>
 *  <li>d(x, y) = 0 if and only if x = y</li>
 *  <li>d(x, y) = d(y, x)</li>
 *  <li>d(x, z) &le; d(x, y) + d(y, z)</li>
 * </ol>
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
public interface DistanceFunction<T> {

    /**
     * Returns the distance between two points.
     *
     * @param firstPoint the first point
     * @param secondPoint the second point
     *
     * @return the distance between the two points
     */
    double getDistance(T firstPoint, T secondPoint);
}
