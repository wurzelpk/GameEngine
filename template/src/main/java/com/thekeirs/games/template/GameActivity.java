package com.thekeirs.games.template;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thekeirs.games.engine.Audio;
import com.thekeirs.games.engine.GameObjectManager;
import com.thekeirs.games.engine.GameView;
import com.thekeirs.games.engine.Images;
import com.thekeirs.games.engine.MessageBus;

public class GameActivity extends AppCompatActivity {
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
        mObjectManager.setLevel(new StartingLevel());

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
