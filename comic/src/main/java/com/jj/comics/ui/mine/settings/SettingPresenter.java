package com.jj.comics.ui.mine.settings;

import android.os.Environment;

import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.RestResponse;
import com.jj.comics.util.SharedPreManger;

import java.io.File;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class SettingPresenter extends BasePresenter<BaseRepository, SettingContract.ISettingView> implements SettingContract.ISettingPresenter {

    @Override
    public void clearCache() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> flowableEmitter) throws Exception {
                try {
                    FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getCacheDir());
                    FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getFilesDir());
                    FileUtil.deleteFile("/data/data/"
                            + BaseApplication.getApplication().getPackageName() + "/shared_prefs/pdr.xml");
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getExternalCacheDir());
                        FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getExternalFilesDir(""));
                    }
                    FileUtil.deleteFilesByDirectory(new File("/data/data/"
                            + BaseApplication.getApplication().getPackageName() + "/app_webview"));
                    ILFactory.getLoader().clearDiskCache(BaseApplication.getApplication());
                    flowableEmitter.onNext(FileUtil.getCaCheSize(BaseApplication.getApplication()));
                } catch (Exception e) {
                    flowableEmitter.onError(e);
                }
                flowableEmitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<String>bindLifecycle())
                .subscribe(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String size) {
                        ILFactory.getLoader().clearMemoryCache(BaseApplication.getApplication());
                        getV().setCacheSize(size);
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void getCacheSize() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> flowableEmitter) throws Exception {
                try {
                    flowableEmitter.onNext(FileUtil.getCaCheSize(BaseApplication.getApplication()));
                } catch (Exception e) {
                    flowableEmitter.onError(e);
                }
                flowableEmitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(getV().<String>bindUntilEvent(FragmentEvent.DESTROY))
//                .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<String>bindLifecycle())
                .subscribe(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String size) {
                        getV().setCacheSize(size);
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getRest() {
        UserRepository.getInstance().getRest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindLifecycle())
                .subscribe(new ApiSubscriber2<RestResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showError(error);
                    }

                    @Override
                    public void onNext(RestResponse responseModel) {
                        if (responseModel.getData() != null) {
                            getV().rest(responseModel);
                        } else {
                            getV().showError(new NetError(NetError.noDataError().getException(), NetError.NoDataError));
                        }
                    }
                });
    }

    @Override
    public void setAuto(int autoBuy, int receive) {
        UserRepository.getInstance().setAutoReceive(autoBuy, receive)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showError(error);
                    }

                    @Override
                    public void onNext(ResponseModel responseModel) {
                        if (responseModel.getCode() == 200) {
                            if (autoBuy == 0) {
                                SharedPreManger.getInstance().saveAutoBuyStatus(false);
                            } else if (autoBuy == 1) {
                                SharedPreManger.getInstance().saveAutoBuyStatus(true);
                            }

                            if (receive == 0) {
                                SharedPreManger.getInstance().saveReceiveStatus(false);
                            } else if (receive == 1) {
                                SharedPreManger.getInstance().saveReceiveStatus(true);
                            }
                        } else {
                            getV().showError(new NetError(responseModel.getMessage(), NetError.NoDataError));
                        }
                    }
                });
    }


}
