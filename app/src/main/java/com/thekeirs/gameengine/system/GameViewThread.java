package com.thekeirs.gameengine.system;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wurzel on 1/8/17.
 */

public class GameViewThread extends Thread {
    final private String TAG = "GameViewThread";
    private GameView.IGameLogicService mGameLogic;
    private GameView.IRedrawService mRedrawService;
    private SurfaceHolder mHolder;
    BlockingQueue<MotionEvent> mEvents = new LinkedBlockingQueue<>();
    private float mXFactor = 1.0f, mYFactor = 1.0f;

    public GameViewThread(SurfaceHolder holder, GameView.IGameLogicService gameLogic, GameView.IRedrawService redrawService) {
        mHolder = holder;
        mGameLogic = gameLogic;
        mRedrawService = redrawService;
    }

    public void setEventScalingFactors(float xfactor, float yfactor) {
        mXFactor = xfactor;
        mYFactor = yfactor;
    }

    public void queueEvent(MotionEvent e) {
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
                MotionEvent e;

                while ((e = mEvents.poll()) != null) {
                    e.setLocation(e.getX() * mXFactor, e.getY() * mYFactor);
                    mGameLogic.onMotionEvent(e);
                }
                mGameLogic.update(30);  // TODO: actual frame timing
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
