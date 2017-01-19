package com.thekeirs.games.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by wurzel on 12/30/16.
 */

public class Sprite extends GameObject {
    private int mImageId = -1;
    protected Bitmap mImage;

    public Sprite(String name, float x, float y, float width, float height) {
        super(name, new RectF(x, y, x + width, y + height));
    }

    public Sprite(String name, float x, float y, float width, float height, int image_id) {
        super(name, new RectF(x, y, x + width, y + height));
        loadImage(image_id);
    }

    public void loadImage(int id) {
        if (id != mImageId) {
            mImageId = id;
            mImage = null;
        }
    }

    public boolean imageLoaded() {
        return mImage != null;
    }

    @Override
    public void update(int msec) {
        super.update(msec);
    }

    public void draw(Canvas c, float xScale, float yScale) {
        if (mImage == null) {
            mImage = BitmapFactory.decodeResource(manager.getResources(), mImageId);
        }
        // Log.d("gameobject", "Drawing " + name + " at " + x + ", " + y);

        RectF screenRect = new RectF(boundingRect.left * xScale, boundingRect.top * yScale, boundingRect.right * xScale, boundingRect.bottom * yScale);

        c.drawBitmap(mImage, new Rect(0, 0, mImage.getWidth(), mImage.getHeight()), screenRect, null);
    }
}
