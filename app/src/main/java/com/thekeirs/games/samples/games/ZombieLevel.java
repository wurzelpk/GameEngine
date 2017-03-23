package com.thekeirs.games.samples.games;

import com.thekeirs.games.engine.Audio;
import com.thekeirs.games.engine.BackgroundImageScene;
import com.thekeirs.games.engine.GameLevel;
import com.thekeirs.games.engine.Rand;
import com.thekeirs.games.engine.Sprite;
import com.thekeirs.games.engine.Text;
import com.thekeirs.games.samples.R;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * Created by Holden Matheson on 1/6/17.
 *
 * Zombie brain hunt - demonstrates the usage of the Text object, among other things.
 */

public class ZombieLevel extends GameLevel {
    // Add your class variables here.  For example, to keep track of the player's sprite:
    // Sprite player1;

    // This is the "constructor" function.  You probably do not need to change this.
    public ZombieLevel() {
        super();
    }
    private int score = 0;
    private int direction = 0;
    private float speed = 6;
    private Sprite zombie = null;
    private Sprite brain = null;
    private Text scoreText = null;
    private boolean started = false;

    // The setup function is run once whenever this level is activated.
    // This is where you should set your background and create most of your game objects.
    @Override
    public void setup() {

        mManager.setWorldScreenSize(1600, 900);
        // mManager.setScene(new SolidColorScene("#5588aa"));
        mManager.setScene(new BackgroundImageScene(mManager, R.raw.zombiebackground));

        mManager.addObject(
                new Sprite("brain", Rand.between(100,1500), Rand.between(100, 800), 84, 60, R.raw.brain)
        );
        mManager.addObject(
                new Sprite("zombie", 400, 100, 200, 200, R.raw.zombie)
        );
        mManager.addObject(
                new Text("score", "", 100, 100, 50, 50)
        );
        mManager.addObject(
                new Text("instruct1", "Fling the zombie to get the brains", 200, 300, 50, 50)
        );
        mManager.addObject(
                new Text("instruct2", "Tap him to fart and slow down", 200, 400, 50, 50)
        );

        mManager.addObject(
                new Sprite("ok_button",
                        800, 400, 400, 75,
                        R.raw.button_understand) {
                    @Override
                    public void onTouch(float x, float y) {
                        started = true;
                        this.requestRemoval();
                        mManager.getObjectByName("instruct1").requestRemoval();
                        mManager.getObjectByName("instruct2").requestRemoval();
                    }
                }
        );
        zombie = (Sprite) mManager.getObjectByName("zombie");
        brain = (Sprite) mManager.getObjectByName("brain");
        scoreText = (Text) mManager.getObjectByName("score");
        scoreText.setTransparency(200);
        scoreText.setHexColor("BB0000");
    }

    @Override
    public void update(int millis) {
        super.update(millis);
        if(started) {
            scoreText.setText("Score: " + score);
            int scoreTint = max(16, min((170 + (score / 10)), 255));
            scoreText.setHexColor(Integer.toHexString(scoreTint) + "0000");

            zombie.hop(this.speed, this.direction);
            zombie.setRotation((float) this.direction);
            if (zombie.isFullyOffScreen()) {
                this.direction = (this.direction + 180) % 360;
                score -= 20;
            }

            if (brain.isInside(zombie)) {
                // http://soundbible.com/976-Eating.html
                Audio.play(R.raw.eating);
                if (speed < 40) {
                    speed += 4;
                }
                score += 100;
                brain.setXY(Rand.between(100, 1500), Rand.between(100, 800));
            }

        }
    }

    @Override
    public boolean onAnyFling(float x, float y, float dx, float dy){
        if (abs(dx) > abs(dy)) {
            if (dx > 0) {
                this.direction = 0;
            }
            else {
                this.direction = 180;
            }
        }
        else {
            if (dy > 0){
                this.direction = 90;
            }
            else{
                this.direction = 270;
            }
        }
        return true;
    }

    @Override
    public boolean onAnyTouch(float x, float y){
        if (zombie.contains(x, y)){
            Audio.play(R.raw.fart);
            if (speed > 8) {
                speed -= 4;
                if (speed < 8) {
                    speed = 8;
                }
                score -= 10;
            }
        }

        if (brain.contains(x, y)){
            // http://soundbible.com/1051-Zombie-Brain-Eater.html
            Audio.play(R.raw.intro);
        }
        return false;
    }

    public void finish() {

    }
}
