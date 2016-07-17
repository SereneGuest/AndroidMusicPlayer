package com.wenzhe.music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.WindowManager;


import com.wenzhe.music.action.ThreadAction;
import com.wenzhe.music.constants.MusicChangeType;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wenzhe on 2016/5/1.
 * 加载bitmap的线程，同时根据bitmap通过palette 加载对应的颜色
 */
public class BitmapTask implements Runnable {

    public static final int COLOR_MUTE = 0X1111;
    public static final int COLOR_MUTE_DARK = 0X1112;
    public static final int COLOR_MUTE_LIGHT = 0X1113;
    public static final int COLOR_VIBRANT = 0X1114;
    public static final int COLOR_VIBRANT_DARK = 0X1115;
    public static final int COLOR_VIBRANT_LIGHT = 0X1116;
    public static final int COLOR_DEFAULT = 0xFFD7A652;
    public  final String TAG = this.getClass().getSimpleName();

    private long albumId;
    private Context context;
    private int colorType;
    private String type;
    private int albumWidth;

    //private static Bitmap albumArt;

    public BitmapTask(Context context, long albumId, int colorType,
                      String type) {
        this.albumId = albumId;
        this.context = context;
        this.colorType = colorType;
        this.type = type;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        albumWidth = Devices.getScreenWidth(windowManager);
        //albumHeight = (int) Devices.getAlbumImgHeight(windowManager);
    }

    @Override
    public void run() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        MediaUtils.getArtworkFromFile(context, albumId, options, true);
        //Log.e(TAG,"width:"+options.outWidth+" height:"+options.outHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inTargetDensity = albumWidth;
        Bitmap albumArt = MediaUtils.getArtworkFromFile(context, albumId, options, false);
        //Log.e(TAG,"width1:"+albumArt.getWidth()+" height1:"+albumArt.getHeight());
        Palette palette = Palette.from(albumArt).generate();
        int color = COLOR_DEFAULT;
        switch (colorType) {
            case COLOR_MUTE:
                color = palette.getMutedColor(color);
                if (color == COLOR_DEFAULT) {
                    color = palette.getVibrantColor(color);
                }
                break;
            case COLOR_VIBRANT:
                color = palette.getVibrantColor(color);
                if (color == COLOR_DEFAULT) {
                    color = palette.getMutedColor(color);
                }
                break;
            case COLOR_MUTE_LIGHT:
                color = palette.getLightMutedColor(color);
                if (color == COLOR_DEFAULT) {
                    color = palette.getVibrantColor(color);
                }
                break;
            case COLOR_VIBRANT_DARK:
                color = palette.getDarkVibrantColor(color);
                if (color == COLOR_DEFAULT) {
                    color = palette.getMutedColor(color);
                }
                break;
            case COLOR_VIBRANT_LIGHT:
                color = palette.getLightVibrantColor(color);
                if (color == COLOR_DEFAULT) {
                    color = palette.getMutedColor(color);
                }
                break;
            case COLOR_MUTE_DARK:
                color = palette.getDarkMutedColor(color);
                if (color == COLOR_DEFAULT) {
                    color = palette.getMutedColor(color);
                }
                break;

        }
        EventBus.getDefault().post(new ThreadAction(color,albumArt,this.type));
    }
}
