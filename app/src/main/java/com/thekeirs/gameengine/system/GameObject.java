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

    public GameObject(String name, RectF extent) {
        this.name = name;
        this.boundingRect = extent;
    }

    public void setManager(GameObjectManager manager) {
        this.manager = manager;

    }

    public void update() {
    }

    public void onTouch(float x, float y) {
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
}
