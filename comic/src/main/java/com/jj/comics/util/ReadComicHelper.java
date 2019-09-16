package com.jj.comics.util;

import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookCatalogContentResponse;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.ui.mine.pay.SubscribeActivity;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.RefreshCatalogListBySubscribeEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ReadComicHelper {
    private static ReadComicHelper mComicHelper;

    private ReadComicHelper() {
    }

    public static ReadComicHelper getComicHelper() {
        if (mComicHelper == null)
            synchronized (ReadComicHelper.class) {
                if (mComicHelper == null)
                    mComicHelper = new ReadComicHelper();
            }
        return mComicHelper;
    }

    public Observable<BookCatalogModel> getBookCatalogContent(final BaseActivity activity,final BookModel bookModel, final long chapterid){
        return ContentRepository.getInstance().getCatalogContent(bookModel.getId(),chapterid)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<BookCatalogContentResponse, ObservableSource<BookCatalogModel>>() {
                    @Override
                    public ObservableSource<BookCatalogModel> apply(final BookCatalogContentResponse response) throws Exception {
                        if (response.getCode() == 1000){//可以直接阅读
                            BookCatalogModel catalogModelNow = response.getData().getNow();
                            if (response.getData().getNow()!=null){
                                if (response.getData().getLast()!=null){//有上一话
                                    catalogModelNow.setHasLast(true);
                                    catalogModelNow.setLastChapterid(response.getData().getLast().getId());
                                }
                                if (response.getData().getNext()!=null){//有下一话
                                    catalogModelNow.setHasNext(true);
                                    catalogModelNow.setNextChapterid(response.getData().getNext().getId());
                                }
                                return Observable.just(catalogModelNow);
                            }else {
                                ToastUtil.showToastShort(response.getMessage());
                                return Observable.empty();
                            }
                        }else if (response.getCode() == 1002){//未购买
                            //如果用户是自动购买就直接调用购买接口,并且支持分章节购买
                            if (SharedPref.getInstance().getBoolean(Constants.SharedPrefKey.AUTO_BUY, false)&&bookModel.getBatchbuy() == 1) {
                                return UserRepository.getInstance().subscribe(bookModel.getId(),chapterid)
                                        .flatMap(new Function<CommonStatusResponse, ObservableSource<BookCatalogModel>>() {
                                            @Override
                                            public ObservableSource<BookCatalogModel> apply(CommonStatusResponse subResponse) throws Exception {
                                                if (subResponse.getCode() == 1000&&subResponse.getData().getStatus()){//订阅成功
                                                    ToastUtil.showToastShort("订阅成功");
                                                    //发送刷新目录列表的通知
                                                    EventBusManager.sendRefreshCatalogListBySubscribeEvent(new RefreshCatalogListBySubscribeEvent());
                                                    //再次请求章节内容接口
                                                    return ContentRepository.getInstance().getCatalogContent(bookModel.getId(),chapterid)
                                                            .flatMap(new Function<BookCatalogContentResponse, ObservableSource<BookCatalogModel>>() {
                                                                @Override
                                                                public ObservableSource<BookCatalogModel> apply(BookCatalogContentResponse response) throws Exception {
                                                                    if (response.getCode() == 1000){//可以直接阅读
                                                                        BookCatalogModel catalogModelNow = response.getData().getNow();
                                                                        if (catalogModelNow!=null){
                                                                            if (response.getData().getLast()!=null){//有上一话
                                                                                catalogModelNow.setHasLast(true);
                                                                            }
                                                                            if (response.getData().getNext()!=null){//有下一话
                                                                                catalogModelNow.setHasNext(true);
                                                                            }
                                                                            return Observable.just(catalogModelNow);
                                                                        }else {
                                                                            ToastUtil.showToastShort("没有数据，换个章节试试");
                                                                            return Observable.empty();
                                                                        }
                                                                    }else{
                                                                        ToastUtil.showToastShort(response.getMessage());
                                                                        return Observable.empty();
                                                                    }
                                                                }
                                                            });
                                                }else if (subResponse.getCode() == 1002){//金币不足，需要充值
                                                    //跳转到订阅界面
                                                    SubscribeActivity.toSubscribe(activity, bookModel,response.getData().getNow());
                                                    return Observable.empty();
                                                }else{
                                                    ToastUtil.showToastShort("订阅失败："+subResponse.getMessage());
                                                    return Observable.empty();
                                                }
                                            }
                                        });
                            }else{
                                //如果不是自动购买就跳转到订阅界面
                                SubscribeActivity.toSubscribe(activity,bookModel, response.getData().getNow());
                                return Observable.empty();
                            }
                        }
                        return Observable.error(new NetError(response.getMessage(),response.getCode()));
                    }
                });
    }

