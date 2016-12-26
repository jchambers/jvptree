package com.eatthepath.jvptree.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.eatthepath.jvptree.IntegerDistanceFunction;
import com.eatthepath.jvptree.util.MedianDistanceThresholdSelectionStrategy;

public class MedianDistanceThresholdSelectionStrategyTest {

    @Test
    public void testSelectThreshold() {
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

    @Test(expected = IllegalArgumentException.class)
    public void testSelectThresholdEmptyList() {
        new MedianDistanceThresholdSelectionStrategy<Number, Integer>().selectThreshold(
                new ArrayList<Integer>(), 0, new IntegerDistanceFunction());
    }
}
