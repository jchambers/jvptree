package com.eatthepath.jvptree;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MetaIteratorTest {

    @Test
    void testHasNextAndNext() {
        {
            final MetaIterator<Object> emptyIterator = new MetaIterator<>(Collections.emptyList());

            assertFalse(emptyIterator.hasNext(), "Empty iterators should not have a next element.");

            assertThrows(NoSuchElementException.class, emptyIterator::next,
                    "Empty iterators should throw NoSuchElementException for next element");
        }

        {
            final List<Integer> integers = Arrays.asList(1, 2, 3);

            final MetaIterator<Integer> singleIterator =
                    new MetaIterator<>(Collections.singletonList(integers.iterator()));

            final ArrayList<Integer> integersFromIterator = new ArrayList<>();

            while (singleIterator.hasNext()) {
                integersFromIterator.add(singleIterator.next());
            }

            assertEquals(integers, integersFromIterator, "Elements from iterator should match initial elements.");
        }

        {
            final List<Integer> firstIntegers = Arrays.asList(1, 2, 3);
            final List<Integer> emptyList = Collections.emptyList();
            final List<Integer> secondIntegers = Arrays.asList(4, 5, 6);

            @SuppressWarnings("RedundantOperationOnEmptyContainer") final List<Iterator<Integer>> iterators =
                    Arrays.asList(firstIntegers.iterator(), emptyList.iterator(), secondIntegers.iterator());

            final MetaIterator<Integer> multipleIterator = new MetaIterator<>(iterators);

            final List<Integer> integersFromIterator = new ArrayList<>();

            while (multipleIterator.hasNext()) {
                integersFromIterator.add(multipleIterator.next());
            }

            final List<Integer> combinedList = new ArrayList<>();
            combinedList.addAll(firstIntegers);
            combinedList.addAll(emptyList);
            combinedList.addAll(secondIntegers);

            assertEquals(combinedList, integersFromIterator, "Elements from iterator should match initial elements.");
        }
    }

    @Test
    void testRemove() {
        assertThrows(UnsupportedOperationException.class, () -> new MetaIterator<>(Collections.emptyList()).remove());
    }
}
