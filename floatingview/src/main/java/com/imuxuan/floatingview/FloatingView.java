package com.imuxuan.floatingview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;

import com.imuxuan.floatingview.utils.EnContext;
import com.imuxuan.floatingview.utils.SystemUtils;
import com.sina.weibo.sdk.utils.UIUtils;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @ClassName FloatingView
 * @Description 悬浮窗管理器
 * @Author Yunpeng Li
 * @Creation 2018/3/15 下午5:05
 * @Mender Yunpeng Li
 * @Modification 2018/3/15 下午5:05
 */
public class FloatingView implements IFloatingView {

    private EnFloatingView mEnFloatingView;
    private static volatile FloatingView mInstance;
    private FrameLayout mContainer;

    private long mShowTime = 0;
    private TimerTask timerTask;

    private FloatingView() {
    }

    public static FloatingView get() {
        if (mInstance == null) {
            synchronized (FloatingView.class) {
                if (mInstance == null) {
                    mInstance = new FloatingView();
                }
            }
        }
        return mInstance;
    }

    @Override
    public FloatingView remove() {
        if (timerTask != null) timerTask.cancel();
        timerTask = null;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mEnFloatingView == null) {
                    return;
                }
                if (ViewCompat.isAttachedToWindow(mEnFloatingView) && mContainer != null) {
                    Animation animation = AnimationUtils.makeOutAnimation(mEnFloatingView.getContext(), false);
                    animation.setDuration(600);
                    mEnFloatingView.startAnimation(animation);
                    mContainer.removeView(mEnFloatingView);
                }
                mEnFloatingView = null;
            }
        });
        return this;
    }

    public FloatingView removeNow() {
        if (timerTask != null) timerTask.cancel();
        timerTask = null;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mEnFloatingView == null) {
                    return;
                }
                if (ViewCompat.isAttachedToWindow(mEnFloatingView) && mContainer != null) {
                    Animation animation = AnimationUtils.makeOutAnimation(mEnFloatingView.getContext(), false);
                    animation.setDuration(600);
                    mEnFloatingView.startAnimation(animation);
                    mContainer.removeView(mEnFloatingView);
                }
                mEnFloatingView = null;
            }
        });
        return this;
    }

    private void ensureMiniPlayer(Context context,String msg,String url) {
        synchronized (this) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    remove();
                }
            };
            if (timerTask != null) new Timer().schedule(timerTask,2000);

            if (mEnFloatingView != null) {
                return;
            }
            mEnFloatingView = new EnFloatingView(context.getApplicationContext(),msg,url);
            mEnFloatingView.setLayoutParams(getParams());
            addViewToWindow(mEnFloatingView);
        }
    }

    @Override
    public FloatingView add() {
        ensureMiniPlayer(EnContext.get(),"","");
        return this;
    }

    public FloatingView add(String msg,String url) {
        ensureMiniPlayer(EnContext.get(),msg,url);
        return this;
    }

    @Override
    public FloatingView attach(Activity activity) {
        attach(getActivityRoot(activity));
        return this;
    }

    @Override
    public FloatingView attach(FrameLayout container) {
        if (container == null || mEnFloatingView == null) {
            mContainer = container;
            return this;
        }
        if (mEnFloatingView.getParent() == container) {
            return this;
        }
        if (mContainer != null && mEnFloatingView.getParent() == mContainer) {
            mContainer.removeView(mEnFloatingView);
        }
        mContainer = container;
        container.addView(mEnFloatingView);
        return this;
    }

    @Override
    public FloatingView detach(Activity activity) {
        detach(getActivityRoot(activity));
        return this;
    }

    @Override
    public FloatingView detach(FrameLayout container) {
        if (mEnFloatingView != null && container != null && ViewCompat.isAttachedToWindow(mEnFloatingView)) {
            container.removeView(mEnFloatingView);
        }
        if (mContainer == container) {
            mContainer = null;
        }
        return this;
    }

    @Override
    public EnFloatingView getView() {
        return mEnFloatingView;
    }

    @Override
    public FloatingView icon(@DrawableRes int resId) {
        if (mEnFloatingView != null) {
            mEnFloatingView.setIconImage(resId);
        }
        return this;
    }

    @Override
    public FloatingView icon(String resId) {
        if (mEnFloatingView != null) {
            mEnFloatingView.setIconImage(resId);
        }
        return this;
    }

    @Override
    public FloatingView layoutParams(ViewGroup.LayoutParams params) {
        if (mEnFloatingView != null) {
            mEnFloatingView.setLayoutParams(params);
        }
        return this;
    }

    @Override
    public FloatingView listener(MagnetViewListener magnetViewListener) {
        if (mEnFloatingView != null) {
            mEnFloatingView.setMagnetViewListener(magnetViewListener);
        }
        return this;
    }

    private void addViewToWindow(final EnFloatingView view) {
        if (mContainer == null) {
            return;
        }
        mContainer.addView(view);
        Animation animation = AnimationUtils.makeInAnimation(view.getContext(), true);
        animation.setDuration(600);
        view.startAnimation(animation);
    }

    private FrameLayout.LayoutParams getParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.START;
        int statusBarHeight = SystemUtils.getStatusBarHeight(getView().getContext());


            if(mContainer != null && mContainer.getPaddingTop() >= statusBarHeight) {
                params.setMargins(0, UIUtils.dip2px(90,getView().getContext()) - statusBarHeight
                        , params.rightMargin,params.bottomMargin);
            }else {
                params.setMargins(0, UIUtils.dip2px(90,getView().getContext())
                        , params.rightMargin,params.bottomMargin);
            }


        return params;
    }


    private FrameLayout getActivityRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}