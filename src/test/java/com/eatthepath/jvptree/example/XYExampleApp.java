package com.eatthepath.jvptree.example;

import java.util.ArrayList;
import java.util.List;

import com.eatthepath.jvptree.VPTree;
import com.eatthepath.jvptree.VPTreeNode;

public class XYExampleApp {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		final List<XYPoint> points = new ArrayList<XYPoint>();
		
		points.add(new XYPoint(1.0, 2.0));
		points.add(new XYPoint(3.0, 2.0));
		points.add(new XYPoint(4.0, 8.0));
		points.add(new XYPoint(5.0, 2.0));
		points.add(new XYPoint(8.0, 9.0));
		points.add(new XYPoint(10.0, 3.0));
		points.add(new XYPoint(12.0, 1.0));
		points.add(new XYPoint(1.0, 8.0));
		points.add(new XYPoint(2.0, 4.0));
		points.add(new XYPoint(3.0, 1.0));

		final VPTree<XYPoint> vpTree = new VPTree<XYPoint>(new XYDistanceFunction(), points);

		final List<XYPoint> nearestNeighbors = vpTree.getNearestNeighbors(new XYPoint(17.4, -22.2), 10);

		final List<XYPoint> pointsNearOrigin = vpTree.getAllWithinDistance(new XYPoint(0, 0), 4.5);

		VPTreeNode<XYPoint> rootPoint = vpTree.getRootNode();

		System.out.println("Number of nodes:" + vpTree.size());

		System.out.println("Height of tree:" + vpTree.height(rootPoint));

		System.out.println(vpTree.toString());
	}

}
