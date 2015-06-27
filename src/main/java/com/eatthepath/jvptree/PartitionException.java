package com.eatthepath.jvptree;

/**
 * Indicates that a list of points could not be partitioned by distance because either all points are on one side of
 * the distance threshold or all points are of equal distance from the pivot point.
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
class PartitionException extends Exception {
    private static final long serialVersionUID = 1L;
}
