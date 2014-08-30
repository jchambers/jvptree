package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.eatthepath.jvptree.util.IntegerDistanceFunction;
import com.eatthepath.jvptree.util.MedianDistanceThresholdSelectionStrategy;

public class VPNodeTest {

    private static final int TEST_NODE_SIZE = 32;

    @Test(expected = IllegalArgumentException.class)
    public void testVPNodeNoPoints() {
        new VPNode<Integer>(new ArrayList<Integer>(), new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Integer>(), VPNode.DEFAULT_NODE_CAPACITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVPNodeZeroCapacity() {
        new VPNode<Integer>(java.util.Collections.singletonList(7), new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Integer>(), 0);
    }

    @Test
    public void testSize() {
        for (final VPNode<Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            assertEquals(TEST_NODE_SIZE, testNode.size());
        }
    }

    @Test
    public void testAdd() {
        final Integer testPoint = TEST_NODE_SIZE * 2;

        for (final VPNode<Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
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

        for (final VPNode<Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
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

        for (final VPNode<Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            for (int i = 0; i < TEST_NODE_SIZE; i++) {
                assertTrue(testNode.contains(i));
            }

            assertFalse(testNode.contains(pointNotInNode));
        }
    }

    @Test
    public void testRetainAll() {
        final ArrayList<Integer> pointsToRetain = new ArrayList<Integer>();

        for (int i = 0; i < TEST_NODE_SIZE / 8; i++) {
            pointsToRetain.add(i);
        }

        for (final VPNode<Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            assertTrue(testNode.retainAll(pointsToRetain));
            assertEquals(pointsToRetain.size(), testNode.size());

            for (int point : pointsToRetain) {
                assertTrue(testNode.contains(point));
            }

            assertFalse(testNode.retainAll(pointsToRetain));
        }
    }

    @Test
    public void testCollectNearestNeighbors() {
        fail("Not yet implemented");
    }

    @Test
    public void testCollectAllWithinRange() {
        final Integer queryPoint = TEST_NODE_SIZE / 2;
        final Integer maxRange = TEST_NODE_SIZE / 8;

        for (final VPNode<Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final ArrayList<Integer> collectedPoints = new ArrayList<Integer>();

            testNode.collectAllWithinRange(queryPoint, maxRange, collectedPoints);

            assertEquals((2 * maxRange) + 1, collectedPoints.size());

            for (int i = queryPoint - maxRange; i <= queryPoint + maxRange; i++) {
                assertTrue(collectedPoints.contains(i));
            }
        }
    }

    @Test
    public void testAddPointsToArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testCollectIterators() {
        fail("Not yet implemented");
    }

    private Collection<VPNode<Integer>> createTestNodes(final int nodeSize) {
        final ArrayList<Integer> points = new ArrayList<Integer>(nodeSize);

        for (int i = 0; i < nodeSize; i++) {
            points.add(i);
        }

        final ArrayList<VPNode<Integer>> testNodes = new ArrayList<VPNode<Integer>>(3);

        testNodes.add(new VPNode<Integer>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Integer>(), points.size() * 2));

        testNodes.add(new VPNode<Integer>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Integer>(), points.size()));

        testNodes.add(new VPNode<Integer>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Integer>(), points.size() / 8));

        return testNodes;
    }
}
