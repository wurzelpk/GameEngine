package com.thekeirs.games.engine;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    public List<GameObject> getObjectsMatching(String prefix) {
        List<GameObject> objects = new ArrayList<>();

        for (GameObject obj : mObjects.values()) {
            if (obj.name.startsWith(prefix)) {
                objects.add(obj);
            }
        }
        return objects;
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

    private void deliverScroll(float x, float y, float dx, float dy, boolean finished) {
        // To deliver these to specific objects will take a bit more complicated tracking
        // of which object the scroll started on.  For now, just implement the simple one until
        // there's a real need for the more complicated.
        mLevel.onUnclaimedScroll(x, y, dx, dy, finished);
    }

    @Override
    public void onMotionEvent(GameView.UIEvent e) {
        // We receive the event with coordinates normalized 0.0-1.0f.  Scale to our world coords.
        float x = e.event1.getX() * mWorldScreenWidth;
        float y = e.event1.getY() * mWorldScreenHeight;
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
            default:
                break;
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
    public void update(int millis) {
        if (mNextLevel != null) {
            gotoNextLevel();
        }

        // Log.d(TAG, "update");
        for (GameObject obj : mObjects.values()) {
            obj.update(16);   // TODO: For now, cheating and assuming 60fps
        }
        mLevel.update();

        // Not available until post-Marshmallow (API 24 or later):
        //     mObjects.entrySet().removeIf(o -> o.getValue().removalRequested());
        // So, we go old-school and use an iterator.
        Iterator<GameObject> it = mObjects.values().iterator();
        while (it.hasNext()) {
            GameObject obj = it.next();
            if (obj.removalRequested()) {
                it.remove();
            }
        }
    }
}
