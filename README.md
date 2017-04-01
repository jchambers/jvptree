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
    <version>0.2</version>
</dependency>
```

If you don't use Maven, you can download jvptree as a `.jar` file and add it to your project directly. Jvptree has no external dependencies, and works with Java 1.7 and newer.

## Major concepts

The main thing vantage-point trees do is partitioning points into groups that are closer or farther than a given distance threshold. To do that, a vp-tree needs to be able to figure out how far apart any two points are and also decide what to use as a distance threshold. At a minimum, you'll need to provide a distance function that can calculate the distance between points. You may optionally specify a threshold selection strategy; if you don't, a reasonable default will be used.

### Distance functions

You must always specify a [distance function](http://jchambers.github.io/jvptree/apidocs/0.2/com/eatthepath/jvptree/DistanceFunction.html) when creating a vp-tree. Distance functions take two points as arguments and must satisfy the requirements of a metric space, namely:

- d(x, y) >= 0
- d(x, y) = 0 if and only if x == y
- d(x, y) == d(y, x)
- d(x, z) <= d(x, y) + d(y, z)

### Threshold selection strategies

You may optionally specify a [strategy for choosing a distance threshold](http://jchambers.github.io/jvptree/apidocs/0.2/com/eatthepath/jvptree/ThresholdSelectionStrategy.html) for partitioning. By default, jvptree will use [sampling median strategy](http://jchambers.github.io/jvptree/apidocs/0.2/com/eatthepath/jvptree/util/SamplingMedianDistanceThresholdSelectionStrategy.html), where it will take the median distance from a small subset of the points to partition. Jvptree also includes a [threshold selection strategy that takes the median of *all* points](http://jchambers.github.io/jvptree/apidocs/0.2/com/eatthepath/jvptree/util/MedianDistanceThresholdSelectionStrategy.html) to be partitioned; this is slower, but may result in a more balanced tree. Most users will not need to specify a threshold selection strategy.

### Node capacity

Additionally, you may specify a desired capacity for the tree's leaf nodes. It's worth mentioning early that you almost certainly do not need to worry about this; a reasonable default (32 points) will be used, and most users won't realize significant performance gains by tuning it.

Still, for those in need, you may choose a desired capacity for leaf nodes in a vp-tree. At one extreme, leaf nodes may contain only a single point. This means that searches will have to traverse more nodes, but once a leaf node is reached, fewer points will need to be searched to find nearest neighbors.

Using a larger node capacity will result in a "flatter" tree, and fewer nodes will need to be traversed when searching, but more nodes will need to be tested once a search reaches a leaf node. Larger node capacities also lead to less memory overhead because there are fewer nodes in the tree.

As a general rule of thumb, node capacities should be on the same order of magnitude as your typical search result size. The idea is that if a search reaches a leaf node, most of the points in the node will wind up in the collection of nearest neighbors (i.e. they all would have had to been checked anyhow) and few other nodes will have to be visited to gather any remaining neighbors.

## Using jvptree

As discussed above, you must provide a distance function when creating a vp-tree and may optionally specify a distance threshold selection strategy and leaf node capacity. As a simple example, let's say you're writing a version of [Space Invaders](https://en.wikipedia.org/wiki/Space_Invaders), and you know you'll need to find the closest enemies to the player's position. To start, everything on the playing field will exist at a specific point:

```java
public interface CartesianPoint {
    double getX();
    double getY();
}
```

To create a vp-tree, you must provide a distance function that will return the distance between any two given points. In this example, you might create a `CartesianDistanceFunction` class:

```java
public class CartesianDistanceFunction implements DistanceFunction<CartesianPoint> {

    public double getDistance(final CartesianPoint firstPoint, final CartesianPoint secondPoint) {
        final double deltaX = firstPoint.getX() - secondPoint.getX();
        final double deltaY = firstPoint.getY() - secondPoint.getY();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
}
```

Once you have your distance function, you can create a vp-tree that stores the locations of all of the space invaders on the playing field:

```java
final VPTree<CartesianPoint, SpaceInvader> vpTree =
        new VPTree<CartesianPoint, SpaceInvader>(
                new CartesianDistanceFunction(), enemies);
```

In this case, we provide all of our points at construction time, but you may also create an empty tree and add points later. The `VPTree` class implements Java's [`Collection`](http://docs.oracle.com/javase/7/docs/api/java/util/Collection.html) interface and supports all optional operations.

Note that a `VPTree` has two generic types: a general "base" point type and a more specific type for the elements actually stored in the tree. You can query the tree using any instance of the base type, but still know that you'll be receiving a list of the more specific type as a result of the query. In our example, this is helpful because the player's location is a cartesian point, but the player is not a space invader. It wouldn't make much sense to create a new space invader at the player's location just to query the vp-tree, and so this construct allows us to query the tree with the player's location instead.

With your tree created, you can find the closest enemies to the player's position. For example, to find (up to) the ten closest space invaders:

```java
final List<SpaceInvader> nearestEnemies =
        vpTree.getNearestNeighbors(playerPosition, 10);
```

You could also find all of the enemies that are within firing range of the player:

```java
final List<SpaceInvader> enemiesWithinFiringRange =
        vpTree.getAllWithinDistance(playerPosition, 4.5);
```

## License

Jvptree is available to the public under the [MIT License](http://opensource.org/licenses/MIT).
