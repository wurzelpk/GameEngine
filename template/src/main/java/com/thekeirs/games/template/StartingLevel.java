package com.thekeirs.games.template;

import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.SolidColorScene;

/**
 * A sample template
 */

public class StartingLevel extends GameLevel {
    // Add your class variables here.  For example, to keep track of the player's sprite:
    // Sprite player1;

    // This is the "constructor" function.  You probably do not need to change this.
    public StartingLevel() {
        super();
    }

    // The setup function is run once whenever this level is activated.
    // This is where you should set your background and create most of your game objects.
    @Override
    public void setup() {

        mManager.setWorldScreenSize(1600, 900);
        mManager.setScene(new SolidColorScene("#5588aa"));
        // mManager.setScene(new BackgroundImageScene(mManager, R.raw.opening_background));

        // Example to create a new sprite and register it with the game engine:
        //
        // player1 = new Sprite("Player 1", 600, 400, 200, 75, R.raw.player1_standing_still)
        // mManager.addObject(player1);
    }

    @Override
    public void update(int millis) {
        super.update(millis);

        // Add your update code here
    }

    // To get user inputs such as taps or flings, place the cursor right below these comments and
    // use the "Code -> Override Methods" menu option.
    // Choose onAnyTouch or onAnyFling.  It will insert the code you need.

}
