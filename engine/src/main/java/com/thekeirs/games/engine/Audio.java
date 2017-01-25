package com.thekeirs.games.engine;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import im.delight.android.audio.SoundManager;


/**
 * <h1>A simple but high-performance audio clip player</h1>
 *
 * <p>
 *     To play a sound in your game, use the following steps:
 *
 * </p>
 * <ul>
 *     <li>Create a {@code .wav}, {@code .m4a}, or {@code .mp3} file with your audio clip.
 *     You may find the <em>Windows Voice Recorder</em> utility handy for this,
 *     or the program <em>Audacity</em></li>
 *     <li>Make sure your file has a simple name consisting only of lower case letters,
 *     digits, and underscore, with no spaces.  eg {code monster_roar1.m4a}</li>
 *     <li>Copy your audio file into the "raw" folder in your Android Studio Project</li>
 *     <li>When you want the sound to be played, simply call
 *     {@code Audio.play(R.raw.monster_roar1)}</li>
 * </ul>

 /*
 * Implementation notes:
 *
 * Wraps https://github.com/delight-im/Android-Audio
 * with a simple wrapper to keep track of what sounds have been
 * loaded previously so we don't have to call load before every play.
 * If the game activity is destroyed, we can preload all the
 * previously-loaded sounds also, though whether this is a good
 * idea is debatable (eg, if player has gone up a few levels, some
 * old sounds may not be used on later levels.)
 *
 */

public class Audio {
    static final int MAX_STREAMS = 6;

    static final String TAG = "GameEngine-Audio";
    static private SoundManager mSoundManager;
    static private Set<Integer> mLoadedSounds = new HashSet<>();

    /**
     * Called by the game engine to resume sound playing after a pause.  Do not call directly.
     */
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

    /** Called by the game engine to pause sound playing.  Do not call directly. */
    static public void onPause() {
        if (mSoundManager != null) {
            mSoundManager.cancel();
            mSoundManager = null;
        }
    }


    /**
     * Request that a sound be played.
     *
     * <p>
     *     The sound will play exactly once (no looping yet).  You may request up to six
     *     sounds playing simultaneously.  If this is not enough, we can up that limit.
     * </p>
     *
     * @param id resource ID of the {@code .wav}, {@code .m4a}, or {@code .mp3} file with
     *           your audio clip, for example {@code R.raw.monster_roar1}
     */
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
