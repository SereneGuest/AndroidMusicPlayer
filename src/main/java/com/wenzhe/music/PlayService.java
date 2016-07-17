package com.wenzhe.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wenzhe.music.action.MusicChangeAction;
import com.wenzhe.music.action.PlayAction;
import com.wenzhe.music.data.MediaInfo;
import com.wenzhe.music.data.MusicInfo;
import com.wenzhe.music.helper.NotificationHelper;
import com.wenzhe.music.helper.PlayHelper;
import com.wenzhe.music.helper.PlayListener;
import com.wenzhe.music.helper.TimerHelper;
import com.wenzhe.music.helper.TimerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    private IntentFilter filter;

    private boolean needUpdateUi;

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
        EventBus.getDefault().register(this);
        filter = new IntentFilter();
        filter.addAction(NotificationHelper.NOTIFICATION_NEXT);
        filter.addAction(NotificationHelper.NOTIFICATION_PLAY);
        filter.addAction(NotificationHelper.NOTIFICATION_PREVIOUS);
        registerReceiver(notificationReceiver, filter);
        Log.e(TAG, "service:onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (musicInfos != null && musicInfos.size() > 0) {
            EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.SERVICE_CREATED,
                    musicInfos));
        }
        Log.e(TAG, "service:onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NotificationHelper.NOTIFICATION_NEXT:
                    next();
                    break;
                case NotificationHelper.NOTIFICATION_PLAY:
                    playHelper.play(musicInfos.get(mCurrentMusic));
                    break;
                case NotificationHelper.NOTIFICATION_PREVIOUS:
                    previous();
                    break;
            }
        }
    };


    //接受相关ui、fragment、activity事件
    @Subscribe
    public void PlayAction(PlayAction action) {
        switch (action.getType()) {
            case PlayAction.LIST_PLAY:
                fromListPlay(action);
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
            case PlayAction.NEED_UPDATE_UI:
                needUpdateUi = (boolean) action.getInfo();
                Log.e(TAG, "---" + needUpdateUi);
                break;
            case PlayAction.EXIT:
                stopSelf();
                break;
        }
    }



    private void fromListPlay(PlayAction action) {
        mCurrentMusic = (int) action.getInfo();
        playHelper.startPlay(musicInfos.get(mCurrentMusic));
        if (needUpdateUi) {
            EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.CURRENT_MUSIC
                    , musicInfos.get(mCurrentMusic)));
        }
    }

    private void next() {
        mCurrentMusic += 1;
        if (mCurrentMusic >= musicInfos.size()) {
            mCurrentMusic = 0;
        }
        playHelper.startPlay(musicInfos.get(mCurrentMusic));
        if (needUpdateUi) {
            EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.NEXT_MUSIC, musicInfos
                    .get(mCurrentMusic)));
        }
        notificationHelper.show(musicInfos.get(mCurrentMusic), playHelper.getCurrentState());
    }


    private void previous() {
        mCurrentMusic -= 1;
        if (mCurrentMusic < 0) {
            mCurrentMusic = musicInfos.size() - 1;
        }
        playHelper.startPlay(musicInfos.get(mCurrentMusic));
        if (needUpdateUi) {
            EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.PRE_MUSIC,
                    musicInfos.get(mCurrentMusic)));
        }
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
        unregisterReceiver(notificationReceiver);

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
        if (needUpdateUi) {
            EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.STATE_CHANGED, state));
        }
        notificationHelper.show(musicInfos.get(mCurrentMusic), state);
    }

    //timer callback
    @Override
    public void onStartJob() {
        int currentProgress = playHelper.getCurrentProgress();
        EventBus.getDefault().post(new MusicChangeAction<>(MusicChangeAction.UPDATE_SEEKBAR,
                currentProgress));
    }


}
