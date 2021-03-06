package com.jj.comics.ui.detail;

import android.app.Activity;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.BookModelResponse;
import com.jj.comics.data.model.CommonStatusResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


class ComicDetailPresenter extends BasePresenter<BaseRepository, ComicDetailContract.IDetailView> implements ComicDetailContract.IDetailPresenter {

    /**
     * 加载漫画
     *
     * @param id 漫画id
     */
    @Override
    public void getComicDetail(long id) {
        getV().showProgress();
        ContentRepository.getInstance().getContentDetail(id, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookModelResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookModelResponse>() {
                    @Override
                    public void onNext(final BookModelResponse response) {
                        getV().onLoadComicDetail(response.getData());
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
        if (getV() instanceof Activity) {
            LoadingActivity.toLoading((Activity) getV(), bookModel, chapterid);
        }
//        getV().showProgress();
//        Observable.just(chapterid)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<Long, ObservableSource<Long>>() {
//                    @Override
//                    public ObservableSource<Long> apply(Long chapterid) throws Exception {
//                        if (chapterid == 0){//没有阅读记录就默认取章节目录的第一张
//                            /*
//                                这里接口做了缓存处理，针对chapterid字段来说，章节目录数据源一般不会实时变动
//                                另一方面增强用户体验，立即阅读按钮点击频率是比较高了，能少开线程最好
//                             */
//                            return ContentRepository.getInstance().getCacheCatalogList(bookModel.getId(),bookModel.getUpdate_chapter_time())
//                                    .subscribeOn(Schedulers.io())
//                                    .flatMap(new Function<BookCatalogListResponse, ObservableSource<Long>>() {
//                                        @Override
//                                        public ObservableSource<Long> apply(BookCatalogListResponse response) throws Exception {
//                                            return Observable.just(response.getData().getData().get(0).getId());
//                                        }
//                                    });
//                        }else{
//                            return Observable.just(chapterid);
//                        }
//                    }
//                }).flatMap(new Function<Long, ObservableSource<BookCatalogModel>>() {
//            @Override
//            public ObservableSource<BookCatalogModel> apply(Long chapterid) throws Exception {
//                return ReadComicHelper.getComicHelper().getBookCatalogContent((BaseActivity) getV(),bookModel,chapterid)
//                        .subscribeOn(Schedulers.io());
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .as(this.<BookCatalogModel>bindLifecycle())
//                .subscribe(new ComicSubscriber<BookCatalogModel>() {
//                    @Override
//                    public void onNext(BookCatalogModel catalogModel) {
//                        ReadComicActivity.toRead((Activity) getV(),bookModel,catalogModel);
//                    }
//
//                    @Override
//                    protected void onFail(NetError error) {
//                        ToastUtil.showToastShort(error.getMessage());
//                    }
//
//                    @Override
//                    protected void onEnd() {
//                        getV().hideProgress();
//                    }
//                });
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
                        getV().fillCollectStatus(response.getData().getStatus());
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

    /**
     *
     * @param bookModel 小说详情
     * @param pageNum 页码
     * @param sort 排序方式
     * @param isNextPage //标记当前操作是否是向下分页
     */
    public void getCatalogList(BookModel bookModel,int pageNum,String sort,boolean isNextPage) {
        if (getV() == null) return;
        getV().showProgress();
        ContentRepository.getInstance().getNewCatalogList(bookModel.getId(),pageNum,sort,bookModel.getUpdate_chapter_time())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookCatalogListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookCatalogListResponse>() {
                    @Override
                    public void onNext(BookCatalogListResponse response) {
                        getV().onGetCatalogList(response.getData().getData(), response.getData().getTotal_num(),pageNum,isNextPage);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetCatalogListFail(error,pageNum,isNextPage);
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                        getV().onGetCatalogListEnd();
                    }
                });

    }

}
