package com.wenzhe.music.action;

import android.support.annotation.Nullable;

import com.wenzhe.music.constants.MusicChangeType;
import com.wenzhe.music.constants.PlayType;

/**
 * Created by wenzhe on 2016/4/29.
 */
public class MusicChangeAction<T> {

    public static final String NEXT_MUSIC = "next_music";
    public static final String PRE_MUSIC = "previous_music";
    public static final String STATE_CHANGED = "state_changed";
    public static final String SERVICE_CREATED = "service_created";
    public static final String CURRENT_MUSIC = "current_music";
    public static final String UPDATE_SEEKBAR = "update_seek_bar";

    private String type;
    @Nullable
    private T info;

    public MusicChangeAction(String type, T info) {
        this.type = type;
        this.info = info;
    }

    public String getType() {
        return type;
    }

    @Nullable
    public T getInfo() {
        return info;
    }

}
