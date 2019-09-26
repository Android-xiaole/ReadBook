package com.jj.comics.ui.recommend;

import android.os.Environment;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.common.net.download.DownLoadManager;
import com.jj.comics.common.net.download.DownloadProgressListener;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BannerResponse;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookListPopShareResponse;
import com.jj.comics.data.model.BookListRecommondResponse;
import com.jj.comics.data.model.BookModel;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class RecommendPresenter extends BasePresenter<BaseRepository, RecommendContract.IRecommendView> implements RecommendContract.IRecommendPresenter {

    private ApiSubscriber2<BookListRecommondResponse> mSubscriberContent;
    private ApiSubscriber2<BookListDataResponse> mSubscriberRecently;
    private ApiSubscriber2<BookListPopShareResponse> mSubscriberShare;
    private ApiSubscriber2<BannerResponse> mSubscriberBanner;

    @Override
    public void loadData(int channelFlag, int pageNum, boolean evict, boolean changeChannel) {
        if (mSubscriberContent != null) mSubscriberContent.dispose();
        mSubscriberContent = new ApiSubscriber2<BookListRecommondResponse>() {
            @Override
            public void onNext(BookListRecommondResponse response) {
                if (response.getData() != null) {
                    getV().fillData(changeChannel, response.getData());
                } else {
                    getV().getDataFail(new NetError("获取数据失败", NetError.noDataError().getType()));
                }

            }

            @Override
            protected void onFail(NetError error) {
                getV().getDataFail(error);
            }
        };
        ContentRepository.getInstance()
                .getRecommond(channelFlag, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListRecommondResponse>bindLifecycle())
                .subscribe(mSubscriberContent);
    }

    @Override
    public void loadRecentlyComic(int pageNum, int channelFlag, boolean changeChannel) {
        if (mSubscriberRecently != null) mSubscriberRecently.dispose();
        mSubscriberRecently = new ApiSubscriber2<BookListDataResponse>() {
            @Override
            public void onNext(BookListDataResponse response) {
                BookListDataResponse.DataBean data = response.getData();
                if (data != null) {
                    List<BookModel> bookModels = data.getData();
                    if (bookModels != null && bookModels.size() > 0) {
                        getV().onLoadRecentlyComicSuccess(changeChannel, bookModels);
                    } else {
                        getV().onLoadRecentlyComicFail(NetError.noDataError());
                    }
                } else {
                    getV().onLoadRecentlyComicFail(NetError.noDataError());
                }
            }

            @Override
            protected void onFail(NetError error) {
                getV().onLoadRecentlyComicFail(error);
            }
        };
        ContentRepository.getInstance()
                .getRecentUpdate(channelFlag, pageNum, 10, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(mSubscriberRecently);
    }

    @Override
    public void loadPopShare(int channelFlag, boolean changeChannel) {
        if (mSubscriberShare != null) mSubscriberShare.dispose();
        mSubscriberShare = new ApiSubscriber2<BookListPopShareResponse>() {
            @Override
            public void onNext(BookListPopShareResponse response) {
                List<BookModel> bookModels = response.getData();
                if (bookModels != null) {
                    getV().onLoadPopShareSucc(changeChannel, bookModels);
                } else {
                    getV().onLoadPopShareFail(NetError.noDataError());
                }
            }

            @Override
            protected void onFail(NetError error) {
                getV().onLoadRecentlyComicFail(error);
            }
        };
        ContentRepository.getInstance()
                .getPopShare(channelFlag, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListPopShareResponse>bindLifecycle())
                .subscribe(mSubscriberShare);
    }


    public void goDown(final String updateAppUrl) {
        getV().sendMessage(RecommendFragment.START_DOWNLOAD, null);

        final String file = Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + File.separator + PackageUtil.getAppName(BaseApplication.getApplication()) + File.separator;
        final File downFile = new File(file + FileUtil.getFileName(updateAppUrl));
        if (downFile.exists()) {
            downFile.delete();
        }
        DownLoadManager.builder().downApkFile(((RecommendFragment) getV()).getBaseActivity(), new ResourceSubscriber() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {
                //不管什么错误先删除文件再说，防止出现不可预知的BUG
                if (downFile.exists()) {
                    downFile.delete();
                }
                getV().sendMessage(RecommendFragment.DOWN_FAIL, t);
            }

            @Override
            public void onComplete() {
            }
        }, updateAppUrl, new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                if (!done) {
                    getV().sendMessage(RecommendFragment.DOWNING, new DownInfo((int) bytesRead, (int) contentLength, 0));
                } else {
                    getV().sendMessage(RecommendFragment.DONE, downFile);
                }
            }
        }, downFile);
    }


    @Override
    public void getBanner() {
        if (mSubscriberBanner != null) mSubscriberBanner.dispose();
        mSubscriberBanner = new ApiSubscriber2<BannerResponse>() {
            @Override
            public void onNext(BannerResponse bannerResponse) {
                getV().refreshBanner(bannerResponse);
            }

            @Override
            protected void onFail(NetError error) {
                getV().showToastShort(error.getMessage());
                getV().getBannerFail();
            }
        };
        ContentRepository.getInstance()
                .getBanner(getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BannerResponse>bindLifecycle())
                .subscribe(mSubscriberBanner);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
