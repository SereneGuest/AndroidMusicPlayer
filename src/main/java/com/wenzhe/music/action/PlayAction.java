package com.wenzhe.music.action;

import android.support.annotation.Nullable;

import com.wenzhe.music.constants.PlayType;
import com.wenzhe.music.data.MusicInfo;

/**
 * Created by wenzhe on 2016/4/29.
 */
public class PlayAction<T> {
    private PlayType type;
    @Nullable
    private T info;

    public PlayAction(PlayType type, T info) {
        this.type = type;
        this.info = info;
    }

    public PlayType getType() {
        return type;
    }

    @Nullable
    public T getInfo() {
        return info;
    }

}