//    public Flowable<CatalogModel> getComicData(final BaseActivity activity, final long contentId, boolean isFinishActivity) {
//        return bindLogin(activity, Flowable.just(contentId)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<Long, Publisher<CatalogModel>>() {
//                    @Override
//                    public Publisher<CatalogModel> apply(Long id) throws Exception {
//                        return getReadComicFlowable(activity, contentId);
//                    }
//                }).doOnNext(new Consumer<CatalogModel>() {
//                    @Override
//                    public void accept(CatalogModel model) throws Exception {
//                        ActionReporter.reportAction(ActionReporter.Event.READ_COMIC, getTitle(activity),
//                                model.getTitle1(), /*EventMap.getInstance().setNew("catalog_id", contentId + "").getMap()*/null);
//                    }
//                }), contentId, isFinishActivity).observeOn(AndroidSchedulers.mainThread());
//    }
//
//
//    private Flowable<CatalogModel> getReadComicFlowable(final BaseActivity activity, long id) {
//        return ContentRepository.getInstance().getContentSource(id, activity.getClass().getName())
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<CatalogModelBundle, Publisher<CatalogModel>>() {
//                    @Override
//                    public Publisher<CatalogModel> apply(CatalogModelBundle catalogModelBundle) throws Exception {
//                        final CatalogModel catalogModel = catalogModelBundle.getContentSource().getContent().getSubContentList().get(0);
//                        if (catalogModel.getNeedPay()) {//需要费用  去查询扣费情况
//                            return getExpenseQueryFlowable(activity, catalogModel);
//                        } else {//不需要费用 ，直接加载
//                            return Flowable.just(catalogModel);
//                        }
//                    }
//                });
//    }
//
//    /**
//     * 查询扣费情况接口  若查询扣费失败 返回失败数据
//     *
//     * @param model
//     * @return
//     */
//    public Flowable<CatalogModel> getExpenseQueryFlowable(final BaseActivity activity, final CatalogModel model) {
//
//        return UserRepository.getInstance().getSubscribeStatus(activity.getClass().getName(), model.getId())
//                .flatMap(new Function<SubscribeResponse, Publisher<CatalogModel>>() {
//                    @Override
//                    public Publisher<CatalogModel> apply(SubscribeResponse responseModel) throws Exception {
//                        if (responseModel.isAvailable()) {
//                            //付费过  直接加载
//                            return Flowable.just(model);
//                        } else {
//                            if (SharedPref.getInstance(activity).getBoolean(Constants.SharedPrefKey.AUTO_BUY, false)) {
//                                //做过自动订阅操作  去做扣费操作
//                                return getExpenseFlowable(activity, model);
//                            } else {
////                                SubscribeActivity.toSubscribe(activity, model, getTitle(activity));
//                                return Flowable.empty();
//                            }
//                        }
////                        return getErrorFlowable(new NetError(activity.getString(R.string.comic_subscribe_fail), NetError.BusinessError));
//                    }
//                }, new Function<Throwable, Publisher<? extends CatalogModel>>() {
//                    @Override
//                    public Publisher<? extends CatalogModel> apply(Throwable throwable) throws Exception {
//                        if (throwable instanceof NetError) {
//                            int type = ((NetError) throwable).getType();
//                            if (type == NetError.NotSubscribe) {
//                                if (SharedPref.getInstance(activity).getBoolean(Constants.SharedPrefKey.AUTO_BUY, false)) {
//                                    //做过自动订阅操作  去做扣费操作
//                                    return getExpenseFlowable(activity, model);
//                                } else {
//                                    //否则跳转到订阅界面
////                                    SubscribeActivity.toSubscribe(activity, model, getTitle(activity));
//                                    return Flowable.empty();
//                                }
//                            }else{
//                                return Flowable.error(new NetError(throwable,type));
//                            }
//                        }
//                        return getErrorFlowable(throwable);
//                    }
//                }, new Callable<Publisher<? extends CatalogModel>>() {
//                    @Override
//                    public Publisher<? extends CatalogModel> call() throws Exception {
//                        return Flowable.empty();
//                    }
//                });
//    }
//
//    /**
//     * 订阅的扣费操作
//     *
//     * @param activity
//     * @param model
//     * @return
//     */
//    public Publisher<CatalogModel> getExpenseFlowable(final BaseActivity activity, final CatalogModel model) {
////        return UserRepository.getInstance().subscribe(activity.getClass().getName(), model, getTitle(activity))
////                .flatMap(new Function<ResponseModel, Publisher<CatalogModel>>() {
////                    @Override
////                    public Publisher<CatalogModel> apply(ResponseModel responseModel) throws Exception {
////                        //上传用户订阅行为  并Toast提示订阅成功
////                        ActionReporter.reportAction(ActionReporter.Event.SUBSCRIBE_COMIC, getTitle(activity), model.getTitle1(), null);
////                        /**
////                         *1.通知阅读页面刷新目录列表
////                         *2.通知我的界面刷新用户数据
////                         *3.通知我的界面更新自动购买
////                         */
////                        EventBusManager.sendRefreshCatalogListBySubscribeEvent(new RefreshCatalogListBySubscribeEvent());
////                        EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent());
////                        EventBusManager.sendUpdateAutoBuyStatusEvent(new UpdateAutoBuyStatusEvent());
////                        ToastUtil.showToastShort(activity.getString(R.string.comic_subscribe_success));
////                        //加载漫画图片
////                        return Flowable.just(model);
////                    }
////                }, new Function<Throwable, Publisher<CatalogModel>>() {
////                    @Override
////                    public Publisher<CatalogModel> apply(Throwable throwable) throws Exception {
////                        if (throwable instanceof NetError && ((NetError) throwable).getType() == NetError.BalanceError) {
////                            SubscribeActivity.toSubscribe(activity, model, getTitle(activity));
//////                            if (SharedPref.getInstance(activity).getBoolean(Constants.SharedPrefKey.AUTO_BUY, false)) {
//////                                //做过自动订阅操作  且金币不够
//////                                SubscribeActivity.toSubscribe(activity, catalogModel, getTitle(activity));
//////                            } else {
//////                                //未做过自动订阅操作  且金币不够 去订阅界面
//////                                SubscribeActivity.toSubscribe(activity, catalogModel, getTitle(activity));
//////                            }
////                        }
////                        return getErrorFlowable(throwable);
////                    }
////                }, new Callable<Publisher<CatalogModel>>() {
////                    @Override
////                    public Publisher<CatalogModel> call() throws Exception {
////                        return Flowable.empty();
////                    }
////                });
//        return null;
//    }
//
//    /**
//     * 未登录或者token过期情况下  显示登录弹窗
//     *
//     * @param activity
//     * @param flowable
//     * @param isFinishActivity 点击叉号结束界面
//     * @return
//     */
//    public Flowable<CatalogModel> bindLogin(final BaseActivity activity, Flowable<CatalogModel> flowable, final long id, final boolean isFinishActivity) {
//        return flowable.observeOn(AndroidSchedulers.mainThread()).doOnError(new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                if (throwable instanceof NetError && ((NetError) throwable).getType() == NetError.AuthError) {
//                    LogUtil.e(Thread.currentThread().getName());
//                    logout(activity, id, isFinishActivity);
//                }
//            }
//        });
//    }
//
//    private LoginNotifyDialog loginNotifyDialog;
//
//    public void createLoginDialog(final Activity activity, final long id, final boolean isFinishActivity) {
//        if (loginNotifyDialog == null) loginNotifyDialog = new LoginNotifyDialog();
//        loginNotifyDialog.show(((FragmentActivity) activity).getSupportFragmentManager(), new DialogUtilForComic.OnDialogClick() {
//            @Override
//            public void onConfirm() {
//                loginNotifyDialog.dismiss();
//                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY)
//                        .withLong(Constants.IntentKey.ID, id)
//                        .navigation(activity, RequestCode.LOGIN_REQUEST_CODE);
//            }
//
//            @Override
//            public void onRefused() {
//                if (isFinishActivity) activity.finish();
//            }
//
//
//        });
//    }
//
//    private void logout(final BaseActivity activity, final long id, final boolean isFinishActivity) {
//        Flowable.<Boolean>create(new FlowableOnSubscribe<Boolean>() {
//            @Override
//            public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
//                SharedPref.getInstance(activity).remove(Constants.SharedPrefKey.TOKEN);
//                LoginHelper.logOffAllUser();
//                new DaoHelper<ReadRecords>().deleteSomeReadRecords();
//                MobclickAgent.onProfileSignOff();
//                TencentHelper.getTencent().logout(activity);
//                AccessTokenKeeper.clear(activity);
//                emitter.onNext(true);
//                emitter.onComplete();
//            }
//        }, BackpressureStrategy.BUFFER)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.<Boolean>autoDisposable(AndroidLifecycleScopeProvider.from(activity.getLifecycle())))
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean s) throws Exception {
//                        createLoginDialog(activity, id, isFinishActivity);
//                    }
//                });
//
//    }
//
//    private Flowable<CatalogModel> getErrorFlowable(Throwable throwable) {
//        return Flowable.<CatalogModel>error(throwable);
//    }
//
//    public String getTitle(Activity activity) {
//        Serializable mainContent = activity.getIntent().getSerializableExtra(Constants.IntentKey.MAIN_CONTENT);
//        if (mainContent == null)
//            mainContent = activity.getIntent().getSerializableExtra(Constants.IntentKey.MODEL);
//        return mainContent != null && mainContent instanceof ComicDetailModel ? ((ComicDetailModel) mainContent).getTitle1() : "";
//    }

}
