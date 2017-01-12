package com.thekeirs.gameengine.system;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wurzel on 12/30/16.
 */

public final class GameObjectManager implements IMessageClient, GameView.IRedrawService, GameView.IGameLogicService {
    final private String TAG = "GameObjectManager";
    private Map<String, GameObject> mObjects;
    private Scene mScene;
    private GameLevel mLevel;
    private GameLevel mNextLevel;
    public float mWorldScreenWidth = 1.0f, mWorldScreenHeight = 1.0f;

    private Resources mResources;
    public MessageBus mBus;

    public GameObjectManager(MessageBus mbus, Resources res) {
        mResources = res;

        mObjects = new HashMap<>();
        mBus = mbus;
        mBus.addClient(this);
    }

    public void setLevel(GameLevel level) {
        // Set up a level change for the next time we're not in the middle
        // of the update loop.
        mNextLevel = level;
    }

    private void gotoNextLevel() {
        if (mLevel != null) {
            mLevel.finish();
        }
        mObjects.clear();
        mScene = null;

        mLevel = mNextLevel;
        mNextLevel = null;
        mLevel.setObjectManager(this);
        mLevel.setup();
    }

    public Resources getResources() {
        return mResources;
    }

    public void addObject(GameObject obj) {
        obj.setManager(this);
        mObjects.put(obj.name, obj);
    }

    public GameObject getObjectByName(String name) {
        return mObjects.get(name);
    }


    public void setScene(Scene scene) {
        this.mScene = scene;
    }

    public void removeScene() {
        this.mScene = null;
    }

    public void setWorldScreenSize(float width, float height) {
        mWorldScreenWidth = width;
        mWorldScreenHeight = height;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.type.equals("touch")) {
            checkTouchedObjects(msg.x, msg.y);
        } else if (msg.type.equals("surface_ready")) {
        }
    }

    public void checkTouchedObjects(float x, float y) {
        for (GameObject obj : mObjects.values()) {
            if (obj.contains(x, y)) {
                obj.onTouch(x, y);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        float xScale = canvas.getWidth() / mWorldScreenWidth;
        float yScale = canvas.getHeight() / mWorldScreenHeight;

        // Log.d(TAG, "draw");
        if (mScene != null) {
            mScene.draw(canvas);
        }
        for (GameObject obj : mObjects.values()) {
            obj.draw(canvas, xScale, yScale);
        }
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        // We receive the event with coordinates normalized 0.0-1.0f.  Scale to our world coords.
        float x = e.getX() * mWorldScreenWidth;
        float y = e.getY() * mWorldScreenHeight;
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Event ACTION_DOWN at " + x + "," + y);
            checkTouchedObjects(x, y);
        }
    }

    @Override
    public void update(int millis) {
        if (mNextLevel != null) {
            gotoNextLevel();
        }

        // Log.d(TAG, "update");
        for (GameObject obj : mObjects.values()) {
            obj.update();
        }
    }
}
