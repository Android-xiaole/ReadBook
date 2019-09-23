package com.jj.comics.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.CusNavigationCallback;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.IntRange;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FRouterHelper {
    private String fragmentPath[];
    private Map<String, BaseFragment> mMap;
    private IInterceptor mInterceptor;

    public FRouterHelper(String[] fragmentPath, IInterceptor mInterceptor) {
        this.fragmentPath = fragmentPath;
        this.mInterceptor = mInterceptor;
        mMap = new HashMap<>();
    }

    public BaseFragment getFragment(String fragmentPath){
        return mMap.get(fragmentPath);
    }

    public void switchFragment(final Object object,
                               @IntRange(from = 0) final int index,
                               @IntRange(from = 0) final int current, final int containerId, final CusNavigationCallback callback) {
        if (index == current) return;
        final Context context = getContext(object);
        final String targetPath = fragmentPath[index];
        BaseFragment baseFragment = mMap.get(targetPath);
        final Postcard postcard = getNotNullPostcard(targetPath, index);
        if (baseFragment == null) {
            if (postcard.getType() != RouteType.FRAGMENT) {
                if (callback != null) callback.onLost(postcard);
                return;
            }
        }
        mInterceptor.init(context);
        mInterceptor.process(postcard, new InterceptorCallback() {
            @Override
            public void onContinue(Postcard postcard) {
                //获取目标fragment
                BaseFragment targetFragment = (BaseFragment) getFragmentManager(object).findFragmentByTag(targetPath);
                if (targetFragment == null) {
                    targetFragment = mMap.get(targetPath);
                    if (targetFragment == null)
                        targetFragment = (BaseFragment) ARouter.getInstance().build(targetPath).navigation(context);
                }
                if (mMap.get(targetPath) != targetFragment) mMap.put(targetPath, targetFragment);
                if (callback != null) {
                    callback.onFound(postcard);
                    callback.onGetFragment(index, targetFragment);
                }

                BaseFragment currentFragment = null;
                if (CommonUtil.checkValid(fragmentPath.length, current)) {
                    currentFragment = (BaseFragment) getFragmentManager(object).findFragmentByTag(fragmentPath[current]);
                    if (currentFragment == null) currentFragment = mMap.get(fragmentPath[current]);
                    if (currentFragment == null) {
                        Postcard currentPostcard = getNotNullPostcard(fragmentPath[current], current);
                        if (currentPostcard.getType() == RouteType.FRAGMENT) {
                            currentFragment = (BaseFragment) currentPostcard.navigation(context);
                            if (mMap.get(fragmentPath[current]) != currentFragment)
                                mMap.put(fragmentPath[current], currentFragment);
                        }
                    }
                }

                getFragmentManager(object).executePendingTransactions();//这个方法是确保fragment.isAdded()值同步
                FragmentTransaction fragmentTransaction = getFragmentManager(object).beginTransaction();
                if (currentFragment != null)
                    fragmentTransaction.hide(currentFragment);
                if (targetFragment.isAdded()) {
                    fragmentTransaction.show(targetFragment).commitAllowingStateLoss();
                } else {
                    fragmentTransaction.add(containerId, targetFragment, targetPath).commitAllowingStateLoss();
                }
                if (null != callback) { // Navigation over.
                    callback.onArrival(postcard);
                }
            }

            @Override
            public void onInterrupt(Throwable exception) {
                if (callback != null) callback.onInterrupt(postcard);
            }
        });
    }


    private FragmentManager getFragmentManager(Object object) {
        FragmentManager manager;
        if (!(object instanceof FragmentActivity) && !(object instanceof Fragment)) {
            throw new IllegalArgumentException("host must be FragmentActivity or fragment,please change first argument to FragmentActivity or fragment");
        }
        if (object instanceof FragmentActivity) {
            manager = ((FragmentActivity) object).getSupportFragmentManager();
        }else {
            manager = ((Fragment) object).getChildFragmentManager();
        }
        return manager;
    }

    private Context getContext(final Object object) {
        Context context;
        if (!(object instanceof Activity) && !(object instanceof Fragment)) {
            throw new IllegalArgumentException("host must be activity or fragment,please change first argument to activity or fragment");
        }
        if (object instanceof Activity) {
            context = (Context) object;
        } else
            context = ((Fragment) object).getContext();

        return context;
    }

    private Postcard getNotNullPostcard(String path, int tag) {
        if (TextUtils.isEmpty(path)) {
            return new Postcard(path, null);
        }
        Postcard postcard = ARouter.getInstance().build(path).setTag(tag);
        LogisticsCenter.completion(postcard);
        if (postcard == null) postcard = new Postcard(path, null);
        return postcard;

    }

}
