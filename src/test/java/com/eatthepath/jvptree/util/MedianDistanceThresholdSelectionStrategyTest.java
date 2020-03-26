package com.eatthepath.jvptree.util;

import java.util.ArrayList;
import java.util.List;

import com.eatthepath.jvptree.IntegerDistanceFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MedianDistanceThresholdSelectionStrategyTest {

    @Test
    void testSelectThreshold() {
        final MedianDistanceThresholdSelectionStrategy<Number, Integer> strategy =
                new MedianDistanceThresholdSelectionStrategy<>();

        {
            final List<Integer> singleIntegerList = new ArrayList<>();
            singleIntegerList.add(7);

            assertEquals(7, (int)strategy.selectThreshold(singleIntegerList, 0, new IntegerDistanceFunction()));
        }

        {
            final List<Integer> multipleIntegerList = new ArrayList<>();
            multipleIntegerList.add(2);
            multipleIntegerList.add(9);
            multipleIntegerList.add(3);
            multipleIntegerList.add(1);
            multipleIntegerList.add(6);
            multipleIntegerList.add(4);
            multipleIntegerList.add(8);
            multipleIntegerList.add(5);
            multipleIntegerList.add(7);

            assertEquals(5, (int)strategy.selectThreshold(multipleIntegerList, 0, new IntegerDistanceFunction()));
        }
    }

    @Test
    void testSelectThresholdEmptyList() {
        assertThrows(IllegalArgumentException.class,
                () -> new MedianDistanceThresholdSelectionStrategy<Number, Integer>().selectThreshold(
                        new ArrayList<>(), 0, new IntegerDistanceFunction()));
    }
}
