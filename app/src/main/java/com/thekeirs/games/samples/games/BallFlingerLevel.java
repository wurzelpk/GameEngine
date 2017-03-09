package com.thekeirs.games.samples.games;

import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.Scene;
import com.thekeirs.games.engine.SolidColorScene;
import com.thekeirs.games.engine.Sprite;
import com.thekeirs.games.samples.R;

import java.util.ArrayList;
import java.util.List;


public class BallFlingerLevel extends GameLevel {
    // WIDTH and HEIGHT set the size of the world the game will take place in.
    final int WIDTH = 1600;
    final int HEIGHT = 900;
    final int PLATFORM_WIDTH = 100;
    final int PLATFORM_HEIGHT = 25;

    final int STATE_IDLE = 1;
    final int STATE_AIMING = 2;
    final int STATE_FLYING = 3;
    private int state = STATE_IDLE;

    private int ballCount = 0;
    private Sprite ball;
    private float dx, dy;
    private List<Sprite> oldBalls = new ArrayList<>();
    private List<Sprite> platforms = new ArrayList<>();
    private final int[][] PLAT_COORDS = {{300, 200}, {500, 800}, {900, 400}, {1300, 600}};

    public BallFlingerLevel() {
        super();
    }

    // Configure the world, set up a background scene, and add the game objects
    // into the world.
    @Override
    public void setup() {
        mManager.setWorldScreenSize(WIDTH, HEIGHT);

        Scene scene = new SolidColorScene("#20c0ff");
        // Scene scene = new BackgroundImageScene(mManager, R.raw.frogger_background);
        mManager.setScene(scene);

        for (int i = 0; i < PLAT_COORDS.length; ++i) {
            Sprite p = new Sprite("platform" + i, PLAT_COORDS[i][0], PLAT_COORDS[i][1], PLATFORM_WIDTH, PLATFORM_HEIGHT, R.raw.button_castle_blaster);
            platforms.add(p);
            mManager.addObject(p);
        }
    }


    @Override
    public void update(int millis) {
        super.update(millis);

        if (state == STATE_FLYING) {
            ball.moveBy(dx, dy);
            dy += 0.5;

            if (!ball.isFullyOnScreen()) {
                state = STATE_IDLE;
                oldBalls.add(ball);
                ball = null;
            }
        }
    }

    @Override
    public void onUnclaimedScroll(float x, float y, float dx, float dy, boolean finished) {
        super.onUnclaimedScroll(x, y, dx, dy, finished);

        if (state == STATE_IDLE) {
            ball = new Sprite("ball" + ballCount, 100, HEIGHT - 100, 50, 50, R.raw.pink_ball);
            ++ballCount;
            mManager.addObject(ball);
            this.dx = 0;
            this.dy = 0;
            state = STATE_AIMING;
        } else if (state == STATE_AIMING) {
            this.dx += -dx;
            this.dy += -dy;

            ball.setXY(100 + this.dx, HEIGHT - 100 + this.dy);
        }

        if (finished) {
            state = STATE_FLYING;
        }
    }
}
