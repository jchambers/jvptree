package com.eatthepath.jvptree;

import java.util.ArrayList;

class VPNode<E> {

    private final int capacity;
    private final DistanceFunction<E> distanceFunction;
    private final ThresholdSelectionStrategy<E> thresholdSelectionStrategy;

    private E vantagePoint;
    private double threshold;

    private VPNode<E> closer;
    private VPNode<E> farther;

    private ArrayList<E> points;

    public VPNode(final E[] points, final int fromIndex, final int toIndex, final int capacity,
            final DistanceFunction<E> distanceFunction, ThresholdSelectionStrategy<E> thresholdSelectionStrategy) {

        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        if (toIndex - fromIndex < 1) {
            throw new IllegalArgumentException("Index range does not contain any points.");
        }

        this.capacity = capacity;
        this.distanceFunction = distanceFunction;
        this.thresholdSelectionStrategy = thresholdSelectionStrategy;

        if (toIndex - fromIndex > this.capacity) {
            // Partially sort the list such that all points closer than or equal to the threshold distance from the
            // vantage point come before the threshold point in the list and all points farther away come after the
            // threshold point.
            this.vantagePoint = points[fromIndex];
            this.threshold = this.thresholdSelectionStrategy.selectThreshold(this.vantagePoint, points, fromIndex, toIndex, this.distanceFunction);

            final int firstIndexPastThreshold;
            {
                int i = fromIndex;
                int j = toIndex - 1;

                for (; i <= j; i++) {
                    if (this.distanceFunction.getDistance(this.vantagePoint, points[i]) > this.threshold) {
                        for (; j >= i; j--) {
                            if (this.distanceFunction.getDistance(this.vantagePoint, points[j]) <= this.threshold) {
                                final E swap = points[i];
                                points[i] = points[j];
                                points[j] = swap;

                                j -= 1;

                                break;
                            }
                        }
                    }
                }

                firstIndexPastThreshold =
                        this.distanceFunction.getDistance(this.vantagePoint, points[i - 1]) > this.threshold ? i - 1 : i;
            }

            if (this.distanceFunction.getDistance(this.vantagePoint, points[fromIndex]) <= this.threshold &&
                    this.distanceFunction.getDistance(this.vantagePoint, points[toIndex - 1]) > this.threshold) {

                this.closer = new VPNode<E>(points, fromIndex, firstIndexPastThreshold, this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
                this.farther = new VPNode<E>(points, firstIndexPastThreshold, toIndex, this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
            }
        }

        if (this.closer == null) {
            // We didn't create child nodes, which means that either (a) we don't want to because the sub-array is
            // smaller than our desired capacity or (b) we can't due to some degenerate case.
            this.points = new ArrayList<E>(fromIndex - toIndex);

            for (int i = fromIndex; i < toIndex; i++) {
                this.points.add(points[i]);
            }
        }
    }
}
