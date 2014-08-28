package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class NearestNeighborCollector<E> {
    private final E origin;
    private final int capacity;

    private final DistanceFunction<E> distanceFunction;
    private final DistanceComparator<E> distanceComparator;
    private final PriorityQueue<E> priorityQueue;

    private double distanceToFarthestPoint;

    public NearestNeighborCollector(final E origin, final DistanceFunction<E> distanceFunction, final int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        this.origin = origin;
        this.distanceFunction = distanceFunction;
        this.capacity = capacity;

        this.distanceComparator = new DistanceComparator<E>(origin, distanceFunction);

        this.priorityQueue =
                new PriorityQueue<E>(this.capacity, java.util.Collections.reverseOrder(this.distanceComparator));
    }

    public void offerPoint(final E point) {
        final boolean pointAdded;

        if (this.priorityQueue.size() < this.capacity) {
            this.priorityQueue.add(point);
            pointAdded = true;
        } else {
            assert this.priorityQueue.size() > 0;

            final double distanceToNewPoint = this.distanceFunction.getDistance(this.origin, point);

            if (distanceToNewPoint < this.distanceToFarthestPoint) {
                this.priorityQueue.poll();
                this.priorityQueue.add(point);
                pointAdded = true;
            } else {
                pointAdded = false;
            }
        }

        if (pointAdded) {
            this.distanceToFarthestPoint = this.distanceFunction.getDistance(this.origin, this.priorityQueue.peek());
        }
    }

    public E getFarthestPoint() {
        return this.priorityQueue.peek();
    }

    public List<E> toSortedList() {
        final ArrayList<E> sortedList = new ArrayList<E>(this.priorityQueue);
        java.util.Collections.sort(sortedList, this.distanceComparator);

        return sortedList;
    }
}
