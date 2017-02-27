package com.thekeirs.games.samples.games;

import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.Rand;
import com.thekeirs.games.engine.Scene;
import com.thekeirs.games.engine.SolidColorScene;
import com.thekeirs.games.engine.Sprite;
import com.thekeirs.games.samples.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by wurzel on 1/6/17.
 */

public class FroggerLevel extends GameLevel {
    // WIDTH and HEIGHT set the size of the world the game will take place in.
    final int WIDTH = 1600;
    final int HEIGHT = 900;
    final int HOPSIZE = 100;
    final int LOGSPEED = 3;
    final int CARSPEED = 4;

    final int STATE_PLAYING = 1;
    final int STATE_GAMEOVER = 2;
    private int state = STATE_PLAYING;

    private Sprite frog;
    private Sprite mYouWonOrLost;

    private List<Sprite> logsR = new ArrayList<>();  // Logs floating to the right
    private List<Sprite> logsL = new ArrayList<>();  // Logs floating to the left
    private List<Sprite> carsR = new ArrayList<>();  // Vehicles going right
    private List<Sprite> carsL = new ArrayList<>();  // Vehicles going left


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

        frog = new Sprite("player1", WIDTH / 2, HEIGHT - 100, 80, 80, R.drawable.frog);
        frog.setMotionSequence("hopping", 100, R.raw.plain_frog1, R.raw.plain_frog2, -1);
        mManager.addObject(frog);

        // Three logs going each direction; Logs are 320x80 and spaced 480 apart.
        // Since the world is 1600 wide, we need 4 logs so there's always 3 on the screen
        for (int i = 0; i < 4; ++i) {
            Sprite logR = new Sprite("logright" + i, i * 480, HEIGHT - 200, 320, 80, R.raw.frogger_log);
            logsR.add(logR);
            mManager.addObject(logR);

            Sprite logL = new Sprite("logleft" + i, i * 480, HEIGHT - 300, 320, 80, R.raw.frogger_log);
            logsL.add(logL);
            mManager.addObject(logL);
        }

        // Nine cars going each direction; Cars are 120x80 and start out spaced 200 apart but
        // are randomly placed after that.
        for (int i = 0; i < 9; ++i) {
            Sprite carR = new Sprite("carright" + i, i * 200, 400, 120, 80, R.raw.frogger_car);
            carsR.add(carR);
            mManager.addObject(carR);

            Sprite carL = new Sprite("carleft" + i, i * 200, 300, 120, 80, R.raw.frogger_car);
            carsL.add(carL);
            mManager.addObject(carL);
        }
    }

    @Override
    public boolean onAnyTouch(float x, float y) {
        // If the player has won (which we see because the mYouWon sprite exists) and the
        // mYouWon sprite has been showing for at least one second (1000 milliseconds), then
        // go back to the opening screen.
        if (mYouWonOrLost != null && mYouWonOrLost.getTimeOnScreen() > 2000) {
            mManager.setLevel(new OpeningScreenLevel());
        }
        return false;
    }

    @Override
    public void onUnclaimedFling(float x, float y, float dx, float dy) {
        if (abs(dx) > abs(dy)) {
            // Fling was primarily left/right
            if (dx > 0) {
                if (frog.getX() < WIDTH - HOPSIZE) {
                    frog.moveBy(HOPSIZE, 0);
                    frog.flipX(true);
                }
            } else {
                if (frog.getX() > HOPSIZE) {
                    frog.moveBy(-HOPSIZE, 0);
                    frog.flipX(false);
                }
            }
        } else {
            // Fling was primarily up/down
            if (dy > 0) {
                if (frog.getY() < HEIGHT - 150) {
                    frog.moveBy(0, HOPSIZE);
                }
            } else {
                frog.moveBy(0, -HOPSIZE);
            }
        }
        frog.setMotionState("hopping");
    }

    @Override
    public void update(int millis) {
        super.update(millis);

        // First deal with moving the logs.  Logs are evenly spaced and predictable.
        for (Sprite log : logsR) {
            log.moveBy(LOGSPEED, 0);
            if (log.isFullyOffScreen() && log.getX() > 0) {
                log.moveBy(-4 * 480, 0);
            }
        }
        for (Sprite log : logsL) {
            log.moveBy(-LOGSPEED, 0);
            if (log.isFullyOffScreen() && log.getX() < 0) {
                log.moveBy(4 * 480, 0);
            }
        }

        // If the frog is on top of a log, then the frog must move at the same speed and direction
        // as that log.  But we also have to track if the frog is touching any log for the next
        // set of checks below.
        boolean frogOnLog = false;
        if (frog.intersectsAny(logsL) != null) {
            frog.moveBy(-LOGSPEED, 0);
            frogOnLog = true;
        }

        if (frog.intersectsAny(logsR) != null) {
            frog.moveBy(LOGSPEED, 0);
            frogOnLog = true;
        }

        // Move the cars ; if they've gone off the side of the screen then delete them from
        // the list.
        for (Sprite car : carsR) {
            car.moveBy(CARSPEED, 0);
            if (car.isFullyOffScreen() && car.getX() >= WIDTH) {
                car.setX(Rand.between(-200, 0));
            }
        }
        for (Sprite car : carsL) {
            car.moveBy(-CARSPEED, 0);
            if (car.isFullyOffScreen() && car.getX() < 0) {
                car.setX(Rand.between(WIDTH, WIDTH + 200));
            }
        }

        // Only check for winning and losing conditions if the game hasn't already ended.
        // This prevents displaying both You Won and Game Over banners at the same time.
        if (state != STATE_GAMEOVER) {
            // If the frog is within the river boundaries and wasn't touching any log, game over.
            if (frog.getY() > HEIGHT - 350 && frog.getY() < HEIGHT - 150 && !frogOnLog) {
                playerLost();
            }

            // If the frog is touching any cars, game over.
            if (frog.intersectsAny(carsR) != null || frog.intersectsAny(carsL) != null) {
                playerLost();
            }

            if (!frog.isFullyOnScreen()) {
                playerWon();
            }
        }
    }

    private void playerWon() {
        mYouWonOrLost = new Sprite("won", WIDTH / 2, HEIGHT / 2, WIDTH - 100, HEIGHT - 100, R.raw.you_won);
        mManager.addObject(mYouWonOrLost);
        state = STATE_GAMEOVER;
    }

    private void playerLost() {
        mYouWonOrLost = new Sprite("lost", WIDTH / 2, HEIGHT / 2, WIDTH - 100, HEIGHT - 100, R.raw.game_over);
        mManager.addObject(mYouWonOrLost);
        state = STATE_GAMEOVER;
    }
}
