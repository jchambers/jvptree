[![Build Status](https://travis-ci.org/jchambers/jvptree.svg?branch=master)](https://travis-ci.org/jchambers/jvptree)

# jvptree

Jvptree is a generic [vantage-point tree](https://en.wikipedia.org/wiki/Vantage-point_tree) implementation written in Java that allows for quick (*O(log(n))*) searches for the nearest neighbors to a given point. Vantage-point trees are binary space partitioning trees that partition points according to their distance from each node's "vantage point." Points that are closer than a chosen threshold go into one child node, while points that are farther away go into the other. Vantage point trees operate on any [metric space](https://en.wikipedia.org/wiki/Metric_space).

Steve Hanov has written a great and accessible [introducton to vp-trees](http://stevehanov.ca/blog/index.php?id=130).

## Getting jvptree

If you use [Maven](http://maven.apache.org/), you can add jvptree to your project by adding the following dependency declaration to your POM:

```xml
<dependency>
    <groupId>com.eatthepath</groupId>
    <artifactId>jvptree</artifactId>
    <version>0.1</version>
</dependency>
```

If you don't use Maven, you can download jvptree as a `.jar` file and add it to your project directly. Jvptree has no external dependencies, and works with Java 1.5 and newer.

## Major concepts

The main thing vantage-point trees do is partitioning points into groups that are closer or farther than a given distance threshold. To do that, a vp-tree needs to be able to figure out how far apart any two points are and also decide what to use as a distance threshold. At a minimum, you'll need to provide a distance function that can calculate the distance between points. You may optionally specify a threshold selection strategy; if you don't, a reasonable default will be used.

### Distance functions

You must always specify a [distance function](http://jchambers.github.io/jvptree/apidocs/0.1/com/eatthepath/jvptree/DistanceFunction.html) when creating a vp-tree. Distance functions take two points as arguments and must satisfy the requirements of a metric space, namely:

- d(x, y) >= 0
- d(x, y) = 0 if and only if x == y
- d(x, y) == d(y, x)
- d(x, z) <= d(x, y) + d(y, z)

### Threshold selection strategies

You may optionally specify a [strategy for choosing a distance threshold](http://jchambers.github.io/jvptree/apidocs/0.1/com/eatthepath/jvptree/ThresholdSelectionStrategy.html) for partitioning. By default, jvptree will use [sampling median strategy](http://jchambers.github.io/jvptree/apidocs/0.1/com/eatthepath/jvptree/util/SamplingMedianDistanceThresholdSelectionStrategy.html), where it will take the median distance from a small subset of the points to partition. Jvptree also includes a [threshold selection strategy that takes the median of *all* points](http://jchambers.github.io/jvptree/apidocs/0.1/com/eatthepath/jvptree/util/MedianDistanceThresholdSelectionStrategy.html) to be partitioned; this is slower, but may result in a more balanced tree. Most users will not need to specify a threshold selection strategy.

### Node capacity

Additionally, you may specify a desired capacity for the tree's leaf nodes. It's worth mentioning early that you almost certainly do not need to worry about this; a reasonable default (32 points) will be used, and most users won't realize significant performance gains by tuning it.

Still, for those in need, you may choose a desired capacity for leaf nodes in a vp-tree. At one extreme, leaf nodes may contain only a single point. This means that searches will have to traverse more nodes, but once a leaf node is reached, fewer points will need to be searched to find nearest neighbors.

Using a larger node capacity will result in a "flatter" tree, and fewer nodes will need to be traversed when searching, but more nodes will need to be tested once a search reaches a leaf node. Larger node capacities also lead to less memory overhead because there are fewer nodes in the tree.

As a general rule of thumb, node capacities should be on the same order of magnitude as your typical search result size. The idea is that if a search reaches a leaf node, most of the points in the node will wind up in the collection of nearest neighbors (i.e. they all would have had to been checked anyhow) and few other nodes will have to be visited to gather any remaining neighbors.

## Using jvptree

As discussed above, you must provide a distance function when creating a vp-tree and may optionally specify a distance threshold selection strategy and leaf node capacity. As a simple example, let's say you have a bunch of points on a two-dimensional grid, and those points are represented by the `XYPoint` class:

```java
public class XYPoint {

    private final double x;
    private final double y;

    public XYPoint(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
```

You must provide a distance function that will return the distance between any two given points. For example, you might create an `XYDistanceFunction` class:

```java
public class XYDistanceFunction implements DistanceFunction<XYPoint> {

    public double getDistance(XYPoint firstPoint, XYPoint secondPoint) {
        final double deltaX = firstPoint.getX() - secondPoint.getX();
        final double deltaY = firstPoint.getY() - secondPoint.getY();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
}
```

Once you have your distance function, you can create a vp-tree that contains your collection of points:

```java
VPTree<XYPoint> vpTree = new VPTree<XYPoint>(new XYDistanceFunction(), points);
```

In this case, we provide all of our points at construction time, but you may also create an empty tree and add points later. The `VPTree` class implements Java's [`Collection`](http://docs.oracle.com/javase/7/docs/api/java/util/Collection.html) interface and supports all optional operations.

With your tree created, you can find the nearest neighbor to a query point. For example, to find the ten closest points to the point (17.4, -22.2), you could:

```java
final List<XYPoint> nearestNeighbors =
    vpTree.getNearestNeighbors(new XYPoint(17.4, -22.2), 10);
```

You can also find all of the points that are within a given distance to a query point. For example, to find all points that are within a distance of 4.5 of the origin:

```java
final List<XYPoint> pointsNearOrigin =
    vpTree.getAllWithinDistance(new XYPoint(0, 0), 4.5);
```

## License

Jvptree is available to the public under the [MIT License](http://opensource.org/licenses/MIT).
