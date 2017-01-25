package com.thekeirs.games.engine;

import java.util.Random;

/**
 * <h1>Helper functions for using random numbers in your game</h1>
 */

final public class Rand {
    final private static String TAG = "Rand";
    final private static int FPS = 60;

    private static Random mRand = new Random();

    /**
     * Returns a random number between {@code min} and {@code max}
     * <p>
     * Note that min and max can be negative, but max must be greater than min.
     * </p>
     *
     * @param min the minimum number to return
     * @param max the maximum number to return
     * @return integer in the range min..max
     */
    public static int between(int min, int max) {
        return min + mRand.nextInt(max - min + 1);
    }

    /**
     * Randomly returns true approximately once every {@code seconds} seconds.
     * @param seconds the average time between events returning true
     * @return usually false, but true randomly every {@code seconds}
     */
    public static boolean onceEvery(float seconds) {
        int ticks = (int) (seconds * FPS);
        int prob = mRand.nextInt(ticks);
        return prob == 0;
    }
}
