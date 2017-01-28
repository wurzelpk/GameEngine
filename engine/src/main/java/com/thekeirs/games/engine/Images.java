package com.thekeirs.games.engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A very basic image cache so that when we create 100 frogs or cycle through an animation
 * we only have one shared copy of the bitmap.  No upper cache size for now.
 */

public final class Images {
    private static Map<Integer, Bitmap> mCache = new HashMap<>();
    private static Resources mResources;

    public static void setResources(Resources r) {
        mResources = r;
    }

    public static Bitmap get(int resourceId) {
        Bitmap bmp = mCache.get(resourceId);
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(mResources, resourceId);
            mCache.put(resourceId, bmp);
        }

        return bmp;
    }
}
