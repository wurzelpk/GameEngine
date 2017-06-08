package com.thekeirs.games.engine;

import android.util.Log;

/**
 * Projections class for use with the collisions subsystem.
 *
 * Created by Holden Matheson on 6/8/2017.
 */

public class CollisionProjection {
    double min;
    double max;

    public CollisionProjection(double i, double j){
        min = i;
        max = j;
    }

    public boolean overlaps(CollisionProjection other){
        if (isBetween(min, other.min, other.max) ||
                isBetween(max, other.min, other.max) ||
                isBetween(other.min, min, max) ||
                isBetween(other.max, min, max)){
            return true;
        }
        Log.d("SAT", "Separating axis found, no collision!");
        return false;
    }

    public static boolean isBetween(double value, double min, double max)
    {
        return((value > min) && (value < max));
    }
}
