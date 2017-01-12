package com.thekeirs.gameengine.system;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import im.delight.android.audio.SoundManager;

/**
 * Created by wurzel on 12/31/16.
 */

/**
 * Wraps https://github.com/delight-im/Android-Audio
 * with a simple wrapper to keep track of what sounds have been
 * loaded previously so we don't have to call load before every play.
 * If the game activity is destroyed, we can preload all the
 * previously-loaded sounds also, though whether this is a good
 * idea is debatable (eg, if player has gone up a few levels, some
 * old sounds may not be used on later levels.)
 */

public class Audio {
    static final int MAX_STREAMS = 4;

    static final String TAG = "GameEngine-Audio";
    static private SoundManager mSoundManager;
    static private Set<Integer> mLoadedSounds = new HashSet<>();

    static public void onResume(Context context) {
        mSoundManager = new SoundManager(context, MAX_STREAMS);
        mSoundManager.start();
        preload();
    }

    private static void preload() {
        for (Integer id : mLoadedSounds) {
            mSoundManager.load(id);
        }
    }

    static public void onPause() {
        if (mSoundManager != null) {
            mSoundManager.cancel();
            mSoundManager = null;
        }
    }

    static public void play(int id) {
        if (mSoundManager != null) {
            if (!mLoadedSounds.contains(id)) {
                mSoundManager.load(id);
                mLoadedSounds.add(id);
            }
            mSoundManager.play(id);
        }
    }
}
