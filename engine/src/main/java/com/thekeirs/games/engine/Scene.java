package com.thekeirs.games.engine;

import android.graphics.Canvas;

/**
 * <h1>An abstract scene (background for a level) class</h1>
 * <p>
 *     A scene is the background behind any moving sprites.  This class cannot be used directly.
 *     See {@link SolidColorScene} and {@link BackgroundImageScene}
 * </p>
 */

abstract public class Scene {
    abstract public void draw(Canvas canvas);
}
