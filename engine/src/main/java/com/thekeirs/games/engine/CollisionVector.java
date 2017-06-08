package com.thekeirs.games.engine;

import static java.lang.Math.abs;

/**
 * Vector class for use with the collisions subsystem.
 *
 * Created by Holden Matheson on 6/7/2017.
 */

public class CollisionVector {
    private final double epsilon = 0.001;

    double x;
    double y;

    public CollisionVector(double i, double j){
        x = i;
        y = j;
    }

    public CollisionVector(CollisionVertex i, CollisionVertex j){
        x = j.x - i.x;
        y = j.y - i.y;
    }

    public CollisionVector perpendicular(){
        return new CollisionVector(y, -x);
    }

    public boolean isParallel(CollisionVector other){
        double pz = x * other.y - y * other.x;
        return abs(pz) < epsilon;
    }
}