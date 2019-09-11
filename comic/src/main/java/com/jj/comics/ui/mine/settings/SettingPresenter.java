package com.jj.comics.ui.mine.settings;

import android.os.Environment;

import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.utils.FileUtil;

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


}
