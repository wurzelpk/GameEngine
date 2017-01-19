package com.thekeirs.games.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by wurzel on 1/5/17.
 */

public class BackgroundImageScene extends Scene {
    private Bitmap mImage;
    private GameObjectManager mManager;

    public BackgroundImageScene(GameObjectManager manager, int id) {
        super();
        mManager = manager;
        setImage(id);
    }

    public void setImage(int id) {
        mImage = BitmapFactory.decodeResource(mManager.getResources(), id);
    }

    @Override
    public void draw(Canvas c) {
        Rect src = new Rect(0, 0, mImage.getWidth(), mImage.getHeight());
        Rect cRect = new Rect(0, 0, c.getWidth(), c.getHeight());
        c.drawBitmap(mImage, src, cRect, null);
    }
}
