package com.wenzhe.music;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wenzhe.music.action.MusicChangeAction;
import com.wenzhe.music.action.PlayAction;
import com.wenzhe.music.action.ThreadAction;
import com.wenzhe.music.constants.MusicChangeType;
import com.wenzhe.music.constants.PlayState;
import com.wenzhe.music.data.MediaInfo;
import com.wenzhe.music.data.MusicInfo;
import com.wenzhe.music.helper.NotificationCompatHelper;
import com.wenzhe.music.helper.NotificationHelper;
import com.wenzhe.music.helper.PlayHelper;
import com.wenzhe.music.helper.PlayListener;
import com.wenzhe.music.helper.TimerHelper;
import com.wenzhe.music.helper.TimerListener;
import com.wenzhe.music.utils.MediaUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by wenzhe on 2016/4/29.
 */
public class PlayService extends Service implements PlayListener, TimerListener {

    private PlayHelper playHelper;
    private List<MusicInfo> musicInfos;
    private int mCurrentMusic = 0;
    private final String TAG = this.getClass().getSimpleName();
    private TimerHelper timerHelper;
    private NotificationHelper notificationHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playHelper = new PlayHelper(getApplicationContext(), this);
        musicInfos = MediaInfo.getMusicInfo(getApplicationContext());
        timerHelper = new TimerHelper(this);
        notificationHelper = new NotificationHelper(this);
        //timerHelper.startTimer();
        EventBus.getDefault().register(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*boolean isNotificationEvent = false;
        if (intent != null) {
            isNotificationEvent = (intent.getAction() != null) && intent.getAction().equals
                    (NotificationHelper.NOTIFICATION_EVENT);
            if (isNotificationEvent) {
                Log.e(TAG, intent.getAction());
                onNotificationEvent(intent.getStringExtra(NotificationHelper.NOTIFICATION_TAG));
            }
        }*/
        if (intent != null && intent.getAction() != null) {
            Log.e(TAG, intent.getAction());
            onNotificationEvent(intent.getAction());
        }

        if (musicInfos != null && musicInfos.size() > 0) {
            EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.SERVICE_CREATED,
                    musicInfos));
            EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.CURRENT_MUSIC,
                    musicInfos.get(mCurrentMusic)));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void onNotificationEvent(String action) {
        switch (action) {
            case NotificationHelper.NOTIFICATION_PREVIOUS:
                previous();
                break;
            case NotificationHelper.NOTIFICATION_PLAY:
                playHelper.play(musicInfos.get(mCurrentMusic));
                break;
            case NotificationHelper.NOTIFICATION_NEXT:
                next();
                break;
        }
    }

    //接受相关ui、fragment、activity事件
    @Subscribe
    public void PlayAction(PlayAction action) {
        switch (action.getType()) {
            case PlayAction.LIST_PLAY:
                mCurrentMusic = (int) action.getInfo();
                playHelper.startPlay(musicInfos.get(mCurrentMusic));
                EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.CURRENT_MUSIC
                        , musicInfos.get(mCurrentMusic)));
                break;
            case PlayAction.PREVIOUS:
                previous();
                break;
            case PlayAction.NEXT:
                next();
                break;
            case PlayAction.BUTTON_PLAY:
                playHelper.play(musicInfos.get(mCurrentMusic));
                break;
            case PlayAction.REQUEST_INFO:
                EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.CURRENT_MUSIC,
                        musicInfos.get(mCurrentMusic)));
                EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.STATE_CHANGED,
                        playHelper.getCurrentState()));
                break;
            case PlayAction.START_TIMER:
                timerHelper.startTimer();
                break;
            case PlayAction.STOP_TIMER:
                timerHelper.stopTimer();
                break;
            case PlayAction.SEEKBAR_CHANGE:
                playHelper.seekTo((int) action.getInfo());
                break;
            case PlayAction.EXIT:
                stopSelf();
                break;
        }
    }


    private void next() {
        mCurrentMusic += 1;
        if (mCurrentMusic >= musicInfos.size()) {
            mCurrentMusic = 0;
        }
        playHelper.startPlay(musicInfos.get(mCurrentMusic));
        EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.NEXT_MUSIC, musicInfos
                .get(mCurrentMusic)));
        notificationHelper.show(musicInfos.get(mCurrentMusic), playHelper.getCurrentState());
    }


    private void previous() {
        mCurrentMusic -= 1;
        if (mCurrentMusic < 0) {
            mCurrentMusic = musicInfos.size() - 1;
        }
        playHelper.startPlay(musicInfos.get(mCurrentMusic));
        EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.PRE_MUSIC,
                musicInfos.get(mCurrentMusic)));
        notificationHelper.show(musicInfos.get(mCurrentMusic), playHelper.getCurrentState());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (playHelper != null) {
            playHelper.release();
        }
        if (notificationHelper != null) {
            notificationHelper.cancel();
        }
        timerHelper.stopTimer();

        Log.e(TAG, "destroy service");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG, "onTrimMemory");
        System.gc();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public void onMusicStateChanged(String state) {
        EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.STATE_CHANGED, state));
        notificationHelper.show(musicInfos.get(mCurrentMusic), state);
    }

    //timer callback
    @Override
    public void onStartJob() {
        int currentProgress = playHelper.getCurrentProgress();
        EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.UPDATE_SEEKBAR,
                currentProgress));
    }



    /*private class MyTask extends AsyncTask<Long, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Long... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            return MediaUtils.getAlbumImgBitmap(params[0].longValue(), PlayService.this, options);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            notificationHelper.setNotificationBitmap(bitmap);
            notificationHelper.show(null);
        }
    }*/
}
