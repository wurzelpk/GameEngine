package com.thekeirs.gameengine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thekeirs.gameengine.games.OpeningScreenLevel;
import com.thekeirs.gameengine.system.Audio;
import com.thekeirs.gameengine.system.GameObjectManager;
import com.thekeirs.gameengine.system.GameView;
import com.thekeirs.gameengine.system.MessageBus;

public class GameActivity extends AppCompatActivity {
    private MessageBus mBus;
    private GameView mGameView;
    private GameObjectManager mObjectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mBus = new MessageBus();

        mObjectManager = new GameObjectManager(mBus, getResources());
        mObjectManager.setLevel(new OpeningScreenLevel());

        mGameView = (GameView) findViewById(R.id.gameview);
        mGameView.setRedrawService(mObjectManager);
        mGameView.setGameLogicService(mObjectManager);
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
}
