package com.thekeirs.games.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * <h1>Gives your level a background scene that is all one solid color</h1>
 */
public class SolidColorScene extends Scene {
    private Paint mPaint;

    /**
     * Creates a new SolidColorScene object with the specified color.
     * <p>
     * This object should be passed into the {@link GameObjectManager#setScene(Scene)}
     * method in your GameLevel's setup code.
     * </p>
     *
     * @param color_str a color string of the form "#f29833", for example from Google's color picker
     */
    public SolidColorScene(String color_str) {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor(color_str));
    }

    /**
     * Called by the game engine to draw this background onto the screen each frame.
     *
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
    }
}
