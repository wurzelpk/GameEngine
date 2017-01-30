package com.thekeirs.games.samples.games;

import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.Scene;
import com.thekeirs.games.engine.SolidColorScene;
import com.thekeirs.games.engine.Sprite;
import com.thekeirs.games.samples.R;

import static java.lang.Math.abs;

/**
 * Created by wurzel on 1/6/17.
 */

public class FroggerLevel extends GameLevel {
    // WIDTH and HEIGHT set the size of the world the game will take place in.
    final int WIDTH = 1600;
    final int HEIGHT = 900;
    final int HOPSIZE = 80;

    private Sprite frog;
    private Sprite mYouWonOrLost;

    public FroggerLevel() {
        super();
    }

    // Configure the world, set up a background scene, and add the game objects
    // into the world.
    @Override
    public void setup() {
        mManager.setWorldScreenSize(WIDTH, HEIGHT);

        Scene scene = new SolidColorScene("#20c0ff");
        // Scene scene = new BackgroundImageScene(mManager, R.raw.my_background);
        mManager.setScene(scene);

        frog = new Sprite("player1", WIDTH / 2, HEIGHT - 80, 80, 80, R.drawable.frog);
        frog.setMotionSequence("hopping", 100, R.raw.plain_frog1, R.raw.plain_frog2, -1);
        mManager.addObject(frog);
    }

    @Override
    public boolean onAnyTouch(float x, float y) {
        // If the player has won (which we see because the mYouWon sprite exists) and the
        // mYouWon sprite has been showing for at least one second (1000 milliseconds), then
        // go back to the opening screen.
        if (mYouWonOrLost != null && mYouWonOrLost.getTimeOnScreen() > 1000) {
            mManager.setLevel(new OpeningScreenLevel());
        }
        return false;
    }

    @Override
    public void onUnclaimedFling(float x, float y, float dx, float dy) {
        if (abs(dx) > abs(dy)) {
            // Fling was primarily left/right
            if (dx > 0) {
                frog.moveBy(HOPSIZE, 0);
            } else {
                frog.moveBy(-HOPSIZE, 0);
            }
        } else {
            // Fling was primarily up/down
            if (dy > 0) {
                frog.moveBy(0, HOPSIZE);
            } else {
                frog.moveBy(0, -HOPSIZE);
            }
        }
        frog.setMotionState("hopping");
    }

    @Override
    public void update(int millis) {
        super.update(millis);

        if (!frog.isFullyOnScreen()) {
            mYouWonOrLost = new Sprite("won", 100, 100, WIDTH - 100, HEIGHT - 100, R.raw.you_won);
            mManager.addObject(mYouWonOrLost);
        }
    }
}
