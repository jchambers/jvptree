package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.eatthepath.jvptree.util.SamplingMedianDistanceThresholdSelectionStrategy;

/**
 * <p>A vantage-point tree (or vp-tree) is a binary space partitioning collection of points in a metric space. The main
 * feature of vantage point trees is that they allow for k-nearest-neighbor searches in any metric space in
 * <em>O(log(n))</em> time.</p>
 *
 * <p>Vantage point trees recursively partition points by choosing a &quot;vantage point&quot; and a distance threshold;
 * points are then partitioned into one collection that contains all of the points closer to the vantage point than the
 * chosen threshold and one collection that contains all of the points farther away than the chosen threshold.</p>
 *
 * <p>A {@linkplain DistanceFunction distance function} that satisfies the properties of a metric space must be provided
 * when constructing a vantage point tree. Callers may also specify a threshold selection strategy (a sampling median
 * strategy is used by default) and a node size to tune the ratio of nodes searched to points inspected per node.
 * Vantage point trees may be constructed with or without an initial collection of points, though specifying a
 * collection of points at construction time is the most efficient approach.</p>
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 *
 * @param <E>
 */
public class VPTree<E> implements SpatialIndex<E> {

    private final DistanceFunction<? super E> distanceFunction;
    private final ThresholdSelectionStrategy<E> thresholdSelectionStrategy;
    private final int nodeCapacity;

    private VPTreeNode<E> rootNode;

    public static final int DEFAULT_NODE_CAPACITY = 32;

    /**
     * Constructs a new vp-tree that uses the given distance function and is initially empty. The constructed tree will
     * use a default {@link SamplingMedianDistanceThresholdSelectionStrategy} and node capacity
     * ({@value VPTree#DEFAULT_NODE_CAPACITY} points).
     *
     * @param distanceFunction the distance function to use to calculate the distance between points
     */
    public VPTree(final DistanceFunction<? super E> distanceFunction) {
        this(distanceFunction, (Collection<E>) null);
    }

    /**
     * Constructs a new vp-tree that uses the given distance function and is initially populated with the given
     * collection of points. The constructed tree will use a default
     * {@link SamplingMedianDistanceThresholdSelectionStrategy} and node capacity ({@value VPTree#DEFAULT_NODE_CAPACITY}
     * points).
     *
     * @param distanceFunction the distance function to use to calculate the distance between points
     * @param points the points with which this tree should be initially populated; may be {@code null}
     */
    public VPTree(final DistanceFunction<? super E> distanceFunction, final Collection<E> points) {
        this(distanceFunction, new SamplingMedianDistanceThresholdSelectionStrategy<E>(
                SamplingMedianDistanceThresholdSelectionStrategy.DEFAULT_NUMBER_OF_SAMPLES),
                VPTree.DEFAULT_NODE_CAPACITY, points);
    }

    /**
     * Constructs a new vp-tree that uses the given distance function and threshold selection strategy to partition
     * points. The tree will be initially empty and will have a default node capacity
     * ({@value VPTree#DEFAULT_NODE_CAPACITY} points).
     *
     * @param distanceFunction the distance function to use to calculate the distance between points
     * @param thresholdSelectionStrategy the function to use to choose distance thresholds when partitioning nodes
     */
    public VPTree(final DistanceFunction<? super E> distanceFunction, final ThresholdSelectionStrategy<E> thresholdSelectionStrategy) {
        this(distanceFunction, thresholdSelectionStrategy, VPTree.DEFAULT_NODE_CAPACITY, null);
    }

    /**
     * Constructs a new vp-tree that uses the given distance function and threshold selection strategy to partition
     * points. The tree will be initially populated with the given collection of points and will have a default node
     * capacity ({@value VPTree#DEFAULT_NODE_CAPACITY} points).
     *
     * @param distanceFunction the distance function to use to calculate the distance between points
     * @param thresholdSelectionStrategy the function to use to choose distance thresholds when partitioning nodes
     * @param nodeCapacity the largest capacity a node may have before it should be partitioned
     */
    public VPTree(final DistanceFunction<? super E> distanceFunction, final ThresholdSelectionStrategy<E> thresholdSelectionStrategy, final Collection<E> points) {
        this(distanceFunction, thresholdSelectionStrategy, VPTree.DEFAULT_NODE_CAPACITY, points);
    }

    /**
     * Constructs a new vp-tree that uses the given distance function and threshold selection strategy to partition
     * points and is initially empty. The tree will attempt to partition nodes that contain more than
     * {@code nodeCapacity} points, and will be initially populated with the given collection of points.
     *
     * @param distanceFunction the distance function to use to calculate the distance between points
     * @param thresholdSelectionStrategy the function to use to choose distance thresholds when partitioning nodes
     * @param nodeCapacity the largest capacity a node may have before it should be partitioned
     */
    public VPTree(final DistanceFunction<? super E> distanceFunction, final ThresholdSelectionStrategy<E> thresholdSelectionStrategy, final int nodeCapacity) {
        this(distanceFunction, thresholdSelectionStrategy, nodeCapacity, null);
    }

