package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class VPNode<E> {

    private final int capacity;
    private final DistanceFunction<E> distanceFunction;
    private final ThresholdSelectionStrategy<E> thresholdSelectionStrategy;

    private ArrayList<E> points;

    private final E vantagePoint;

    // Note to self: threshold is not meaningful
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

        // All nodes must have a vantage point; choose one at random from the available points
        this.vantagePoint = points.get(new Random().nextInt(points.size()));

        if (points.size() > this.capacity) {
            // Partially sort the list such that all points closer than or equal to the threshold distance from the
            // vantage point come before the threshold point in the list and all points farther away come after the
            // threshold point.
            this.threshold = this.thresholdSelectionStrategy.selectThreshold(points, this.vantagePoint, this.distanceFunction);

            try {
                final int firstIndexPastThreshold =
                        VPNode.partitionPoints(points, this.vantagePoint, this.threshold, this.distanceFunction);

                this.closer = new VPNode<E>(points.subList(0, firstIndexPastThreshold), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
                this.farther = new VPNode<E>(points.subList(firstIndexPastThreshold, points.size()), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
            } catch (PartitionException e) {
                // We couldn't partition the list, so just store all of the points in this node
                this.points = new ArrayList<E>(points);
            }
        } else {
            // No need to partition; just store everything in this node
            this.points = new ArrayList<E>(points);
        }
    }

    public int size() {
        if (this.points == null) {
            return this.closer.size() + this.farther.size();
        } else {
            return this.points.size();
        }
    }

    public void add(final E point) {
        if (this.points == null) {
            // This is not a leaf node; pass this point on to the appropriate child
            this.getChildNodeForPoint(point).add(point);
        } else {
            this.points.add(point);

            if (this.points.size() > this.capacity) {
                try {
                    this.threshold = this.thresholdSelectionStrategy.selectThreshold(this.points, this.vantagePoint, this.distanceFunction);

                    final int firstIndexPastThreshold =
                            VPNode.partitionPoints(this.points, this.vantagePoint, this.threshold, this.distanceFunction);

                    this.closer = new VPNode<E>(this.points.subList(0, firstIndexPastThreshold), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
                    this.farther = new VPNode<E>(this.points.subList(firstIndexPastThreshold, this.points.size()), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);

                    this.points = null;
                } catch (PartitionException e) {
                    // We couldn't partition the list in any useful way; just keep all of the points here
                }
            }
        }
    }

    public boolean remove(final E point) {
        if (this.points == null) {
            // This is not a leaf node; try to remove the point from an appropriate child node
            final VPNode<E> childNode = this.getChildNodeForPoint(point);
            final boolean modified = childNode.remove(point);

            if (childNode.size() == 0) {
                final ArrayList<E> collectedPoints = new ArrayList<E>(this.size());
                this.addAllPointsToCollection(collectedPoints);

                this.threshold = this.thresholdSelectionStrategy.selectThreshold(collectedPoints, this.vantagePoint, this.distanceFunction);

                try {
                    final int firstIndexPastThreshold =
                            VPNode.partitionPoints(collectedPoints, this.vantagePoint, this.threshold, this.distanceFunction);

                    this.closer = new VPNode<E>(collectedPoints.subList(0, firstIndexPastThreshold), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
                    this.farther = new VPNode<E>(collectedPoints.subList(firstIndexPastThreshold, collectedPoints.size()), this.capacity, this.distanceFunction, this.thresholdSelectionStrategy);
                } catch (PartitionException e) {
                    this.closer = null;
                    this.farther = null;

                    this.points = collectedPoints;
                }
            }

            return modified;
        } else {
            return this.points.remove(point);
        }
    }

    public boolean contains(final E point) {
        return this.points == null ? this.getChildNodeForPoint(point).contains(point) : this.points.contains(point);
    }

    public boolean retainAll(final Collection<?> points) {
        return this.points == null ?
                this.closer.retainAll(points) || this.farther.retainAll(points) : this.points.retainAll(points);
    }

    public void collectNearestNeighbors(final E queryPoint, final NearestNeighborCollector<E> collector) {
        if (this.points == null) {
            final VPNode<E> firstNodeSearched = this.getChildNodeForPoint(queryPoint);
            firstNodeSearched.collectNearestNeighbors(queryPoint, collector);

            final double distanceFromVantagePointToQueryPoint =
                    this.distanceFunction.getDistance(this.vantagePoint, queryPoint);

            final double distanceFromQueryPointToFarthestPoint =
                    this.distanceFunction.getDistance(queryPoint, collector.getFarthestPoint());

            if (firstNodeSearched == this.closer) {
                // We've already searched the node that contains points within this node's threshold. We also want to
                // search the farther node if the distance from the query point to the most distant point in the
                // neighbor collector is greater than the distance from the query point to this node's threshold, since
                // there could be a point outside of this node that's closer than the most distant neighbor we've found
                // so far.

                final double distanceFromQueryPointToThreshold = this.threshold - distanceFromVantagePointToQueryPoint;

                if (distanceFromQueryPointToFarthestPoint > distanceFromQueryPointToThreshold) {
                    this.farther.collectNearestNeighbors(queryPoint, collector);
                }
            } else {
                // We've already searched the node that contains points beyond this node's threshold. We want to search
                // the within-threshold node if it's "easier" to get from the query point to this node's region than it
                // is to get from the query point to the most distant match, since there could be a point within this
                // node's threshold that's closer than the most distant match.
                double distanceFromQueryPointToThreshold = distanceFromVantagePointToQueryPoint - this.threshold;

                if(distanceFromQueryPointToThreshold <= distanceFromQueryPointToFarthestPoint) {
                    this.closer.collectNearestNeighbors(queryPoint, collector);
                }
            }
        } else {
            for (final E point : this.points) {
                collector.offerPoint(point);
            }
        }
    }

    public void collectAllWithinRange(final E queryPoint, final double maxDistance, final Collection<E> collection) {
        if (this.points == null) {
            double distanceFromVantagePointToQueryPoint =
                    this.distanceFunction.getDistance(this.vantagePoint, queryPoint);

            // We want to search any of this node's children that intersect with the query region
            if (distanceFromVantagePointToQueryPoint <= this.threshold + maxDistance) {
                this.closer.collectAllWithinRange(queryPoint, maxDistance, collection);
            }

            if (distanceFromVantagePointToQueryPoint + maxDistance > this.threshold) {
                this.farther.collectAllWithinRange(queryPoint, maxDistance, collection);
            }
        } else {
            for (final E point : this.points) {
                if (this.distanceFunction.getDistance(this.vantagePoint, point) <= maxDistance) {
                    collection.add(point);
                }
            }
        }
    }

    private VPNode<E> getChildNodeForPoint(final E point) {
        return this.distanceFunction.getDistance(this.vantagePoint, point) <= this.threshold ? this.closer : this.farther;
    }

    private void addAllPointsToCollection(final Collection<E> collection) {
        if (this.points == null) {
            this.closer.addAllPointsToCollection(collection);
            this.farther.addAllPointsToCollection(collection);
        } else {
            collection.addAll(this.points);
        }
    }

    /**
     * 
     * @param points
     * @param vantagePoint
     * @param threshold
     * @param distanceFunction
     * @return the index of the first point in the list that falls beyond the distance threshold
     * @throws PartitionException
     */
    private static <E> int partitionPoints(final List<E> points, final E vantagePoint, final double threshold, final DistanceFunction<E> distanceFunction) throws PartitionException {
        int i = 0;
        int j = points.size() - 1;

        // This is, essentially, a single swapping quicksort iteration
        for (; i <= j; i++) {
            if (distanceFunction.getDistance(vantagePoint, points.get(i)) > threshold) {
                for (; j >= i; j--) {
                    if (distanceFunction.getDistance(vantagePoint, points.get(j)) <= threshold) {
                        Collections.swap(points, i, j--);
                        break;
                    }
                }
            }
        }

        final int firstIndexPastThreshold = distanceFunction.getDistance(vantagePoint, points.get(i - 1)) > threshold ? i - 1 : i;

        if (distanceFunction.getDistance(vantagePoint, points.get(0)) <= threshold &&
                distanceFunction.getDistance(vantagePoint, points.get(points.size() - 1)) > threshold) {

            return firstIndexPastThreshold;
        } else {
            throw new PartitionException();
        }
    }
}
