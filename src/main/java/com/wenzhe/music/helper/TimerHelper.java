package com.wenzhe.music.helper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wenzhe on 2016/5/5.
 */
public class TimerHelper {
    private TimerListener listener;
    private Timer timer;
    public TimerHelper(TimerListener listener) {
        this.listener = listener;
        timer = new Timer();
    }

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.onStartJob();
            }
        },0,1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


}
