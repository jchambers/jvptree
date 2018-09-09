package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import com.eatthepath.jvptree.util.MedianDistanceThresholdSelectionStrategy;

public class VPTreeNodeTest {

    private static final int TEST_NODE_SIZE = 32;

    private static final PointFilter<Object> NO_OP_POINT_FILTER = new PointFilter<Object>() {

        @Override
        public boolean allowPoint(final Object point) {
            return true;
        }
    };

    @Test(expected = IllegalArgumentException.class)
    public void testVPNodeNoPoints() {
        new VPTreeNode<>(new ArrayList<Integer>(), new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), VPTree.DEFAULT_NODE_CAPACITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVPNodeZeroCapacity() {
        new VPTreeNode<>(java.util.Collections.singletonList(7), new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), 0);
    }

    @Test
    public void testSize() {
        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            assertEquals(TEST_NODE_SIZE, testNode.size());
        }
    }

    @Test
    public void testAdd() {
        final Integer testPoint = TEST_NODE_SIZE * 2;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            assertFalse(testNode.contains(testPoint));

            testNode.add(testPoint);

            assertEquals(TEST_NODE_SIZE + 1, testNode.size());
            assertTrue(testNode.contains(testPoint));
        }
    }

    @Test
    public void testRemove() {
        final Integer pointNotInNode = TEST_NODE_SIZE * 2;
        final Integer pointInNode = TEST_NODE_SIZE / 2;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            assertFalse(testNode.remove(pointNotInNode));
            assertTrue(testNode.remove(pointInNode));

            assertEquals(TEST_NODE_SIZE - 1, testNode.size());
            assertFalse(testNode.contains(pointInNode));

            for (int i = 0; i < TEST_NODE_SIZE; i++) {
                testNode.remove(i);
            }

            assertEquals(0, testNode.size());
        }
    }

    @Test
    public void testContains() {
        final Integer pointNotInNode = TEST_NODE_SIZE * 2;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            for (int i = 0; i < TEST_NODE_SIZE; i++) {
                assertTrue(testNode.contains(i));
            }

            assertFalse(testNode.contains(pointNotInNode));
        }
    }

    @Test
    public void testRetainAll() {
        final ArrayList<Integer> pointsToRetain = new ArrayList<>();

        for (int i = 0; i < TEST_NODE_SIZE / 8; i++) {
            pointsToRetain.add(i);
        }

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            assertTrue(testNode.retainAll(pointsToRetain));
            assertEquals(pointsToRetain.size(), testNode.size());

            for (final int point : pointsToRetain) {
                assertTrue(testNode.contains(point));
            }

            assertFalse(testNode.retainAll(pointsToRetain));
        }
    }

    @Test
    public void testCollectNearestNeighbors() {
        final Integer queryPoint = TEST_NODE_SIZE / 2;
        final int numberOfNeighbors = 3;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final NearestNeighborCollector<Number, Integer> collector =
                    new NearestNeighborCollector<>(queryPoint, new IntegerDistanceFunction(), numberOfNeighbors);

            testNode.collectNearestNeighbors(collector, NO_OP_POINT_FILTER);

            assertEquals(numberOfNeighbors, collector.toSortedList().size());
            assertEquals(queryPoint, collector.toSortedList().get(0));
            assertTrue(collector.toSortedList().containsAll(
                    java.util.Arrays.asList(queryPoint - 1, queryPoint, queryPoint + 1)));
        }
    }

    @Test
    public void testCollectAllWithinRange() {
        final Integer queryPoint = TEST_NODE_SIZE / 2;
        final int maxRange = TEST_NODE_SIZE / 8;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final ArrayList<Integer> collectedPoints = new ArrayList<>();

            testNode.collectAllWithinDistance(queryPoint, maxRange, collectedPoints, NO_OP_POINT_FILTER);

            assertEquals((2 * maxRange) + 1, collectedPoints.size());

            for (int i = queryPoint - maxRange; i <= queryPoint + maxRange; i++) {
                assertTrue(collectedPoints.contains(i));
            }
        }
    }

    @Test
    public void testAddPointsToArray() {
        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final Integer[] array = new Integer[TEST_NODE_SIZE];
            testNode.addPointsToArray(array, 0);

            assertFalse(testNode.retainAll(java.util.Arrays.asList(array)));
        }
    }

    @Test
    public void testCollectIterators() {
        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final ArrayList<Iterator<Integer>> iterators = new ArrayList<>();
            testNode.collectIterators(iterators);

            final ArrayList<Integer> pointsFromIterators = new ArrayList<>();

            for (final Iterator<Integer> iterator : iterators) {
                while (iterator.hasNext()) {
                    pointsFromIterators.add(iterator.next());
                }
            }

            assertEquals(testNode.size(), pointsFromIterators.size());
            assertFalse(testNode.retainAll(pointsFromIterators));
        }
    }

    private Collection<VPTreeNode<Number, Integer>> createTestNodes(final int nodeSize) {
        final ArrayList<Integer> points = new ArrayList<>(nodeSize);

        for (int i = 0; i < nodeSize; i++) {
            points.add(i);
        }

        final ArrayList<VPTreeNode<Number, Integer>> testNodes = new ArrayList<>(3);

        testNodes.add(new VPTreeNode<>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), points.size() * 2));

        testNodes.add(new VPTreeNode<>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), points.size()));

        testNodes.add(new VPTreeNode<>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), points.size() / 8));

        return testNodes;
    }
}
