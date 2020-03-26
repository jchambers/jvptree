package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class VPTreeTest {

    private static final int TEST_TREE_SIZE = 256;

    @Test
    public void testGetNearestNeighbors() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int numberOfNeighbors = 3;

        final List<Integer> nearestNeighbors = vpTree.getNearestNeighbors(queryPoint, numberOfNeighbors);

        Assert.assertEquals(numberOfNeighbors, nearestNeighbors.size());
        Assert.assertEquals(queryPoint, nearestNeighbors.get(0));
        Assert.assertTrue(nearestNeighbors.containsAll(
                java.util.Arrays.asList(queryPoint - 1, queryPoint, queryPoint + 1)));
    }

    @Test
    public void testGetNearestNeighborsWithFilter() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int numberOfNeighbors = 3;

        final PointFilter<Integer> evenNumberFilter = new PointFilter<Integer>() {
            @Override
            public boolean allowPoint(final Integer point) {
                return point % 2 == 0;
            }
        };

        final List<Integer> nearestNeighbors = vpTree.getNearestNeighbors(queryPoint, numberOfNeighbors, evenNumberFilter);

        Assert.assertEquals(numberOfNeighbors, nearestNeighbors.size());
        Assert.assertEquals(queryPoint, nearestNeighbors.get(0));
        Assert.assertTrue(nearestNeighbors.containsAll(
                java.util.Arrays.asList(queryPoint - 2, queryPoint, queryPoint + 2)));
    }

    @Test
    public void testGetAllWithinRange() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int maxDistance = TEST_TREE_SIZE / 8;

        final PointFilter<Integer> evenNumberFilter = new PointFilter<Integer>() {
            @Override
            public boolean allowPoint(final Integer point) {
                return point % 2 == 0;
            }
        };

        final List<Integer> pointsWithinRange = vpTree.getAllWithinDistance(queryPoint, maxDistance, evenNumberFilter);

        Assert.assertEquals(maxDistance + 1, pointsWithinRange.size());

        for (int i = queryPoint - maxDistance; i <= queryPoint + maxDistance; i += 2) {
            Assert.assertTrue(pointsWithinRange.contains(i));
        }
    }

    @Test
    public void testGetAllWithinRangeWithFilter() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int maxDistance = TEST_TREE_SIZE / 8;

        final List<Integer> pointsWithinRange = vpTree.getAllWithinDistance(queryPoint, maxDistance);

        Assert.assertEquals((2 * maxDistance) + 1, pointsWithinRange.size());

        for (int i = queryPoint - maxDistance; i <= queryPoint + maxDistance; i++) {
            Assert.assertTrue(pointsWithinRange.contains(i));
        }
    }

    @Test
    public void testSize() {
        final ArrayList<Integer> points = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i++) {
            points.add(i);
        }

        {
            final VPTree<Number, Integer> initiallyEmptyTree = new VPTree<>(new IntegerDistanceFunction());
            assertEquals(0, initiallyEmptyTree.size());

            initiallyEmptyTree.addAll(points);

            assertEquals(points.size(), initiallyEmptyTree.size());

            initiallyEmptyTree.removeAll(points);

            assertEquals(0, initiallyEmptyTree.size());
        }

        {
            final VPTree<Number, Integer> initiallyPopulatedTree = new VPTree<>(new IntegerDistanceFunction(), points);
            assertEquals(points.size(), initiallyPopulatedTree.size());
        }
    }

    @Test
    public void testIsEmpty() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;

        Assert.assertTrue(vpTree.isEmpty());

        vpTree.add(testPoint);
        Assert.assertFalse(vpTree.isEmpty());

        vpTree.remove(testPoint);
        Assert.assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testAdd() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;

        Assert.assertFalse(vpTree.contains(testPoint));

        Assert.assertTrue(vpTree.add(testPoint));
        Assert.assertTrue(vpTree.contains(testPoint));
    }

    @Test
    public void testAddAll() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);

        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        Assert.assertTrue(vpTree.addAll(points));
        assertEquals(points.size(), vpTree.size());
        Assert.assertTrue(vpTree.containsAll(points));
    }

    @Test
    public void testRemove() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;

        Assert.assertFalse(vpTree.remove(testPoint));

        vpTree.add(testPoint);
        Assert.assertTrue(vpTree.remove(testPoint));
        Assert.assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testRemoveAll() {
        final ArrayList<Integer> pointsToRemove = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i += 2) {
            pointsToRemove.add(i);
        }

        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        Assert.assertTrue(vpTree.removeAll(pointsToRemove));
        assertEquals(TEST_TREE_SIZE - pointsToRemove.size(), vpTree.size());

        for (final Integer point : pointsToRemove) {
            Assert.assertFalse(vpTree.contains(point));
        }

        Assert.assertFalse(vpTree.removeAll(pointsToRemove));
    }

    @Test
    public void testRetainAll() {
        final ArrayList<Integer> pointsToRetain = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i += 2) {
            pointsToRetain.add(i);
        }

        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        Assert.assertTrue(vpTree.retainAll(pointsToRetain));
        assertEquals(pointsToRetain.size(), vpTree.size());

        for (final Integer point : pointsToRetain) {
            Assert.assertTrue(vpTree.contains(point));
        }

        Assert.assertFalse(vpTree.retainAll(pointsToRetain));
    }

    @Test
    public void testClear() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        Assert.assertFalse(vpTree.isEmpty());

        vpTree.clear();
        Assert.assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testContains() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);

        final Integer pointAdded = 12;
        final Integer pointNotAdded = 7;

        Assert.assertFalse(vpTree.contains(pointAdded));

        vpTree.add(pointAdded);
        Assert.assertTrue(vpTree.contains(pointAdded));
        Assert.assertFalse(vpTree.contains(pointNotAdded));

        vpTree.remove(pointAdded);
        Assert.assertFalse(vpTree.contains(pointAdded));
    }

    @Test
    public void testContainsAll() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Number, Integer> vpTree = new VPTree<>(new IntegerDistanceFunction(), points);

        Assert.assertTrue(vpTree.containsAll(points));

        points.add(numberOfPoints + 1);
        Assert.assertFalse(vpTree.containsAll(points));
    }

    @Test
    public void testIterator() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Number, Integer> vpTree = new VPTree<>(new IntegerDistanceFunction(), points);

        final ArrayList<Integer> pointsFromIterator = new ArrayList<>();
        final Iterator<Integer> iterator = vpTree.iterator();

        while (iterator.hasNext()) {
            pointsFromIterator.add(iterator.next());
        }

        Assert.assertEquals(points.size(), pointsFromIterator.size());
        Assert.assertTrue(pointsFromIterator.containsAll(points));
    }

    @Test
    public void testToArray() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Number, Integer> vpTree = new VPTree<>(new IntegerDistanceFunction(), points);
        final Object[] array = vpTree.toArray();

        assertEquals(vpTree.size(), array.length);

        for (final Object point : array) {
            Assert.assertTrue(vpTree.contains(point));
        }
    }

    @Test
    public void testToArrayTArray() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        {
            final Integer[] array = vpTree.toArray(new Integer[0]);

            assertEquals(vpTree.size(), array.length);

            for (final Integer point : array) {
                Assert.assertTrue(vpTree.contains(point));
            }
        }

        {
            final Integer[] array = vpTree.toArray(new Integer[0]);

            assertEquals(vpTree.size(), array.length);

            for (final Integer point : array) {
                Assert.assertTrue(vpTree.contains(point));
            }
        }

        {
            final Integer[] array = vpTree.toArray(new Integer[vpTree.size() + 1]);

            assertEquals(vpTree.size() + 1, array.length);

            for (int i = 0; i < vpTree.size(); i++) {
                Assert.assertTrue(vpTree.contains(array[i]));
            }

            Assert.assertNull(array[vpTree.size()]);
        }
    }

    private VPTree<Number, Integer> createTestTree(final int numberOfPoints) {
        final List<Integer> points;

        if (numberOfPoints == 0) {
            points = null;
        } else {
            points = new ArrayList<>(numberOfPoints);

            for (int i = 0; i < numberOfPoints; i++) {
                points.add(i);
            }
        }

        return new VPTree<>(new IntegerDistanceFunction(), points);
    }
}
