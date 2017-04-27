package com.thekeirs.games.engine;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <h1>The central class of the game engine, responsible for keeping track of all the game objects
 * helping them interact with the Android system.</h1>
 *
 * <p>
 *     One instance of this class is created by the main game Activity and provided to the
 *     {@link GameView} at startup time.
 * </p>
 */

public final class GameObjectManager implements IMessageClient, GameView.IRedrawService, GameView.IGameLogicService {
    final private String TAG = "GameObjectManager";
    private Map<String, GameObject> mObjects;
    private Scene mScene;
    private GameLevel mLevel;
    private GameLevel mNextLevel;
    private float mWorldScreenWidth = 1600f, mWorldScreenHeight = 900f;
    private Resources mResources;
    private SortedMap<Integer, List<GameObject>> mZOrder = new TreeMap<>();
    private Set<GameObject> solidThings = new HashSet<>();
    private float rightStickX, rightStickY;
    private float leftStickX, leftStickY;

    /**
     * An instance of the {@link MessageBus} that can be used to send messages, assuming we start using
     * that feature again.
     */
    public MessageBus mBus;

    /**
     * Called by the game's main Activity to set up all the game logic.
     *
     * @param mbus {@link MessageBus} for passing messages between game objects and infrastructure
     * @param res  pointer to the resources for this Activity so we can make them accesible to
     *             things that need them, in particular {@link Sprite} and
     *             {@link BackgroundImageScene}
     */
    public GameObjectManager(MessageBus mbus, Resources res) {
        mResources = res;

        mObjects = new HashMap<>();
        mBus = mbus;
        mBus.addClient(this);
        Images.setDefaultImage(R.drawable.default_image);
    }

    /**
     * Requests the game engine to move to a different game level after the
     * current update/draw cycle is complete.
     *
     * @param level a subclass of {@link GameLevel} customized for your game
     */
    public void setLevel(GameLevel level) {
        mNextLevel = level;
    }

    /**
     * Make the change to a new level effective.
     */
    private void gotoNextLevel() {
        if (mLevel != null) {
            mLevel.finish();
        }
        mObjects.clear();
        mZOrder.clear();
        mScene = null;

        mLevel = mNextLevel;
        mNextLevel = null;
        mLevel.setObjectManager(this);
        mLevel.setup();
    }

    /**
     * Returns the cached resources object.
     *
     * @return the resources associated with the Activity that this game is running with.
     */
    public Resources getResources() {
        return mResources;
    }


    /**
     * Adds a {@link GameObject} or a subclass into the list of objects being managed.
     * <p>
     *     Objects in this list will have their update() and draw() routines called at regular
     *     intervals, and will receive UI events (taps, swipes) that are within their boundary.
     * </p>
     * @param obj a {@link GameObject} or derived class to manage
     */
    public void addObject(GameObject obj) {
        if (mObjects.containsKey(obj)) {
            hint("Object added multiple times: " + obj.name,
                    "You added the same object to the game manager more than once.  This is " +
                            "probably not what you meant to do.  Are you calling addObject() " +
                            "from update() rather than setup()?");
        } else {
            GameObject older = getObjectByName(obj.name);
            if (older != null) {
                hint("Object with same name: " + obj.name,
                        "You created an object with the same name as an existing object.  " +
                                "This will replace the existing object.  If this was not what you " +
                                "meant to do, give the new object a unique name.");
                prepareToRemove(older);
            }
            obj.setManager(this);
            mObjects.put(obj.name, obj);
            addObjectToZOrder(obj);
            setObjectSolidity(obj, obj.isSolid());
        }
    }

    /**
     * Look up the first object that was registered with name {@code name}
     * @param name the name of the object to search for
     * @return the object, if one was found; otherwise {@code null}.
     */
    public GameObject getObjectByName(String name) {
        return mObjects.get(name);
    }

    /**
     * Return a list of all game objects whose names start with the given prefix.
     * <p>
     *     For example, if you register game objects named orc1, orc2, orc3 then you could
     *     get a list of these objects by calling with prefix = "orc".
     * </p>
     * @param prefix the string to match against the object names.
     * @return a {@link List} containing zero or more game objects.
     */
    public List<GameObject> getObjectsMatching(String prefix) {
        List<GameObject> objects = new ArrayList<>();

        for (GameObject obj : mObjects.values()) {
            if (obj.name.startsWith(prefix)) {
                objects.add(obj);
            }
        }
        return objects;
    }

    /**
     * Set the scene to be used as a background starting on the next draw() cycle.
     * <p>
     *     If you want the background to just be a solid color, pass in an instance of
     *     {@link SolidColorScene}.
     *     If you want the background to be an image, pass in an instance of
     *     {@link BackgroundImageScene}.
     * </p>
     * @param scene a subclass of {@link Scene} of the desired type
     */
    public void setScene(Scene scene) {
        this.mScene = scene;
    }

