package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A single node of a vantage-point tree. Nodes may either be leaf nodes that contain points directly or branch nodes
 * that have a "closer than threshold" and "farther than threshold" child node.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
class VPTreeNode<P, E extends P> {

    private final int capacity;
    private final DistanceFunction<P> distanceFunction;
    private final ThresholdSelectionStrategy<P, E> thresholdSelectionStrategy;

    private ArrayList<E> points;

    private final E vantagePoint;

    private double threshold;

    private VPTreeNode<P, E> closer;
    private VPTreeNode<P, E> farther;

    /**
     * Constructs a new node that contains the given collection of points. If the given collection of points is larger
     * than the given maximum capacity, the new node will attempts to partition the collection of points into child
     * nodes using the given distance function and threshold selection strategy.
     *
     * @param points the collection of points to store in or below this node
     * @param distanceFunction the distance function to use when partitioning points
     * @param thresholdSelectionStrategy the threshold selection strategy to use when selecting points
     * @param capacity the desired maximum capacity of this node; this node may contain more points than the given
     * capacity if the given collection of points cannot be partitioned (for example, because all of the points are an
     * equal distance away from the vantage point)
     */
    public VPTreeNode(final Collection<E> points, final DistanceFunction<P> distanceFunction,
            final ThresholdSelectionStrategy<P, E> thresholdSelectionStrategy, final int capacity) {

        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        if (points.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a VPTreeNode with an empty list of points.");
        }

        this.capacity = capacity;
        this.distanceFunction = distanceFunction;
        this.thresholdSelectionStrategy = thresholdSelectionStrategy;
        this.points = new ArrayList<>(points);

        // All nodes must have a vantage point; choose one at random from the available points
        this.vantagePoint = this.points.get(new Random().nextInt(points.size()));

        this.anneal();
    }

    protected void anneal() {
        if (this.points == null) {
            final int closerSize = this.closer.size();
            final int fartherSize = this.farther.size();

            if (closerSize == 0 || fartherSize == 0) {
                // One of the child nodes has become empty, and needs to be pruned.
                this.points = new ArrayList<>(closerSize + fartherSize);
                this.addAllPointsToCollection(this.points);

                this.closer = null;
                this.farther = null;

                this.anneal();
            } else {
                this.closer.anneal();
                this.farther.anneal();
            }
        } else {
            if (this.points.size() > this.capacity) {
                // Partially sort the list such that all points closer than or equal to the threshold distance from the
                // vantage point come before the threshold point in the list and all points farther away come after the
                // threshold point.
                this.threshold = this.thresholdSelectionStrategy.selectThreshold(this.points, this.vantagePoint, this.distanceFunction);

                try {
                    final int firstIndexPastThreshold =
                            VPTreeNode.partitionPoints(this.points, this.vantagePoint, this.threshold, this.distanceFunction);

                    this.closer = new VPTreeNode<>(this.points.subList(0, firstIndexPastThreshold), this.distanceFunction, this.thresholdSelectionStrategy, this.capacity);
                    this.farther = new VPTreeNode<>(this.points.subList(firstIndexPastThreshold, this.points.size()), this.distanceFunction, this.thresholdSelectionStrategy, this.capacity);

                    this.points = null;
                } catch (final PartitionException e) {
                    // We couldn't partition the list, so just store all of the points in this node
                    this.closer = null;
                    this.farther = null;
                }
            }
        }
    }

    /**
     * Returns the number of points stored in this node and its children.
     *
     * @return the number of points stored in this node and its children
     */
    public int size() {
        if (this.points == null) {
            return this.closer.size() + this.farther.size();
        } else {
            return this.points.size();
        }
    }

    /**
     * Adds a point to this node or one of its children. If this node is a leaf node and the addition of the new point
     * increases the size of the node beyond its desired capacity, the node will attempt to partition its points into
     * two child nodes.
     *
     * @param point the point to add to this node
     */
    public void add(final E point) {
        if (this.points == null) {
            // This is not a leaf node; pass this point on to the appropriate child
            this.getChildNodeForPoint(point).add(point);
        } else {
            this.points.add(point);
        }
    }

