package com.eatthepath.jvptree.util;

import java.util.List;
import java.util.Random;

import com.eatthepath.jvptree.DistanceFunction;
import com.eatthepath.jvptree.ThresholdSelectionStrategy;

/**
 * A threshold distance selection strategy that uses the median distance from the origin as the threshold.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
public class MedianDistanceThresholdSelectionStrategy<P, E extends P> implements ThresholdSelectionStrategy<P, E> {

    /**
     * Returns the median distance of the given points from the given origin. This method will partially sort the list
     * of points in the process.
     *
     * @param points the list of points from which a median distance will be chosen
     * @param origin the point from which distances to other points will be calculated
     * @param distanceFunction the function to be used to calculate the distance between the origin and other points
     *
     * @return the median distance from the origin to the given list of points
     *
     * @throws IllegalArgumentException if the given list of points is empty
     */
    public double selectThreshold(final List<E> points, final P origin, final DistanceFunction<P> distanceFunction) {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("Point list must not be empty.");
        }

        int left = 0;
        int right = points.size() - 1;

        final int medianIndex = points.size() / 2;
        final Random random = new Random();

        // The strategy here is to use quickselect (https://en.wikipedia.org/wiki/Quickselect) to recursively partition
        // the parts of a list on one side of a pivot, working our way toward the center of the list.
        while (left != right) {
            final int pivotIndex = left + (right - left == 0 ? 0 : random.nextInt(right - left));
            final double pivotDistance = distanceFunction.getDistance(origin, points.get(pivotIndex));

            // Temporarily move the pivot point all the way out to the end of this section of the list
            java.util.Collections.swap(points, pivotIndex, right);

            int storeIndex = left;

            for (int i = left; i < right; i++) {
                if (distanceFunction.getDistance(origin, points.get(i)) < pivotDistance) {
                    java.util.Collections.swap(points, storeIndex++, i);
                }
            }

            // ...and now bring that original pivot point back to its rightful place.
            java.util.Collections.swap(points, right, storeIndex);

            if (storeIndex == medianIndex) {
                // Mission accomplished; we've placed the point that should rightfully be at the median index
                break;
            } else if (storeIndex < medianIndex) {
                // We need to work on the section of the list to the right of the pivot
                left = storeIndex + 1;
            } else {
                // We need to work on the section of the list to the left of the pivot
                right = storeIndex - 1;
            }
        }

        return distanceFunction.getDistance(origin, points.get(medianIndex));
    }
}
