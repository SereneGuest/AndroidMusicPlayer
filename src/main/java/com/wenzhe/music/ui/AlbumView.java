package com.wenzhe.music.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wenzhe.music.action.MusicChangeAction;
import com.wenzhe.music.constants.AppConstant;
import com.wenzhe.music.constants.MusicChangeType;
import com.wenzhe.music.utils.Devices;


/**
 * Created by wenzhe on 2016/6/18.
 */
public class AlbumView extends RelativeLayout {

    private Context context;
    private WindowManager manager;

    private int width,height,radius;

    public AlbumView(Context context) {
        this(context,null);
    }

    public AlbumView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void init(Bitmap bitmap) {
        width = Devices.getScreenWidth(manager);
        height = (int) Devices.getAlbumImgHeight(manager);
        radius = (int) Math.sqrt(width * width + height * height);
        addView(createImageView(bitmap));
    }

    private ImageView createImageView(Bitmap bitmap) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, height);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    public void setImageWithAnimation(Bitmap bitmap, String type) {
        addView(createImageView(bitmap));
        createReveal(getChildAt(getChildCount() - 1),type).start();

    }

    private Animator createReveal(View view,String type) {
        Animator animator1;
        switch (type) {
            case MusicChangeAction.NEXT_MUSIC:
                animator1 = ViewAnimationUtils.createCircularReveal(view,0,
                        height,0,radius);
                break;
            case MusicChangeAction.PRE_MUSIC:
                animator1 = ViewAnimationUtils.createCircularReveal(view,width,
                        height,0,radius);
                break;
            default:
                animator1 = ViewAnimationUtils.createCircularReveal(view,width/2,
                        height/2,0,radius/2);
                break;
        }
        animator1.setDuration(AppConstant.ANIMATION_DURATION);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeViewAt(0);
                //Log.e("wenzhe", "count:" + getChildCount());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator1;
    }

}
