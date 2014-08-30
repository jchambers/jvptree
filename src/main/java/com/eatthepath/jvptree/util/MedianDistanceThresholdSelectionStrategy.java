package com.eatthepath.jvptree.util;

import java.util.List;

import com.eatthepath.jvptree.DistanceComparator;
import com.eatthepath.jvptree.DistanceFunction;
import com.eatthepath.jvptree.ThresholdSelectionStrategy;

/**
 * A threshold distance selection strategy that uses the median distance from the origin as the threshold.
 * 
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 *
 * @param <T>
 */
public class MedianDistanceThresholdSelectionStrategy<T> implements ThresholdSelectionStrategy<T> {

    /**
     * Returns the median distance of the given points from the given origin. This method will partially sort the list
     * of points in the process.
     * 
     * @param points the list of points from which a median distance will be chosen
     * @param origin the point from which distances to other points will be calculated
     * @param distanceFunction the function to be used to calculate the distance between the origin and other points
     * 
     * @return the median distance from the origin to the given list of points
     */
    public double selectThreshold(final List<T> points, final T origin, final DistanceFunction<T> distanceFunction) {
        // TODO This is terrible and should be replaced with quickselect or introselect
        java.util.Collections.sort(points, new DistanceComparator<T>(origin, distanceFunction));
        return distanceFunction.getDistance(origin, points.get(points.size() / 2));
    }
}
