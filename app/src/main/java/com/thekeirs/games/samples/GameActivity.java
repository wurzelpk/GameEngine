package com.thekeirs.games.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.thekeirs.games.engine.Audio;
import com.thekeirs.games.engine.GameObjectManager;
import com.thekeirs.games.engine.GameView;
import com.thekeirs.games.engine.Images;
import com.thekeirs.games.engine.MessageBus;
import com.thekeirs.games.samples.games.OpeningScreenLevel;

public class GameActivity extends AppCompatActivity {
    final String TAG = "GameActivity";
    private MessageBus mBus;
    private GameView mGameView;
    private GameObjectManager mObjectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Images.setResources(getResources());

        mBus = new MessageBus();

        mObjectManager = new GameObjectManager(mBus, getResources());
        mObjectManager.setLevel(new OpeningScreenLevel());

        mGameView = (GameView) findViewById(R.id.gameview);
        mGameView.setRedrawService(mObjectManager);
        mGameView.setGameLogicService(mObjectManager);
        mGameView.setFocusable(true);
        mGameView.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Audio.onResume(this);
        mGameView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.onPause();
        Audio.onPause();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        Log.d(TAG, "Motion: ");
        return mGameView.onGenericMotionEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "Event: " + event.toString());
        return (event.getAction() == KeyEvent.ACTION_DOWN)
                ? mGameView.onKeyDown(event.getKeyCode(), event)
                : mGameView.onKeyUp(event.getKeyCode(), event);

    }
}
