package com.jj.comics.ui.recommend;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.common.net.download.DownLoadManager;
import com.jj.comics.common.net.download.DownloadProgressListener;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BannerResponse;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookListPopShareResponse;
import com.jj.comics.data.model.BookListRecommondResponse;
import com.jj.comics.data.model.BookModel;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class RecommendPresenter extends BasePresenter<BaseRepository, RecommendContract.IRecommendView> implements RecommendContract.IRecommendPresenter {

    @Override
    public void loadData(int channelFlag,int pageNum, boolean evict,boolean changeChannel) {
        ContentRepository.getInstance()
                .getRecommond(channelFlag,getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListRecommondResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListRecommondResponse>() {
                    @Override
                    public void onNext(BookListRecommondResponse response) {
                        if (response.getData() != null) {
                            getV().fillData(changeChannel,response.getData());
                        } else {
                            getV().getDataFail(new NetError("获取数据失败", NetError.noDataError().getType()));
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getDataFail(error);
                    }
                });
    }

    @Override
    public void loadRecentlyComic(int pageNum,int channelFlag,boolean changeChannel) {
        getV().showProgress();
        ContentRepository.getInstance()
                .getRecentUpdate(channelFlag,pageNum, 10, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse response) {
                        BookListDataResponse.DataBean data = response.getData();
                        if (data != null) {
                            List<BookModel> bookModels = data.getData();
                            if (bookModels != null && bookModels.size() > 0) {
                                getV().onLoadRecentlyComicSuccess(changeChannel,bookModels);
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
                });
    }

    @Override
    public void loadPopShare(int channelFlag,boolean changeChannel) {
        getV().showProgress();
        ContentRepository.getInstance()
                .getPopShare(channelFlag, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListPopShareResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListPopShareResponse>() {
                    @Override
                    public void onNext(BookListPopShareResponse response) {
                        List<BookModel> bookModels = response.getData();
                        if (bookModels != null) {
                            getV().onLoadPopShareSucc(changeChannel,bookModels);
                        } else {
                            getV().onLoadPopShareFail(NetError.noDataError());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onLoadRecentlyComicFail(error);
                    }
                });
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
    public void umengOnEvent(String from, BookModel model) {
        Application application = BaseApplication.getApplication();
        if (!TextUtils.isEmpty(from)) {
            switch (from) {
                case "本周头牌":
                    MobclickAgent.onEvent(application, Constants.UMEventId.WEEK_TOP, model.getId() + " : " + model.getTitle());
                    break;
                case "宅男专区":
                    MobclickAgent.onEvent(application, Constants.UMEventId.OTAKU_DISTRICT, model.getId() + " : " + model.getTitle());
                    break;
                case "少女恋爱":
                    MobclickAgent.onEvent(application, Constants.UMEventId.GRIL_LOVE, model.getId() + " : " + model.getTitle());
                    break;
                case "抢看新作":
                    MobclickAgent.onEvent(application, Constants.UMEventId.NEW_COMIC, model.getId() + " : " + model.getTitle());
                    break;
                case "惊悚悬疑":
                    MobclickAgent.onEvent(application, Constants.UMEventId.TERROR_COMIC, model.getId() + " : " + model.getTitle());
                    break;
                case "banner":
                    MobclickAgent.onEvent(application, Constants.UMEventId.CLICK_BANNER, model.getId() + " : " + model.getTitle());
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void getBanner() {
        ContentRepository.getInstance()
                .getBanner(getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BannerResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BannerResponse>() {
                    @Override
                    public void onNext(BannerResponse bannerResponse) {
                        getV().refreshBanner(bannerResponse);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                        getV().getBannerFail();
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
