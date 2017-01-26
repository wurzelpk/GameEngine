package com.thekeirs.games.engine;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * <h1>Abstract superclass to represent a game object in the game world</h1>
 * <p>
 *     This class is abstract so it is never used directly to create instances.  The
 *     {@link Sprite} class is a subclass that also manages an image and is thus usable
 *     in a game.
 * </p>
 */

abstract public class GameObject {
    final static private String TAG = "GameObject";
    /**
     * The game object's name, used to look up an object in the game manager, or determine which
     * object something has collided with.
     */
    public String name;

    /**
     * Tracks the upper left and lower right coordinates of a box that surrounds this object
     * in world coordinates.
     */
    public RectF boundingRect;

    /**
     * To be useful, every game object must register with the game object manager
     */
    protected GameObjectManager manager;

    private long mTimeOnScreen;     // Time we've been on screen in millisec
    private long mMaxTimeOnScreen;  // Time after which we will request removal
    private boolean mRemovalRequested;  // Flag indicating this sprite should be removed

    /**
     * Basic constructor.
     *
     * @param name   name of this sprite (used to look it up later)
     * @param extent initial area on the screen this sprite will occupy, in world units
     */
    public GameObject(String name, RectF extent) {
        this.name = name;
        this.boundingRect = extent;
    }

    /**
     * Called by the {@link GameObjectManager} to be sure this game object knows who its
     * manager is.  This method is called automatically - do not use.
     * @param manager the current game object manager
     */
    public void setManager(GameObjectManager manager) {
        this.manager = manager;
    }


    /**
     * Called once per screen refresh by the {@link GameObjectManager} to update the size,
     * location, or other attributes of this game object as needed by the game's logic.
     * <p>
     *     If you are creating a subclass of {@link GameObject} or {@link Sprite} be sure to call
     *     {@code super.update()} in your {@code update()} method so that the object removal
     *     logic will work correctly.
     * </p>
     * @param msec  number of millseconds since the previous update call.
     *              Typically 16msec at 60Hz screen refresh.
     */
    public void update(int msec) {
        mTimeOnScreen += msec;
        if (mMaxTimeOnScreen > 0 && mTimeOnScreen > mMaxTimeOnScreen) {
            requestRemoval();
        }
    }

    /**
     * Called when the user taps the screen at a location inside this game object's bounding box.
     * @param x  horizontal coordinate in world units
     * @param y  vertical coordinate in world units
     */
    public void onTouch(float x, float y) {
    }

    /**
     * Called when the user makes a fling motion on the screen, starting at a location inside this
     * game object's bounding box.
     * @param x  horizontal coordinate in world units
     * @param y  vertical coordinate in world units
     */
    public void onFling(float x, float y, float dx, float dy) {
    }

    /**
     * Configure this game object to self-destruct after a given number of milliseconds on the
     * screen.
     * <p>
     *     When the game engine properly supports pausing/unpausing the game, this count should also
     *     "freeze" in place and resume counting again automatically.
     * </p>
     *
     * @param msec number of milliseconds until this game object requests automatic removal
     */
    public void setMaxTimeOnScreen(int msec) {
        mMaxTimeOnScreen = msec;
    }

    /**
     * Check how long a game object has been alive.
     *
     * @return number of milliseconds this game object has been on screen/managed by the manager.
     */
    public long getTimeOnScreen() {
        return mTimeOnScreen;
    }

    /**
     * Must be implemented by any subclasses to do the work of actually drawing the game object
     * onto the screen.
     *
     * @param c   the {@link Canvas} object the game is currently being drawn onto
     * @param xScale the horizontal scaling factor between world coordinates and screen coordinates
     * @param yScale the vertical scaling factor between world coordinates and screen coordinates
     */
    abstract public void draw(Canvas c, float xScale, float yScale);

