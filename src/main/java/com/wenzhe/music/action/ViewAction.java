package com.wenzhe.music.action;

import android.view.View;

import com.wenzhe.music.constants.ViewType;

/**
 * Created by wenzhe on 2016/4/26.
 */
public class ViewAction {

    private View view;
    private ViewType type;

    public ViewAction(View view,ViewType type) {
        this.view = view;
        this.type = type;
    }

    public View getView() {
        return this.view;
    }

    public ViewType getType() {
        return this.type;
    }
}
