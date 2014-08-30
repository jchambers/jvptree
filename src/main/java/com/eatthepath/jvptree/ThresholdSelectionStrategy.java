package com.eatthepath.jvptree;

import java.util.List;

/**
 * A function for choosing a distance threshold for vp-tree nodes.
 * 
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 *
 * @param <T>
 */
public interface ThresholdSelectionStrategy<T> {
    double selectThreshold(List<T> points, T origin, DistanceFunction<T> distanceFunction);
}