    /**
     * Removes a point from this node (if it is a leaf node) or one of its children. If the removal of the point would
     * result in an empty node, the empty node's parent will absorb and re-partition all points from all child nodes.
     *
     * @param point the point to remove from this node or one of its children
     * @return {@code true} if a points was removed or {@code false} otherwise
     */
    public boolean remove(final E point) {
        final boolean modified;

        if (this.points == null) {
            // This is not a leaf node; try to remove the point from an appropriate child node
            modified = this.getChildNodeForPoint(point).remove(point);
        } else {
            modified = this.points.remove(point);
        }

        return modified;
    }

    /**
     * Removes all from this node and its children that are not in the given collection of points. If the removal of a
     * point would result in an empty node, the empty node's parent will absorb and re-partition all points from all
     * child nodes.
     *
     * @param points the collection of points to retain
     *
     * @return {@code true} if any points were removed from this node or one of its children as a result of this
     * operation or {@code false} otherwise
     */
    public boolean retainAll(final Collection<?> points) {
        final boolean modified;

        if (this.points == null) {
            final boolean modifiedCloser = this.closer.retainAll(points);
            final boolean modifiedFarther = this.farther.retainAll(points);

            modified = modifiedCloser || modifiedFarther;
        } else {
            modified = this.points.retainAll(points);
        }

        return modified;
    }

    /**
     * Tests whether this node or one of its children contains the given point.
     *
     * @param point the point to check
     *
     * @return {@code true} if this node or one of its children contains the given point or {@code false} otherwise
     */
    public boolean contains(final E point) {
        return this.points == null ? this.getChildNodeForPoint(point).contains(point) : this.points.contains(point);
    }

    public void collectNearestNeighbors(final NearestNeighborCollector<P, E> collector, final PointFilter<? super E> filter) {
        if (this.points == null) {
            final VPTreeNode<P, E> firstNodeSearched = this.getChildNodeForPoint(collector.getQueryPoint());
            firstNodeSearched.collectNearestNeighbors(collector, filter);

            final double distanceFromVantagePointToQueryPoint =
                    this.distanceFunction.getDistance(this.vantagePoint, collector.getQueryPoint());

            final double distanceFromQueryPointToFarthestPoint =
                    this.distanceFunction.getDistance(collector.getQueryPoint(), collector.getFarthestPoint());

            if (firstNodeSearched == this.closer) {
                // We've already searched the node that contains points within this node's threshold. We also want to
                // search the farther node if the distance from the query point to the most distant point in the
                // neighbor collector is greater than the distance from the query point to this node's threshold, since
                // there could be a point outside of this node that's closer than the most distant neighbor we've found
                // so far.

                final double distanceFromQueryPointToThreshold = this.threshold - distanceFromVantagePointToQueryPoint;

                if (distanceFromQueryPointToFarthestPoint > distanceFromQueryPointToThreshold) {
                    this.farther.collectNearestNeighbors(collector, filter);
                }
            } else {
                // We've already searched the node that contains points beyond this node's threshold. We want to search
                // the within-threshold node if it's "easier" to get from the query point to this node's region than it
                // is to get from the query point to the most distant match, since there could be a point within this
                // node's threshold that's closer than the most distant match.
                final double distanceFromQueryPointToThreshold = distanceFromVantagePointToQueryPoint - this.threshold;

                if(distanceFromQueryPointToThreshold <= distanceFromQueryPointToFarthestPoint) {
                    this.closer.collectNearestNeighbors(collector, filter);
                }
            }
        } else {
            for (final E point : this.points) {
                if (filter.allowPoint(point)) {
                    collector.offerPoint(point);
                }
            }
        }
    }

