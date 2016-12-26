package com.eatthepath.jvptree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class MetaIteratorTest {

    @Test
    public void testHasNextAndNext() {
        {
            final MetaIterator<Object> emptyIterator = new MetaIterator<>(new ArrayList<Iterator<Object>>());

            assertFalse("Empty iterators should not have a next element.", emptyIterator.hasNext());

            try {
                emptyIterator.next();
                fail("Empty iterators should throw NoSuchElementException for next element");
            } catch (NoSuchElementException e) {
                // This is supposed to happen for empty iterators
            }
        }

        {
            final ArrayList<Integer> integers = new ArrayList<>();
            integers.add(1);
            integers.add(2);
            integers.add(3);

            final MetaIterator<Integer> singleIterator =
                    new MetaIterator<>(Collections.singletonList(integers.iterator()));

            final ArrayList<Integer> integersFromIterator = new ArrayList<>();

            while (singleIterator.hasNext()) {
                integersFromIterator.add(singleIterator.next());
            }

            assertEquals("Elements from iterator should match initial elements.",
                    integers, integersFromIterator);
        }

        {
            final ArrayList<Integer> firstIntegers = new ArrayList<>();
            firstIntegers.add(1);
            firstIntegers.add(2);
            firstIntegers.add(3);

            final ArrayList<Integer> emptyList = new ArrayList<>();

            final ArrayList<Integer> secondIntegers = new ArrayList<>();
            secondIntegers.add(4);
            secondIntegers.add(5);
            secondIntegers.add(6);

            final ArrayList<Iterator<Integer>> iterators = new ArrayList<>();
            iterators.add(firstIntegers.iterator());
            iterators.add(emptyList.iterator());
            iterators.add(secondIntegers.iterator());

            final MetaIterator<Integer> multipleIterator = new MetaIterator<>(iterators);

            final ArrayList<Integer> integersFromIterator = new ArrayList<>();

            while (multipleIterator.hasNext()) {
                integersFromIterator.add(multipleIterator.next());
            }

            final ArrayList<Integer> combinedList = new ArrayList<>();
            combinedList.addAll(firstIntegers);
            combinedList.addAll(emptyList);
            combinedList.addAll(secondIntegers);

            assertEquals("Elements from iterator should match initial elements.",
                    combinedList, integersFromIterator);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        new MetaIterator<>(new ArrayList<Iterator<Object>>()).remove();
    }

}
