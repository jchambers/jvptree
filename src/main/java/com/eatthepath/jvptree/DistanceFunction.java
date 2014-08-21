package com.eatthepath.jvptree;

public interface DistanceFunction<T> {
	double getDistance(T firstPoint, T secondPoint);
}
