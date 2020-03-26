package com.eatthepath.jvptree;

import com.eatthepath.jvptree.DistanceFunction;

public class CartesianDistanceFunction implements DistanceFunction<CartesianPoint> {

    @Override
    public double getDistance(final CartesianPoint firstPoint, final CartesianPoint secondPoint) {
        final double deltaX = firstPoint.getX() - secondPoint.getX();
        final double deltaY = firstPoint.getY() - secondPoint.getY();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
}