    /**
     * Sets the width and height of the game world in arbitrary units.
     * <p>
     *     This method should be called once at the start of a level to set the desired
     *     size of the world for that level.  Changing the world size after the level is running
     *     has undefined behavior.
     * </p>
     * <p>
     *     If you don't call this method, the default will be whatever size was configured by
     *     the previous level, or 1600x900 if it was never called.  1600x900 is a good choice
     *     if you don't have a reason to do otherwise.
     * </p>
     *
     * @param width  width of the world in units
     * @param height height of the world in units
     */
    public void setWorldScreenSize(float width, float height) {
        mWorldScreenWidth = width;
        mWorldScreenHeight = height;
    }

    /**
     * Ignore this for now.
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
    }

    /**
     * Find responsible party to handle a tap on the screen.
     * <p>
     *     Note that a 'tap' or 'touch' is a single short press on the screen without moving
     *     your finger sideways.
     * </p>
     *
     * <ul>
     *     <li>First the current level gets a shot via the onAnyTouch callback.  If the
     *     callback returns true, then it has handled the event and that's it.</li>
     *     <li>If the level didn't handle the touch, then we go through the list of
     *     game objects looking for one that overlaps the touched area.  If there is
     *     one, it gets the touch via its onTouch callback.</li>
     *     <li>Finally, if the event still hasn't been handled, we give the current level
     *     another shot by way of the onUnclaimedTouch callback.</li>
     * </ul>
     * @param x the x coordinate of the tap, in world units
     * @param y the y coordinate of the tap, in world units
     */
    private void checkTouchedObjects(float x, float y) {
        if (!mLevel.onAnyTouch(x, y)) {
            for (GameObject obj : mObjects.values()) {
                if (obj.contains(x, y)) {
                    obj.onTouch(x, y);
                    return;
                }
            }
            mLevel.onUnclaimedTouch(x, y);
        }
    }

    /**
     * Find responsible party to handle a "fling" action on the screen.
     * <p>
     *     Note that a 'fling' is a rapid swipe of the finger across the screen.  The velocity
     *     (both direction and speed) of the fling is available.
     * </p>
     * <ul>
     *     <li>First the current level gets a shot via the onAnyFling callback.  If the
     *     callback returns true, then it has handled the event and that's it.</li>
     *     <li>If the level didn't handle the fling, then we go through the list of
     *     game objects looking for one that overlaps the start of the fling action.  If there is
     *     one, it gets the fling via its onFling callback.</li>
     *     <li>Finally, if the event still hasn't been handled, we give the current level
     *     another shot by way of the onUnclaimedFling callback.</li>
     * </ul>
     * @param x the x coordinate of the tap, in world units
     * @param y the y coordinate of the tap, in world units
     */
    private void deliverFling(float x, float y, float dx, float dy) {
        if (!mLevel.onAnyFling(x, y, dx, dy)) {
            for (GameObject obj : mObjects.values()) {
                if (obj.contains(x, y)) {
                    obj.onFling(x, y, dx, dy);
                    return;
                }
            }
            mLevel.onUnclaimedFling(x, y, dx, dy);
        }
    }

    /**
     *     Deliver a scroll action to the current level
     *     <p>
     *         Currently the engine only provides scroll events to the level to handle.
     *     </p>
     *     <p>
     *         Note that a 'scroll' is a slow, deliberate movement of the finger across the screen
     *         with a defined starting and stopping point.  This is the kind of movement you might
     *         need for stretching the slingshot in a game like angry birds.
     *     </p>
     *     <p>
     *         Also important is the fact that you will get many of these callbacks with
     *         finished == false as the finger is moved around, and then one last callback
     *         with finished == true.  That way you can make sound effets or change the slingshot
     *         image on screen while the user decides just how far to pull it.
     *     </p>
     */
    private void deliverScroll(float x, float y, float dx, float dy, boolean finished) {
        // To deliver these to specific objects will take a bit more complicated tracking
        // of which object the scroll started on.  For now, just implement the simple one until
        // there's a real need for the more complicated.
        mLevel.onUnclaimedScroll(x, y, dx, dy, finished);
    }

    /**
     * Called by the game engine thread as events arrive.  Do not call this directly.
     *
     * @param e the event to deliver.
     */
    @Override
    public void onMotionEvent(GameView.UIEvent e) {
        // We receive the event with coordinates normalized 0.0-1.0f.  Scale to our world coords.
        float x = (e.event1 != null) ? e.event1.getX() * mWorldScreenWidth : 0.0f;
        float y = (e.event1 != null) ? e.event1.getY() * mWorldScreenHeight : 0.0f;
        switch (e.type) {
            case Down:
                Log.d(TAG, "Event ACTION_DOWN at " + x + "," + y);
                checkTouchedObjects(x, y);
                break;
            case Fling:
                deliverFling(x, y, e.dx * mWorldScreenWidth, e.dy * mWorldScreenHeight);
                break;
            case Scroll:
                deliverScroll(x, y, e.dx * mWorldScreenWidth, e.dy * mWorldScreenHeight,
                        e.event2.getAction() == MotionEvent.ACTION_UP);
                break;
            case ButtonDown:
                if (mLevel != null) {
                    mLevel.onButtonDown(e.keyCode);
                }
                break;
            case ButtonUp:
                if (mLevel != null) {
                    mLevel.onButtonUp(e.keyCode);
                }
                break;
            case Joystick:
                leftStickX = e.event1.getAxisValue(MotionEvent.AXIS_X);
                leftStickY = e.event1.getAxisValue(MotionEvent.AXIS_Y);
                rightStickX = e.event1.getAxisValue(MotionEvent.AXIS_Z);
                rightStickY = e.event1.getAxisValue(MotionEvent.AXIS_RZ);
                Log.d(TAG, "sticks: " + leftStickX + " " + leftStickY + " " + rightStickX + " " + rightStickY);
                Log.d(TAG, "count: " + e.event1.getHistorySize());
                break;
            default:
                break;
        }
    }

