package com.eatthepath.jvptree;

import java.util.Collection;
import java.util.List;

/**
 * A collection of points that can be searched efficiently to find points near a given query point. A spatial index
 * takes two generic types. The first, {@code P}, is the base type of point for which distances can be measured. The
 * second, {@code E}, is the specific type of point contained within the index. The two ideas are separated because
 * callers may want to use an instance of {@code E} when querying the index. For example, an index that is used to
 * search for local businesses might have a base type of {@code GeospatialPoint}, but a specific type of
 * {@code HardwareStore}, which implements {@code GeospatialPoint} but has a number of additional required properties.
 * By separating the types, callers may realize the benefits of using a specific type when working with elements in the
 * index without the need to construct a new {@code HardwareStore} instance when querying points. Instead, they might
 * call {@link SpatialIndex#getNearestNeighbors(Object, int)} with a {@code new GeospatialPoint} instead of a much
 * heavier {@code HardwareStore}.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 *
 * @param <P> the base type of points between which distances can be measured
 * @param <E> the specific type of point contained in this vantage point tree
 */
public interface SpatialIndex<P, E extends P> extends Collection<E> {
    /**
     * <p>Returns a list of the nearest neighbors to a given query point. The returned list is sorted by increasing
     * distance from the query point.</p>
     *
     * <p>This returned list will contain at most {@code maxResults} elements (and may contain fewer if
     * {@code maxResults} is larger than the number of points in the index). If multiple points have the same distance
     * from the query point, the order in which they appear in the returned list is undefined. By extension, if multiple
     * points have the same distance from the query point and those points would &quot;straddle&quot; the end of the
     * returned list, which points are included in the list and which are cut off is not prescribed.</p>
     *
     * @param queryPoint the point for which to find neighbors
     * @param maxResults the maximum length of the returned list
     *
     * @return a list of the nearest neighbors to the given query point sorted by increasing distance from the query
     * point
     */
    List<E> getNearestNeighbors(P queryPoint, int maxResults);

    /**
     * <p>Returns a list of the nearest neighbors accepted by the given filter to a given query point. The returned list
     * is sorted by increasing distance from the query point.</p>
     *
     * <p>This returned list will contain at most {@code maxResults} elements (and may contain fewer if
     * {@code maxResults} is larger than the number of points in the index). If multiple points have the same distance
     * from the query point, the order in which they appear in the returned list is undefined. By extension, if multiple
     * points have the same distance from the query point and those points would &quot;straddle&quot; the end of the
     * returned list, which points are included in the list and which are cut off is not prescribed.</p>
     *
     * @param queryPoint the point for which to find neighbors
     * @param maxResults the maximum length of the returned list
     * @param filter a filter to apply to each element to determine if it should be included in the list of neighbors
     *
     * @return a list of the nearest neighbors to the given query point sorted by increasing distance from the query
     * point
     */
    List<E> getNearestNeighbors(P queryPoint, int maxResults, PointFilter<? super E> filter);

    /**
     * Returns a list of all points within a given distance to a query point.
     *
     * @param queryPoint the point for which to find neighbors
     * @param maxDistance the maximum allowable distance from the query point; points farther away than
     * {@code maxDistance} will not be included in the returned list
     *
     * @return a list of all points within the given distance to the query point; the returned list is sorted in order
     * of increasing distance from the query point
     */
    List<E> getAllWithinDistance(P queryPoint, double maxDistance);

    /**
     * Returns a list of all points within a given distance to a query point that match the given filter.
     *
     * @param queryPoint the point for which to find neighbors
     * @param maxDistance the maximum allowable distance from the query point; points farther away than
     * {@code maxDistance} will not be included in the returned list
     * @param filter a filter to apply to each element to determine if it should be included in the list of neighbors
     *
     * @return a list of all points within the given distance to the query point; the returned list is sorted in order
     * of increasing distance from the query point
     */
    List<E> getAllWithinDistance(P queryPoint, double maxDistance, PointFilter<? super E> filter);
}
