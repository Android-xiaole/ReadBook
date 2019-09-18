package com.jj.novelpro.present;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.BaseApplication;
import com.jj.base.CusNavigationCallback;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.common.interceptor.LoginInterceptor;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.common.net.download.DownLoadManager;
import com.jj.comics.common.net.download.DownloadProgressListener;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.FeedbackStatusModel;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignTaskResponse;
import com.jj.comics.data.model.UpdateModelProxy;
import com.jj.comics.util.FRouterHelper;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.TencentHelper;
import com.jj.novelpro.R;
import com.jj.novelpro.activity.MainActivity;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class MainPresenter extends BasePresenter<BaseRepository, MainContract.IMainView> implements MainContract.IMainPresenter {
    private String fragmentPath[] = new String[]{RouterMap.COMIC_HOME_FRAGMENT,
            RouterMap.COMIC_SORT_FRAGMENT,
            RouterMap.COMIC_MONEY_FRAGMENT,
            RouterMap.COMIC_BOOKCOLLECTION_FRAGMENT,
            RouterMap.COMIC_MINE_FRAGMENT};

    private FRouterHelper mFRouterHelper;

    public MainPresenter() {
        mFRouterHelper = new FRouterHelper(fragmentPath, new LoginInterceptor());
    }

    @Override
    public void dealTokenUseLess() {
        Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
                //删除token
                SharedPreManger.getInstance().removeToken();
                //删除所有本地保存的用户
                LoginHelper.logOffAllUser();
                MobclickAgent.onProfileSignOff();
                TencentHelper.getTencent().logout(BaseApplication.getApplication());
                AccessTokenKeeper.clear(BaseApplication.getApplication());
                emitter.onNext(true);
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(getV().<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
//                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getV())))
                .as(bindLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void accept(Boolean s) throws Exception {
//                        getV().exitLogin();
                        ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY)
                                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                .navigation(getV().getActivity(), RequestCode.LOGIN_REQUEST_CODE);
                    }
                });
    }

    @Override
    public void switchFragment(@IntRange(from = 0, to = 4) final int index, @IntRange(from = 0, to = 4) final int current, final CusNavigationCallback callback) {
        mFRouterHelper.switchFragment(getV(), index, current, R.id.fl_container, callback);
    }


    private boolean getAgree() {
        return SharedPref.getInstance().getBoolean(Constants.SharedPrefKey.AGREE_KEY, false);
    }

//    public void showPopupWindow(final RadioGroup mRadioGroup) {
//        if (!getAgree()) {
//            Observable.timer(2, TimeUnit.SECONDS)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
////                    .compose(getV().<Long>bindUntilEvent(ActivityEvent.DESTROY))
////                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getV())))
//                    .as(bindLifecycle())
//                    .subscribe(new Consumer<Long>() {
//                        @Override
//                        public void accept(Long aLong) throws Exception {
//                            if (!getAgree()) {
//                                AgreePopupWindow popupWindow = new AgreePopupWindow(getV());
//                                popupWindow.setText("");
//                                popupWindow.showAtLocation(mRadioGroup, Gravity.CENTER, 0, 0);
//
//                            }
//                        }
//                    });
//
//        }
//    }

    @Override
    public void onActivityResult(List<Fragment> fragments,int requestCode, int resultCode, @Nullable Intent data) {
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

        }
    }

    @Override
    public void checkUpdate() {
        ProductRepository.getInstance().getChannelUpdateInfo(getV().getClass().getName())
                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getV())))
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<UpdateModelProxy>() {
                    @Override
                    public void onNext(UpdateModelProxy channelUpdate) {
                        //model类以判断是否需要更新  此时可直接走升级逻辑
                        if (Constants.SHOW_UPDATE_DIALOG) {
                            // 判断是否升级
                            getV().updateAlert(channelUpdate.getProductChannelUpdate());
                        } else {
                            goDown(channelUpdate.getProductChannelUpdate().getProductDownUrl());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        LogUtil.e("升级APP信息--->" + error.getMessage());
                    }
                });
//                .subscribe(new Consumer<ChannelUpdate>() {
//                    @Override
//                    public void accept(ChannelUpdate channelUpdate) throws Exception {
//                        if (PackageUtil.getPackageInfo().versionCode < Integer.parseInt(channelUpdate.getProductVersion())) {
//                            if (Constants.SHOW_UPDATE_DIALOG) {
//                                // 判断是否升级
//                                getV().updateAlert(channelUpdate);
//                            } else {
//                                goDown(channelUpdate.getProductDownUrl());
//                            }
//                        } else {
//                            LogUtil.e("下载：已经是最新版");
//                        }
//                    }
//                });
    }

    public void goDown(final String updateAppUrl) {
        getV().sendMessage(MainActivity.START_DOWNLOAD, null);

        final String file = Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + File.separator + PackageUtil.getAppName(BaseApplication.getApplication()) + File.separator;
        final File downFile = new File(file + FileUtil.getFileName(updateAppUrl));
        if (downFile.exists()) {
            downFile.delete();
        }
        DownLoadManager.builder().downApkFile(getV().getActivity(), new ResourceSubscriber() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {
                //不管什么错误先删除文件再说，防止出现不可预知的BUG
                if (downFile.exists()) {
                    downFile.delete();
                }
                getV().sendMessage(MainActivity.DOWN_FAIL, t);
            }

            @Override
            public void onComplete() {
            }
        }, updateAppUrl, new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                if (!done) {
                    getV().sendMessage(MainActivity.DOWNING, new DownInfo((int) bytesRead, (int) contentLength, 0));
                } else {
                    getV().sendMessage(MainActivity.DONE, downFile);
                }
            }
        }, downFile);
    }

}
