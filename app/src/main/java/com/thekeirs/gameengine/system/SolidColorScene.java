package com.thekeirs.gameengine.system;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by wurzel on 12/31/16.
 */

public class SolidColorScene extends Scene {
    Paint mPaint;

    public SolidColorScene(String color_str) {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor(color_str));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
    }
}
