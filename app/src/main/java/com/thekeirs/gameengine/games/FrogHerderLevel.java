package com.thekeirs.gameengine.games;

import com.thekeirs.gameengine.framework.Rand;
import com.thekeirs.gameengine.system.FrogSprite;
import com.thekeirs.gameengine.system.GameLevel;
import com.thekeirs.gameengine.system.GameObject;
import com.thekeirs.gameengine.system.Scene;
import com.thekeirs.gameengine.system.SolidColorScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wurzel on 1/6/17.
 */

public class FrogHerderLevel extends GameLevel {
    final int WIDTH = 1600;
    final int HEIGHT = 900;
    final int NUM_FROGS = 5;

    public FrogHerderLevel() {
        super();
    }

    public void setup() {
        mManager.setWorldScreenSize(WIDTH, HEIGHT);
        Scene scene = new SolidColorScene("#20c0ff");
        // scene = new BackgroundImageScene(mManager, R.raw.my_background);
        mManager.setScene(scene);

        for (int i = 0; i < NUM_FROGS; ++i) {
            int frogSize = Rand.between(80, 150);
            GameObject frog = new FrogSprite("frog" + i, Rand.between(0, WIDTH - 30), Rand.between(0, HEIGHT - 30), frogSize, frogSize);
            mManager.addObject(frog);
        }
    }
}
