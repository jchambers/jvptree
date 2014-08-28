package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class VPTree<E> implements Collection<E> {

    private VPNode<E> rootNode;

    public int size() {
        return this.rootNode == null ? 0 : this.rootNode.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(final Object o) {
        try {
            return this.rootNode == null ? false : this.rootNode.contains((E) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public Iterator<E> iterator() {
        final ArrayList<Iterator<E>> iterators = new ArrayList<Iterator<E>>();

        if (this.rootNode != null) {
            this.rootNode.collectIterators(iterators);
        }

        return new MetaIterator<E>(iterators);
    }

    public Object[] toArray() {
        final Object[] array = new Object[this.size()];

        if (this.rootNode != null) {
            this.rootNode.addPointsToArray(array, 0);
        }

        return array;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] array) {
        final T[] arrayToPopulate;

        if (array.length < this.size()) {
            arrayToPopulate = (T[])java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), this.size());
        } else {
            arrayToPopulate = array;
        }

        if (this.rootNode != null) {
            this.rootNode.addPointsToArray(arrayToPopulate, 0);
        }

        return arrayToPopulate;
    }

    public boolean add(final E point) {
        if (this.rootNode == null) {
            // TODO Create a root node
        } else {
            this.rootNode.add(point);
        }

        // Adding a point always modifies a VPTree
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean remove(final Object point) {
        try {
            return this.rootNode == null ? false : this.rootNode.remove((E) point);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean containsAll(final Collection<?> points) {
        for (final Object point : points) {
            if (!this.contains(point)) { return false; }
        }

        return true;
    }

    public boolean addAll(final Collection<? extends E> points) {
        for (final E point : points) {
            this.add(point);
        }

        // Adding points always modifies a VPTree
        return !points.isEmpty();
    }

    public boolean removeAll(final Collection<?> points) {
        boolean pointRemoved = false;

        for (final Object point : points) {
            pointRemoved = pointRemoved || this.remove(point);
        }

        return pointRemoved;
    }

    public boolean retainAll(final Collection<?> points) {
        return this.rootNode == null ? false : this.rootNode.retainAll(points);
    }

    public void clear() {
        this.rootNode = null;
    }
}
