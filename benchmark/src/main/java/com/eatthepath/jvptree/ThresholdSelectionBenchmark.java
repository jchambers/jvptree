/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

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

import com.eatthepath.jvptree.util.MedianDistanceThresholdSelectionStrategy;
import com.eatthepath.jvptree.util.SamplingMedianDistanceThresholdSelectionStrategy;

@State(Scope.Thread)
public class ThresholdSelectionBenchmark {

    @Param({"100000"})
    public int pointCount;

    private List<CartesianPoint> points;

    private final Random random = new Random();
    private final CartesianDistanceFunction distanceFunction = new CartesianDistanceFunction();

    private final MedianDistanceThresholdSelectionStrategy<CartesianPoint, CartesianPoint> medianSelectionStrategy =
            new MedianDistanceThresholdSelectionStrategy<>();

    private final SamplingMedianDistanceThresholdSelectionStrategy<CartesianPoint, CartesianPoint> samplingMedianSelectionStrategy =
            new SamplingMedianDistanceThresholdSelectionStrategy<>(100);

    @Setup
    public void setUp() {
        this.points = new ArrayList<>(this.pointCount);

        for (int i = 0; i < this.pointCount; i++) {
            this.points.add(this.createRandomPoint());
        }
    }

    @Benchmark
    public double benchmarkRandomThresholdSelection() {
        final CartesianPoint origin = this.createRandomPoint();

        return this.distanceFunction.getDistance(origin, this.points.get(this.random.nextInt(this.pointCount)));
    }

    @Benchmark
    public double benchmarkMedianThresholdSelection() {
        final CartesianPoint origin = this.createRandomPoint();

        return this.medianSelectionStrategy.selectThreshold(this.points, origin, this.distanceFunction);
    }

    @Benchmark
    public double benchmarkSamplingMedianThresholdSelection() {
        final CartesianPoint origin = this.createRandomPoint();

        return this.samplingMedianSelectionStrategy.selectThreshold(this.points, origin, this.distanceFunction);
    }

    @Benchmark
    public double benchmarkNaiveMedianThresholdSelection() {
        final CartesianPoint origin = this.createRandomPoint();

        Collections.sort(this.points, new DistanceComparator<>(origin, this.distanceFunction));

        return this.distanceFunction.getDistance(origin, this.points.get(this.points.size() / 2));
    }

    private CartesianPoint createRandomPoint() {
        return new CartesianPoint(this.random.nextDouble(), this.random.nextDouble());
    }
}
