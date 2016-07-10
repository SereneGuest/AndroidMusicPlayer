package com.wenzhe.music.helper;

import android.media.MediaPlayer;

import com.wenzhe.music.constants.PlayState;

/**
 * Created by wenzhe on 2016/4/29.
 */
public interface PlayListener {
    void onCompletion(MediaPlayer mp);
    void onMusicStateChanged(String state);
    //void onSeekComplete(MediaPlayer mp);
    //void onError(MediaPlayer mp, int what, int extra);
}
