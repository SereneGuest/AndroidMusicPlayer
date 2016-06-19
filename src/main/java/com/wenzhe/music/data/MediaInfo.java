package com.wenzhe.music.data;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.wenzhe.music.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen-zhe on 2015/5/28.
 */
public class MediaInfo {
    //查询音乐
    public static List<MusicInfo> getMusicInfo(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.TITLE);
        List<MusicInfo> musicInfos = new ArrayList<>();
        assert cursor != null;
        for (int i = 0; i < cursor.getCount(); i++) {
            MusicInfo musicInfo = new MusicInfo();
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));

            if (isMusic != 0 && duration>10000) {
                musicInfo.setId(id);
                musicInfo.setAlbumId(albumId);
                musicInfo.setAlbum(album);
                musicInfo.setTitle(title);
                musicInfo.setArtist(artist);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                musicInfo.setUrl(url);
                musicInfos.add(musicInfo);
            }

        }
        cursor.close();
        return musicInfos;
    }

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

    public static String formatDouble(long size) {
        double result = size / (1024 * 1024);
        DecimalFormat decimalFormat = new DecimalFormat("##.0");
        return decimalFormat.format(result);
    }


    //歌曲详细信息
    public static void musicDetail(int id,List<MusicInfo> musicInfos,Context context){
        String title = musicInfos.get(id).getTitle();
        String artist = musicInfos.get(id).getArtist();
        String album = musicInfos.get(id).getAlbum();
        String duration = MediaInfo.formatTime(musicInfos.get(id).getDuration());
        String size = MediaInfo.formatDouble(musicInfos.get(id).getSize());
        String url =musicInfos.get(id).getUrl();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("歌曲信息");
        builder.setMessage("标题："+title+"\n\n"+"歌手："+artist+"\n\n"+"专辑："+album+"\n\n"+
                "长度："+duration+"\n\n"+"大小："+size+" MB\n\n"+"路径："+url+"\n");
        builder.setPositiveButton("确定",null);
        builder.show();
    }

    public static Bitmap getAlbumImgBitmap(long id,Context context){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg);
        String selection = MediaStore.Audio.Albums._ID+" = "+id;
        String [] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection, selection, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        if(cursor == null)
            return bitmap;
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        if(path==null)
            return bitmap;
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(path);
                bitmap = BitmapFactory.decodeStream(fileInputStream);
                return bitmap;
            } catch (FileNotFoundException e) {
                return bitmap;
            }
        }

    }

}
