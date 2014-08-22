package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class VPNode<E> {

    private final int capacity;
    private final DistanceFunction<E> distanceFunction;
    private final ThresholdSelectionStrategy<E> thresholdSelectionStrategy;

    private ArrayList<E> points;

    private E vantagePoint;
    private double threshold;

    private VPNode<E> closer;
    private VPNode<E> farther;

    public VPNode(final List<E> points, final int capacity, final DistanceFunction<E> distanceFunction,
            final ThresholdSelectionStrategy<E> thresholdSelectionStrategy) {

        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        if (points.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a VPNode with an empty list of points.");
        }

        this.capacity = capacity;
        this.distanceFunction = distanceFunction;
        this.thresholdSelectionStrategy = thresholdSelectionStrategy;

        if (points.size() > this.capacity) {
            // All nodes must have a vantage point; choose one at random from the available points
            this.vantagePoint = points.get(new Random().nextInt(points.size()));

            // Partially sort the list such that all points closer than or equal to the threshold distance from the
            // vantage point come before the threshold point in the list and all points farther away come after the
            // threshold point.
            this.threshold = this.thresholdSelectionStrategy.selectThreshold(this.vantagePoint, points, this.distanceFunction);

            final int firstIndexPastThreshold;
            {
                int i = 0;
                int j = points.size() - 1;

                for (; i <= j; i++) {
                    if (this.distanceFunction.getDistance(this.vantagePoint, points.get(i)) > this.threshold) {
                        for (; j >= i; j--) {
                            if (this.distanceFunction.getDistance(this.vantagePoint, points.get(j)) <= this.threshold) {
                                Collections.swap(points, i, j--);
                                break;
                            }
                        }
                    }
                }

                firstIndexPastThreshold =
                        this.distanceFunction.getDistance(this.vantagePoint, points.get(i - 1)) > this.threshold ? i - 1 : i;
            }

            if (this.distanceFunction.getDistance(this.vantagePoint, points.get(0)) <= this.threshold &&
                    this.distanceFunction.getDistance(this.vantagePoint, points.get(points.size() - 1)) > this.threshold) {

                this.closer = new VPNode<E>(points.subList(0, firstIndexPastThreshold), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
                this.farther = new VPNode<E>(points.subList(firstIndexPastThreshold, points.size()), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
            }
        }

        if (this.closer == null) {
            // We didn't create child nodes, which means that either (a) we don't want to because the sub-array is
            // smaller than our desired capacity or (b) we can't due to some degenerate case.
            this.points = new ArrayList<E>(points);
        }
    }
}