    /**
     * Checks if this game object's bounding box includes a given point.
     * @param x horizontal coordinate of the point to check, in world units
     * @param y vertical coofdinate of the point to check, in world units
     * @return true if the point is inside this object's bounding box
     */
    public boolean contains(float x, float y) {
        return boundingRect.contains(x, y);
    }

    /**
     * Instantaneously move the game object a given distance in the given direction.
     *
     * @param distance  distance to hop, in world units
     * @param direction direction to hop in degrees counter-clockwise from the X axis
     */
    public void hop(float distance, float direction) {
        double radians = Math.toRadians(direction);
        float dx = (float) (distance * Math.cos(radians));
        float dy = (float) (distance * Math.sin(radians));
        boundingRect.offset(dx, dy);
    }

    /**
     * Adjust the game object's position by a given dx and dy offset
     * @param dx horizontal offset in world coordinates
     * @param dy vertical offset in world coordinates
     */
    public void moveBy(float dx, float dy) {
        boundingRect.offset(dx, dy);
    }

    /**
     * Instantaneously hop the sprite a given distance in the direction of another point in
     * world space.
     * <p>
     * For instance, if you want bullets from a flying saucer to always fly towards your
     * player's ship, this routine might be helpful.
     * </p>
     *
     * @param distance distance in world units to move the game object
     * @param destx    horizontal coordinate of the point to move towards
     * @param desty    vertical coordinate of the point to move towards
     */
    public void hopToward(float distance, float destx, float desty) {
        float dx = destx - boundingRect.centerX();
        float dy = desty - boundingRect.centerY();
        float totaldist = (float) Math.hypot(dx, dy);
        if (distance < totaldist) {
            boundingRect.offset(dx * distance / totaldist, dy * distance / totaldist);
        } else {
            boundingRect.offsetTo(destx, desty);
        }
    }

    /**
     * Instantly move the game object so its center is at the given coordinates.
     *
     * @param x horizontal coordinate of the destination in world units
     * @param y vertical coordinate of the destination in world units
     */
    public void setCenterXY(float x, float y) {
        boundingRect.offset(x - boundingRect.centerX(), y - boundingRect.centerY());
    }

    /**
     * Instantly move the game object so its upper left is at the given coordinates.
     *
     * @param x horizontal coordinate of the destination in world units
     * @param y vertical coordinate of the destination in world units
     */
    public void setUpperLeftXY(float x, float y) {
        boundingRect.offset(x - boundingRect.left, y - boundingRect.top);
    }

    /**
     * Checks if this game object's bounding rectangle is entirely inside of another object.
     *
     * @param other the game object to test if this object is inside of.
     * @return true if this object is entirely inside {@code other}
     */
    final public boolean isInside(GameObject other) {
        return other.boundingRect.contains(boundingRect);
    }

    /**
     * Checks if this game object's bounding area intersects at all with another game object.
     *
     * @param other the game object to test if this object is touching
     * @return true if this object intersects/touches {@code other}
     */
    final public boolean intersects(GameObject other) {
        return RectF.intersects(other.boundingRect, boundingRect);
    }

    /**
     * Returns true if this game object is entirely within the boundaries of the screen at
     * the object's current location.
     *
     * @return {@code true} if the object is entirely on-screen
     */
    public boolean isFullyOnScreen() {
        return manager.isFullyOnScreen(this);
    }

    /**
     * Returns true if this object is entirely outside the boundaries of the screen
     * at the object's current location.
     *
     * @return {@code true} if the object is entirely off-screen
     */
    public boolean isFullyOffScreen() {
        return manager.isFullyOffScreen(this);
    }


    /**
     * Request that the game object manager remove this sprite after this update loop is complete.
     */
    final public void requestRemoval() {
        mRemovalRequested = true;
    }

    /**
     * Check if this game object is scheduled for removal
     *
     * @return true if this object will be removed.
     */
    final public boolean removalRequested() {
        return mRemovalRequested;
    }
}
