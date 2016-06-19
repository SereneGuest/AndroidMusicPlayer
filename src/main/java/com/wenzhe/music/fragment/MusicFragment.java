package com.wenzhe.music.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wenzhe.music.PlayService;
import com.wenzhe.music.R;
import com.wenzhe.music.action.MusicChangeAction;
import com.wenzhe.music.action.PlayAction;
import com.wenzhe.music.action.ThreadAction;
import com.wenzhe.music.constants.MusicChangeType;
import com.wenzhe.music.constants.PlayType;
import com.wenzhe.music.data.MusicAdapter;
import com.wenzhe.music.data.MusicInfo;
import com.wenzhe.music.ui.MusicUi;
import com.wenzhe.music.utils.BitmapTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by wenzhe on 2016/4/24.
 */
public class MusicFragment extends Fragment implements MusicAdapter.ListItemClickListener {


    private List<MusicInfo> musicInfos;
    private MusicUi mUi;

    private boolean isPause = false;

    private ThreadPoolExecutor executor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
        //加载bitmap的线程池
        executor = new ThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new
                LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mUi = new MusicUi(getActivity(), inflater, container);
        Log.e(this.getClass().getSimpleName(), "onCreateView");
        return mUi.getView();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //start service
        Intent intent = new Intent(getActivity(), PlayService.class);
        getActivity().startService(intent);
        setListAdapter(mUi.getListView());
        Log.e(this.getClass().getSimpleName(), "onViewCreated");
    }

    //负责接受service返回的事件，改变对应UI
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateChange(MusicChangeAction action) {
        //handle all music change event from service and Ui
        switch (action.getType()) {
            case serviceCreated:
                // data prepare completely get list info
                if (musicInfos == null) {
                    musicInfos = (List<MusicInfo>) action.getInfo();
                    setListAdapter(mUi.getListView());
                }
                break;
            case nextChange:
            case previousChange:
            case currentMusicInfo:
                startLoadBitmapAndColor((MusicInfo) action.getInfo(),action.getType());
                break;
        }
        System.gc();
    }
    //bitmap加载完后调用，
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBitmapAndColorLoaded(ThreadAction action) {
        mUi.setBitmapAndColor(action.getColor(), action.getBitmap(), isPause);
    }

    private void startLoadBitmapAndColor(MusicInfo info, MusicChangeType type) {
        executor.execute(new BitmapTask(getActivity(),
                info.getAlbumId(), 0, BitmapTask.COLOR_VIBRANT_DARK,type));
    }

    public void setListAdapter(RecyclerView recyclerView) {
        //musicInfos = MediaInfo.getMusicInfo(getContext());
        if (musicInfos == null || musicInfos.size() < 1) {
            return;
        }
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        MusicAdapter adapter = new MusicAdapter(musicInfos, getContext());
        adapter.setListItemListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.music_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EventBus.getDefault().post(item);
        Log.e("wenzhe", item.getItemId() + "");
        return true;
    }


    @Override
    public void onItemClicked(int position) {
        EventBus.getDefault().post(new PlayAction<>(PlayType.fromListPlay, position));
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
