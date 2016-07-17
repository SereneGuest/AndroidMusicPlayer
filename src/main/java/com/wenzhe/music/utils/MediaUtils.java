package com.wenzhe.music.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.wenzhe.music.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

/**
 * Created by wenzhe on 2016/5/1.
 */
public class MediaUtils {

    private static Uri mArtworkUri;

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

    public static String getPathFromId(long albumId,Context context) {
        String selection = MediaStore.Audio.Albums._ID + " = " + albumId;
        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums
                        .EXTERNAL_CONTENT_URI,
                projection, selection, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        return path;
    }

    public static Bitmap getAlbumImgBitmap(long id, Context context,
                                           BitmapFactory.Options options) {
        Bitmap bitmap;
        String path = getPathFromId(id, context);
        if (TextUtils.isEmpty(path)) {
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_bg,options);
        } else {

            bitmap = BitmapFactory.decodeFile(path, options);
            if (bitmap == null) {
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_bg,options);
            }
            return bitmap;

        }

    }
    static {
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
    }

    public static Bitmap getArtworkFromFile(Context context, long albumId, BitmapFactory.Options
            options,boolean justDecodeBound) {
        options.inJustDecodeBounds = justDecodeBound;
        if (albumId < 0) {
            return null;
        }
        Bitmap artwork = null;
        try {
            final Uri uri = ContentUris.withAppendedId(mArtworkUri, albumId);
            final ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                artwork = BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
            }
        } catch (final IllegalStateException | FileNotFoundException | OutOfMemoryError e) {
            Log.e("wenzhe", e.getMessage());
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_bg,
                    options);
        }
        return artwork;
    }
}
