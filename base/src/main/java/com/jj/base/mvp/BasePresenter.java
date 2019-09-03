package com.jj.base.mvp;


import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.jj.base.exception.IllgealMvpException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.annotation.NotNull;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import javax.inject.Inject;

public class BasePresenter<Rp extends IRepository, V extends IView> implements IPresenter<V> , DefaultLifecycleObserver {

    private static final String TAG = "BasePresenterTag";
    private V v;
    private Rp rp;
    private LifecycleOwner lifecycleOwner;

    public BasePresenter() {}

    public <T> AutoDisposeConverter<T> bindLifecycle() {
        if (null == lifecycleOwner) {
            throw new NullPointerException("lifecycleOwner == null");
        }
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner,Lifecycle.Event.ON_DESTROY));
    }

    @Override
    public V getV() {
        return v;
    }

    @Override
    public void attachV(V v) {
        this.v = v;
    }


    @Override
    public void detachV() {
        if (v != null) {
            v = null;
        }
        if (rp != null) {
            rp = null;
        }
    }

    @Override
    public boolean useEventBus() {
        return false;
    }


    @Override
    public void onCreate(@NotNull LifecycleOwner owner) {
        this.lifecycleOwner = owner;
        if (useEventBus() && !EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {}

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {}

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {}

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {}

    @Override
    public void onDestroy(@NotNull LifecycleOwner owner) {
        detachV();
        if (useEventBus() && EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (lifecycleOwner != null) lifecycleOwner.getLifecycle().removeObserver(this);
        lifecycleOwner = null;
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    void onCreate(@NotNull LifecycleOwner owner);
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    void onDestroy(@NotNull LifecycleOwner owner);
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
//    void onLifecycleChanged(@NotNull LifecycleOwner owner,
//                            @NotNull Lifecycle.Event event);

}
