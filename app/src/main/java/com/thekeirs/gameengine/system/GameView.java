package com.thekeirs.gameengine.system;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Provides a surface for various tests to write to from background thread
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    final private String TAG = "GameView";


    public interface IRedrawService {
        void draw(Canvas canvas);
    }

    public interface IGameLogicService extends IMessageClient {
        void onMotionEvent(MotionEvent e);
        void update(int millis);
    }

    private IRedrawService mRedrawService;
    private IGameLogicService mGameLogicService;
    private GameViewThread mGameViewThread;

    public GameView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        getHolder().addCallback(this);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO: Change to applyTransform or something like that
                mGameViewThread.queueEvent(event);
                return true;
            }
        });
    }

    public void setRedrawService(IRedrawService rs) {
        mRedrawService = rs;
    }

    public void setGameLogicService(IGameLogicService gs) {
        mGameLogicService = gs;
    }


    public void onResume() {

    }

    public void onPause() {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Don't actually care about creation; only surfaceChanged when the surface is ready to go
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mGameViewThread != null) {
            mGameViewThread.gracefulStop();
        }
        if (holder != null) {
            Log.d(TAG, "surfaceChanged, launching thread");
            mGameViewThread = new GameViewThread(holder, mGameLogicService, mRedrawService);
            if (width == 0 || height == 0) {
                Log.e(TAG, "Illegal width/height: " + width + ", " + height);
            } else {
                mGameViewThread.setEventScalingFactors(1.0f / width, 1.0f / height);
            }
            mGameViewThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mGameViewThread != null) {
            mGameViewThread.gracefulStop();
        }
    }
}

