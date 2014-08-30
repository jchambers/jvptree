package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.eatthepath.jvptree.util.IntegerDistanceFunction;
import com.eatthepath.jvptree.util.MedianDistanceThresholdSelectionStrategy;

public class VPNodeTest {

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
        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 16; i++) {
                points.add(i);
            }

            final VPNode<Integer> unpartitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            assertEquals(points.size(), unpartitionedNode.size());
        }

        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 256; i++) {
                points.add(i);
            }

            final VPNode<Integer> partitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            assertEquals(points.size(), partitionedNode.size());
        }
    }

    @Test
    public void testAdd() {
        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 16; i++) {
                points.add(i);
            }

            final VPNode<Integer> unpartitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            assertEquals(points.size(), unpartitionedNode.size());

            unpartitionedNode.add(7000);

            assertEquals(points.size() + 1, unpartitionedNode.size());
            assertTrue(unpartitionedNode.contains(7000));
        }

        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 16; i++) {
                points.add(i);
            }

            final VPNode<Integer> atCapacityNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), points.size());

            assertEquals(points.size(), atCapacityNode.size());

            atCapacityNode.add(7000);

            assertEquals(points.size() + 1, atCapacityNode.size());
            assertTrue(atCapacityNode.contains(7000));
        }

        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 256; i++) {
                points.add(i);
            }

            final VPNode<Integer> partitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            assertEquals(points.size(), partitionedNode.size());

            partitionedNode.add(7000);

            assertEquals(points.size() + 1, partitionedNode.size());
            assertTrue(partitionedNode.contains(7000));
        }
    }

    @Test
    public void testRemove() {
        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 16; i++) {
                points.add(i);
            }

            final VPNode<Integer> unpartitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            assertEquals(points.size(), unpartitionedNode.size());

            assertFalse(unpartitionedNode.remove(7000));
            assertTrue(unpartitionedNode.remove(8));

            assertEquals(points.size() - 1, unpartitionedNode.size());
            assertFalse(unpartitionedNode.contains(8));

            for (int i = 0; i < 16; i++) {
                unpartitionedNode.remove(i);
            }

            assertEquals(0, unpartitionedNode.size());
        }

        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 256; i++) {
                points.add(i);
            }

            final VPNode<Integer> partitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            assertEquals(points.size(), partitionedNode.size());

            assertFalse(partitionedNode.remove(7000));
            assertTrue(partitionedNode.remove(8));

            assertEquals(points.size() - 1, partitionedNode.size());
            assertFalse(partitionedNode.contains(8));

            for (int i = 0; i < 256; i++) {
                partitionedNode.remove(i);
            }

            assertEquals(0, partitionedNode.size());
        }
    }

    @Test
    public void testContains() {
        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 16; i++) {
                points.add(i);
            }

            final VPNode<Integer> unpartitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            for (int i = 0; i < 16; i++) {
                assertTrue(unpartitionedNode.contains(i));
            }

            assertFalse(unpartitionedNode.contains(7000));
        }

        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 256; i++) {
                points.add(i);
            }

            final VPNode<Integer> partitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            for (int i = 0; i < 256; i++) {
                assertTrue(partitionedNode.contains(i));
            }

            assertFalse(partitionedNode.contains(7000));
        }
    }

    @Test
    public void testRetainAll() {
        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 16; i++) {
                points.add(i);
            }

            final VPNode<Integer> unpartitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            final ArrayList<Integer> pointsToRetain = new ArrayList<Integer>();

            for (int i = 0; i < 4; i++) {
                pointsToRetain.add(i);
            }

            assertTrue(unpartitionedNode.retainAll(pointsToRetain));

            assertEquals(4, unpartitionedNode.size());

            for (int point : pointsToRetain) {
                assertTrue(unpartitionedNode.contains(point));
            }

            assertFalse(unpartitionedNode.retainAll(pointsToRetain));
        }

        {
            final ArrayList<Integer> points = new ArrayList<Integer>();

            for (int i = 0; i < 256; i++) {
                points.add(i);
            }

            final VPNode<Integer> partitionedNode = new VPNode<Integer>(points, new IntegerDistanceFunction(),
                    new MedianDistanceThresholdSelectionStrategy<Integer>(), 32);

            assertEquals(points.size(), partitionedNode.size());

            final ArrayList<Integer> pointsToRetain = new ArrayList<Integer>();

            for (int i = 0; i < 4; i++) {
                pointsToRetain.add(i);
            }

            assertTrue(partitionedNode.retainAll(pointsToRetain));

            assertEquals(4, partitionedNode.size());

            for (int point : pointsToRetain) {
                assertTrue(partitionedNode.contains(point));
            }

            assertFalse(partitionedNode.retainAll(pointsToRetain));
        }
    }

    @Test
    public void testCollectNearestNeighbors() {
        fail("Not yet implemented");
    }

    @Test
    public void testCollectAllWithinRange() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddPointsToArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testCollectIterators() {
        fail("Not yet implemented");
    }
}
