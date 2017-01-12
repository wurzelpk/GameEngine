package com.thekeirs.gameengine.games;

import com.thekeirs.gameengine.framework.Rand;
import com.thekeirs.gameengine.system.GameLevel;
import com.thekeirs.gameengine.system.GameObject;
import com.thekeirs.gameengine.system.Scene;
import com.thekeirs.gameengine.system.SolidColorScene;

/**
 * Created by wurzel on 1/6/17.
 */

public class FrogHerderLevel extends GameLevel {
    // WIDTH and HEIGHT set the size of the world the game will take place in.
    final int WIDTH = 1600;
    final int HEIGHT = 900;

    // Configure the number of frogs present when the level starts
    final int NUM_FROGS = 5;

    public FrogHerderLevel() {
        super();
    }

    // Configure the world, set up a background scene, and add the game objects
    // into the world.
    public void setup() {
        mManager.setWorldScreenSize(WIDTH, HEIGHT);

        // Set the background to a single solid color
        Scene scene = new SolidColorScene("#20c0ff");

        // If you want to make a custom background, create a new image, place it in
        // the app/resources/raw folder with name my_background.png or my_background.jpg,
        // and uncomment this line:
        // Scene scene = new BackgroundImageScene(mManager, R.raw.my_background);
        mManager.setScene(scene);

        for (int i = 0; i < NUM_FROGS; ++i) {
            // Since the world is 900 units tall, 100 unit tall frogs means we can fit 9 frogs
            // high on the screen.
            int frogSize = 100;

            // Create a frog named frog0, frog1, etc, at a random location with the requested size
            GameObject frog = new FrogSprite("frog" + i,
                    Rand.between(0, WIDTH - frogSize),
                    Rand.between(0, HEIGHT - frogSize),
                    frogSize, frogSize);

            // Add the frog to the Game Object Manager so it can be drawn on the screen,
            // touched, and have its location updated.
            mManager.addObject(frog);
        }
    }
}
