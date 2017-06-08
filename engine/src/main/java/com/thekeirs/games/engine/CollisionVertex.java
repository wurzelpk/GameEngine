package com.thekeirs.games.engine;

/**
 * Vertex class for the collision subsystem.
 *
 * Created by Holden Matheson on 6/7/2017.
 */

public class CollisionVertex {

    double x;
    double y;

    public CollisionVertex(double i, double j){
        x = i;
        y = j;
    }

    public double projectOnto(CollisionVector axis, double offset_x, double offset_y){
        return (x + offset_x) * axis.x + (y + offset_y) * axis.y;
    }
}
