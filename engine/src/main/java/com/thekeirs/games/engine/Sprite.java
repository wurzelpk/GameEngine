package com.thekeirs.games.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>Represents a {@link GameObject} that has an image associated with it, and a
 * location on the screen to draw that image (left x, upper y, width, height).
 *
 * <p>
 *     A Sprite has no behaviors on its own.  Rather than using a Sprite directly, in
 *     most cases you will create a subclass of Sprite that has the behaviors you need.
 * </p>
 *
 * <p>
 *     All coordinates and width/height are in world units.
 * </p>
 *
 */

public class Sprite extends GameObject {
    final static String DEFAULT_STATE_NAME = "default";
    protected String mMotionState = DEFAULT_STATE_NAME;
    protected Map<String, MotionSequence> mMotionSequences = new HashMap<>();
    private long timeInThisMotionState;

    /**
     * Returns a Sprite game object with the given name and location/size on the screen.
     * This constructor does not take an image_id, so nothing will be shown on the screen
     * for a Sprite created this way until {@link #loadImage} is called.
     *
     * @param name   GameObject name for this sprite
     * @param x      left edge of the sprite
     * @param y      top edge of the sprite
     * @param width  width of the sprite
     * @param height height of the sprite
     */
    public Sprite(String name, float x, float y, float width, float height) {
        super(name, new RectF(x, y, x + width, y + height));
    }

    /**
     * Returns a Sprite game object with the given name and location/size on the screen and
     * the given image.
     *
     * @param name   GameObject name for this sprite
     * @param x      left edge of the sprite
     * @param y      top edge of the sprite
     * @param width  width of the sprite
     * @param height height of the sprite
     * @param image_id   the ID of the image to display (eg {@code R.id.my_sprite})
     */
    public Sprite(String name, float x, float y, float width, float height, int image_id) {
        super(name, new RectF(x, y, x + width, y + height));
        setDefaultImage(image_id);
    }

    /**
     * Configures the Sprite to display the image with the given id.  This change takes effect
     * on the very next screen redraw.  A Sprite can change images as often as desired.
     *
     * @param id the ID of the image to display (eg {@literal R.id.my_sprite})
     */
    public void loadImage(int id) {
        setDefaultImage(id);
    }

    public void setDefaultImage(int id) {
        mMotionSequences.put(DEFAULT_STATE_NAME, new MotionSequence(id));
    }

    public void setMotionSequence(String motionStateName, int frameDuration, int... ids) {
        mMotionSequences.put(motionStateName, new MotionSequence(frameDuration, ids));
    }

    public void setMotionState(String motionState) {
        mMotionState = motionState;
        timeInThisMotionState = 0;
    }

    /**
     * Called when the user taps the screen at a location inside this sprite's bounding box.
     * @param x  horizontal coordinate in world units
     * @param y  vertical coordinate in world units
     */
    public void onTouch(float x, float y) {
        super.onTouch(x, y);
    }

    /**
     * Called when the user makes a fling motion on the screen, starting at a location inside this
     * sprite's bounding box.
     * @param x  horizontal coordinate in world units
     * @param y  vertical coordinate in world units
     */
    public void onFling(float x, float y, float dx, float dy) {
        super.onFling(x, y, dx, dy);
    }

    /**
     * Called once per screen refresh to do whatever modifications are desired to the sprite's
     * location, size, etc.
     * <p>
     * By default, sprites have no behavior - they just sit in one place.
     * Override this method if desired to make your sprite move.
     *
     * @param msec number of milliseconds since the last screen update.
     */
    @Override
    public void update(int msec) {
        super.update(msec);
        timeInThisMotionState += msec;
    }

    /**
     * Called by the Game Engine to draw this sprite onto the screen every frame.  Do not call
     * this routine yourself.
     *
     * @param c       Canvas object provided by the operating system
     * @param xScale  horizontal scale factor between world and screen coordinates
     * @param yScale  horizontal scale factor between world and screen coordinates
     */
    public void draw(Canvas c, float xScale, float yScale) {
        MotionSequence ms = mMotionSequences.get(mMotionState);
        if (ms == null || ms.resourceIds.isEmpty()) {
            // No image has been requested, or someone put is in a bad state.  Draw nothing.
            return;
        }

        // Loop through resourceIDs, spending specified time on each frame.
        int frameIndex = (int) (timeInThisMotionState / ms.msecPerFrame) % ms.resourceIds.size();
        int resourceID = ms.resourceIds.get(frameIndex);

        // -1 at end of sequence is a sentinal to go back to default motion state rather than
        // looping.
        if (resourceID == -1) {
            setMotionState(DEFAULT_STATE_NAME);
            // Call recursively to make sure everything gets checked again.
            draw(c, xScale, yScale);
            return;
        }
        Bitmap image = Images.get(ms.resourceIds.get(frameIndex));

        // Log.d("gameobject", "Drawing " + name + " at " + x + ", " + y);

        RectF screenRect = new RectF(boundingRect.left * xScale, boundingRect.top * yScale, boundingRect.right * xScale, boundingRect.bottom * yScale);

        c.drawBitmap(image, new Rect(0, 0, image.getWidth(), image.getHeight()), screenRect, null);
    }

    private class MotionSequence {
        public int msecPerFrame;
        public List<Integer> resourceIds;

        public MotionSequence(int id) {
            msecPerFrame = Integer.MAX_VALUE;
            resourceIds = new ArrayList<>();
            resourceIds.add(id);
        }

        public MotionSequence(int frameDurationMsec, int[] ids) {
            msecPerFrame = frameDurationMsec;
            resourceIds = new ArrayList<>(ids.length);
            for (int i : ids) {
                resourceIds.add(i);
            }
        }
    }
}
