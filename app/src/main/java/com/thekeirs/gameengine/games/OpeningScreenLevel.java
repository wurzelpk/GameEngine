package com.thekeirs.gameengine.games;

import com.thekeirs.gameengine.R;
import com.thekeirs.gameengine.system.BackgroundImageScene;
import com.thekeirs.gameengine.system.GameLevel;
import com.thekeirs.gameengine.system.Sprite;

/**
 * Created by wurzel on 1/6/17.
 */

public class OpeningScreenLevel extends GameLevel {
    public OpeningScreenLevel() {
        super();
    }

    public void setup() {

        mManager.setWorldScreenSize(1600, 900);
        mManager.setScene(new BackgroundImageScene(mManager, R.raw.opening_background));

        mManager.addObject(
                new Sprite("frog_herder_button",
                        400, 100, 800, 75,
                        R.raw.button_frog_herder) {
                    @Override
                    public void onTouch(float x, float y) {
                        mManager.setLevel(new FrogHerderLevel());
                    }
                }
        );

        mManager.addObject(
                new Sprite("frogger_button",
                        400, 200, 800, 75,
                        R.raw.button_frogger) {
                    @Override
                    public void onTouch(float x, float y) {
                        mManager.setLevel(new FroggerLevel());
                    }
                }
        );

        mManager.addObject(
                new Sprite("castle_blaster_button",
                        400, 300, 800, 75,
                        R.raw.button_castle_blaster) {
                    @Override
                    public void onTouch(float x, float y) {
                        mManager.setLevel(new FrogHerderLevel());
                    }
                }
        );

    }

    public void finish() {

    }
}
