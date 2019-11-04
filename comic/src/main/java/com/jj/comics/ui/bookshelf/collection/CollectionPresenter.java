package com.jj.comics.ui.bookshelf.collection;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.ComicSubscriber;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookListResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CollectionResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.ui.read.ReadComicActivity;
import com.jj.comics.util.ReadComicHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

public class CollectionPresenter extends BasePresenter<BaseRepository, CollectionContract.ICollectionView> implements CollectionContract.ICollectionPresenter {

    @Override
    public void getCollectionList(int pageNum) {
        ContentRepository.getInstance().getCollectionByUserId(pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CollectionResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CollectionResponse>() {
                    @Override
                    public void onNext(CollectionResponse collectionResponse) {
                        if (collectionResponse.getData() != null && collectionResponse.getData().getData() != null) {
                            getV().onLoadCollectionList(collectionResponse.getData().getData());
                        } else {
                            getV().onLoadCollectionListFail(new NetError(BaseApplication.getApplication().getString(com.jj.base.R.string.base_empty_data), NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onLoadCollectionListFail(error);
                    }

                    @Override
                    protected void onEnd() {
                        getV().onLoadCollectionListEnd();
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
                        if (chapterid == 0) {//没有阅读记录就默认取章节目录的第一张
                            /*
                                这里接口做了缓存处理，针对chapterid字段来说，章节目录数据源一般不会实时变动
                                另一方面增强用户体验，立即阅读按钮点击频率是比较高了，能少开线程最好
                             */
                            return ContentRepository.getInstance().getNewCatalogList(bookModel.getId(),1,Constants.RequestBodyKey.SORT_ASC,bookModel.getUpdate_chapter_time())
                                    .subscribeOn(Schedulers.io())
                                    .flatMap(new Function<BookCatalogListResponse, ObservableSource<Long>>() {
                                        @Override
                                        public ObservableSource<Long> apply(BookCatalogListResponse response) throws Exception {
                                            return Observable.just(response.getData().getData().get(0).getId());
                                        }
                                    });
                        } else {
                            return Observable.just(chapterid);
                        }
                    }
                }).flatMap(new Function<Long, ObservableSource<BookCatalogModel>>() {
            @Override
            public ObservableSource<BookCatalogModel> apply(Long chapterid) throws Exception {
                return ReadComicHelper.getComicHelper().getBookCatalogContent(((BaseFragment) getV()).getBaseActivity(), bookModel, chapterid)
                        .subscribeOn(Schedulers.io());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookCatalogModel>bindLifecycle())
                .subscribe(new ComicSubscriber<BookCatalogModel>() {
                    @Override
                    public void onNext(BookCatalogModel catalogModel) {
                        ReadComicActivity.toRead(((BaseFragment) getV()).getBaseActivity(), bookModel, catalogModel);
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
    public void removeShelf(List<BookModel> delete,int position) {
        UserRepository.getInstance().unCollect(delete, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()) {
                            getV().onDeleteComplete(position);
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

}
