package com.thekeirs.games.engine;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <h1>[internal] This is the thread where all update() and draw() calls are done for every game
 * object</h1>
 *
 * <p>
 *     It is prohibited to run CPU-intensive work on the UI Thread in Android, so we launch
 *     this separate thread whenever the UI is ready, and take it down any time the UI becomes
 *     un-ready.
 * </p>
 * <p>
 *     The main Game Activity for the app is responsible for configuring this thread with
 *     a {@link GameView.IGameLogicService} and {@link GameView.IRedrawService} to pass along
 *     the actual update() and draw() calls.
 *  </p>
 */

public class GameViewThread extends Thread {
    final private String TAG = "GameViewThread";
    private GameView.IGameLogicService mGameLogic;
    private GameView.IRedrawService mRedrawService;
    private SurfaceHolder mHolder;
    private BlockingQueue<GameView.UIEvent> mEvents = new LinkedBlockingQueue<>();
    private float mXFactor = 1.0f, mYFactor = 1.0f;

    public GameViewThread(SurfaceHolder holder, GameView.IGameLogicService gameLogic, GameView.IRedrawService redrawService) {
        mHolder = holder;
        mGameLogic = gameLogic;
        mRedrawService = redrawService;
    }

    /**
     * Set factors to normalize user input event coordinates into range 0.0-1.0
     */
    public void setEventScalingFactors(float xfactor, float yfactor) {
        mXFactor = xfactor;
        mYFactor = yfactor;
    }

    /** UI Events arrive on the UI thread and must be handed off to the worker thread */
    public void queueEvent(GameView.UIEvent e) {
        mEvents.add(e);
    }

    public void run() {
        int loops = 0;
        long lastLog = System.currentTimeMillis();
        Log.d(TAG, "thread starting");
        while (!isInterrupted()) {
            Canvas c = mHolder.lockCanvas();
            if (c == null) {
                Log.d(TAG, "null canvas from mHolder");
            } else {
                GameView.UIEvent e;

                while ((e = mEvents.poll()) != null) {
                    e.event1.setLocation(e.event1.getX() * mXFactor, e.event1.getY() * mYFactor);
                    if (e.event2 != null) {
                        e.event2.setLocation(e.event2.getX() * mXFactor, e.event2.getY() * mYFactor);
                        e.dx *= mXFactor;
                        e.dy *= mYFactor;
                    }
                    mGameLogic.onMotionEvent(e);
                }
                mGameLogic.update(16);  // TODO: actual frame timing
                mRedrawService.draw(c);
                mHolder.unlockCanvasAndPost(c);
                ++loops;
            }

            long now = System.currentTimeMillis();
            if (now - lastLog > 5000) {
                float fps = loops * 1000.0f / (now - lastLog);
                lastLog = now;
                loops = 0;
                Log.d(TAG, "fps = " + fps);
            }
        }
        Log.d(TAG, "thread terminating");
    }

    public void gracefulStop() {
        Log.d(TAG, "thread termination requested");

        this.interrupt();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
