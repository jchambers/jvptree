package com.eatthepath.jvptree.example;

import java.util.ArrayList;
import java.util.List;

import com.eatthepath.jvptree.VPTree;

public class XYExampleApp {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        final List<XYPoint> points = new ArrayList<XYPoint>();

        final VPTree<XYPoint> vpTree =
                new VPTree<XYPoint>(new XYDistanceFunction(), points);

        final List<XYPoint> nearestNeighbors =
                vpTree.getNearestNeighbors(new XYPoint(17.4, -22.2), 10);

        final List<XYPoint> pointsNearOrigin =
                vpTree.getAllWithinDistance(new XYPoint(0, 0), 4.5);
    }

}
