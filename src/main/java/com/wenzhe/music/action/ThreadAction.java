package com.wenzhe.music.action;

import android.graphics.Bitmap;

import com.wenzhe.music.constants.MusicChangeType;

/**
 * Created by wenzhe on 2016/5/1.
 */
public class ThreadAction {

    public int getColor() {
        return color;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public MusicChangeType getType() {
        return type;
    }

    private int color;
    private Bitmap bitmap;
    private MusicChangeType type;

    public ThreadAction(int color, Bitmap bitmap, MusicChangeType type) {
        this.color = color;
        this.bitmap = bitmap;
        this.type = type;
    }


}
