package com.eatthepath.jvptree.example;

import com.eatthepath.jvptree.DistanceFunction;

public class XYDistanceFunction implements DistanceFunction<XYPoint> {

    public double getDistance(final XYPoint firstPoint, final XYPoint secondPoint) {
        final double deltaX = firstPoint.getX() - secondPoint.getX();
        final double deltaY = firstPoint.getY() - secondPoint.getY();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
}
