package com.jj.comics.ui.detail;

import android.app.Activity;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.ComicSubscriber;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookCatalogContentResponse;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.BookModelResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.read.ReadComicActivity;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.ReadComicHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;


class ComicDetailPresenter extends BasePresenter<BaseRepository,
        ComicDetailContract.IDetailView> implements ComicDetailContract.IDetailPresenter {

    /**
     * 获取漫画阅读历史
     *
     * setDefault? 未查询到记录是是否设置默认第一章
     */
    @Override
    public void getCatalogHistory(final long contentId, int chapterid, ApiSubscriber2 apiSubscriber) {
        Observable.just(chapterid)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer chapterid) throws Exception {
                        if (chapterid == 0) {//表示没有阅读记录
                            return chapterid;
                        } else {//有阅读记录就和本地记录去比较
                            //暂时先不管本地存储阅读记录的逻辑,假设这里已经和本地做了比较
                            return chapterid;
                        }
                    }
                }).flatMap(new Function<Integer, ObservableSource<BookCatalogModel>>() {
            @Override
            public ObservableSource<BookCatalogModel> apply(final Integer chapterid) throws Exception {
                if (chapterid == 0){
                    //没有阅读记录就先去调用章节目录接口获取第一章详情信息
                    return ContentRepository.getInstance().getCatalogList(contentId,1,Constants.RequestBodyKey.SORT_ASC)
                                .map(new Function<BookCatalogListResponse, BookCatalogModel>() {
                                    @Override
                                    public BookCatalogModel apply(BookCatalogListResponse response) throws Exception {
                                        return response.getData().getData().get(0);
                                    }
                                });
                }else{
                    //有阅读记录就直接调用章节内容详情信息
                    return ContentRepository.getInstance().getCatalogContent(contentId,chapterid)
                            .map(new Function<BookCatalogContentResponse, BookCatalogModel>() {
                                @Override
                                public BookCatalogModel apply(BookCatalogContentResponse response) throws Exception {
                                    BookCatalogModel catalogModel = new BookCatalogModel();
//                                    BookCatalogContentResponse.DataBean.NowBean nowCatalog = response.getData().getNow();
//                                    catalogModel.setId(chapterid);
//                                    catalogModel.setChapterorder(nowCatalog.getChapterorder()+"");
//                                    catalogModel.setSaleprice(nowCatalog.getSaleprice());
                                    return catalogModel;
                                }
                            });
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(apiSubscriber);
    }

    /**
     * 加载漫画
     *
     * @param id          漫画id
     * @param umengUpload 是否要上传umeng时间
     */
    @Override
    public void getComicDetail(long id, final boolean umengUpload) {
        ContentRepository.getInstance().getContentDetailWithUserActions(id, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookModelResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookModelResponse>() {
                    @Override
                    public void onNext(final BookModelResponse response) {
                        getV().onLoadComicDetail(response.getData(), umengUpload);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });

    }

    @Override
    public void toRead(final BookModel bookModel, final long chapterid) {
        getV().showProgress();
        Observable.just(chapterid)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Long chapterid) throws Exception {
                        if (chapterid == 0){//没有阅读记录就默认取章节目录的第一张
                            /*
                                这里接口做了缓存处理，针对chapterid字段来说，章节目录数据源一般不会实时变动
                                另一方面增强用户体验，立即阅读按钮点击频率是比较高了，能少开线程最好
                             */
                            return ComicApi.getProviders().getCatalogList(ContentRepository.getInstance().getCatalogList(bookModel.getId(),1,Constants.RequestBodyKey.SORT_ASC), new DynamicKey(1),
                                    new EvictDynamicKey(true))
                                    .subscribeOn(Schedulers.io())
                                    .flatMap(new Function<BookCatalogListResponse, ObservableSource<Long>>() {
                                        @Override
                                        public ObservableSource<Long> apply(BookCatalogListResponse response) throws Exception {
                                            return Observable.just(response.getData().getData().get(0).getId());
                                        }
                                    });
                        }else{
                            return Observable.just(chapterid);
                        }
                    }
                }).flatMap(new Function<Long, ObservableSource<BookCatalogModel>>() {
            @Override
            public ObservableSource<BookCatalogModel> apply(Long chapterid) throws Exception {
                return ReadComicHelper.getComicHelper().getBookCatalogContent((BaseActivity) getV(),bookModel,chapterid)
                        .subscribeOn(Schedulers.io());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookCatalogModel>bindLifecycle())
                .subscribe(new ComicSubscriber<BookCatalogModel>() {
                    @Override
                    public void onNext(BookCatalogModel catalogModel) {
                        ReadComicActivity.toRead((Activity) getV(),bookModel,catalogModel);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void addOrRemoveCollect(final BookModel model, final boolean collectByCurrUser) {
        getV().showProgress();
        List<BookModel> models = new ArrayList<>();
        models.add(model);
        Observable<CommonStatusResponse> observable = collectByCurrUser
                ? UserRepository.getInstance().unCollect(models, getV().getClass().getName())
                : UserRepository.getInstance().collect(model.getId(), getV().getClass().getName());
        observable.observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()) {
                            getV().onCollectionSuccess(!collectByCurrUser);
                        } else {
                            ToastUtil.showToastShort(response.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void getCollectStatus(long id) {
        UserRepository.getInstance().getCollectStatus(id, getV().getClass().getSimpleName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        getV().fillCollectStatus(response);
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });

    }

    /**
     * 获取先关推荐的列表的列表
     */
    @Override
    public void loadCommendList(long id, int pageNum, int sectionId) {
        ContentRepository.getInstance().getLikeBookList(id, pageNum, sectionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse response) {
                        if (response.getData() != null && response.getData().getData() != null) {
                            getV().onLoadRecommendList(response);
                        } else {
                            onFail(new NetError(response.getMessage(), response.getCode()));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });

    }

}
