package com.thekeirs.games.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

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
    /**
     * Holds the resource ID (R.drawable.* or R.raw.*) for this sprite's image
     */
    protected int mImageId = -1;

    /**
     * Stores the bitmap image to be displayed by this sprite
     */
    protected Bitmap mImage;

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
        loadImage(image_id);
    }

    /**
     * Configures the Sprite to display the image with the given id.  This change takes effect
     * on the very next screen redraw.  A Sprite can change images as often as desired.
     *
     * @param id the ID of the image to display (eg {@literal R.id.my_sprite})
     */
    public void loadImage(int id) {
        if (id != mImageId) {
            mImageId = id;
            mImage = null;
        }
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
        if (mImageId < 0) {
            // No image has been requested, so don't draw anything.
            return;
        }
        if (mImage == null) {
            mImage = BitmapFactory.decodeResource(manager.getResources(), mImageId);
        }
        // Log.d("gameobject", "Drawing " + name + " at " + x + ", " + y);

        RectF screenRect = new RectF(boundingRect.left * xScale, boundingRect.top * yScale, boundingRect.right * xScale, boundingRect.bottom * yScale);

        c.drawBitmap(mImage, new Rect(0, 0, mImage.getWidth(), mImage.getHeight()), screenRect, null);
    }
}
