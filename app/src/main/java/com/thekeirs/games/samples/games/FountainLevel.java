package com.thekeirs.games.samples.games;

import android.view.KeyEvent;

import com.thekeirs.games.engine.Audio;
import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.GameObject;
import com.thekeirs.games.engine.Rand;
import com.thekeirs.games.engine.Scene;
import com.thekeirs.games.engine.SolidColorScene;
import com.thekeirs.games.engine.Sprite;
import com.thekeirs.games.samples.R;

import java.util.ArrayList;
import java.util.List;


public class FountainLevel extends GameLevel {
    // WIDTH and HEIGHT set the size of the world the game will take place in.
    final int WIDTH = 1600;
    final int HEIGHT = 900;
    final int PLATFORM_WIDTH = 100;
    final int PLATFORM_HEIGHT = 25;

    private List<Sprite> platforms = new ArrayList<>();
    private final int[][] PLAT_COORDS = {{300, 200}, {500, 800}, {1000, 400}, {1300, 600}};
    Sprite floor;
    Sprite frog;

    public FountainLevel() {
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
            p.setSolid(true);
            p.setBouncy(true);
            platforms.add(p);
            mManager.addObject(p);
        }

        frog = new Sprite("frog", WIDTH / 4, HEIGHT / 4, 60, 60, R.drawable.frog);
        mManager.addObject(frog);

        floor = new Sprite("floor", WIDTH / 2, HEIGHT - 5, WIDTH, 10, R.raw.button_castle_blaster);
        floor.setSolid(true);
        mManager.addObject(floor);
    }

    @Override
    public void update(int millis) {
        super.update(millis);


        if (Rand.onceEvery(1.0f)) {
            Sprite ball = new Sprite("", WIDTH / 2, HEIGHT / 2, 40, 40, R.raw.pink_ball) {
                @Override
                public void onCollision(GameObject other) {
                    if (other != floor) {
                        Audio.play(R.raw.frog_croak);
                    }
                }
            };
            ball.setFeelsGravity(true);
            ball.setAutoDieOffscreen(true);
            ball.setdX(Rand.between(-150, 150));
            ball.setdY(Rand.between(-300, -100));
            mManager.addObject(ball);
            Audio.play(R.raw.bloop);
        }
        frog.setdX(mManager.getRightStickX() * 300);
        frog.setdY(mManager.getRightStickY() * 300);
    }

    @Override
    public void onButtonDown(int keyCode) {
        Sprite ball = null;

        if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            ball = new Sprite("", WIDTH / 2, HEIGHT / 2, 60, 60, R.raw.prince_headshot);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
            ball = new Sprite("", WIDTH / 2, HEIGHT / 2, 60, 60, R.raw.frogger_car);
        }
        if (ball != null) {
            ball.setFeelsGravity(true);
            ball.setAutoDieOffscreen(true);
            ball.setdX(Rand.between(-150, 150));
            ball.setdY(Rand.between(-300, -100));
            mManager.addObject(ball);
        }
    }
}
