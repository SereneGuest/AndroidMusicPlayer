package com.wenzhe.music.action;

import android.support.annotation.Nullable;

import com.wenzhe.music.constants.PlayType;
import com.wenzhe.music.data.MusicInfo;

/**
 * Created by wenzhe on 2016/4/29.
 */
public class PlayAction<T> {

    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String BUTTON_PLAY = "button_play";
    public static final String LIST_PLAY = "list_play";
    public static final String REQUEST_INFO = "request_info";
    public static final String START_TIMER = "start_timer";
    public static final String STOP_TIMER = "stop_timer";
    public static final String SEEKBAR_CHANGE = "seek_bar_change";
    public static final String EXIT = "exit";
    public static final String NEED_UPDATE_UI = "need_update_ui";

    private String type;
    @Nullable
    private T info;

    public PlayAction(String type, T info) {
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
