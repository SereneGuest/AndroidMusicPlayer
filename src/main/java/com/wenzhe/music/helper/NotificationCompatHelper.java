package com.wenzhe.music.helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.wenzhe.music.R;
import com.wenzhe.music.data.MusicInfo;
import com.wenzhe.music.utils.MediaUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wenzhe on 2016/5/8.
 */
public class NotificationCompatHelper {

    private Context context;
    private NotificationManagerCompat managerCompat;

    private final int REQUEST_CODE = 0x100;
    private static final int NOTIFICATION_ID = 0x1000;

    public final static String NOTIFICATION_PREVIOUS = "notification_previous";
    public final static String NOTIFICATION_PLAY = "notification_play";
    public final static String NOTIFICATION_NEXT = "notification_next";
    public final static String NOTIFICATION_ACTIVITY = "notification_activity";

    private PendingIntent playIntent, previousIntent, nextIntent, activityIntent;

    public NotificationCompatHelper(Context context) {
        this.context = context;

        managerCompat = NotificationManagerCompat.from(context);

        String pkg = context.getPackageName();
        playIntent = PendingIntent.getService(context, REQUEST_CODE, new Intent(NOTIFICATION_PLAY)
                .setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        previousIntent = PendingIntent.getService(context, REQUEST_CODE, new Intent
                (NOTIFICATION_PREVIOUS)
                .setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        nextIntent = PendingIntent.getService(context, REQUEST_CODE, new Intent(NOTIFICATION_NEXT)
                .setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        activityIntent = PendingIntent.getService(context, REQUEST_CODE, new Intent
                (NOTIFICATION_ACTIVITY)
                .setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        managerCompat.cancelAll();
    }

    private NotificationCompat.Builder createNotification(MusicInfo info) {
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap
        // .ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2))
                .setColor(Color.GRAY)
                .setSmallIcon(R.drawable.ic_icon_notification)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(info.getTitle())
                .setContentText(info.getArtist())
                .addAction(R.mipmap.widget_previous_normal, "previous", previousIntent)
                .addAction(R.mipmap.widget_play_normal, "play", playIntent)
                .addAction(R.mipmap.widget_next_normal, "next", nextIntent);
        return builder;
    }

    public void show(MusicInfo info) {
        //managerCompat.notify(NOTIFICATION_ID, createNotification(info));
        new MyTask(createNotification(info)).execute(info.getAlbumId());
    }

    private class MyTask extends AsyncTask<Long, Void, Bitmap> {
        private NotificationCompat.Builder builder;
        public MyTask(NotificationCompat.Builder builder) {
            this.builder = builder;
        }

        @Override
        protected Bitmap doInBackground(Long... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            return MediaUtils.getAlbumImgBitmap(params[0], context, options);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            builder.setLargeIcon(bitmap);
            managerCompat.notify(NOTIFICATION_ID,builder.build());
        }
    }
}
