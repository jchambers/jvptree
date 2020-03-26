package com.eatthepath.jvptree.example;

public class SpaceInvader implements CartesianPoint {

    private final double x;
    private final double y;

    private final String color;

    public SpaceInvader(final double x, final double y, final String color) {
        this.x = x;
        this.y = y;

        this.color = color;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public String getColor() {
        return this.color;
    }
}
