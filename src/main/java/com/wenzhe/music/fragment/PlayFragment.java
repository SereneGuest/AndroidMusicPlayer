package com.wenzhe.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wenzhe.music.action.MusicChangeAction;
import com.wenzhe.music.action.PlayAction;
import com.wenzhe.music.action.ThreadAction;
import com.wenzhe.music.ui.PlayUi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by wenzhe on 2016/4/24.
 */
public class PlayFragment extends Fragment {

    private PlayUi mUi;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mUi = new PlayUi(getActivity(), inflater, container);
        return mUi.getView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new PlayAction<>(PlayAction.REQUEST_INFO,null));
        EventBus.getDefault().post(new PlayAction<>(PlayAction.START_TIMER,null));
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().post(new PlayAction<>(PlayAction.STOP_TIMER,null));
        Log.e(TAG, "onPause");
    }

    //负责接受service返回的事件，改变对应UI
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateChange(MusicChangeAction action) {
        //handle all music change event from service and Ui
        switch (action.getType()) {
            case MusicChangeAction.SERVICE_CREATED:
                break;
            case MusicChangeAction.NEXT_MUSIC:
            case MusicChangeAction.PRE_MUSIC:
            case MusicChangeAction.CURRENT_MUSIC:
            case MusicChangeAction.STATE_CHANGED:
                mUi.changeUi(action);
                break;
            case MusicChangeAction.UPDATE_SEEKBAR:
                mUi.updateSeekBar((int)action.getInfo());
                break;
        }
    }
    //bitmap加载成功后被调用
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBitmapAndColorLoaded(ThreadAction action) {
        mUi.setBitmapAndColor(action.getColor(),action.getBitmap(),action.getType());
    }



    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
