package com.eatthepath.jvptree;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NearestNeighborCollectorTest {

    private NearestNeighborCollector<Number, Integer> collector;

    private static final int CAPACITY = 5;

    @BeforeEach
    void setup() {
        this.collector = new NearestNeighborCollector<>(0, new IntegerDistanceFunction(), CAPACITY);
    }

    @Test
    void testOfferPointAndGetFarthestPoint() {
        assertNull(this.collector.getFarthestPoint(), "Farthest point in an empty collector should be null.");

        this.collector.offerPoint(17);
        assertEquals(17, this.collector.getFarthestPoint(),
                "Farthest point in collector with single element should be the single element");

        this.collector.offerPoint(2);
        assertEquals(17, this.collector.getFarthestPoint(),
                "Farthest point after adding a closer point should still be the farther point");

        this.collector.offerPoint(19);
        assertEquals(19, this.collector.getFarthestPoint(),
                "Farthest point after adding a new farther point should be the new point");

        for (int i = 0; i < CAPACITY; i++) {
            this.collector.offerPoint(3);
        }

        assertEquals(3, this.collector.getFarthestPoint(),
                "Farthest point after flushing with identical closer points should be closer point");

        for (int i = 0; i < CAPACITY; i++) {
            this.collector.offerPoint(20);
        }

        assertEquals(3, this.collector.getFarthestPoint(),
                "Farthest point after flushing with identical farther points should still be closer point");

    }

    @Test
    public void testToSortedList() {
        assertTrue(this.collector.toSortedList().isEmpty(), "Sorted list from empty collector should be empty.");

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
