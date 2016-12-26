package com.eatthepath.jvptree;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that concatenates a number of sub-iterators.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
class MetaIterator<E> implements Iterator<E> {

    private final Deque<Iterator<E>> iterators;

    /**
     * Constructs an iterator that concatenates the contents of the given collection of iterators.
     *
     * @param iterators the iterators to concatenate
     */
    public MetaIterator(final Collection<Iterator<E>> iterators) {
        this.iterators = new ArrayDeque<>(iterators);
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        while (!this.iterators.isEmpty()) {
            if (this.iterators.peek().hasNext()) {
                return true;
            }

            this.iterators.pop();
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        return this.iterators.peek().next();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
