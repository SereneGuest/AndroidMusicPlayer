package com.wenzhe.music.action;

import android.support.annotation.Nullable;

import com.wenzhe.music.constants.MusicChangeType;
import com.wenzhe.music.constants.PlayType;

/**
 * Created by wenzhe on 2016/4/29.
 */
public class MusicChangeAction<T> {
    private MusicChangeType type;
    @Nullable
    private T info;

    public MusicChangeAction(MusicChangeType type, T info) {
        this.type = type;
        this.info = info;
    }

    public MusicChangeType getType() {
        return type;
    }

    @Nullable
    public T getInfo() {
        return info;
    }

}
