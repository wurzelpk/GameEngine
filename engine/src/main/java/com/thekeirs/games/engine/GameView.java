package com.thekeirs.games.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Provides a surface for various tests to write to from background thread
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    final private String TAG = "GameView";
    private GestureDetectorCompat mDetector;

    public interface IRedrawService {
        void draw(Canvas canvas);
    }

    public interface IGameLogicService extends IMessageClient {
        void onMotionEvent(UIEvent e);
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

        mDetector = new GestureDetectorCompat(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                mGameViewThread.queueEvent(new UIEvent(UIEventType.Down, e));
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                mGameViewThread.queueEvent(new UIEvent(UIEventType.ShowPress, e));
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mGameViewThread.queueEvent(new UIEvent(UIEventType.SingleTapUp, e));
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mGameViewThread.queueEvent(new UIEvent(UIEventType.Scroll, e1, e2, distanceX, distanceY));
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                mGameViewThread.queueEvent(new UIEvent(UIEventType.LongPress, e));
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                mGameViewThread.queueEvent(new UIEvent(UIEventType.Fling, e1, e2, velocityX, velocityY));
                return true;
            }
        });
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
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

    public enum UIEventType {
        Down, ShowPress, SingleTapUp, Scroll, LongPress, Fling
    }

    public class UIEvent {
        UIEvent(UIEventType type, MotionEvent event1) {
            this.type = type;
            this.event1 = event1;
        }

        UIEvent(UIEventType type, MotionEvent event1, MotionEvent event2, float dx, float dy) {
            this.type = type;
            this.event1 = event1;
            this.event2 = event2;
            this.dx = dx;
            this.dy = dy;
        }

        UIEventType type;
        public MotionEvent event1, event2;
        public float dx, dy;        // Holds Velocity for flings, distance for scrolls
    }
}

