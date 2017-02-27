package com.thekeirs.games.samples.games;

import com.thekeirs.games.engine.BackgroundImageScene;
import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.Sprite;
import com.thekeirs.games.samples.R;

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
                        800, 100, 800, 75,
                        R.raw.button_frog_herder) {
                    @Override
                    public void onTouch(float x, float y) {
                        mManager.setLevel(new FrogHerderLevel());
                    }
                }
        );

        mManager.addObject(
                new Sprite("frogger_button",
                        800, 200, 800, 75,
                        R.raw.button_frogger) {
                    @Override
                    public void onTouch(float x, float y) {
                        mManager.setLevel(new FroggerLevel());
                    }
                }
        );

        mManager.addObject(
                new Sprite("castle_blaster_button",
                        800, 300, 800, 75,
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
