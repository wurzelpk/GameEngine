package com.thekeirs.games.template;

import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.SolidColorScene;

/**
 * Created by wurzel on 1/6/17.
 */

public class StartingLevel extends GameLevel {
    public StartingLevel() {
        super();
    }

    public void setup() {

        mManager.setWorldScreenSize(1600, 900);
        mManager.setScene(new SolidColorScene("#5588aa"));
        // mManager.setScene(new BackgroundImageScene(mManager, R.raw.opening_background));

        // mManager.addObject(
        //        new Sprite("my_image", 400, 100, 800, 75, R.raw.my_new_sprite)
        //);

    }

    public void finish() {

    }
}
