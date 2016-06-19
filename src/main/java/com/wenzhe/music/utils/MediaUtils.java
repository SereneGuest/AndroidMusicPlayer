package com.wenzhe.music.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.wenzhe.music.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by wenzhe on 2016/5/1.
 */
public class MediaUtils {
    public static String formatTime(long time) {
        String minute = time / 60000 + "";
        String second = time % 60000 + "";
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        switch (second.length()) {
            case 4:
                second = "0" + second;
                break;
            case 3:
                second = "00";
                break;
            case 2:
                second = "00";
                break;
            case 1:
                second = "00";
                break;
        }

        return minute + ":" + second.trim().substring(0, 2);
    }

    public static Bitmap getAlbumImgBitmap(long id, Context context,
                                           BitmapFactory.Options options) {
        Bitmap bitmap;
        String selection = MediaStore.Audio.Albums._ID + " = " + id;
        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums
                        .EXTERNAL_CONTENT_URI,
                projection, selection, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_bg);
        }
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        if (TextUtils.isEmpty(path)) {
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_bg);
        } else {

            bitmap = BitmapFactory.decodeFile(path, options);
            if (bitmap == null) {
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_bg);
            }
            return bitmap;

        }

    }
}
