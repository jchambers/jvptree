package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class NearestNeighborCollectorTest {

    private NearestNeighborCollector<Number, Integer> collector;

    private static final int CAPACITY = 5;

    @Before
    public void setup() {
        this.collector = new NearestNeighborCollector<>(0, new IntegerDistanceFunction(), CAPACITY);
    }

    @Test
    public void testOfferPointAndGetFarthestPoint() {
        assertNull("Farthest point in an empty collector should be null.", this.collector.getFarthestPoint());

        this.collector.offerPoint(17);
        assertEquals("Farthest point in collector with single element should be the single element",
                (Integer) 17, this.collector.getFarthestPoint());

        this.collector.offerPoint(2);
        assertEquals("Farthest point after adding a closer point should still be the farther point",
                (Integer) 17, this.collector.getFarthestPoint());

        this.collector.offerPoint(19);
        assertEquals("Farthest point after adding a new farther point should be the new point",
                (Integer) 19, this.collector.getFarthestPoint());

        for (int i = 0; i < CAPACITY; i++) {
            this.collector.offerPoint(3);
        }

        assertEquals("Farthest point after flushing with identical closer points should be closer point",
                (Integer) 3, this.collector.getFarthestPoint());

        for (int i = 0; i < CAPACITY; i++) {
            this.collector.offerPoint(20);
        }

        assertEquals("Farthest point after flushing with identical farther points should still be closer point",
                (Integer) 3, this.collector.getFarthestPoint());

    }

    @Test
    public void testToSortedList() {
        assertTrue("Sorted list from empty collector should be empty.",
                this.collector.toSortedList().isEmpty());

        this.collector.offerPoint(19);
        this.collector.offerPoint(77);
        this.collector.offerPoint(4);
        this.collector.offerPoint(1);
        this.collector.offerPoint(2);
        this.collector.offerPoint(62);
        this.collector.offerPoint(8375);
        this.collector.offerPoint(3);
        this.collector.offerPoint(5);
        this.collector.offerPoint(5);

        final ArrayList<Integer> expectedList = new ArrayList<>();
        java.util.Collections.addAll(expectedList, 1, 2, 3, 4, 5);

        assertEquals(CAPACITY, expectedList.size());
        assertEquals(expectedList, this.collector.toSortedList());
    }
}
