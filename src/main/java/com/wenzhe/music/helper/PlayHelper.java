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
public class PlayHelper implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {

    private MediaPlayer mediaPlayer;
    private Context context;
    private PlayState state = PlayState.stop;
    private PlayListener playerListener;

    public PlayHelper(Context context,PlayListener playerListener) {
        this.context = context;
        this.playerListener = playerListener;
    }

    public void play(MusicInfo info){
        switch (state) {
            case stop:
                startPlay(info);
                break;
            case pause:
                resume();
                break;
            case playing:
                pause();
                break;
        }

    }

    private void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            state = PlayState.playing;
            playerListener.onMusicStateChanged(state);
        }
    }

    public void startPlay(MusicInfo info) {
        createMediaPlayer();
        state = PlayState.prepare;
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
            state = PlayState.pause;
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

    public PlayState getCurrentState() {
        return state;
    }

    public int getCurrentProgress() {
        if (state == PlayState.prepare) {
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
            state =PlayState.stop;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        state = PlayState.playing;
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
        if (state == PlayState.playing) {
            mp.start();
        }
    }
}
