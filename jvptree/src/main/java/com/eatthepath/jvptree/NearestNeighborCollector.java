package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A utility class that uses a priority queue to efficiently collect results for a k-nearest-neighbors query in a
 * vp-tree.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
class NearestNeighborCollector<P, E extends P> {
    private final P queryPoint;
    private final int capacity;

    private final DistanceFunction<P> distanceFunction;
    private final DistanceComparator<P> distanceComparator;
    private final PriorityQueue<E> priorityQueue;

    private double distanceToFarthestPoint;

    /**
     * Constructs a new nearest neighbor collector that selectively accepts points that are close to the given query
     * point as determined by the given distance function. Up to the given number of nearest neighbors are collected,
     * and if neighbors are found that are closer than points in the current set, the most distant previously collected
     * point is replaced with the closer candidate.
     *
     * @param queryPoint the point for which nearest neighbors are to be collected
     * @param distanceFunction the distance function to be used to determine the distance between the query point and
     * potential neighbors
     * @param capacity the maximum number of nearest neighbors to collect
     */
    public NearestNeighborCollector(final P queryPoint, final DistanceFunction<P> distanceFunction, final int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        this.queryPoint = queryPoint;
        this.distanceFunction = distanceFunction;
        this.capacity = capacity;

        this.distanceComparator = new DistanceComparator<>(queryPoint, distanceFunction);

        this.priorityQueue =
                new PriorityQueue<>(this.capacity, java.util.Collections.reverseOrder(this.distanceComparator));
    }

    /**
     * Returns the query point for this collector.
     *
     * @return the query point for this collector
     */
    public P getQueryPoint() {
        return this.queryPoint;
    }

    /**
     * Offers a point to this collector. The point may or may not be added to the collection; points will only be added
     * if the collector is not already full, or if the collector is full, but the offered point is closer to the query
     * point than the most distant point already in the collection.
     *
     * @param point the point to offer to this collector
     */
    public void offerPoint(final E point) {
        final boolean pointAdded;

        if (this.priorityQueue.size() < this.capacity) {
            this.priorityQueue.add(point);
            pointAdded = true;
        } else {
            assert this.priorityQueue.size() > 0;

            final double distanceToNewPoint = this.distanceFunction.getDistance(this.queryPoint, point);

            if (distanceToNewPoint < this.distanceToFarthestPoint) {
                this.priorityQueue.poll();
                this.priorityQueue.add(point);
                pointAdded = true;
            } else {
                pointAdded = false;
            }
        }

        if (pointAdded) {
            this.distanceToFarthestPoint = this.distanceFunction.getDistance(this.queryPoint, this.priorityQueue.peek());
        }
    }

    /**
     * Returns the point retained by this collector that is the farthest from the query point.
     *
     * @return the point retained by this collector that is the farthest from the query point
     */
    public E getFarthestPoint() {
        return this.priorityQueue.peek();
    }

    /**
     * Returns a list of points retained by this collector, sorted by distance from the query point.
     *
     * @return a list of points retained by this collector, sorted by distance from the query point
     */
    public List<E> toSortedList() {
        final ArrayList<E> sortedList = new ArrayList<>(this.priorityQueue);
        java.util.Collections.sort(sortedList, this.distanceComparator);

        return sortedList;
    }
}
