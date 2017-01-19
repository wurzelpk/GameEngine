package com.thekeirs.games.samples.games;

import com.thekeirs.games.samples.R;
import com.thekeirs.games.engine.Rand;
import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.GameObject;
import com.thekeirs.games.engine.Scene;
import com.thekeirs.games.engine.SolidColorScene;
import com.thekeirs.games.engine.Sprite;

/**
 * Created by wurzel on 1/6/17.
 */

public class FrogHerderLevel extends GameLevel {
    // WIDTH and HEIGHT set the size of the world the game will take place in.
    final int WIDTH = 1600;
    final int HEIGHT = 900;

    // Configure the number of frogs present when the level starts
    final int NUM_FROGS = 5;

    private Sprite mLillypad;
    private Sprite mYouWon;

    public FrogHerderLevel() {
        super();
    }

    // Configure the world, set up a background scene, and add the game objects
    // into the world.
    @Override
    public void setup() {
        mManager.setWorldScreenSize(WIDTH, HEIGHT);

        // Set the background to a single solid color
        Scene scene = new SolidColorScene("#20c0ff");

        // If you want to make a custom background, create a new image, place it in
        // the app/resources/raw folder with name my_background.png or my_background.jpg,
        // and uncomment this line:
        // Scene scene = new BackgroundImageScene(mManager, R.raw.my_background);
        mManager.setScene(scene);

        // Create one lillypad centered on the screen and give it to the game manager.
        mLillypad = new Sprite("lillypad", 700, 350, 200, 200, R.raw.lillypad);
        mManager.addObject(mLillypad);

        for (int i = 0; i < NUM_FROGS; ++i) {
            // Since the world is 900 units tall, 100 unit tall frogs means we can fit 9 frogs
            // high on the screen.
            int frogSize = 100;

            // Create a frog named frog0, frog1, etc, at a random location with the requested size
            FrogSprite frog = new FrogSprite("frog" + i,
                    Rand.between(0, WIDTH - frogSize),
                    Rand.between(0, HEIGHT - frogSize),
                    frogSize, frogSize);

            // Make sure the frog knows where the center of the lillypad is so it can hop towards
            // it when touched.
            frog.setTargetLocation(WIDTH / 2, HEIGHT / 2);

            // Add the frog to the Game Object Manager so it can be drawn on the screen,
            // touched, and have its location updated.
            mManager.addObject(frog);
        }
    }

    @Override
    public boolean onAnyTouch(float x, float y) {
        // If the player has won (which we see because the mYouWon sprite exists) and the
        // mYouWon sprite has been showing for at least one second (1000 milliseconds), then
        // go back to the opening screen.
        if (mYouWon != null && mYouWon.getTimeOnScreen() > 1000) {
            mManager.setLevel(new OpeningScreenLevel());
        }
        return false;
    }

    @Override
    public void update() {
        // Use a loop to count how many frogs are not touching the lillypad right now
        int frogsOffPad = 0;
        for (GameObject frog : mManager.getObjectsMatching("frog")) {
            if (frog.intersects(mLillypad) == false) {
                ++frogsOffPad;
            }
        }

        // If we're in the winning condition and we haven't already displayed the
        // "You Won" banner, display it.
        if (frogsOffPad == 0 && mYouWon == null) {
            // Cool, they're all on there.  Display the winning banner.
            mYouWon = new Sprite("won", 100, 100, WIDTH - 100, HEIGHT - 100, R.raw.you_won);
            mManager.addObject(mYouWon);
        }
    }
}
