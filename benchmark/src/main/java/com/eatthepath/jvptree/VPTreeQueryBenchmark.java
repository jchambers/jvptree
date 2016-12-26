package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import com.eatthepath.jvptree.util.SamplingMedianDistanceThresholdSelectionStrategy;

@State(Scope.Thread)
public class VPTreeQueryBenchmark {

    @Param({"100000"})
    public int pointCount;

    @Param({"2", "16", "128"})
    public int nodeSize;

    @Param({"2", "16", "128"})
    public int resultSetSize;

    private List<CartesianPoint> points;
    private VPTree<CartesianPoint, CartesianPoint> vptree;

    private final Random random = new Random();
    private final CartesianDistanceFunction distanceFunction = new CartesianDistanceFunction();

    @Setup
    public void setUp() {
        this.points = new ArrayList<>(this.pointCount);

        for (int i = 0; i < this.pointCount; i++) {
            this.points.add(this.createRandomPoint());
        }

        this.vptree = new VPTree<>(this.distanceFunction,
                new SamplingMedianDistanceThresholdSelectionStrategy<CartesianPoint, CartesianPoint>(32),
                this.nodeSize, this.points);
    }

    @Benchmark
    public List<CartesianPoint> benchmarkNaiveSearch() {
        Collections.sort(this.points, new DistanceComparator<>(this.createRandomPoint(), this.distanceFunction));
        return this.points.subList(0, this.resultSetSize);
    }

    @Benchmark
    public List<CartesianPoint> benchmarkQueryTree() {
        return this.vptree.getNearestNeighbors(this.createRandomPoint(), this.resultSetSize);
    }

    private CartesianPoint createRandomPoint() {
        return new CartesianPoint(this.random.nextDouble(), this.random.nextDouble());
    }
}