    /**
     * Constructs a new vp-tree that uses the given distance function and threshold selection strategy to partition
     * points. The tree will attempt to partition nodes that contain more than {@code nodeCapacity} points, and will
     * be initially populated with the given collection of points.
     *
     * @param distanceFunction the distance function to use to calculate the distance between points
     * @param thresholdSelectionStrategy the function to use to choose distance thresholds when partitioning nodes
     * @param nodeCapacity the largest capacity a node may have before it should be partitioned
     * @param points the points with which this tree should be initially populated; may be {@code null}
     */
    public VPTree(final DistanceFunction<? super E> distanceFunction, final ThresholdSelectionStrategy<E> thresholdSelectionStrategy, final int nodeCapacity, final Collection<E> points) {
        this.distanceFunction = distanceFunction;
        this.thresholdSelectionStrategy = thresholdSelectionStrategy;
        this.nodeCapacity = nodeCapacity;

        if (points != null && !points.isEmpty()) {
            this.rootNode = new VPTreeNode<E>(
                    new ArrayList<E>(points),
                    this.distanceFunction,
                    this.thresholdSelectionStrategy,
                    this.nodeCapacity);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eatthepath.jvptree.SpatialIndex#getNearestNeighbors(java.lang.Object, int)
     */
    public List<E> getNearestNeighbors(E queryPoint, int maxResults) {
        final List<E> nearestNeighbors;

        if (this.rootNode == null) {
            nearestNeighbors = null;
        } else {
            final NearestNeighborCollector<E> collector =
                    new NearestNeighborCollector<E>(queryPoint, this.distanceFunction, maxResults);

            this.rootNode.collectNearestNeighbors(collector);

            nearestNeighbors = collector.toSortedList();
        }

        return nearestNeighbors;
    }

    /*
     * (non-Javadoc)
     * @see com.eatthepath.jvptree.SpatialIndex#getAllWithinRange(java.lang.Object, double)
     */
    public List<E> getAllWithinDistance(final E queryPoint, final double maxDistance) {
        final List<E> pointsWithinRange;

        if (this.rootNode == null) {
            pointsWithinRange = null;
        } else {
            pointsWithinRange = new ArrayList<E>();
            this.rootNode.collectAllWithinDistance(queryPoint, maxDistance, pointsWithinRange);
        }

        return pointsWithinRange;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#size()
     */
    public int size() {
        return this.rootNode == null ? 0 : this.rootNode.size();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public boolean contains(final Object o) {
        try {
            return this.rootNode == null ? false : this.rootNode.contains((E) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#iterator()
     */
    public Iterator<E> iterator() {
        final ArrayList<Iterator<E>> iterators = new ArrayList<Iterator<E>>();

        if (this.rootNode != null) {
            this.rootNode.collectIterators(iterators);
        }

        return new MetaIterator<E>(iterators);
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        final Object[] array = new Object[this.size()];

        if (this.rootNode != null) {
            this.rootNode.addPointsToArray(array, 0);
        }

        return array;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] array) {
        final T[] arrayToPopulate;

        if (array.length < this.size()) {
            arrayToPopulate = (T[])java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), this.size());
        } else {
            arrayToPopulate = array;
        }

        if (this.rootNode != null) {
            this.rootNode.addPointsToArray(arrayToPopulate, 0);
        }

        return arrayToPopulate;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(final E point) {
        if (this.rootNode == null) {
            this.rootNode = new VPTreeNode<E>(
                    java.util.Collections.singletonList(point),
                    this.distanceFunction,
                    this.thresholdSelectionStrategy,
                    this.nodeCapacity);
        } else {
            this.rootNode.add(point);
        }

        // Adding a point always modifies a VPTree
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#remove(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public boolean remove(final Object point) {
        try {
            return this.rootNode == null ? false : this.rootNode.remove((E) point);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(final Collection<?> points) {
        for (final Object point : points) {
            if (!this.contains(point)) { return false; }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(final Collection<? extends E> points) {
        for (final E point : points) {
            this.add(point);
        }

        // Adding points always modifies a VPTree
        return !points.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(final Collection<?> points) {
        boolean pointRemoved = false;

        for (final Object point : points) {
            pointRemoved = this.remove(point) || pointRemoved;
        }

        return pointRemoved;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(final Collection<?> points) {
        return this.rootNode == null ? false : this.rootNode.retainAll(points);
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#clear()
     */
    public void clear() {
        this.rootNode = null;
    }
}
