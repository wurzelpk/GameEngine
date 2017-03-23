package com.thekeirs.games.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Holden Matheson on 3/1/2017.
 *
 * Basic text object
 */

public class Text extends GameObject {
    private String text = "";
    private Paint paint = new Paint();
    public Text(String name, RectF extent) {
        super(name, extent);
        this.paint.setARGB(255, 0, 0, 0);
        this.paint.setTextSize(100);
    }

    public Text(String name, String text, float centerX, float centerY, float width, float height) {
        super(name, new RectF(centerX - width / 2, centerY - height / 2,
                centerX + width / 2, centerY + height / 2));
        this.text = text;
        this.paint.setARGB(255, 0, 0, 0);
        this.paint.setTextSize(100);
    }

    public void setText(String text){
        this.text = text;
    }

    /**
     * Sets the color of the text
     *
     * @param RGB Hex string for the color (eg. FF2100)
     **/
    public void setHexColor(String RGB){
        int r, g, b;
        r = Integer.parseInt(RGB.substring(0,2), 16);
        g = Integer.parseInt(RGB.substring(2,4), 16);
        b = Integer.parseInt(RGB.substring(4,6), 16);

        paint.setARGB(this.paint.getAlpha(), r, g, b);
    }

    /**
     * Sets the transparency of the test
     * @param num integer from 0 (invisible) to 255 (solid)
     */
    public void setTransparency(int num) { this.paint.setAlpha(num);}

    /**
     * Sets the text size
     * @param scale Size of the text (default is 100)
     */
    public void setSize(float scale) { this.paint.setTextSize(scale);}

    public void copyPaint(Text textToCopy){ this.paint = textToCopy.paint;}
    @Override
    public void draw(Canvas c, float xScale, float yScale) {
        c.drawText(this.text, boundingRect.left, boundingRect.bottom, paint);
    }
}
