package com.eatthepath.jvptree.util;

import com.eatthepath.jvptree.DistanceFunction;
import com.eatthepath.jvptree.IntegerDistanceFunction;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SamplingMedianDistanceThresholdSelectionStrategyTest {

    private static class FakeGiganticList extends AbstractList<Integer> {

        @Override
        public Integer get(final int index) {
            if (index >= size() || index < 0) {
                throw new IndexOutOfBoundsException();
            }

            return index;
        }

        @Override
        public int size() {
            return 305574400;
        }
    }

    @Test
    void selectThreshold() {
        final SamplingMedianDistanceThresholdSelectionStrategy<Integer, Integer> strategy =
                new SamplingMedianDistanceThresholdSelectionStrategy<>();

        final List<Integer> points = new FakeGiganticList();

        assertDoesNotThrow(() -> strategy.selectThreshold(points, 17, (a, b) -> Math.abs(a - b)));
    }
}