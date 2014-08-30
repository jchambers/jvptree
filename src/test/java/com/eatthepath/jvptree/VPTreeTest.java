package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.eatthepath.jvptree.util.IntegerDistanceFunction;

public class VPTreeTest {

    @Test
    public void testGetNearestNeighbors() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        final Integer queryPoint = numberOfPoints / 2;
        final int numberOfNeighbors = 3;

        final List<Integer> nearestNeighbors = vpTree.getNearestNeighbors(queryPoint, numberOfNeighbors);

        assertEquals(numberOfNeighbors, nearestNeighbors.size());
        assertEquals(queryPoint, nearestNeighbors.get(0));
        assertTrue(nearestNeighbors.containsAll(
                java.util.Arrays.asList(new Integer[] { queryPoint - 1, queryPoint, queryPoint + 1 })));
    }

    @Test
    public void testGetAllWithinRange() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        final Integer queryPoint = numberOfPoints / 2;
        final int maxDistance = numberOfPoints / 8;

        final List<Integer> pointsWithinRange = vpTree.getAllWithinRange(queryPoint, maxDistance);

        assertEquals((2 * maxDistance) + 1, pointsWithinRange.size());

        for (int i = queryPoint - maxDistance; i <= queryPoint + maxDistance; i++) {
            assertTrue(pointsWithinRange.contains(i));
        }
    }

    @Test
    public void testSize() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        {
            final VPTree<Integer> initiallyEmptyTree = new VPTree<Integer>(new IntegerDistanceFunction());
            assertEquals(0, initiallyEmptyTree.size());

            initiallyEmptyTree.addAll(points);

            assertEquals(points.size(), initiallyEmptyTree.size());

            initiallyEmptyTree.removeAll(points);

            assertEquals(0, initiallyEmptyTree.size());
        }

        {
            final VPTree<Integer> initiallyPopulatedTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);
            assertEquals(points.size(), initiallyPopulatedTree.size());
        }
    }

    @Test
    public void testIsEmpty() {
        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction());
        final Integer testPoint = 12;

        assertTrue(vpTree.isEmpty());

        vpTree.add(testPoint);
        assertFalse(vpTree.isEmpty());

        vpTree.remove(testPoint);
        assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testAdd() {
        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction());
        final Integer testPoint = 12;

        assertFalse(vpTree.contains(testPoint));

        assertTrue(vpTree.add(testPoint));
        assertTrue(vpTree.contains(testPoint));
    }

    @Test
    public void testAddAll() {
        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction());

        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        assertTrue(vpTree.addAll(points));
        assertEquals(points.size(), vpTree.size());
        assertTrue(vpTree.containsAll(points));
    }

    @Test
    public void testRemove() {
        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction());
        final Integer testPoint = 12;

        assertFalse(vpTree.remove(testPoint));

        vpTree.add(testPoint);
        assertTrue(vpTree.remove(testPoint));
        assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testRemoveAll() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);
        final ArrayList<Integer> pointsToRemove = new ArrayList<Integer>(numberOfPoints / 2);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);

            if (i % 2 == 0) {
                pointsToRemove.add(i);
            }
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        assertTrue(vpTree.removeAll(pointsToRemove));
        assertEquals(points.size() - pointsToRemove.size(), vpTree.size());

        for (final Integer point : pointsToRemove) {
            assertFalse(vpTree.contains(point));
        }

        assertFalse(vpTree.removeAll(pointsToRemove));
    }

    @Test
    public void testRetainAll() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);
        final ArrayList<Integer> pointsToRetain = new ArrayList<Integer>(numberOfPoints / 2);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);

            if (i % 2 == 0) {
                pointsToRetain.add(i);
            }
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        assertTrue(vpTree.retainAll(pointsToRetain));
        assertEquals(pointsToRetain.size(), vpTree.size());

        for (final Integer point : pointsToRetain) {
            assertTrue(vpTree.contains(point));
        }

        assertFalse(vpTree.retainAll(pointsToRetain));
    }

    @Test
    public void testClear() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        assertFalse(vpTree.isEmpty());

        vpTree.clear();
        assertTrue(vpTree.isEmpty());
    }

    @Test
    public void testContains() {
        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction());
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
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        assertTrue(vpTree.containsAll(points));

        points.add(numberOfPoints + 1);
        assertFalse(vpTree.containsAll(points));
    }

    @Test
    public void testIterator() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        final ArrayList<Integer> pointsFromIterator = new ArrayList<Integer>();
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
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);
        final Object[] array = vpTree.toArray();

        assertEquals(vpTree.size(), array.length);

        for (final Object point : array) {
            assertTrue(vpTree.contains(point));
        }
    }

    @Test
    public void testToArrayTArray() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<Integer>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Integer> vpTree = new VPTree<Integer>(new IntegerDistanceFunction(), points);

        {
            final Integer[] array = vpTree.toArray(new Integer[0]);

            assertEquals(vpTree.size(), array.length);

            for (final Integer point : array) {
                assertTrue(vpTree.contains(point));
            }
        }

        {
            final Integer[] array = vpTree.toArray(new Integer[vpTree.size()]);

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
}
