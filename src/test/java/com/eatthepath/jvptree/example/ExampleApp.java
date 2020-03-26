package com.eatthepath.jvptree.example;

import java.util.ArrayList;
import java.util.List;

import com.eatthepath.jvptree.VPTree;

public class ExampleApp {

    @SuppressWarnings("unused")
    public static void main(final String[] args) {
        final CartesianPoint playerPosition = new CartesianPoint() {
            public double getX() {
                return 20;
            }

            public double getY() {
                return 10;
            }
        };

        final List<SpaceInvader> enemies = new ArrayList<SpaceInvader>();

        final VPTree<CartesianPoint, SpaceInvader> vpTree =
                new VPTree<CartesianPoint, SpaceInvader>(
                        new CartesianDistanceFunction(), enemies);

        final List<SpaceInvader> nearestEnemies =
                vpTree.getNearestNeighbors(playerPosition, 10);

        final List<SpaceInvader> enemiesWithinFiringRange =
                vpTree.getAllWithinDistance(playerPosition, 4.5);
    }

}
