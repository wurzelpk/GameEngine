package com.thekeirs.games.engine;

/**
 * Created by wurzel on 12/30/16.
 */

abstract public class GameLevel {
    protected GameObjectManager mManager;

    public GameLevel() {
    }

    public void setObjectManager(GameObjectManager manager) {
        mManager = manager;
    }

    public void setup() {
    }

    public void update() {
    }

    public boolean onAnyTouch(float x, float y) {
        return false;
    }

    public void onUnclaimedTouch(float x, float y) {
    }

    public boolean onAnyFling(float x, float y, float dx, float dy) {
        return false;
    }

    public void onUnclaimedFling(float x, float y, float dx, float dy) {
    }

    public void onUnclaimedScroll(float x, float y, float dx, float dy, boolean finished) {
    }

    public void finish() {
    }
}
