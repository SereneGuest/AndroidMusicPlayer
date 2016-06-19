package com.wenzhe.music.utils;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by wenzhe on 2016/4/30.
 */
public class Devices {
    public static float getAlbumImgHeight(WindowManager wm) {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels*0.620f;
    }

    public static int getScreenWidth(WindowManager wm) {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
