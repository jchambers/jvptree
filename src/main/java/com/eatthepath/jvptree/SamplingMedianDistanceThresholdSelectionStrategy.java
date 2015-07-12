package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.List;

/**
 * A threshold distance selection strategy that uses the median distance from the origin to a subset of the given list
 * of points as the threshold.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
public class SamplingMedianDistanceThresholdSelectionStrategy<T> extends MedianDistanceThresholdSelectionStrategy<T> implements ThresholdSelectionStrategy<T> {

    private final int numberOfSamples;

    public static final int DEFAULT_NUMBER_OF_SAMPLES = 32;

    /**
     * Constructs a threshold selector that uses up to the given number of samples from a list of points to choose a
     * median distance.
     *
     * @param numberOfSamples the maximum number of samples to use when choosing a median distance
     */
    public SamplingMedianDistanceThresholdSelectionStrategy(final int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    /**
     * Returns the median distance of a subset of the given points from the given origin. The given list of points may
     * be partially sorted in the process.
     *
     * @param points the list of points from which a median distance will be chosen
     * @param origin the point from which distances to other points will be calculated
     * @param distanceFunction the function to be used to calculate the distance between the origin and other points
     *
     * @return the median distance from the origin to the given list of points
     */
    @Override
    public <R extends T> double selectThreshold(final List<R> points, final R origin, final DistanceFunction<? super R> distanceFunction) {
        return super.selectThreshold(this.getSampledPoints(points), origin, distanceFunction);
    }

    /**
     * Chooses a subset of points from which to calculate a median by sampling the given list.
     *
     * @param points the points from which to choose a subset of points
     *
     * @return a list containing at most the number of points chosen at construction time
     */
    private <R extends T> List<R> getSampledPoints(final List<R> points) {
        final List<R> sampledPoints;
        final int numberOfPoints = points.size();

        if (numberOfPoints > this.numberOfSamples) {
            sampledPoints = new ArrayList<R>(this.numberOfSamples);

            for (int i = 0; i < this.numberOfSamples; i++) {
                sampledPoints.add(points.get((i * numberOfPoints) / this.numberOfSamples));
            }
        } else {
            sampledPoints = points;
        }

        return sampledPoints;
    }
}
