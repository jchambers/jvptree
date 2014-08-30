package com.eatthepath.jvptree.util;

import java.util.ArrayList;
import java.util.List;

import com.eatthepath.jvptree.DistanceFunction;
import com.eatthepath.jvptree.ThresholdSelectionStrategy;

public class SamplingMedianDistanceThresholdSelectionStrategy<T> extends MedianDistanceThresholdSelectionStrategy<T> implements ThresholdSelectionStrategy<T> {

    private final int numberOfSamples;

    public static final int DEFAULT_NUMBER_OF_SAMPLES = 32;

    public SamplingMedianDistanceThresholdSelectionStrategy(final int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    @Override
    public double selectThreshold(final List<T> points, final T origin, final DistanceFunction<T> distanceFunction) {
        return super.selectThreshold(this.getSampledPoints(points), origin, distanceFunction);
    }

    private List<T> getSampledPoints(final List<T> points) {
        final List<T> sampledPoints;
        final int numberOfPoints = points.size();

        if (numberOfPoints > this.numberOfSamples) {
            sampledPoints = new ArrayList<T>(this.numberOfSamples);

            for (int i = 0; i < this.numberOfSamples; i++) {
                sampledPoints.add(points.get((i * numberOfPoints) / this.numberOfSamples));
            }
        } else {
            sampledPoints = points;
        }

        return sampledPoints;
    }
}
