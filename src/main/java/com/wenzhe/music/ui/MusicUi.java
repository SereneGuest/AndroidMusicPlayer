package com.wenzhe.music.ui;


import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.wenzhe.music.R;
import com.wenzhe.music.constants.AppConstant;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wenzhe on 2016/4/26.
 */
public class MusicUi {

    private View rootView;
    private Activity activity;
    private CollapsingToolbarLayout header;
    private ImageView headerImg;
    // private TextView imgCover;

    private int currentColor;


    public MusicUi(Activity activity, LayoutInflater inflater, ViewGroup container) {
        this.activity = activity;
        this.rootView = inflater.inflate(R.layout.music_fragment, container, false);
        initView(rootView, activity);
    }

    private void initView(View rootView, Activity activity) {
        setWindowFlag(activity.getWindow());

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Music");
        EventBus.getDefault().post(toolbar);
        header = (CollapsingToolbarLayout) rootView.findViewById(R.id
                .collapse_layout);
        //header.setContentScrimColor(0x99eeee44);
        //header.setStatusBarScrimColor(0x99eeee44);
        headerImg = (ImageView) rootView.findViewById(R.id.header_img);

        currentColor = activity.getResources().getColor(R.color.colorPrimary);


        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //imgCover = (TextView) rootView.findViewById(R.id.img_cover);
    }

    public RecyclerView getListView() {
        return (RecyclerView) rootView.findViewById(R.id.music_list);
    }

    public View getView() {
        return rootView;
    }

    public void setWindowFlag(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public void setBitmapAndColor(int color, Bitmap bitmap,boolean isPause) {
        //imgCover.setBackgroundColor(color);
        //imgCover.setAlpha(0.3f);
        headerImg.setImageBitmap(bitmap);
        if (isPause) {
            setColor(color);
        } else {
            getValueAnimator(currentColor,color).start();
        }
        currentColor = color;

    }

    private void setColor(int color) {
        header.setContentScrimColor(color);
        header.setStatusBarScrimColor(color);
        activity.getWindow().setNavigationBarColor(color);
    }

    public ValueAnimator getValueAnimator(int from, int to) {
        ValueAnimator animator = ValueAnimator.ofArgb(from, to);
        animator.setDuration(AppConstant.ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setColor((int)animation.getAnimatedValue());
            }
        });
        return animator;
    }

}
