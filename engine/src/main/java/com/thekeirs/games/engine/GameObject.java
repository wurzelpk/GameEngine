package com.thekeirs.games.engine;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.List;

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

    private float dX, dY;
    private boolean feelsGravity;
    private boolean isBouncy;
    private boolean isSolid;
    private float ddX;
    private float ddY = 60.0f;  // Pixels/sec/sec
    private boolean autoDieOffscreen;
    private static int anonymousCount;

    /**
     * To be useful, every game object must register with the game object manager
     */
    protected GameObjectManager manager;

    private long mTimeOnScreen;     // Time we've been on screen in millisec
    private long mMaxTimeOnScreen;  // Time after which we will request removal
    private boolean mRemovalRequested;  // Flag indicating this sprite should be removed
    private int mZOrder;                // Order to draw this on the screen

    /**
     * Basic constructor.
     *
     * @param name   name of this sprite (used to look it up later).  If null or zero-length,
     *               a name like "anon-000123" will be automatically assigned.
     * @param extent initial area on the screen this sprite will occupy, in world units
     */
    public GameObject(String name, RectF extent) {
        this.name = (name != null && name.length() > 0) ? name : String.format("anon-%06d", anonymousCount++);
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
        float fracsec = msec / 1000.0f; // Velocities and gravity are in units of seconds

        mTimeOnScreen += msec;
        if (mMaxTimeOnScreen > 0 && mTimeOnScreen > mMaxTimeOnScreen) {
            requestRemoval();
        }

        if (feelsGravity) {
            dX += ddX * fracsec;
            dY += ddY * fracsec;
        }
        if (dX != 0.0f || dY != 0.0f) {
            PositionUpdate posup = new PositionUpdate(
                    this.getX(), this.getY(),
                    this.getX() + dX * fracsec, this.getY() + dY * fracsec);

            interactWithSolids(posup);
            setXY(posup.newx, posup.newy);
        }

        if (autoDieOffscreen && isFullyOffScreen()) {
            requestRemoval();
        }
    }

    protected class PositionUpdate {
        public float oldx, oldy;
        public float newx, newy;

        public PositionUpdate(float ox, float oy, float nx, float ny) {
            oldx = ox;
            oldy = oy;
            newx = nx;
            newy = ny;
        }
    }

    private void interactWithSolids(PositionUpdate posup) {
        for (GameObject obj : manager.getSolidObjects()) {
            if (!intersects(obj)) {
                continue;
            }
            // If we're moving downwards and our center is above the top of the solid object
            if (dY > 0 && posup.oldy < obj.boundingRect.top) {
                posup.newy = obj.boundingRect.top - boundingRect.height() / 2.0f;
                dY = (obj.isBouncy) ? -dY : 0;
                onCollision(obj);
            }
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
     * Get the horizontal location of the center of this sprite.
     *
     * @return x value in world units
     */
    public float getX() {
        return boundingRect.centerX();
    }

    /**
     * Get the vertical location of the center of this sprite.
     *
     * @return y value in world units
     */
    public float getY() {
        return boundingRect.centerY();
    }

    /**
     * Get the width of the bounding box for this sprite.
     *
     * @return width in world units
     */
    public float getWidth() {
        return boundingRect.width();
    }

    /**
     * Get the height of the bounding box for this sprite.
     *
     * @return height in world units
     */
    public float getHeight() {
        return boundingRect.height();
    }

    /**
     * Set the sprite's center X and Y and its width and height all in one go.
     *
     * @param centerX horizontal coordinate of center of sprite in world units
     * @param centerY vertical coordinate of center of sprite in world units
     * @param width   width of sprite in world units
     * @param height  height of sprite in world units
     */
    public void setXYWH(float centerX, float centerY, float width, float height) {
        boundingRect.set(centerX - width / 2, centerY - height / 2,
                centerX + width / 2, centerY + height / 2);
    }

    /**
     * Instantly move the game object so its center is at the given coordinates.
     *
     * @param x horizontal coordinate of the destination in world units
     * @param y vertical coordinate of the destination in world units
     */
    public void setXY(float x, float y) {
        setXYWH(x, y, boundingRect.width(), boundingRect.height());
    }

    /**
     * Instantly move the game object so the center is at a given X location without
     * changing the Y location.
     *
     * @param x horizontal coordinate of the destination in world units
     */
    public void setX(float x) {
        setXYWH(x, boundingRect.centerY(), boundingRect.width(), boundingRect.height());
    }

    /**
     * Instantly move the game object so the center is at a given Y location without
     * changing the X location.
     *
     * @param y vertical coordinate of the destination in world units
     */
    public void setY(float y) {
        setXYWH(boundingRect.centerX(), y, boundingRect.width(), boundingRect.height());
    }

    /**
     * Set the width of the sprite without changing the location of its center.
     *
     * @param width new width in world units
     */
    public void setWidth(float width) {
        setXYWH(boundingRect.centerX(), boundingRect.centerY(), width, boundingRect.height());
    }

    /**
     * Set the height of the sprite without changing the location of its center.
     *
     * @param height new height in world units
     */
    public void setHeight(float height) {
        setXYWH(boundingRect.centerX(), boundingRect.centerY(), boundingRect.width(), height);
    }


    /**
     * Instantly move the game object so its upper left is at the given coordinates.
     *
     * Normally we use the center coordinates of a sprite but occasionally it's handy to place
     * one based on the upper left corner.
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
     * Checks if this game object's bounding area intersects any of a given list of game objects.
     *
     * @param objects list of game objects to check against
     * @return {@code null} if no intersection, otherwise the first game object it intersected
     */
    final public GameObject intersectsAny(List<? extends GameObject> objects) {
        for (GameObject obj : objects) {
            if (RectF.intersects(obj.boundingRect, boundingRect)) {
                return obj;
            }
        }
        return null;
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

    /**
     * Sets the order for this object to be drawn on the screen during each screen redraw loop.
     * Higher numbers are drawn later so they appear to be "on top" of earlier items.
     *
     * @param zOrder the Z Order for this object. Can be any integer
     */
    final public void setZOrder(int zOrder) {
        mZOrder = zOrder;

        // If we're already being managed, make sure the manager knows about the update.
        if (manager != null) {
            manager.updateObjectZOrder(this);
        }
    }

    /**
     * Gets the Z order of this object.  Higher numbers are drawn on top of lower numbers.
     *
     * @return Object's Z Order.
     */
    final public int getZOrder() {
        return mZOrder;
    }


    /**
     * Gets the horizontal velocity of this object
     *
     * @return velocity in pixels per second
     */
    public float getdX() {
        return dX;
    }

    /**
     * Sets the horizontal velocity of this object
     *
     * @param dX velocity in pixels per second
     */
    public void setdX(float dX) {
        this.dX = dX;
    }

    /**
     * Gets the vertical velocity of this object
     *
     * @return velocity in pixels per second
     */
    public float getdY() {
        return dY;
    }

    /**
     * Sets the vertical velocity of this object
     *
     * @param dY velocity in pixels per second
     */
    public void setdY(float dY) {
        this.dY = dY;
    }

    /**
     * Checks whether this object has been configured to be affected by gravity
     *
     * @return {@code true} if this object feels gravity
     */
    public boolean feelsGravity() {
        return feelsGravity;
    }

    /**
     * Sets whether this object is affected by gravity
     *
     * @param feelsGravity {@code true} to make this object respond to gravity
     */
    public void setFeelsGravity(boolean feelsGravity) {
        this.feelsGravity = feelsGravity;
    }

    /**
     * Sets whether objects that collide with this one bounce off or just stick.
     *
     * @return {@code true} if this object is bouncy
     */
    public boolean isBouncy() {
        return isBouncy;
    }

    /**
     * Checks whether this object is configured so that other colliding objects will bounce off
     * instead of sticking.
     *
     * @param bouncy {@code true} to make it bouncy
     */
    public void setBouncy(boolean bouncy) {
        isSolid = true;
        isBouncy = bouncy;
    }

    /**
     * Checks whether objects that collide with this one will be affected or will simply pass
     * through.  See also isBouncy/setBouncy to configure what happens in a collision.
     *
     * @return {@code true} to make this object have an effect on colliding objects
     */
    public boolean isSolid() {
        return isSolid;
    }

    /**
     * Configures whether objects that collide with this one will be affected or will simply pass
     * through.  See also isBouncy/setBouncy to configure what happens in a collision.
     *
     * @param solid {@code true} to make it solid
     */
    public void setSolid(boolean solid) {
        if (manager != null) {
            manager.setObjectSolidity(this, solid);
        }
        isSolid = solid;
    }

    /**
     * Get this object's horizontal gravitational acceleration
     *
     * @return acceleration in pixels/second/second
     */
    public float getAccelX() {
        return ddX;
    }

    /**
     * Set this object's horizontal gravitational acceleration.  Typical ranges are 1.0-10.0
     *
     * @param accelX acceleration in pixels/second/second
     */
    public void setAccelX(float accelX) {
        this.ddX = accelX;
    }

    /**
     * Get this object's vertical gravitational acceleration
     *
     * @return acceleration in pixels/second/second
     */
    public float getAccelY() {
        return ddY;
    }

    /**
     * Set this object's vertical gravitational acceleration.  Typical ranges are 1.0-10.0
     *
     * @param accelY acceleration in pixels/second/second
     */
    public void setAccelY(float accelY) {
        this.ddY = accelY;
    }

    /**
     * Checks if this object is configured to automatically be destroyed when fully offscreen.
     *
     * @return {@code true} if this object will die when no longer within screen boundaries
     */
    public Boolean getAutoDieOffscreen() {
        return autoDieOffscreen;
    }

    /**
     * Configures whether this object will automatically be destroyed when fully offscreen.
     *
     * @param autoDieOffscreen {@code true} to make the object die
     */
    public void setAutoDieOffscreen(Boolean autoDieOffscreen) {
        this.autoDieOffscreen = autoDieOffscreen;
    }

    /**
     * Called when this objects collides with another one in the physics engine
     *
     * @param other The other object it collided with
     */
    public void onCollision(GameObject other) {
    }
}
