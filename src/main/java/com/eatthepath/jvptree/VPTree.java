package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.eatthepath.jvptree.util.SamplingMedianDistanceThresholdSelectionStrategy;

public class VPTree<E> implements Collection<E> {

    private final DistanceFunction<E> distanceFunction;
    private final ThresholdSelectionStrategy<E> thresholdSelectionStrategy;
    private final int nodeCapacity;

    private VPNode<E> rootNode;

    public VPTree(final DistanceFunction<E> distanceFunction) {
        this(distanceFunction,
                new SamplingMedianDistanceThresholdSelectionStrategy<E>(
                        SamplingMedianDistanceThresholdSelectionStrategy.DEFAULT_NUMBER_OF_SAMPLES));
    }

    public VPTree(final DistanceFunction<E> distanceFunction, final ThresholdSelectionStrategy<E> thresholdSelectionStrategy) {
        this(distanceFunction, thresholdSelectionStrategy, VPNode.DEFAULT_NODE_CAPACITY);
    }

    public VPTree(final DistanceFunction<E> distanceFunction, final ThresholdSelectionStrategy<E> thresholdSelectionStrategy, final int nodeCapacity) {
        this(distanceFunction, thresholdSelectionStrategy, nodeCapacity, null);
    }

    public VPTree(final DistanceFunction<E> distanceFunction, final ThresholdSelectionStrategy<E> thresholdSelectionStrategy, final int nodeCapacity, final Collection<E> points) {
        this.distanceFunction = distanceFunction;
        this.thresholdSelectionStrategy = thresholdSelectionStrategy;
        this.nodeCapacity = nodeCapacity;

        if (points != null) {
            this.rootNode = new VPNode<E>(
                    new ArrayList<E>(points),
                    this.distanceFunction,
                    this.thresholdSelectionStrategy,
                    this.nodeCapacity);
        }
    }

    public List<E> getNearestNeighbors(final E queryPoint, final int maxResults) {
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

    public List<E> getAllWithinRange(final E queryPoint, final double maxDistance) {
        final List<E> pointsWithinRange;

        if (this.rootNode == null) {
            pointsWithinRange = null;
        } else {
            pointsWithinRange = new ArrayList<E>();
            this.rootNode.collectAllWithinRange(queryPoint, maxDistance, pointsWithinRange);
        }

        return pointsWithinRange;
    }

    public int size() {
        return this.rootNode == null ? 0 : this.rootNode.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(final Object o) {
        try {
            return this.rootNode == null ? false : this.rootNode.contains((E) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public Iterator<E> iterator() {
        final ArrayList<Iterator<E>> iterators = new ArrayList<Iterator<E>>();

        if (this.rootNode != null) {
            this.rootNode.collectIterators(iterators);
        }

        return new MetaIterator<E>(iterators);
    }

    public Object[] toArray() {
        final Object[] array = new Object[this.size()];

        if (this.rootNode != null) {
            this.rootNode.addPointsToArray(array, 0);
        }

        return array;
    }

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

    public boolean add(final E point) {
        if (this.rootNode == null) {
            this.rootNode = new VPNode<E>(
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

    @SuppressWarnings("unchecked")
    public boolean remove(final Object point) {
        try {
            return this.rootNode == null ? false : this.rootNode.remove((E) point);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean containsAll(final Collection<?> points) {
        for (final Object point : points) {
            if (!this.contains(point)) { return false; }
        }

        return true;
    }

    public boolean addAll(final Collection<? extends E> points) {
        for (final E point : points) {
            this.add(point);
        }

        // Adding points always modifies a VPTree
        return !points.isEmpty();
    }

    public boolean removeAll(final Collection<?> points) {
        boolean pointRemoved = false;

        for (final Object point : points) {
            pointRemoved = pointRemoved || this.remove(point);
        }

        return pointRemoved;
    }

    public boolean retainAll(final Collection<?> points) {
        return this.rootNode == null ? false : this.rootNode.retainAll(points);
    }

    public void clear() {
        this.rootNode = null;
    }
}
