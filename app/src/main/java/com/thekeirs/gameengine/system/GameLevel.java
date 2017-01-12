package com.thekeirs.gameengine.system;

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

    public void finish() {
    }
}
