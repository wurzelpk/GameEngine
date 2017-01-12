package com.thekeirs.gameengine.framework;

import java.util.Random;

/**
 * Created by wurzel on 12/30/16.
 */

final public class Rand {
    final static private String TAG = "Rand";
    final static int FPS = 60;

    private static Random mRand = new Random();

    public static int between(int min, int max) {
        return min + mRand.nextInt(max - min + 1);
    }

    public static boolean onceEvery(float seconds) {
        int ticks = (int) (seconds * FPS);
        int prob = mRand.nextInt(ticks);
        return prob == 0;
    }
}