    /**
     * Called by the game engine thread to redraw all game objects.  Do not call this directly.
     *
     * @param canvas the current canvas object associated with our {@link SurfaceView}
     */
    @Override
    public void draw(Canvas canvas) {
        float xScale = canvas.getWidth() / mWorldScreenWidth;
        float yScale = canvas.getHeight() / mWorldScreenHeight;

        // Log.d(TAG, "draw");
        if (mScene != null) {
            mScene.draw(canvas);
        }
        for (List<GameObject> layer : mZOrder.values()) {
            for (GameObject obj : layer) {
                obj.draw(canvas, xScale, yScale);
            }
        }
    }

    /**
     * Called by the game engine thread to update all game objects.  Do not call this directly.
     *
     * @param millis number of milliseconds since the last world update.
     */
    @Override
    public void update(int millis) {
        if (mNextLevel != null) {
            gotoNextLevel();
        }

        // Log.d(TAG, "update");
        for (GameObject obj : mObjects.values()) {
            obj.update(millis);
        }
        mLevel.update(millis);

        // Not available until post-Marshmallow (API 24 or later):
        //     mObjects.entrySet().removeIf(o -> o.getValue().removalRequested());
        // So, we go old-school and use an iterator.
        Iterator<GameObject> it = mObjects.values().iterator();
        while (it.hasNext()) {
            GameObject obj = it.next();
            if (obj.removalRequested()) {
                prepareToRemove(obj);
                it.remove();
            }
        }
    }

    /**
     * Returns a rectangle (RectF) that can be used to check if something is on or off the screen
     * in world coordinates.
     */
    private RectF getWorldBoundary() {
        return new RectF(0, 0, mWorldScreenWidth, mWorldScreenHeight);
    }

    /**
     * Returns true if the game object is entirely within the boundaries of the screen at
     * the object's current location.
     *
     * @param obj the game object to check for on-screen-ness
     * @return {@code true} if the object is entirely on-screen
     */
    public boolean isFullyOnScreen(GameObject obj) {
        return getWorldBoundary().contains(obj.boundingRect);
    }

    /**
     * Returns true if the object is entirely outside the boundaries of the screen
     * at the object's current location.
     *
     * @param obj the game object to check for off-screen-ness
     * @return {@code true} if the object is entirely off-screen
     */
    public boolean isFullyOffScreen(GameObject obj) {
        return !RectF.intersects(obj.boundingRect, getWorldBoundary());
    }


    /**
     * Get the currently-configured world width in world units
     *
     * @return the world width in world units
     */
    public float getWorldScreenWidth() {
        return mWorldScreenWidth;
    }

    /**
     * Get the currently-configured world height in world units
     *
     * @return the world height in world units
     */
    public float getWorldScreenHeight() {
        return mWorldScreenHeight;
    }

    public void addObjectToZOrder(GameObject obj) {
        int z = obj.getZOrder();
        if (!mZOrder.containsKey(z)) {
            mZOrder.put(z, new ArrayList<GameObject>());
        }
        mZOrder.get(z).add(obj);
    }

    public void removeObjectFromZOrder(GameObject obj) {
        for (Map.Entry<Integer, List<GameObject>> entry : mZOrder.entrySet()) {
            int z = entry.getKey();
            List<GameObject> lst = entry.getValue();

            if (lst.contains(obj)) {
                lst.remove(obj);
                break;
            }
        }
    }

    public void updateObjectZOrder(GameObject obj) {
        removeObjectFromZOrder(obj);
        addObjectToZOrder(obj);
    }

    public void setObjectSolidity(GameObject obj, boolean isSolid) {
        if (isSolid) {
            solidThings.add(obj);
        } else {
            solidThings.remove(obj);
        }
    }

    public void prepareToRemove(GameObject obj) {
        removeObjectFromZOrder(obj);
        solidThings.remove(obj);
    }

    public Set<GameObject> getSolidObjects() {
        return solidThings;
    }

    public float getLeftStickX() {
        return leftStickX;
    }

    public float getLeftStickY() {
        return leftStickY;
    }

    public float getRightStickX() {
        return rightStickX;
    }

    public float getRightStickY() {
        return rightStickY;
    }

    public void hint(String summary, String description) {
        // TODO: Make some kind of pop-up to help game engine user see hints
    }
}
