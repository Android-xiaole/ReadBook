package com.jj.comics.ui.recommend;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.common.net.download.DownLoadManager;
import com.jj.comics.common.net.download.DownloadProgressListener;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.goods.GoodsRepository;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.model.BannerResponse;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookListPopShareResponse;
import com.jj.comics.data.model.BookListRecommondResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.Push;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.PayUtils;
import com.jj.comics.util.reporter.ActionReporter;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import retrofit2.HttpException;

public class RecommendPresenter extends BasePresenter<BaseRepository, RecommendContract.IRecommendView> implements RecommendContract.IRecommendPresenter {

    @Override
    public void loadData(int pageNum, boolean evict,boolean changeChannel) {
        ComicApi.getProviders().getRecommendData(ContentRepository.getInstance()
                        .getRecommond(getV().getClass().getName()),
                new DynamicKey(pageNum),
                new EvictDynamicKey(evict))
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
                            if (bookModels != null) {
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



    @Override
    public void checkFreeGoldStatus() {
        TaskRepository.getInstance().presentGold()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData() != null && response.getData().getStatus()) {
                            getV().onFreeGoldChecked();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    @Override
    public void getAdsPush_128() {
        ProductRepository.getInstance().getAdsPush(getV().getClass().getName(), Constants.AD_ID_133)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<Push>bindLifecycle())
                .subscribe(new ApiSubscriber2<Push>() {

                    @Override
                    public void onNext(Push push) {
                        try {
                            getV().onAdsPush_133(push);
                        } catch (Exception e) {
                            getV().onAdsPush_128_fail(new NetError(e, NetError.OtherError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onAdsPush_128_fail(error);
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

    /**
     * 获取广告推送
     */
    @Override
    public void getAdsPush_Comic() {
        ProductRepository.getInstance().getAdsPush(getV().getClass().getName(), Constants.AD_ID_132)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<Push>bindLifecycle())
                .subscribe(new ApiSubscriber2<Push>() {
                    @Override
                    public void onNext(Push push) {
                        if (push == null || push.getId() == null || !push.getId().equals(Constants.AD_ID_132)) {
                            //获取免费金币
                            if (LoginHelper.getOnLineUser() != null) {
                                checkFreeGoldStatus();
                            }
                            return;
                        }
                        if (!SharedPref.getInstance().getString("ad_push_id", "").equals(push.getText1())) {
                            SharedPref.getInstance().putBoolean("main_push", false);
                            getV().adsPush(push);
                        } else {
                            //获取免费金币
                            if (LoginHelper.getOnLineUser() != null) {
                                checkFreeGoldStatus();
                            }
                        }
                        SharedPref.getInstance().putString("ad_push_id", push.getText1());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        //获取免费金币
                        if (LoginHelper.getOnLineUser() != null) {
                            checkFreeGoldStatus();
                        }
                    }
                });

    }

    /**
     * 获取支付活动弹窗信息
     */
    public void getPayAction(){
        ProductRepository.getInstance().getPayAction()
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<PayActionResponse>() {

                    @Override
                    public void onNext(PayActionResponse payActionResponse) {
                        PayActionResponse.DataBean data = payActionResponse.getData();
                        if (data!=null){
                            PayActionResponse.DataBean.PayinfoBean payinfo = data.getPayinfo();
                            if (data.getIs_alert() != 0&&payinfo!=null){
                                getV().onGetPayAction(payinfo,data.getClose_time());
                                return;
                            }
                        }
                        getAdsPush_128();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        //失败就去调用别的弹窗接口
                        getAdsPush_128();
                    }
                });

    }

    @Override
    public void payAli(long goodsId) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                emitter.onNext(LoginHelper.getOnLineUser() == null);
            }
        }).flatMap(new Function<Boolean, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Boolean loginState) throws Exception {
                return GoodsRepository.getInstance()
                        .createPreOrderAli(goodsId, 0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .flatMap(new Function<PrePayOrderResponseAli, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(PrePayOrderResponseAli prePayOrderResponseAli) throws Exception {
                                String result = "";
                                PrePayOrderResponseAli.DataBean data = prePayOrderResponseAli.getData();
                                if (data != null) {
                                    Activity activity = ((BaseFragment)getV()).getBaseActivity();
                                    PayTask alipay = new PayTask(activity);
                                    Map<String, String> resultMap = alipay.payV2(data.getPay_content(), true);
                                    result = PayUtils.getAlipayResponse(resultMap.get("resultStatus"));
                                    MobclickAgent.onEvent(activity, Constants.UMEventId.PAY_TYPE, "AliPay");
                                    MobclickAgent.onEvent(activity, Constants.UMEventId.PAY_RESULT, result);
                                    String reg = "NZ_\\d+";
                                    Pattern pattern = Pattern.compile(reg);
                                    Matcher matcher = pattern.matcher(data.getPay_content());
                                    String fqdnId = data.getPay_content();
                                    if (matcher.find()) {
                                        fqdnId = matcher.group();
                                    }
                                    String pay_info = LoginHelper.getOnLineUser().getUid() + "&" + fqdnId + "&" + result;
                                    ActionReporter.reportAction(ActionReporter.Event.PAY_RESULT, pay_info, null, null);
                                }
                                return Observable.just(result);
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<String>() {
                    @Override
                    public void onNext(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            //处理支付宝支付
                            if (TextUtils.equals(response, "支付失败")
                                    || TextUtils.equals(response, "网络连接错误")
                                    || TextUtils.equals(response, "您取消了支付")) {
                                //支付宝支付失败 弹窗提示
                                getV().payFail(response);
                            } else {
                                //支付宝支付失败  toast提示
                                getV().showToastShort(response);
                            }
                        }
                        if (TextUtils.equals("支付成功", response)) {
                            getV().onPaySuccess();
                        }
                        getV().hideProgress();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error != null)
                            getV().payFail(error.getMessage() + "");
                    }
                });
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
