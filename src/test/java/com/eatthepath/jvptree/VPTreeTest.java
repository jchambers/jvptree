package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.eatthepath.jvptree.util.IntegerDistanceFunction;

public class VPTreeTest {

    @Test
    public void testGetNearestNeighbors() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetAllWithinRange() {
        fail("Not yet implemented");
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
        fail("Not yet implemented");
    }

    @Test
    public void testIterator() {
        fail("Not yet implemented");
    }

    @Test
    public void testToArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testToArrayTArray() {
        fail("Not yet implemented");
    }
}
