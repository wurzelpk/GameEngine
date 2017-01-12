package com.thekeirs.gameengine.system;

import com.thekeirs.gameengine.R;
import com.thekeirs.gameengine.framework.Rand;

/**
 * Created by wurzel on 12/30/16.
 */

public class FrogSprite extends Sprite {
    final private static String TAG = "FrogSprite";

    public FrogSprite(String name, float x, float y, float width, float height) {
        super(name, x, y, width, height);
        loadImage(R.drawable.frog);
    }

    @Override
    public void update() {
        if (!imageLoaded()) {
            return;
        }
        if (Rand.onceEvery(2.0f)) {
            int dist = Rand.between(30, 50);
            float direction = (float) Math.toRadians(Rand.between(0, 360));
            hop(dist, direction);
        }

        if (boundingRect.left < 0) {
            boundingRect.offsetTo(0, boundingRect.top);
        } else if (boundingRect.right > manager.mWorldScreenWidth) {
            boundingRect.offset(manager.mWorldScreenWidth - boundingRect.right, 0);
        }

        if (boundingRect.top < 0) {
            boundingRect.offsetTo(boundingRect.left, 0);
        } else if (boundingRect.bottom > manager.mWorldScreenHeight) {
            boundingRect.offset(0, manager.mWorldScreenHeight - boundingRect.bottom);
        }
    }

    @Override
    public void onTouch(float x, float y) {
        int dist = Rand.between(30, 50);

        hopToward(dist, manager.mWorldScreenWidth / 2.0f, manager.mWorldScreenHeight / 2.0f);

        manager.mBus.postMessage(new Message("ribbit"));
        Audio.play(R.raw.frog_croak);
    }
}
