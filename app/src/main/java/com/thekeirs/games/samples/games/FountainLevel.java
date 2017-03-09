package com.thekeirs.games.samples.games;

import com.thekeirs.games.engine.GameLevel;
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

        floor = new Sprite("floor", WIDTH / 2, HEIGHT - 5, WIDTH, 10, R.raw.button_castle_blaster);
        floor.setSolid(true);
        mManager.addObject(floor);
    }

    @Override
    public void update(int millis) {
        super.update(millis);


        if (Rand.onceEvery(1.0f)) {
            Sprite ball = new Sprite("", WIDTH / 2, HEIGHT / 2, 40, 40, R.raw.pink_ball);
            ball.setFeelsGravity(true);
            ball.setAutoDieOffscreen(true);
            ball.setdX(Rand.between(-150, 150));
            ball.setdY(Rand.between(-300, -100));
            mManager.addObject(ball);
        }
    }
}