    /**
     * Gathers all points within a given maximum distance of the given query point into the given collection.
     *
     * @param queryPoint the point from which to measure distance to other points
     * @param maxDistance the distance within which to collect points
     * @param collection the collection to which points within the maximum distance should be added
     */
    public void collectAllWithinDistance(final P queryPoint, final double maxDistance, final Collection<E> collection, final PointFilter<? super E> filter) {
        if (this.points == null) {
            final double distanceFromVantagePointToQueryPoint =
                    this.distanceFunction.getDistance(this.vantagePoint, queryPoint);

            // We want to search any of this node's children that intersect with the query region
            if (distanceFromVantagePointToQueryPoint <= this.threshold + maxDistance) {
                this.closer.collectAllWithinDistance(queryPoint, maxDistance, collection, filter);
            }

            if (distanceFromVantagePointToQueryPoint + maxDistance > this.threshold) {
                this.farther.collectAllWithinDistance(queryPoint, maxDistance, collection, filter);
            }
        } else {
            for (final E point : this.points) {
                if (this.distanceFunction.getDistance(queryPoint, point) <= maxDistance) {
                    if (filter.allowPoint(point)) {
                        collection.add(point);
                    }
                }
            }
        }
    }

    /**
     * Returns the child node (either the closer node or farther node) that would contain the given point given its
     * distance from this node's vantage point.
     *
     * @param point the point for which to choose an appropriate child node; the point need not actually exist within
     * either child node
     *
     * @return this node's "closer" child node if the given point is within this node's distance threshold of the
     * vantage point or the "farther" node otherwise
     */
    private VPTreeNode<P, E> getChildNodeForPoint(final P point) {
        return this.distanceFunction.getDistance(this.vantagePoint, point) <= this.threshold ? this.closer : this.farther;
    }

    /**
     * Adds all points contained by this node and its children to the given collection.
     *
     * @param collection the collection to which points should be added.
     */
    private void addAllPointsToCollection(final Collection<E> collection) {
        if (this.points == null) {
            this.closer.addAllPointsToCollection(collection);
            this.farther.addAllPointsToCollection(collection);
        } else {
            collection.addAll(this.points);
        }
    }

    /**
     * Adds all points contained by this node and its children to the given array.
     *
     * @param array the array to which points should be added
     * @param offset the starting index at which to add points to the array
     *
     * @return the number of points added to the array
     */
    public int addPointsToArray(final Object[] array, final int offset) {
        final int pointsAdded;

        if (this.points == null) {
            final int pointsAddedFromCloserNode = this.closer.addPointsToArray(array, offset);
            final int pointsAddedFromFartherNode = this.farther.addPointsToArray(array, offset + pointsAddedFromCloserNode);

            pointsAdded = pointsAddedFromCloserNode + pointsAddedFromFartherNode;
        } else {
            System.arraycopy(this.points.toArray(), 0, array, offset, this.points.size());
            pointsAdded = this.points.size();
        }

        return pointsAdded;
    }

    /**
     * Recursively gathers iterators that span the points contained in this node and its children into the given
     * collection.
     *
     * @param collection the collection to which iterators should be added
     */
    public void collectIterators(final Collection<Iterator<E>> collection) {
        if (this.points == null) {
            this.closer.collectIterators(collection);
            this.farther.collectIterators(collection);
        } else {
            collection.add(this.points.iterator());
        }
    }

    /**
     * Partitions the points in the given list such that all points that fall within the given distance threshold of the
     * given vantage point are on one "side" of the list and all points beyond the threshold are on the other.
     *
     * @param points the list of points to partition
     * @param vantagePoint the point from which to measure distances
     * @param threshold the distance threshold to be used for partitioning
     * @param distanceFunction the function to use to calculate distances from the vantage point
     * @return the index of the first point in the list that falls beyond the distance threshold
     *
     * @throws PartitionException if the list of points could not be partitioned (i.e. because they are all the same
     * distance from the vantage point).
     */
    private static <E> int partitionPoints(final List<E> points, final E vantagePoint, final double threshold, final DistanceFunction<? super E> distanceFunction) throws PartitionException {
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
