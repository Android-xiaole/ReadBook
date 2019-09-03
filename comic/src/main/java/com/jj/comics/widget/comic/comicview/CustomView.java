package com.jj.comics.widget.comic.comicview;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.jj.base.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.RequiresApi;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class CustomView extends RelativeLayout {
    private Unbinder mBinder;

    public CustomView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (useEventBus() && !EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (getLayoutId() > 0) {
            LayoutInflater.from(context).inflate(getLayoutId(), this, true);
            bindUI(this);
        }
        initData(context, attrs);
    }

    protected abstract int getLayoutId();

    public void bindUI(View rootView) {
        mBinder = ButterKnife.bind(this);
    }

    public void release() {
        if (useEventBus() && EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (mBinder != null) mBinder.unbind();
    }

    public void initData(Bundle savedInstanceState) {

    }

    public abstract void initData(Context context, AttributeSet attrs);


    public BaseActivity getActivity() {
        if (getContext() instanceof BaseActivity) {
            return (BaseActivity) getContext();
        }
        return null;
    }

    public boolean useEventBus() {
        return false;
    }
}
