package com.thekeirs.games.engine;

/**
 * <h1>Abstract superclass to represent one level in a game</h1>
 * <p>
 *     For our purposes, a level is loosely defined by having a background, some number of sprites
 *     that stick around for a while, and some kind of goal to get to the end of the level.  To make
 *     things simple, we treat an opening screen as its own level as well (with the goal being to
 *     choose what game to play!)
 * </p>
 * <p>
 *     When a level is complete, it can ask the game manager to start up another level with the
 *     {@link GameObjectManager#setLevel(GameLevel)} call.  That new level can be a different
 *     subclass of GameLevel, or the same subclass but with a different (presumably more
 *     difficult) configuration.
 * </p>
 */

abstract public class GameLevel {
    /**
     * Reference to the game object manager currently in charge.
     * <p>
     * This is used by the game level to configure the world size, set a scene, and
     * add sprites or other game objects to be managed. (updated and drawn on the screen).
     * </p>
     */
    protected GameObjectManager mManager;

    /**
     * This constructor does nothing.  Make your own constructor for your subclass.
     */
    public GameLevel() {
    }

    /**
     * Called by the object manager to configure this level.  Do not call this directly.
     * @param manager
     */
    public void setObjectManager(GameObjectManager manager) {
        mManager = manager;
    }

    /**
     * Your setup method is where all your work for setting up a new level should go.
     */
    public void setup() {
    }

    /**
     * You can choose how much of your game logic is done by individual sprites and how much
     * is done centrally here in your override of the update method.
     *
     * @param millis number of milliseconds since the previous game engine update.
     */
    public void update(int millis) {
    }

    /**
     * Override this routine if you want your central game logic to handle ALL screen touches.
     * <p>
     *     Any time the screen is touched, this method will be called first to give your central
     *     game logic a shot at doing something about the touch.  If you want to accept this event
     *     and keep it from being passed to any game objects, return true.  If you return false,
     *     every game object will be compared for overlap.
     * </p>
     * @param x horizontal coordinate of the point tapped, in world units
     * @param y vertical coordinate of the point tapped, in world units
     * @return {@code true} to swallow the event, {@code false} to pass it on to game objects
     */
    public boolean onAnyTouch(float x, float y) {
        return false;
    }

    /**
     * Override this routine if you want your central game logic to handle screen touches that
     * don't hit any game objects.
     * <p>
     *     After all the game objects have been checked for overlap with a touch, if none of them
     *     was a 'hit', then this routine will be called.  This might be useful, for example,
     *     to play a sound indicating they didn't tap the right place, or to pop up instructions
     *     on how to use the controls for the game.
     * </p>
     * @param x horizontal coordinate of the point tapped, in world units
     * @param y vertical coordinate of the point tapped, in world units
     */
    public void onUnclaimedTouch(float x, float y) {
    }

    /**
     * Override this routine if you want your central game logic to handle ALL screen fling actions.
     * <p>
     *     Any time the user flings, this method will be called first to give your central
     *     game logic a shot at doing something about the fling.  If you want to accept this event
     *     and keep it from being passed to any game objects, return true.  If you return false,
     *     every game object will be compared for overlap.
     * </p>
     * @param x horizontal coordinate of the point the fling started, in world units
     * @param y vertical coordinate of the point the fling started, in world units
     * @return {@code true} to swallow the event, {@code false} to pass it on to game objects
     */
    public boolean onAnyFling(float x, float y, float dx, float dy) {
        return false;
    }

    /**
     * Override this routine if you want your central game logic to handle screen fling actions that
     * don't start on a game object.
     * <p>
     *     After all the game objects have been checked for overlap with a fling, if none of them
     *     was a 'hit', then this routine will be called.  This might be useful, for example,
     *     if your game simply uses flings at the side of the screen to contol your character,
     *     so you don't care where the fling action happened but simply about the direction.
     * </p>
     * @param x horizontal coordinate of the point the fling started, in world units
     * @param y vertical coordinate of the point the fling started, in world units
     * @param dx horizontal velocity of the fling
     * @param dy vertical velocity of the fling
     */
    public void onUnclaimedFling(float x, float y, float dx, float dy) {
    }

    /**
     * Override this routine if you want to detect "scroll" events on the screen.
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
     *
     * @param x horizontal coordinate of the point the scroll started, in world units
     * @param y vertical coordinate of the point the scroll started, in world units
     * @param dx horizontal distance the user's finger has dragged
     * @param dy vertical distance the user's finger has dragged
     * @param finished {@code true} if this is the final event (user just lifted their finger)
     */
    public void onUnclaimedScroll(float x, float y, float dx, float dy, boolean finished) {
    }

    /**
     * Called by the game object manager when this level is about to be destroyed before starting
     * a new level.
     * <p>
     *     If you have any important cleanup code to do (for example, stopping the background music
     *     for one level before beginning another, once we support that) then override this
     *     method and put that code there.
     * </p>
     */
    public void finish() {
    }
}
