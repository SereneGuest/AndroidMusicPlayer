package com.wenzhe.music;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.wenzhe.music.action.PlayAction;
import com.wenzhe.music.constants.AppConstant;
import com.wenzhe.music.constants.PlayType;
import com.wenzhe.music.fragment.MusicFragment;
import com.wenzhe.music.fragment.PlayFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class MusicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager
        .OnBackStackChangedListener, DrawerLayout.DrawerListener {

    private FragmentManager fm;
    private String mCurrentFragment = AppConstant.MUSIC_FRAGMENT;
    private final String TAG = this.getClass().getSimpleName();

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initView();
        initFragment();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(this);
    }

    private void initFragment() {
        fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, new MusicFragment());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean needCloseDrawer = false;
    private int navigationId;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        navigationId = item.getItemId();
        needCloseDrawer = true;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navigationClickEvent() {
        switch (navigationId) {
            case R.id.nav_home:
                if (mCurrentFragment.equals(AppConstant.PLAY_FRAGMENT)) {
                    fm.popBackStack();
                }
                mCurrentFragment = AppConstant.MUSIC_FRAGMENT;
                break;
            case R.id.nav_list:
                goToPlayFragment();
                break;
            case R.id.nav_exit:
                EventBus.getDefault().post(new PlayAction<>(PlayType.exit, null));
                finish();
                break;
        }
    }

    public void goToPlayFragment() {
        if (mCurrentFragment != AppConstant.PLAY_FRAGMENT) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.add(R.id.container, new PlayFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            mCurrentFragment = AppConstant.PLAY_FRAGMENT;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //设置optionMenu
    @Subscribe
    public void onUiReady(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    //option menu click event
    @Subscribe
    public void onMenuClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_playing:
                goToPlayFragment();
                break;
        }
    }
    @Override
    public void onBackStackChanged() {
        Log.d(TAG, "back stack count:" + fm.getBackStackEntryCount());
        if (fm.getBackStackEntryCount() == 0) {
            mCurrentFragment = AppConstant.MUSIC_FRAGMENT;
        }
    }

    //DrawerLayout Event
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }
    @Override
    public void onDrawerOpened(View drawerView) {

    }
    @Override
    public void onDrawerClosed(View drawerView) {
        if (needCloseDrawer) {
            navigationClickEvent();
            needCloseDrawer = false;
        }
    }
    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
