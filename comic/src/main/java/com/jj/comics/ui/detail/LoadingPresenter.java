package com.jj.comics.ui.detail;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicSubscriber;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.util.ReadComicHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoadingPresenter extends BasePresenter<BaseRepository,LoadingContract.View>implements LoadingContract.Presenter{

    @Override
    public void toRead(BookModel bookModel, long chapterid) {
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
                            return ContentRepository.getInstance().getNewCatalogList(bookModel.getId(),1, Constants.RequestBodyKey.SORT_ASC,bookModel.getUpdate_chapter_time())
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
                .subscribe(new ApiSubscriber2<BookCatalogModel>() {
                    @Override
                    public void onNext(BookCatalogModel catalogModel) {
                        if (catalogModel.isPaid()){//已经付费
                            getV().loadSuccess(catalogModel);
                        }else{//未付费的逻辑在这里处理
                            getV().onLoadChapterContentNoPayError(catalogModel);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().loadFail(error);
                    }

                    @Override
                    protected void onEnd() {
                    }
                });
    }
}
