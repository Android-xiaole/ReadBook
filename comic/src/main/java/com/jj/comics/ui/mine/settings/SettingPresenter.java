package com.jj.comics.ui.mine.settings;

import android.os.Environment;

import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.common.net.download.DownLoadManager;
import com.jj.comics.common.net.download.DownloadProgressListener;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.UpdateModelProxy;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class SettingPresenter extends BasePresenter<BaseRepository, SettingContract.ISettingView> implements SettingContract.ISettingPresenter {


    @Override
    public void checkUpdate(final BaseActivity baseActivity) {
        ProductRepository.getInstance().getChannelUpdateInfo(getV().getClass().getName())
                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.<UpdateModelProxy>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<UpdateModelProxy>bindLifecycle())
                .subscribe(new ApiSubscriber2<UpdateModelProxy>() {
                    @Override
                    public void onNext(UpdateModelProxy channelUpdate) {
                        //model类以判断是否需要更新  此时可直接走升级逻辑
                        if (Constants.SHOW_UPDATE_DIALOG) {
                            // 判断是否升级
                            getV().updateAlert(channelUpdate.getProductChannelUpdate());
                        } else {
                            goDown(channelUpdate.getProductChannelUpdate().getProductDownUrl(), baseActivity);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        LogUtil.e("升级APP信息--->" + error.getMessage());
                        if (error.getType() == NetError.NoConnectError) {
                            ToastUtil.showToastLong(error.getMessage());
                        } else
                            ToastUtil.showToastLong(BaseApplication.getApplication().getString(R.string.comic_update_app_msg));
                    }
                });
    }

    public void goDown(final String updateAppUrl, BaseActivity baseActivity) {
        getV().sendMessage(SettingActivity.START_DOWNLOAD, null);

        final String file = Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + File.separator + PackageUtil.getAppName(BaseApplication.getApplication()) + File.separator;
        final File downFile = new File(file + FileUtil.getFileName(updateAppUrl));
        if (downFile.exists()) {
            downFile.delete();
        }
        DownLoadManager.builder().downApkFile(baseActivity, new ResourceSubscriber() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {
                //不管什么错误先删除文件再说，防止出现不可预知的BUG
                if (downFile.exists()) {
                    downFile.delete();
                }
                getV().sendMessage(SettingActivity.DOWN_FAIL, t);
            }

            @Override
            public void onComplete() {
            }
        }, updateAppUrl, new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                if (!done) {
                    getV().sendMessage(SettingActivity.DOWNING, new DownInfo((int) bytesRead, (int) contentLength, 0));
                } else {
                    getV().sendMessage(SettingActivity.DONE, downFile);
                }
            }
        }, downFile);
    }
}
