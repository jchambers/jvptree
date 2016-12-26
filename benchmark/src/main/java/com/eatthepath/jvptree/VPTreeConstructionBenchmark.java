package com.eatthepath.jvptree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class VPTreeConstructionBenchmark {

    @Param({"100000"})
    public int pointCount;

    private List<CartesianPoint> points;

    private final Random random = new Random();
    private final CartesianDistanceFunction distanceFunction = new CartesianDistanceFunction();

    @Setup
    public void setUp() {
        this.points = new ArrayList<>(this.pointCount);

        for (int i = 0; i < this.pointCount; i++) {
            this.points.add(this.createRandomPoint());
        }
    }

    @Benchmark
    public VPTree<CartesianPoint, CartesianPoint> benchmarkConstructTreeWithPoints() {
        return new VPTree<>(this.distanceFunction, this.points);
    }

    @Benchmark
    public VPTree<CartesianPoint, CartesianPoint> benchmarkConstructAndAddPoints() {
        final VPTree<CartesianPoint, CartesianPoint> vptree = new VPTree<>(this.distanceFunction);
        vptree.addAll(this.points);

        return vptree;
    }

    private CartesianPoint createRandomPoint() {
        return new CartesianPoint(this.random.nextDouble(), this.random.nextDouble());
    }
}
