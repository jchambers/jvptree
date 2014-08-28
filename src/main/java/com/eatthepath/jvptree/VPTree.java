package com.eatthepath.jvptree;

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
        // TODO Auto-generated method stub
        return null;
    }

    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
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
