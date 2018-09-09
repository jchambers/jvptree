package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class VPTreeTest {

    private static final int TEST_TREE_SIZE = 256;

    @Test
    public void testGetNearestNeighbors() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int numberOfNeighbors = 3;

        final List<Integer> nearestNeighbors = vpTree.getNearestNeighbors(queryPoint, numberOfNeighbors);

        assertEquals(numberOfNeighbors, nearestNeighbors.size());
        assertEquals(queryPoint, nearestNeighbors.get(0));
        assertTrue(nearestNeighbors.containsAll(
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

        assertEquals(numberOfNeighbors, nearestNeighbors.size());
        assertEquals(queryPoint, nearestNeighbors.get(0));
        assertTrue(nearestNeighbors.containsAll(
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

        assertEquals(maxDistance + 1, pointsWithinRange.size());

        for (int i = queryPoint - maxDistance; i <= queryPoint + maxDistance; i += 2) {
            assertTrue(pointsWithinRange.contains(i));
        }
    }

    @Test
    public void testGetAllWithinRangeWithFilter() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int maxDistance = TEST_TREE_SIZE / 8;

        final List<Integer> pointsWithinRange = vpTree.getAllWithinDistance(queryPoint, maxDistance);

        assertEquals((2 * maxDistance) + 1, pointsWithinRange.size());

        for (int i = queryPoint - maxDistance; i <= queryPoint + maxDistance; i++) {
            assertTrue(pointsWithinRange.contains(i));
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

        assertTrue(vpTree.isEmpty());

        vpTree.add(testPoint);
        assertFalse(vpTree.isEmpty());

        vpTree.remove(testPoint);
        assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testAdd() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;

        assertFalse(vpTree.contains(testPoint));

        assertTrue(vpTree.add(testPoint));
        assertTrue(vpTree.contains(testPoint));
    }

    @Test
    public void testAddAll() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);

        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        assertTrue(vpTree.addAll(points));
        assertEquals(points.size(), vpTree.size());
        assertTrue(vpTree.containsAll(points));
    }

    @Test
    public void testRemove() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;

        assertFalse(vpTree.remove(testPoint));

        vpTree.add(testPoint);
        assertTrue(vpTree.remove(testPoint));
        assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testRemoveAll() {
        final ArrayList<Integer> pointsToRemove = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i += 2) {
            pointsToRemove.add(i);
        }

        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        assertTrue(vpTree.removeAll(pointsToRemove));
        assertEquals(TEST_TREE_SIZE - pointsToRemove.size(), vpTree.size());

        for (final Integer point : pointsToRemove) {
            assertFalse(vpTree.contains(point));
        }

        assertFalse(vpTree.removeAll(pointsToRemove));
    }

    @Test
    public void testRetainAll() {
        final ArrayList<Integer> pointsToRetain = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i += 2) {
            pointsToRetain.add(i);
        }

        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        assertTrue(vpTree.retainAll(pointsToRetain));
        assertEquals(pointsToRetain.size(), vpTree.size());

        for (final Integer point : pointsToRetain) {
            assertTrue(vpTree.contains(point));
        }

        assertFalse(vpTree.retainAll(pointsToRetain));
    }

    @Test
    public void testClear() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        assertFalse(vpTree.isEmpty());

        vpTree.clear();
        assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testContains() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);

        final Integer pointAdded = 12;
        final Integer pointNotAdded = 7;

        assertFalse(vpTree.contains(pointAdded));

        vpTree.add(pointAdded);
        assertTrue(vpTree.contains(pointAdded));
        assertFalse(vpTree.contains(pointNotAdded));

        vpTree.remove(pointAdded);
        assertFalse(vpTree.contains(pointAdded));
    }

    @Test
    public void testContainsAll() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Number, Integer> vpTree = new VPTree<>(new IntegerDistanceFunction(), points);

        assertTrue(vpTree.containsAll(points));

        points.add(numberOfPoints + 1);
        assertFalse(vpTree.containsAll(points));
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

        assertEquals(points.size(), pointsFromIterator.size());
        assertTrue(pointsFromIterator.containsAll(points));
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
            assertTrue(vpTree.contains(point));
        }
    }

    @Test
    public void testToArrayTArray() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        {
            final Integer[] array = vpTree.toArray(new Integer[0]);

            assertEquals(vpTree.size(), array.length);

            for (final Integer point : array) {
                assertTrue(vpTree.contains(point));
            }
        }

        {
            final Integer[] array = vpTree.toArray(new Integer[0]);

            assertEquals(vpTree.size(), array.length);

            for (final Integer point : array) {
                assertTrue(vpTree.contains(point));
            }
        }

        {
            final Integer[] array = vpTree.toArray(new Integer[vpTree.size() + 1]);

            assertEquals(vpTree.size() + 1, array.length);

            for (int i = 0; i < vpTree.size(); i++) {
                assertTrue(vpTree.contains(array[i]));
            }

            assertNull(array[vpTree.size()]);
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
