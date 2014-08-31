package com.eatthepath.jvptree;

import java.util.Collection;
import java.util.List;

public interface SpatialIndex<E> extends Collection<E> {
    public List<E> getNearestNeighbors(E queryPoint, int maxResults);
    public List<E> getAllWithinRange(E queryPoint, double maxDistance);
}
