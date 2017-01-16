package com.thekeirs.gameengine.system;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by wurzel on 12/30/16.
 */

abstract public class GameObject {
    final static private String TAG = "GameObject";
    public String name;
    protected GameObjectManager manager;
    public RectF boundingRect;
    private long mMaxTimeOnScreen;
    protected long mTimeOnScreen;
    private boolean mRemovalRequested;

    public GameObject(String name, RectF extent) {
        this.name = name;
        this.boundingRect = extent;
    }

    public void setManager(GameObjectManager manager) {
        this.manager = manager;
    }

    public void update(int msec) {
        mTimeOnScreen += msec;
        if (mMaxTimeOnScreen > 0 && mTimeOnScreen > mMaxTimeOnScreen) {
            requestRemoval();
        }
    }

    public void onTouch(float x, float y) {
    }

    public void setMaxTimeOnScreen(int msec) {
        mMaxTimeOnScreen = msec;
    }

    public long getTimeOnScreen() {
        return mTimeOnScreen;
    }

    abstract public void draw(Canvas c, float xScale, float yScale);

    public boolean contains(float x, float y) {
        return boundingRect.contains(x, y);
    }

    protected void hop(float distance, float direction) {
        float dx = (float) (distance * Math.cos(direction));
        float dy = (float) (distance * Math.sin(direction));
        boundingRect.offset(dx, dy);
    }

    protected void hopToward(float distance, float destx, float desty) {
        float dx = destx - boundingRect.centerX();
        float dy = desty - boundingRect.centerY();
        float totaldist = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < totaldist) {
            boundingRect.offset(dx * distance / totaldist, dy * distance / totaldist);
        } else {
            boundingRect.offsetTo(destx, desty);
        }
    }

    final public boolean isInside(GameObject other) {
        return other.boundingRect.contains(boundingRect);
    }

    final public boolean intersects(GameObject other) {
        return RectF.intersects(other.boundingRect, boundingRect);
    }


    final public void requestRemoval() {
        mRemovalRequested = true;
    }

    final public boolean removalRequested() {
        return mRemovalRequested;
    }
}
