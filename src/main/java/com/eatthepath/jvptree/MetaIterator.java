package com.eatthepath.jvptree;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

class MetaIterator<E> implements Iterator<E> {

    private final Deque<Iterator<E>> iterators;

    public MetaIterator(final Collection<Iterator<E>> iterators) {
        this.iterators = new ArrayDeque<Iterator<E>>(iterators);
    }

    public boolean hasNext() {
        while (!this.iterators.isEmpty()) {
            if (this.iterators.peek().hasNext()) {
                return true;
            }

            this.iterators.pop();
        }

        return false;
    }

    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        return this.iterators.peek().next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
