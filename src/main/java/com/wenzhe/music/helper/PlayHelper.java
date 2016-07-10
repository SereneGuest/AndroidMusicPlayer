package com.wenzhe.music.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;

import com.wenzhe.music.constants.PlayState;
import com.wenzhe.music.data.MusicInfo;

import java.io.IOException;

/**
 * Created by wenzhe on 2016/4/29.
 */
public class PlayHelper implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener {

    public static final String STATE_PLAYING = "state_playing";
    public static final String STATE_PAUSE = "state_pause";
    public static final String STATE_STOP = "state_stop";
    public static final String STATE_PREPARE = "state_prepare";

    private MediaPlayer mediaPlayer;
    private Context context;
    private String state = STATE_STOP;
    private PlayListener playerListener;

    public PlayHelper(Context context,PlayListener playerListener) {
        this.context = context;
        this.playerListener = playerListener;
    }

    public void play(MusicInfo info){
        switch (state) {
            case STATE_STOP:
                startPlay(info);
                break;
            case STATE_PAUSE:
                resume();
                break;
            case STATE_PLAYING:
                pause();
                break;
        }

    }

    private void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            playerListener.onMusicStateChanged(state);
        }
    }

    public void startPlay(MusicInfo info) {
        createMediaPlayer();
        state = STATE_PREPARE;
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(info.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    public void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            state = STATE_PAUSE;
            playerListener.onMusicStateChanged(state);
        }
    }

    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    private void createMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
        }else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public String getCurrentState() {
        return state;
    }

    public int getCurrentProgress() {
        if (state == STATE_PREPARE) {
            return 0;
        }
        return mediaPlayer == null ? 0 : mediaPlayer.getCurrentPosition();
    }

    public void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            state =STATE_STOP;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        state = STATE_PLAYING;
        playerListener.onMusicStateChanged(state);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playerListener != null) {
            playerListener.onCompletion(mp);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (state == STATE_PLAYING) {
            mp.start();
        }
    }
}
