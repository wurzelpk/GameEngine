package com.thekeirs.games.engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * A very basic image cache so that when we create 100 frogs or cycle through an animation
 * we only have one shared copy of the bitmap.  No upper cache size for now.
 */

public final class Images {
    private static Map<Integer, Bitmap> mCache = new HashMap<>();
    private static Resources mResources;
    private static int mDefaultId;

    public static void setResources(Resources r) {
        mResources = r;
    }

    public static void setDefaultImage(int defaultId) {
        mDefaultId = defaultId;
    }
    public static Bitmap get(int resourceId) {
        Bitmap bmp = mCache.get(resourceId);
        if (bmp == null) {
            try {
                bmp = BitmapFactory.decodeResource(mResources, resourceId);
            } catch (Exception e) {
                bmp = null;
                Log.d("Images", "decodeResource: " + e.toString());
            }
            if (bmp == null) {
                // Handle exception case or case where decodeResources returns null on its own
                Log.d("Images", "Error decoding id " + resourceId);
                bmp = BitmapFactory.decodeResource(mResources, mDefaultId);
            }
            mCache.put(resourceId, bmp);
        }

        return bmp;
    }
}
