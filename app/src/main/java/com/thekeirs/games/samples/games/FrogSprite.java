package com.thekeirs.games.samples.games;

import com.thekeirs.games.samples.R;
import com.thekeirs.games.engine.Rand;
import com.thekeirs.games.engine.Audio;
import com.thekeirs.games.engine.Sprite;

/**
 * Created by wurzel on 2016-Dec-20
 *
 * This class uses Sprite (which draws an image on the screen at a requested location)
 * and adds two additional behaviors:
 *    in update() the frog may randomly hop around every once in a while
 *    in onTouch() the frog emits a noise and hops in a certain direction when tapped on the screen
 */

public class FrogSprite extends Sprite {
    final private static String TAG = "FrogSprite";
    final private static int TAPS_BEFORE_BECOMING_PRINCE = 7;
    private int timesTapped;
    private float targetX, targetY;

    public FrogSprite(String name, float x, float y, float width, float height) {
        super(name, x, y, width, height);
        loadImage(R.drawable.frog);
    }

    public void setTargetLocation(float x, float y) {
        targetX = x;
        targetY = y;
    }

    // The update() routine is called once every time the screen is redrawn, generally
    // 60 times per second.
    @Override
    public void update(int msec) {
        super.update(msec);

        if (!imageLoaded()) {
            return;
        }
        // onceEvery(N) does something randomly approximately every N seconds.
        // The "f" in "2.0f" means it's a floating point number.
        if (Rand.onceEvery(2.0f)) {
            // Choose random distance and angle to hop.
            int dist = Rand.between(50, 100);
            float direction = (float) Math.toRadians(Rand.between(0, 360));
            // Move the sprite in the desired direction
            hop(dist, direction);
        }

        // We don't want the frog to hop off the edge of the screen, so this mess of
        // conditionals checks if the frog's coordinates are outside our world size and
        // forces them back inside.
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
        return;
    }

    @Override
    public void onTouch(float x, float y) {
        ++timesTapped;
        if (timesTapped == TAPS_BEFORE_BECOMING_PRINCE) {
            loadImage(R.raw.prince_headshot);
        }

        // Pick a random distance to hop when tapped.
        int dist = Rand.between(30, 50);

        // When tapped, always hop exactly towards the middle of the screen.
        hopToward(dist, targetX, targetY);

        // And make a suitable frog noise.
        Audio.play(R.raw.frog_croak);
    }
}
